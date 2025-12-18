package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import jogo.appstate.HudAppState;
import jogo.gameobject.item.Inventory;
import jogo.interaction.Interactable;

public class NpcEater extends Ally implements Interactable {

    public NpcEater() {
        super("Eater");
        this.setPosition(143, 35, 136 ); // posição de iniciar
    }

    @Override
    public void onInteract() {
        // interações
        Inventory inv = Inventory.getInventory();

        if (jogo.appstate.HudAppState.getMonstrosMortos() < 2) {
            // se nao tiver morto os dois monstros
            jogo.appstate.HudAppState.mostrarMensagem("Tens de matar os 2 monstros\nantes de entregar as cenouras!");
            return;
        }
        // Verifica se tens cenouras
        if (inv.hasItem("cenoura", 20)) {
            // remove cenouras
            inv.removeItem("cenoura", 20);

            HudAppState.finalizarMissao();


        } else {
            HudAppState.mostrarMensagem("O Fazendeiro prometeu-me 20 cenouras... Ainda nada?");
        }
    }

    @Override
    public Spatial getModel(AssetManager assetManager) {
        Node node = new Node("EaterVisual");

        // Materiais
        Material matSkin = createMaterial(assetManager, new ColorRGBA(0.96f, 0.76f, 0.62f, 1f));
        Material matShirt = createMaterial(assetManager, ColorRGBA.Magenta);
        Material matPants = createMaterial(assetManager, ColorRGBA.Blue);

        // Cabeça
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(matSkin);
        head.setLocalTranslation(0, 1.55f, 0);
        node.attachChild(head);

        // Corpo
        Geometry body = new Geometry("Body", new Box(0.35f, 0.45f, 0.2f));
        body.setMaterial(matShirt);
        body.setLocalTranslation(0, 0.85f, 0);
        node.attachChild(body);

        // Braços
        Geometry armL = new Geometry("ArmL", new Box(0.12f, 0.45f, 0.12f));
        armL.setMaterial(matShirt);
        armL.setLocalRotation(new Quaternion().fromAngleAxis(-0.5f, Vector3f.UNIT_X));
        armL.setLocalTranslation(-0.48f, 0.95f, 0.2f);
        node.attachChild(armL);

        Geometry armR = new Geometry("ArmR", new Box(0.12f, 0.45f, 0.12f));
        armR.setMaterial(matShirt);
        armR.setLocalRotation(new Quaternion().fromAngleAxis(-0.5f, Vector3f.UNIT_X));
        armR.setLocalTranslation(0.48f, 0.95f, 0.2f);
        node.attachChild(armR);

        // Pernas
        Geometry legL = new Geometry("LegL", new Box(0.14f, 0.45f, 0.14f));
        legL.setMaterial(matPants);
        legL.setLocalTranslation(-0.16f, 0.45f, 0);
        node.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.14f, 0.45f, 0.14f));
        legR.setMaterial(matPants);
        legR.setLocalTranslation(0.16f, 0.45f, 0);
        node.attachChild(legR);

        node.setLocalTranslation(0, -0.9f, 0);
        return node;
    }
}