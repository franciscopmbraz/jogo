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

                if (dropped.isCollected()) {
                    removeItem(dropped);
                    continue;
                }

                if (!items.containsKey(dropped)) {
                    setupPhysics(dropped);
                } else {
                    RigidBodyControl rb = items.get(dropped);
                    Vector3f loc = rb.getPhysicsLocation();
                    dropped.setPosition(loc.x, loc.y, loc.z);
                }
            }
        }

        items.keySet().removeIf(d -> !registry.getAll().contains(d));
    }

    private void setupPhysics(DroppedItem item) {
        Node gameObjectsNode = (Node) rootNode.getChild("GameObjects");
        if (gameObjectsNode != null) {
            Spatial spatial = gameObjectsNode.getChild(item.getName());
            if (spatial != null && spatial.getControl(RigidBodyControl.class) == null) {

                RigidBodyControl rb = new RigidBodyControl(1.0f);
                spatial.addControl(rb);
                physicsSpace.add(rb);

                rb.setPhysicsLocation(new Vector3f(item.getPosition().x, item.getPosition().y, item.getPosition().z));

                items.put(item, rb);
                item.setPhysicsControl(rb);
            }
        }
    }

    private void removeItem(DroppedItem item) {
        if (items.containsKey(item)) {
            RigidBodyControl rb = items.get(item);
            physicsSpace.remove(rb);
            items.remove(item);
        }
        registry.remove(item);
    }
}