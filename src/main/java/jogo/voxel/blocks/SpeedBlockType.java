package jogo.voxel.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import jogo.voxel.UnbreakableType;
import jogo.voxel.VoxelBlockType;
import jogo.voxel.VoxelPalette;

public class SpeedBlockType extends VoxelBlockType implements UnbreakableType {
    public SpeedBlockType() {super(VoxelPalette.SPEED_ID,"speed");
    }
    // isSolid() inherits true from base

    @Override
    public Material getMaterial(AssetManager assetManager) {
        //Texture2D tex = ProcTextures.checker(128, 4, ColorRGBA.Gray, ColorRGBA.DarkGray);
        Texture tex = assetManager.loadTexture("Textures/");
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setTexture("DiffuseMap", tex);
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", ColorRGBA.White);
        m.setColor("Specular", ColorRGBA.White.mult(0.02f)); // reduced specular
        m.setFloat("Shininess", 32f); // tighter, less intense highlight
        return m;
    }

}
