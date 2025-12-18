package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class ZombieEnemy extends Enemy {

    public ZombieEnemy() {
        super("Zombie");
        this.setPosition(172, 47, 175);
        this.setHealth(100);
        this.damage = 10;
        this.speed = 4.0f;
    }

    @Override
    public Spatial getModel(AssetManager assetManager) {
        Node zombieNode = new Node("ZombieVisual");

        // Materiais
        Material mat = createMaterial(assetManager, new ColorRGBA(0.0f, 0.5f, 0.0f, 1.0f));
        Material matClothes = createMaterial(assetManager, new ColorRGBA(0.0f, 0.0f, 0.6f, 1.0f));

        // Cabeça
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(mat);
        head.setLocalTranslation(0, 1.55f, 0);
        zombieNode.attachChild(head);

        // Corpo
        Geometry body = new Geometry("Body", new Box(0.3f, 0.45f, 0.15f));
        body.setMaterial(matClothes);
        body.setLocalTranslation(0, 0.85f, 0);
        zombieNode.attachChild(body);

        // Braços
        Geometry armL = new Geometry("ArmL", new Box(0.12f, 0.45f, 0.12f));
        armL.setMaterial(mat);
        armL.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        armL.setLocalTranslation(-0.42f, 1.15f, 0.45f);
        zombieNode.attachChild(armL);

        Geometry armR = new Geometry("ArmR", new Box(0.12f, 0.45f, 0.12f));
        armR.setMaterial(mat);
        armR.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        armR.setLocalTranslation(0.42f, 1.15f, 0.45f);
        zombieNode.attachChild(armR);

        // Pernas
        Geometry legL = new Geometry("LegL", new Box(0.13f, 0.45f, 0.13f));
        legL.setMaterial(matClothes);
        legL.setLocalTranslation(-0.15f, 0.45f, 0);
        zombieNode.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.13f, 0.45f, 0.13f));
        legR.setMaterial(matClothes);
        legR.setLocalTranslation(0.15f, 0.45f, 0);
        zombieNode.attachChild(legR);

        zombieNode.setLocalTranslation(0, -0.9f, 0);
        return zombieNode;
    }
}