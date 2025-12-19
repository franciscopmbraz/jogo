package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import jogo.engine.GameRegistry;
import jogo.gameobject.GameObject;
import jogo.gameobject.item.DroppedItem;

import java.util.HashMap;
import java.util.Map;

public class DropManagerAppState extends BaseAppState {

    private final Node rootNode;
    private final GameRegistry registry;
    private final PhysicsSpace physicsSpace;
    private final Map<DroppedItem, RigidBodyControl> items = new HashMap<>();

    public DropManagerAppState(Node rootNode, GameRegistry registry, PhysicsSpace physicsSpace) {
        this.rootNode = rootNode;
        this.registry = registry;
        this.physicsSpace = physicsSpace;
    }

    @Override
    protected void initialize(Application app) { }

    @Override
    protected void cleanup(Application app) {
        // Limpar tudo ao sair
        for (DroppedItem item : items.keySet()) {
            Spatial s = findSpatial(item);
            if (s != null) s.removeFromParent();
        }
        for (RigidBodyControl rb : items.values()) {
            physicsSpace.remove(rb);
        }
        items.clear();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }

    @Override
    public void update(float tpf) {
        for (GameObject obj : registry.getAll()) {
            if (obj instanceof DroppedItem) {
                DroppedItem dropped = (DroppedItem) obj;

                // Se já foi apanhado, remove tudo
                if (dropped.isCollected()) {
                    removeItem(dropped);
                    continue;
                }

                // Se ainda não tem física, tenta adicionar
                if (!items.containsKey(dropped)) {
                    setupPhysics(dropped);
                } else {
                    // Sincroniza a posição da Física -> Lógica do Jogo
                    RigidBodyControl rb = items.get(dropped);
                    Vector3f loc = rb.getPhysicsLocation();
                    dropped.setPosition(loc.x, loc.y, loc.z);
                }
            }
        }

        // Remove da lista local se já não existir no Registry
        items.keySet().removeIf(d -> !registry.getAll().contains(d));
    }

    // --- CORREÇÃO 1: Método auxiliar para encontrar o visual onde quer que esteja ---
    private Spatial findSpatial(DroppedItem item) {
        // Tenta encontrar dentro de "GameObjects" (caso uses essa organização)
        Node gameObjectsNode = (Node) rootNode.getChild("GameObjects");
        if (gameObjectsNode != null) {
            Spatial s = gameObjectsNode.getChild(item.getName());
            if (s != null) return s;
        }

        // Se não encontrar, procura diretamente na raiz (rootNode)
        return rootNode.getChild(item.getName());
    }

    private void setupPhysics(DroppedItem item) {
        // Usamos o método auxiliar para garantir que encontramos o objeto
        Spatial spatial = findSpatial(item);

        // Só adiciona física se encontrou o visual e ainda não tem controlo
        if (spatial != null && spatial.getControl(RigidBodyControl.class) == null) {

            RigidBodyControl rb = new RigidBodyControl(1.0f); // Massa 1.0 = tem gravidade
            spatial.addControl(rb);
            physicsSpace.add(rb);

            // Coloca o corpo físico na posição inicial do item
            rb.setPhysicsLocation(new Vector3f(item.getPosition().x, item.getPosition().y, item.getPosition().z));

            items.put(item, rb);
            item.setPhysicsControl(rb);
        }
    }

    private void removeItem(DroppedItem item) {
        // 1. Remover Física
        if (items.containsKey(item)) {
            RigidBodyControl rb = items.get(item);
            physicsSpace.remove(rb);
            items.remove(item);
        }

        // --- CORREÇÃO 2: Remover o Visual da Cena ---
        Spatial spatial = findSpatial(item);
        if (spatial != null) {
            spatial.removeFromParent(); // << ISTO FAZ O ITEM DESAPARECER VISUALMENTE
        }

        // 3. Remover da Lógica (Registry)
        registry.remove(item);
    }
}