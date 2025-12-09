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
    private final float ATTACK_RATE = 0.5f; // Tempo entre ataques (segundos)

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
        if (attackCooldown > 0) {
            attackCooldown -= tpf;
        }

        // Se o rato não estiver capturado, ignora
        if (!input.isMouseCaptured()) return;

        // Verificamos se o cooldown já passou
        if (input.isBreakingHeld() && attackCooldown <= 0) {
            performAttack();
            attackCooldown = ATTACK_RATE; // Reinicia o cooldown
        }
    }

    private void performAttack() {
        Vector3f origin = cam.getLocation();
        Vector3f dir = cam.getDirection();
        Ray ray = new Ray(origin, dir);
        ray.setLimit(4.5f);

        CollisionResults results = new CollisionResults();
        rootNode.collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            Spatial s = closest.getGeometry();
            GameObject obj = findObject(s);

            // Calcular dano
            int damage = 1;
            Inventory inv = Inventory.getInventory();
            ItemStack hand = inv.getSelectedStack();

            if (hand != null && !hand.isEmpty() && hand.getItem() instanceof SwordItem sword) {
                damage = sword.getDamage();
            }

            // 1. Se for INIMIGO NORMAL
            if (obj instanceof Enemy enemy) {
                int novaVida = enemy.getHealth() - damage;
                enemy.setHealth(novaVida);
                System.out.println("Hit no Zombie! Vida: " + novaVida);
            }
            // 2. Se for TANK (GIGANTE)
            else if (obj instanceof TankEnemy tank) {
                int novaVida = tank.getHealth() - damage;
                tank.setHealth(novaVida);
                System.out.println("Hit no Gigante! Vida: " + novaVida);
            }
        }
    }

    // Metodo auxiliar para encontrar o GameObject a partir do Spatial (sobe na hierarquia se necessário)
    private GameObject findObject(Spatial spatial) {
        Spatial cur = spatial;
        while (cur != null) {
            GameObject obj = renderIndex.lookup(cur);
            if (obj != null) return obj;
            cur = cur.getParent();
        }
        return null;
    }
}