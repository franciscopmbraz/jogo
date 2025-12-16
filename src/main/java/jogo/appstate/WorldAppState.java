package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import jogo.gameobject.character.Player;
import jogo.gameobject.item.*;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;



public class WorldAppState extends BaseAppState {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final PhysicsSpace physicsSpace;
    private final Camera cam;
    private final InputAppState input;
    private PlayerAppState playerAppState;

    // world root for easy cleanup
    private Node worldNode;
    private VoxelWorld voxelWorld;
    private com.jme3.math.Vector3f spawnPosition;

    private VoxelWorld.Vector3i breakingCell = null;
    private float breakingProgress = 0f;

    public WorldAppState(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace, Camera cam, InputAppState input) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.cam = cam;
        this.input = input;
    }

    public void registerPlayerAppState(PlayerAppState playerAppState) {
        this.playerAppState = playerAppState;
    }

    @Override
    protected void initialize(Application app) {
        worldNode = new Node("World");
        rootNode.attachChild(worldNode);

        // Lighting
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.20f)); // slightly increased ambient
        worldNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.35f, -1.3f, -0.25f).normalizeLocal()); // more top-down to reduce harsh contrast
        sun.setColor(ColorRGBA.White.mult(0.85f)); // slightly dimmer sun
        worldNode.addLight(sun);

        // Voxel world 16x16x16 (reduced size for simplicity)
        voxelWorld = new VoxelWorld(assetManager, 320, 64, 320);
        voxelWorld.generateLayers();
        voxelWorld.buildMeshes();
        voxelWorld.clearAllDirtyFlags();
        worldNode.attachChild(voxelWorld.getNode());
        voxelWorld.buildPhysics(physicsSpace);
        // compute recommended spawn
        spawnPosition = voxelWorld.getRecommendedSpawn();

    }

    public com.jme3.math.Vector3f getRecommendedSpawnPosition() {
        return spawnPosition != null ? spawnPosition.clone() : new com.jme3.math.Vector3f(25.5f, 12f, 25.5f);
    }

    public VoxelWorld getVoxelWorld() {
        return voxelWorld;
    }


    @Override
    public void update(float tpf) {
        if (input != null && input.isMouseCaptured() && input.isBreakingHeld()) {

            // (terias de adaptar isBreakingHeld() ou usar um flag de "está a segurar")
            var pick = voxelWorld.pickFirstSolid(cam, 6f);
            if (pick.isEmpty()) {
                breakingCell = null;
                breakingProgress = 0f;
                return;
            }

            pick.ifPresent(hit -> {
                VoxelWorld.Vector3i cell = hit.cell;

                if (breakingCell == null || cell.x != breakingCell.x || cell.y != breakingCell.y || cell.z != breakingCell.z) {
                    breakingCell = cell;
                    breakingProgress = 0f;
                }

                // velocidade base
                float baseSpeed = 1.0f;

                // ver item na mão
                Inventory inv = playerAppState.getPlayer().getInventory();
                ItemStack hand = inv.getSelectedStack();
                float toolMult = 1.0f;

                if (hand != null && !hand.isEmpty()) {
                    Item item = hand.getItem();
                    if (item instanceof PickaxeItem pickaxe) {
                        toolMult = pickaxe.getSpeedMultiplier();
                    }
                }

                breakingProgress += tpf * baseSpeed * toolMult;

                float timeToBreak = 0.5f; // 1 segundo com toolMult = 1

                if (breakingProgress >= timeToBreak) {
                    voxelWorld.breakAt(cell.x, cell.y, cell.z);
                    voxelWorld.rebuildDirtyChunks(physicsSpace);
                    playerAppState.refreshPhysics();
                    breakingProgress = 0f;
                    breakingCell = null;
                }

            });

        } else {
            // botão não está a ser mantido → reset
            breakingCell = null;
            breakingProgress = 0f;
        }


        if (input != null && input.isMouseCaptured() && input.consumePlaceRequested()) {
            var pick = voxelWorld.pickFirstSolid(cam, 6f);
            pick.ifPresent(hit -> {
                VoxelWorld.Vector3i cell = hit.cell;
                Vector3f normal = hit.normal;

                // célula vizinha na direção da face clicada
                int x = cell.x + (int) normal.x;
                int y = cell.y + (int) normal.y;
                int z = cell.z + (int) normal.z;

                // garantir que temos player + inventário
                if (playerAppState == null) return;
                Player player = playerAppState.getPlayer();
                if (player == null) return;

                Inventory inv = player.getInventory();
                ItemStack stack = inv.getSelectedStack();
                if (stack == null || stack.isEmpty()) return;

                if (!(stack.getItem() instanceof BlockItem blockItem)) {
                    return; // item na mão não é bloco
                }

                // só coloca se o alvo estiver vazio (ar)
                byte existing = voxelWorld.getBlock(x, y, z);
                if (existing != VoxelPalette.AIR_ID) {
                    return;
                }

                // coloca bloco
                voxelWorld.setBlock(x, y, z, blockItem.getBlockId());
                voxelWorld.rebuildDirtyChunks(physicsSpace);
                playerAppState.refreshPhysics();

                // consome 1 do stack
                stack.remove(1);
            });
        }

        if (input != null && input.consumeToggleShadingRequested()) {
            voxelWorld.toggleRenderDebug();
        }
    }

    @Override
    protected void cleanup(Application app) {
        if (worldNode != null) {
            // Remove all physics controls under worldNode
            worldNode.depthFirstTraversal(spatial -> {
                RigidBodyControl rbc = spatial.getControl(RigidBodyControl.class);
                if (rbc != null) {
                    physicsSpace.remove(rbc);
                    spatial.removeControl(rbc);
                }
            });
            worldNode.removeFromParent();
            worldNode = null;
        }
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
