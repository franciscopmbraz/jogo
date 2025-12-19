package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import jogo.appstate.HudAppState;
import jogo.gameobject.GameObject;
import jogo.interaction.Interactable;

import java.util.UUID; // <--- IMPORTANTE: Importar isto

public class DroppedItem extends GameObject implements Interactable {

    private final Item item;
    private RigidBodyControl physicsControl;
    private boolean collected = false;

    public DroppedItem(Item item, float x, float y, float z) {
        // CORREÇÃO: Usamos um ID aleatório para garantir que o nome é ÚNICO
        // Ex: "Dropped_Cenoura_a1b2-c3d4..."
        super("Dropped_" + item.getName() + "_" + UUID.randomUUID().toString());

        this.item = item;
        this.setPosition(x, y, z);
    }

    public boolean isCollected() {
        return collected;
    }

    public Item getItem() {
        return item;
    }

    // ... (O resto do método getModel mantém-se igual, não precisas de mudar) ...
    public Spatial getModel(AssetManager assetManager) {
        // --- CÓDIGO VISUAL (Mantém o que já tinhas) ---
        if (item.getName().toLowerCase().contains("cenoura")) {
            Node carrotNode = new Node("CarrotVisual");
            Cylinder bodyShape = new Cylinder(4, 10, 0.01f, 0.07f, 0.4f, true, false);
            Geometry body = new Geometry("Body", bodyShape);
            Material matBody = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            matBody.setBoolean("UseMaterialColors", true);
            matBody.setColor("Diffuse", new ColorRGBA(1.0f, 0.5f, 0.0f, 1f));
            matBody.setColor("Specular", ColorRGBA.White.mult(0.1f));
            matBody.setFloat("Shininess", 4f);
            body.setMaterial(matBody);
            Quaternion rot = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
            body.setLocalRotation(rot);
            Box leavesShape = new Box(0.02f, 0.08f, 0.02f);
            Geometry leaves = new Geometry("Leaves", leavesShape);
            Material matLeaves = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            matLeaves.setBoolean("UseMaterialColors", true);
            matLeaves.setColor("Diffuse", ColorRGBA.Green);
            leaves.setMaterial(matLeaves);
            leaves.setLocalTranslation(0, 0.2f, 0);
            carrotNode.attachChild(body);
            carrotNode.attachChild(leaves);
            carrotNode.setLocalTranslation(0, -0.1f, 0);
            return carrotNode;
        }

        Geometry g = new Geometry(item.getName(), new Box(0.2f, 0.2f, 0.2f));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Yellow);
        mat.setColor("Specular", ColorRGBA.White.mult(0.1f));
        mat.setFloat("Shininess", 8f);
        g.setMaterial(mat);
        return g;
    }

    public void setPhysicsControl(RigidBodyControl control) {
        this.physicsControl = control;
    }

    public RigidBodyControl getPhysicsControl() {
        return physicsControl;
    }

    @Override
    public void onInteract() {
        boolean added = Inventory.getInventory().addItem(item, 1);
        if (added) {
            HudAppState.mostrarMensagem("Apanhaste: " + item.getName());
            this.collected = true;
        } else {
            HudAppState.mostrarMensagem("Inventário cheio!");
        }
    }
}