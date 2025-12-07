package jogo.interaction;

import com.jme3.bullet.PhysicsSpace;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;

public class WorldInteraction {


    public static void interactWithBlock(VoxelWorld vw, VoxelWorld.Vector3i cell, PhysicsSpace physicsSpace) {

        // Descobrir qual é o bloco
        byte idAtual = vw.getBlock(cell.x, cell.y, cell.z);

        // Regra 1: Transformar Relva em Speed
        if (idAtual == VoxelPalette.GRASS_ID || idAtual == VoxelPalette.DIRT_ID) {
            vw.setBlock(cell.x, cell.y, cell.z, VoxelPalette.TILLED_ID);

            // Só reconstruímos se houver mudanças físicas/visuais
            if (physicsSpace != null) {
                vw.rebuildDirtyChunks(physicsSpace);
            }
        }
        if (idAtual == VoxelPalette.TILLED_ID) {
            vw.setBlock(cell.x, cell.y, cell.z, VoxelPalette.CARROT_ID);
            // Só reconstruímos se houver mudanças físicas/visuais
            if (physicsSpace != null) {
                vw.rebuildDirtyChunks(physicsSpace);
            }
        }
    }
}
