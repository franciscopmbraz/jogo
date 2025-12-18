package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import jogo.interaction.Interactable;

public abstract class Ally extends Character implements Interactable {

    public Ally(String name) {
        super(name);
    }

    // Obriga todos os NPCs a terem um visual próprio
    public abstract Spatial getModel(AssetManager assetManager);

    // Helper para criar materiais (evita duplicação)
    protected Material createMaterial(AssetManager assetManager, ColorRGBA color) {
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", color.clone());
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 8f);
        return m;
    }
}