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

public class EnemyAppState extends BaseAppState {

    private final GameRegistry registry;
    private final PhysicsSpace physicsSpace;
    private final PlayerAppState playerAppState;
    private final Node rootNode; // encontrar o Spatial do inimigo

    private Enemy enemy;
    private BetterCharacterControl enemyControl;
    private float attackTimer = 0f;
    private float printTimer = 0f;

    public EnemyAppState(Node rootNode, GameRegistry registry, PhysicsSpace physicsSpace, PlayerAppState playerAppState) {
        this.rootNode = rootNode;
        this.registry = registry;
        this.physicsSpace = physicsSpace;
        this.playerAppState = playerAppState;
    }

    @Override
    protected void initialize(Application app) {
        enemy = new Enemy();
        registry.add(enemy); // Isto fará o RenderAppState desenhá-lo

    }

    @Override
    public void update(float tpf) {
        // Se o controlo físico
        if (enemyControl == null) {
            setupEnemyPhysics();
            return;
        }

        Player player = playerAppState.getPlayer();
        if (player == null) return;


        // --- LÓGICA DE PERSEGUIÇÃO ---
        Vector3f playerPos = new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z);
        Vector3f enemyPos = enemyControl.getRigidBody().getPhysicsLocation();
        printTimer += tpf;
        if (printTimer >= 1.0f) {
            System.out.printf("Eu: %.1f, %.1f, %.1f | Inimigo: %.1f, %.1f, %.1f%n",
                    playerPos.x, playerPos.y, playerPos.z,
                    enemyPos.x, enemyPos.y, enemyPos.z);
            printTimer = 0f;
        }
        // ---------

        Vector3f direction = playerPos.subtract(enemyPos);
        float distance = direction.length();

        // Só anda se estiver longe
        if (distance > 1.5f) {
            direction.y = 0; // Não voar
            direction.normalizeLocal().multLocal(enemy.getSpeed());
            enemyControl.setWalkDirection(direction);
            enemyControl.setViewDirection(direction);
        } else {
            // Parar se estiver perto
            enemyControl.setWalkDirection(Vector3f.ZERO);
        }


        // Sincroniza a posição física de volta para o objeto do jogo
        enemy.setPosition(enemyPos.x, enemyPos.y, enemyPos.z);

        // --- LÓGICA DE ATAQUE ---
        if (distance < 2.0f) {
            attackTimer += tpf;
            if (attackTimer >= 1.5f) { // Ataca a cada 1.5 segundos
                int currentHealth = player.getHealth();
                player.setHealth(currentHealth - enemy.getDamage());
                System.out.println("Ai! Levaste dano! Vida: " + player.getHealth());
                attackTimer = 0f; // Reset timer
            }
        } else {
            attackTimer = 0f; // Se fugir tempo reseta
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