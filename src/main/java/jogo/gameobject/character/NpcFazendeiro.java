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
import jogo.gameobject.item.CenouraItem;
import jogo.gameobject.item.Inventory;
import jogo.interaction.Interactable;

public class NpcFazendeiro extends Ally implements Interactable {

    private boolean deuSemente = false;

    public NpcFazendeiro() {
        super("Fazendeiro");
        this.setPosition(138, 35, 136); // posição inicial
    }




    @Override
    public void onInteract() {
        if (!deuSemente) {
            HudAppState.mostrarMensagem("Toma esta cenoura! Planta-a na terra lavrada.|\n Depois colhe (20 cenouras) e leva ao meu amigo aqui ao lado.\n Nao te esqueças de matar os inimigos!!");
            HudAppState.iniciarTimer();
            Inventory.addInventory(new CenouraItem(), 1);


            deuSemente = true;
        } else {
            HudAppState.mostrarMensagem("Já plantaste? O meu amigo está à espera da comida.");
        }
    }

    @Override
    public Spatial getModel(AssetManager assetManager) {
        Node node = new Node("FarmerVisual");

        // Materiais
        Material matSkin = createMaterial(assetManager, new ColorRGBA(0.96f, 0.76f, 0.62f, 1f));
        Material matShirt = createMaterial(assetManager, ColorRGBA.Yellow);
        Material matPants = createMaterial(assetManager, new ColorRGBA(0.4f, 0.25f, 0.1f, 1f));
        Material matHat = createMaterial(assetManager, new ColorRGBA(0.8f, 0.6f, 0.2f, 1f));

        // Cabeça
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(matSkin);
        head.setLocalTranslation(0, 1.55f, 0);
        node.attachChild(head);

        // Chapéu
        Geometry hatBrim = new Geometry("HatBrim", new Box(0.35f, 0.02f, 0.35f));
        hatBrim.setMaterial(matHat);
        hatBrim.setLocalTranslation(0, 1.78f, 0);
        node.attachChild(hatBrim);

        Geometry hatTop = new Geometry("HatTop", new Box(0.20f, 0.12f, 0.20f));
        hatTop.setMaterial(matHat);
        hatTop.setLocalTranslation(0, 1.9f, 0);
        node.attachChild(hatTop);

        // Corpo
        Geometry body = new Geometry("Body", new Box(0.3f, 0.45f, 0.15f));
        body.setMaterial(matShirt);
        body.setLocalTranslation(0, 0.85f, 0);
        node.attachChild(body);

        // Braços
        Geometry armL = new Geometry("ArmL", new Box(0.12f, 0.45f, 0.12f));
        armL.setMaterial(matShirt);
        armL.setLocalTranslation(-0.42f, 0.85f, 0);
        node.attachChild(armL);

        Geometry armR = new Geometry("ArmR", new Box(0.12f, 0.45f, 0.12f));
        armR.setMaterial(matShirt);
        armR.setLocalTranslation(0.42f, 0.85f, 0);
        node.attachChild(armR);

        // Pernas
        Geometry legL = new Geometry("LegL", new Box(0.13f, 0.45f, 0.13f));
        legL.setMaterial(matPants);
        legL.setLocalTranslation(-0.15f, 0.45f, 0);
        node.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.13f, 0.45f, 0.13f));
        legR.setMaterial(matPants);
        legR.setLocalTranslation(0.15f, 0.45f, 0);
        node.attachChild(legR);

        node.setLocalTranslation(0, -0.9f, 0);
        return node;
    }
}