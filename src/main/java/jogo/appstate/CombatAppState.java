package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import jogo.engine.RenderIndex;
import jogo.gameobject.GameObject;
import jogo.gameobject.character.Enemy;
import jogo.gameobject.character.TankEnemy;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.ItemStack;
import jogo.gameobject.item.SwordItem;

public class CombatAppState extends BaseAppState {

    private final Node rootNode;
    private final Camera cam;
    private final InputAppState input;
    private final RenderIndex renderIndex;

    private float attackCooldown = 0f;
    private final float ATTACK_RATE = 0.5f; // Tempo entre ataques

    public CombatAppState(Node rootNode, Camera cam, InputAppState input, RenderIndex renderIndex) {
        this.rootNode = rootNode;
        this.cam = cam;
        this.input = input;
        this.renderIndex = renderIndex;
    }

    @Override
    protected void initialize(Application app) { }

    @Override
    protected void cleanup(Application app) { }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }

    @Override
    public void update(float tpf) {
        // Gestão do Cooldown
        if (attackCooldown > 0) {  // se attackColldown exitir então tiramos tempo
            attackCooldown -= tpf;
        }

        // Se o rato não estiver capturado, ignora  proteção para os clicks do menu
        if (!input.isMouseCaptured()) return;

        // Verificamos se o cooldown já passou
        if (input.isBreakingHeld() && attackCooldown <= 0) { // se clicar e não houver coldown
            performAttack(); // atacamos
            attackCooldown = ATTACK_RATE; // Reinicia o cooldown passa aos 0,5f
        }
    }

    private void performAttack() {
        // Cria uma linha invisível que começa na câmara (olhos do jogador)
        // e segue na direção para onde ele está a olhar.
        Vector3f origin = cam.getLocation();
        Vector3f dir = cam.getDirection();
        Ray ray = new Ray(origin, dir);
        ray.setLimit(4.5f); // maxima distancia do hit

        // verifica se esse raio "toca" em algum objeto do mundo
        CollisionResults results = new CollisionResults();
        //guarda a lista de todos os objetos atravessados na variável results
        rootNode.collideWith(ray, results);

        if (results.size() > 0) { // verificamos se acertamos em algo
            CollisionResult closest = results.getClosestCollision(); // apenas apanhamos o objeto mais proximo
            Spatial s = closest.getGeometry();
            GameObject obj = findObject(s); // vê qual o objeto proximo

            // Calcular dano
            int damage = 5;  // dano default
            Inventory inv = Inventory.getInventory(); // ver o inventário
            ItemStack hand = inv.getSelectedStack(); // ver o item na mao

            // Verifica o que o jogador tem na mao
            if (hand != null && !hand.isEmpty() && hand.getItem() instanceof SwordItem sword) {
                damage = sword.getDamage(); // se for espada damos o dano na espada
            }

            // Se for o Zombie dá dano a ele
            if (obj instanceof Enemy enemy) {
                int novaVida = enemy.getHealth() - damage;
                enemy.setHealth(novaVida);
                System.out.println("Hit no Zombie! Vida: " + novaVida);
            }
            // Se for TANK dá dano ao gigante
            else if (obj instanceof TankEnemy tank) {
                int novaVida = tank.getHealth() - damage;
                tank.setHealth(novaVida);
                System.out.println("Hit no Gigante! Vida: " + novaVida);
            }
        }
    }

    // Metodo auxiliar para encontrar o GameObject a partir do Spatial
    // Metedo que com o sitio que acertei identifica a que boneco faz parte
    private GameObject findObject(Spatial spatial) {
        Spatial cur = spatial;   // onde acertei

        while (cur != null) {
            GameObject obj = renderIndex.lookup(cur); // verifica se o cur existe no RenderIndex
            // se sim retorna o objeto se nao vai buscar o pai ate dar o objeto
            if (obj != null) return obj;
            cur = cur.getParent();
        }
        return null;
    }
}