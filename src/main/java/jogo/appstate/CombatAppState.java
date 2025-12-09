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
        // Reduzir o cooldown
        if (attackCooldown > 0) {
            attackCooldown -= tpf;
        }

        // Se o rato não estiver capturado, ignora
        if (!input.isMouseCaptured()) return;

        // Usa o input de "Break" (Botão Esquerdo) para atacar
        // Verificamos se o cooldown já passou
        if (input.isBreakingHeld() && attackCooldown <= 0) {
            performAttack();
            attackCooldown = ATTACK_RATE; // Reinicia o cooldown
        }
    }

    private void performAttack() {
        // 1. Lançar o raio a partir da câmara
        Vector3f origin = cam.getLocation();
        Vector3f dir = cam.getDirection();
        Ray ray = new Ray(origin, dir);
        ray.setLimit(3.5f); // Alcance da espada (3.5 metros)

        // 2. Verificar colisões com objetos da cena
        CollisionResults results = new CollisionResults();
        rootNode.collideWith(ray, results);

        if (results.size() > 0) {
            // Pegar no objeto mais próximo
            CollisionResult closest = results.getClosestCollision();
            Spatial s = closest.getGeometry();

            // Procurar o GameObject associado a este Spatial (usando o RenderIndex)
            GameObject obj = findObject(s);

            // 3. Se for um Inimigo, dar dano
            if (obj instanceof Enemy enemy) {

                // Calcular dano base
                int damage = 1; // Dano do punho (sem arma)

                // Verificar se tem espada na mão
                Inventory inv = Inventory.getInventory();
                ItemStack hand = inv.getSelectedStack();

                if (hand != null && !hand.isEmpty() && hand.getItem() instanceof SwordItem sword) {
                    damage = sword.getDamage();
                }

                // Aplicar dano
                int novaVida = enemy.getHealth() - damage;
                enemy.setHealth(novaVida);

                System.out.println("Ataque! Dano: " + damage + " | Vida Inimigo: " + novaVida);

                // (Opcional) Empurrão visual ou som de hit aqui
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