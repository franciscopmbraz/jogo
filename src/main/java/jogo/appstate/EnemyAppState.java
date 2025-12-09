package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import jogo.engine.GameRegistry;
import jogo.gameobject.character.Enemy;
import jogo.gameobject.character.Player;
import jogo.voxel.VoxelPalette;

public class EnemyAppState extends BaseAppState {

    private final GameRegistry registry;
    private final PhysicsSpace physicsSpace;
    private final PlayerAppState playerAppState;
    private final Node rootNode; // encontrar o Spatial do inimigo
    private final WorldAppState world;

    private Enemy enemy;
    private BetterCharacterControl enemyControl;
    private float attackTimer = 0f;
    private float printTimer = 0f;

    public EnemyAppState(Node rootNode, GameRegistry registry, PhysicsSpace physicsSpace, PlayerAppState playerAppState,WorldAppState world) {
        this.rootNode = rootNode;
        this.registry = registry;
        this.physicsSpace = physicsSpace;
        this.playerAppState = playerAppState;
        this.world = world;
    }

    @Override
    protected void initialize(Application app) {
        enemy = new Enemy();
        registry.add(enemy); // Isto fará o RenderAppState desenhá-lo

    }

    @Override
    public void update(float tpf) {

        // 1. VERIFICAÇÃO DE MORTE
        if (enemy.getHealth() <= 0) {
            // Se ainda tem corpo físico, vamos limpar tudo
            if (enemyControl != null) {
                System.out.println("O inimigo morreu!");

                // Remove a física
                physicsSpace.remove(enemyControl);
                enemyControl = null;

                // Remove do registo (o RenderAppState vai apagar o visual automaticamente)
                registry.remove(enemy);
            }
            return;
        }

        // Se o controlo físico
        if (enemyControl == null) {
            setupEnemyPhysics();
            return;
        }

        Player player = playerAppState.getPlayer();
        if (player == null) return;

        Vector3f playerPos = new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        Vector3f enemyPos = enemyControl.getRigidBody().getPhysicsLocation();

        Vector3f direction = playerPos.subtract(enemyPos);
        float distance = direction.length();// --- MOVIMENTO ---
        if (distance > 1.5f) {
            direction.y = 0;
            Vector3f walkDir = direction.normalize().mult(enemy.getSpeed());
            enemyControl.setWalkDirection(walkDir);
            enemyControl.setViewDirection(walkDir);

            // --- LÓGICA DE SALTO (NOVO) ---
            if (enemyControl.isOnGround()) {
                // 1. Verificar se o jogador está num sitio mais alto
                boolean playerIsHigher = playerPos.y > (enemyPos.y + 0.8f);

                // 2. Verificar se há um bloco à frente (Obtáculo)
                // Calculamos a posição 1 metro à frente do inimigo
                Vector3f checkPos = enemyPos.add(walkDir.normalize().mult(1.2f));

                // Vamos buscar o ID do bloco nessas coordenadas (usando Math.floor para ser preciso)
                int bx = (int) Math.floor(checkPos.x);
                int by = (int) Math.floor(enemyPos.y + 0.1f); // Um pouco acima dos pés
                int bz = (int) Math.floor(checkPos.z);

                byte blockId = world.getVoxelWorld().getBlock(bx, by, bz);
                boolean wallAhead = blockId != VoxelPalette.AIR_ID && blockId != VoxelPalette.GRASS_ID; // Ignora relva/flores se forem não-sólidos

                // Se houver parede ou o jogador estiver alto -> SALTA
                if (wallAhead || (playerIsHigher && distance < 5.0f)) {
                    enemyControl.jump();
                }
            }

        } else {
            enemyControl.setWalkDirection(Vector3f.ZERO);
        }

        // --- SINCRONIZAR POSIÇÃO LÓGICA ---
        enemy.setPosition(enemyPos.x, enemyPos.y, enemyPos.z);

        // --- PRINT DE DEBUG ---
        printTimer += tpf;
        if (printTimer >= 1.0f) {
            System.out.printf("Eu: %.1f, %.1f, %.1f | Inimigo: %.1f, %.1f, %.1f%n",
                    playerPos.x, playerPos.y, playerPos.z,
                    enemyPos.x, enemyPos.y, enemyPos.z);
            printTimer = 0f;
        }

        // --- ATAQUE ---
        if (distance < 2.0f) {
            attackTimer += tpf;
            if (attackTimer >= 1.5f) {
                int currentHealth = player.getHealth();
                player.setHealth(currentHealth - enemy.getDamage());
                System.out.println("Ai! Levaste dano! Vida: " + player.getHealth());
                attackTimer = 0f;
            }
        } else {
            attackTimer = 0f;
        }
    }


    private void setupEnemyPhysics() {
        // Procura o Spatial que o RenderAppState para este inimigo
        Node gameObjectsNode = (Node) rootNode.getChild("GameObjects");
        if (gameObjectsNode != null) {
            Spatial enemySpatial = gameObjectsNode.getChild(enemy.getName());
            if (enemySpatial != null && enemySpatial.getControl(BetterCharacterControl.class) == null) {
                // Adiciona física ao inimigo
                enemyControl = new BetterCharacterControl(0.4f, 1.8f, 60f); // Raio, Altura, Massa
                enemyControl.setGravity(new Vector3f(0, -20f, 0));
                enemyControl.warp(new Vector3f(enemy.getPosition().x, enemy.getPosition().y, enemy.getPosition().z));

                enemySpatial.addControl(enemyControl);
                physicsSpace.add(enemyControl);
                System.out.println("Física do Inimigo ativada!");
            }
        }
    }

    @Override
    protected void cleanup(Application app) {
        if (enemyControl != null) {
            physicsSpace.remove(enemyControl);
        }
        registry.remove(enemy);
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}