package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import jogo.engine.GameRegistry;
import jogo.gameobject.character.Player;
import jogo.gameobject.character.TankEnemy;

public class TankAppState extends BaseAppState {

    private final GameRegistry registry;
    private final PhysicsSpace physicsSpace;
    private final PlayerAppState playerAppState;
    private final Node rootNode;

    private TankEnemy tank;
    private BetterCharacterControl tankControl;
    private float attackTimer = 0f;

    public TankAppState(Node rootNode, GameRegistry registry, PhysicsSpace physicsSpace, PlayerAppState playerAppState, WorldAppState world) {
        this.rootNode = rootNode;
        this.registry = registry;
        this.physicsSpace = physicsSpace;
        this.playerAppState = playerAppState;
    }

    @Override
    protected void initialize(Application app) {
        tank = new TankEnemy();
        registry.add(tank);
    }

    @Override
    public void update(float tpf) {
        // Verificar Morte
        if (tank.getHealth() <= 0) {
            if (tankControl != null) {
                System.out.println("O TANK morreu!");
                // Remove a física
                physicsSpace.remove(tankControl);
                tankControl = null;

                // Remove do registo
                registry.remove(tank);
                jogo.appstate.HudAppState.registarMorteMonstro();
            }
            return;
        }

        // se controlo físico for null criamos fisicas
        if (tankControl == null) {
            setupPhysics();
            return;
        }

        // Movimento de seguir o player
        Player player = playerAppState.getPlayer();
        if (player == null) return;
        // posiçao do player
        Vector3f playerPos = new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        // posiçao do tank
        Vector3f tankPos = tankControl.getRigidBody().getPhysicsLocation();

        //calcula a distância entre o player e o enemigo
        Vector3f direction = playerPos.subtract(tankPos);
        // tranforma em float
        float distance = direction.length();

       //MOVIMENTO
        if (distance > 2.0f) {
            direction.y = 0;
            // speed npc
            Vector3f walkDir = direction.normalize().mult(tank.getSpeed());
            // Aplica o movimento ao corpo físico
            tankControl.setWalkDirection(walkDir);
            // faz o player olhar para onde esta a olhar
            tankControl.setViewDirection(walkDir);

        } else {
            // Parar se estiver perto
            tankControl.setWalkDirection(Vector3f.ZERO);
        }

        tank.setPosition(tankPos.x, tankPos.y, tankPos.z);

        // ATAQUE
            if (distance < 2.5f) {
            attackTimer += tpf;
            if (attackTimer >= 2.0f) {
                int currentHealth = player.getHealth();
                player.setHealth(currentHealth - tank.getDamage());
                System.out.println("Esmagado pelo Tank! Vida: " + player.getHealth());
                attackTimer = 0f;
            }
        } else {
            attackTimer = 0f;
        }
    }   // GIGANTE NAO SALTA POR SER PESADO

    private void setupPhysics() {
        // Procura o Spatial que o RenderAppState para este inimigo
        Node gameObjectsNode = (Node) rootNode.getChild("GameObjects");
        if (gameObjectsNode != null) {
            // procura o modelo 3D que tem o mesmo nome que o nosso inimigo
            Spatial tankSpatial = gameObjectsNode.getChild(tank.getName());
            // Verifica se encontrámos o modelo e se ele ainda não tem física
            if (tankSpatial != null && tankSpatial.getControl(BetterCharacterControl.class) == null) {
                    // Configuração Gigante
                    tankControl = new BetterCharacterControl(0.7f, 2.5f, 200f);
                    tankControl.setGravity(new Vector3f(0, -20f, 0));

                    tankControl.warp(new Vector3f(tank.getPosition().x, tank.getPosition().y, tank.getPosition().z));
                    // se a fisica andar o 3d tmb anda
                    tankSpatial.addControl(tankControl);
                    physicsSpace.add(tankControl);
            }
        }
    }


    public void warpToPosition() { // USADO QUANDO DAMOS LOAD AO JOGO
        if (tank != null && tankControl != null) {
            tankControl.warp(new Vector3f(tank.getPosition().x, tank.getPosition().y, tank.getPosition().z));
        }
    }


    @Override
    protected void cleanup(Application app) {
        if (tankControl != null) physicsSpace.remove(tankControl);
        registry.remove(tank);
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}