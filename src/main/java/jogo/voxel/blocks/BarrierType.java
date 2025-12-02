package jogo.voxel.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.texture.Texture;
import jogo.voxel.UnbreakableType;
import jogo.voxel.VoxelBlockType;
import jogo.voxel.VoxelPalette;

public class BarrierType extends VoxelBlockType implements UnbreakableType{
    public BarrierType(){ super(VoxelPalette.Barrier_ID,"barrier");}

    @Override
    public Material getMaterial(AssetManager assetManager) {
        //Texture2D tex = ProcTextures.checker(128, 4, ColorRGBA.Gray, ColorRGBA.DarkGray);
        Texture tex = assetManager.loadTexture("Textures/Barreira.png");
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", tex);

        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        m.setFloat("AlphaDiscardThreshold",0.1f);
        return m;
    }
}

