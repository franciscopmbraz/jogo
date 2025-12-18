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
import jogo.gameobject.character.ZombieEnemy;
import jogo.voxel.VoxelPalette;

public class EnemyAppState extends BaseAppState {

    private final GameRegistry registry;
    private final PhysicsSpace physicsSpace;
    private final PlayerAppState playerAppState;
    private final Node rootNode; // encontrar o Spatial do inimigo
    private final WorldAppState world;

    private ZombieEnemy zombieEnemy;
    private BetterCharacterControl enemyControl;
    private float attackTimer = 0f;

    public EnemyAppState(Node rootNode, GameRegistry registry, PhysicsSpace physicsSpace, PlayerAppState playerAppState,WorldAppState world) {
        this.rootNode = rootNode;
        this.registry = registry;
        this.physicsSpace = physicsSpace;
        this.playerAppState = playerAppState;
        this.world = world;
    }

    @Override
    protected void initialize(Application app) {
        zombieEnemy = new ZombieEnemy();
        registry.add(zombieEnemy); // Isto fará o RenderAppState desenhá-lo

    }

    @Override
    public void update(float tpf) {

        // VERIFICAÇÃO DE MORTE
        if (zombieEnemy.getHealth() <= 0) {
            //
            if (enemyControl != null) {
                System.out.println("O inimigo morreu!");

                // Remove a física
                physicsSpace.remove(enemyControl);
                enemyControl = null;

                // Remove do registo
                registry.remove(zombieEnemy);
                HudAppState.registarMorteMonstro();
            }
            return;
        }

        // se controlo físico for null criamos fisicas
        if (enemyControl == null) {
            setupEnemyPhysics();
            return;
        }

        Player player = playerAppState.getPlayer();
        if (player == null) return; // Se o jogador não existir, pára tudo

        // Converter a posição lógica do jogador para Vector3f (física)
        Vector3f playerPos = new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z);

        // Obter a posição física atual do inimigo
        Vector3f enemyPos = enemyControl.getRigidBody().getPhysicsLocation();

        //calcula a distância entre o player e o enemigo
        Vector3f direction = playerPos.subtract(enemyPos);
        // tranforma em float
        float distance = direction.length();
        if (distance > 1.5f) { // se a diastancia for maior que 1,5
            direction.y = 0; // Ignora a altura para não voar para cima do jogador
            // speed do npc
            Vector3f walkDir = direction.normalize().mult(zombieEnemy.getSpeed());
            // Aplica o movimento ao corpo físico
            enemyControl.setWalkDirection(walkDir);
            // Faz o inimigo olhar para onde está a andar
            enemyControl.setViewDirection(walkDir);

            // LÓGICA DE SALTO
            if (enemyControl.isOnGround()) {
                //Verificar se o jogador está num sitio mais alto
                boolean playerIsHigher = playerPos.y > (enemyPos.y + 0.8f);

                // Verificar se há um bloco à frente
                // Calcular a posição 1 metro à frente do inimigo
                Vector3f checkPos = enemyPos.add(walkDir.normalize().mult(1.2f));

                // Vamos buscar o ID do bloco nessas coordenadas
                int bx = (int) Math.floor(checkPos.x);
                int by = (int) Math.floor(enemyPos.y + 0.1f); // Um pouco acima dos pés
                int bz = (int) Math.floor(checkPos.z);

                byte blockId = world.getVoxelWorld().getBlock(bx, by, bz); // ve o id do bloco
                boolean wallAhead = blockId != VoxelPalette.AIR_ID ; // ignora o ar

                // Se houver parede ou o jogador estiver alto ele salta
                if (wallAhead || (playerIsHigher && distance < 5.0f)) {
                    enemyControl.jump();
                }
            }

        } else {
            enemyControl.setWalkDirection(Vector3f.ZERO);
        }

        // SINCRONIZAR POSIÇÃO LÓGICA
        zombieEnemy.setPosition(enemyPos.x, enemyPos.y, enemyPos.z);

        // ATAQUE
        if (distance < 2.0f) { // se a distancia for menor que 2f ele aumenta o timer
            attackTimer += tpf;
            if (attackTimer >= 1.5f) { // quando chega a 1.5 de timer
                //ver a vida do player
                int currentHealth = player.getHealth();
                // da dano ao player
                player.setHealth(currentHealth - zombieEnemy.getDamage());
                // Debug com print
                System.out.println("Levaste dano! Vida: " + player.getHealth());
                // reseta o timer
                attackTimer = 0f;
            }
        } else {// caso a distancia a meio aumentar o timer reseta
            // o player pode fugir
            attackTimer = 0f;
        }
    }


    private void setupEnemyPhysics() {
        // Procura o Spatial que o RenderAppState para este inimigo
        Node gameObjectsNode = (Node) rootNode.getChild("GameObjects");
        if (gameObjectsNode != null) {
            // procura o modelo 3D que tem o mesmo nome que o nosso inimigo
            Spatial enemySpatial = gameObjectsNode.getChild(zombieEnemy.getName());

            // Verifica se encontrámos o modelo e se ele ainda não tem física
            if (enemySpatial != null && enemySpatial.getControl(BetterCharacterControl.class) == null) {
                // Adiciona física ao inimigo
                enemyControl = new BetterCharacterControl(0.4f, 1.8f, 60f); // Raio, Altura, Massa
                enemyControl.setGravity(new Vector3f(0, -20f, 0));
                enemyControl.warp(new Vector3f(zombieEnemy.getPosition().x, zombieEnemy.getPosition().y, zombieEnemy.getPosition().z));

                // Se a física andar, o desenho anda atrás
                enemySpatial.addControl(enemyControl);
                // inimigo começa a cair, colidir com paredes e ser bloqueado pelo chão
                physicsSpace.add(enemyControl);
                System.out.println("Física do Inimigo ativada!");
            }
        }
    }
    public void warpToPosition() { // USADO QUANDO DAMOS LOAD AO JOGO
        if (zombieEnemy != null && enemyControl != null) {
            enemyControl.warp(new Vector3f(zombieEnemy.getPosition().x, zombieEnemy.getPosition().y, zombieEnemy.getPosition().z));
        }
    }

    @Override
    protected void cleanup(Application app) {
        if (enemyControl != null) {
            physicsSpace.remove(enemyControl);
        }
        registry.remove(zombieEnemy);
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}