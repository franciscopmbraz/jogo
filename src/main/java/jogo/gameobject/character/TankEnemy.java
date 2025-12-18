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

public class TankEnemy extends Enemy {

    public TankEnemy() {
        super("Tank");
        this.setPosition(170, 47, 170);
        this.setHealth(200);
        this.damage = 25;
        this.speed = 2f;
    }

    @Override
    public Spatial getModel(AssetManager assetManager) {
        Node node = new Node("GiantVisual");

        Material matSkin = createMaterial(assetManager, new ColorRGBA(0.96f, 0.76f, 0.62f, 1f));
        Material matCoat = createMaterial(assetManager, new ColorRGBA(0.6f, 0.4f, 0.2f, 1f));
        Material matPants = createMaterial(assetManager, new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        Material matBeard = createMaterial(assetManager, new ColorRGBA(0.3f, 0.15f, 0.05f, 1f));

        float scale = 1.8f;

        Geometry head = new Geometry("Head", new Box(0.25f * scale, 0.28f * scale, 0.25f * scale));
        head.setMaterial(matSkin);
        head.setLocalTranslation(0, 1.65f * scale, 0);
        node.attachChild(head);

        Geometry beard = new Geometry("Beard", new Box(0.26f * scale, 0.1f * scale, 0.15f * scale));
        beard.setMaterial(matBeard);
        beard.setLocalTranslation(0, 1.55f * scale, -0.1f * scale);
        node.attachChild(beard);

        Geometry body = new Geometry("Body", new Box(0.5f * scale, 0.55f * scale, 0.35f * scale));
        body.setMaterial(matCoat);
        body.setLocalTranslation(0, 0.85f * scale, 0);
        node.attachChild(body);

        // Braços
        Geometry shoulderL = new Geometry("ShoulderL", new Box(0.2f * scale, 0.2f * scale, 0.2f * scale));
        shoulderL.setMaterial(matCoat);
        shoulderL.setLocalTranslation(-0.6f * scale, 1.2f * scale, 0);
        node.attachChild(shoulderL);

        Geometry armL = new Geometry("ArmL", new Box(0.18f * scale, 0.45f * scale, 0.18f * scale));
        armL.setMaterial(matSkin);
        armL.setLocalTranslation(-0.6f * scale, 0.6f * scale, 0.2f * scale);
        armL.setLocalRotation(new Quaternion().fromAngleAxis(-0.3f, Vector3f.UNIT_X));
        node.attachChild(armL);

        Geometry shoulderR = new Geometry("ShoulderR", new Box(0.2f * scale, 0.2f * scale, 0.2f * scale));
        shoulderR.setMaterial(matCoat);
        shoulderR.setLocalTranslation(0.6f * scale, 1.2f * scale, 0);
        node.attachChild(shoulderR);

        Geometry armR = new Geometry("ArmR", new Box(0.18f * scale, 0.45f * scale, 0.18f * scale));
        armR.setMaterial(matSkin);
        armR.setLocalTranslation(0.6f * scale, 0.6f * scale, 0.2f * scale);
        armR.setLocalRotation(new Quaternion().fromAngleAxis(-0.3f, Vector3f.UNIT_X));
        node.attachChild(armR);

        // Pernas
        Geometry legL = new Geometry("LegL", new Box(0.22f * scale, 0.35f * scale, 0.22f * scale));
        legL.setMaterial(matPants);
        legL.setLocalTranslation(-0.25f * scale, 0.35f * scale, 0);
        node.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.22f * scale, 0.35f * scale, 0.22f * scale));
        legR.setMaterial(matPants);
        legR.setLocalTranslation(0.25f * scale, 0.35f * scale, 0);
        node.attachChild(legR);

        node.setLocalTranslation(0, -0.9f, 0);
        return node;
    }
}