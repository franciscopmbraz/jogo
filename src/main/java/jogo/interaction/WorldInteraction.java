package jogo.interaction;

import com.jme3.bullet.PhysicsSpace;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.ItemStack;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;

public class WorldInteraction {

    public static void interactWithBlock(VoxelWorld vw, VoxelWorld.Vector3i cell, PhysicsSpace physicsSpace) {

        // Descobrir qual é o bloco alvo
        byte idAtual = vw.getBlock(cell.x, cell.y, cell.z);

        // Obter o inventário e o item que está "na mão" (selecionado)
        Inventory inv = Inventory.getGlobalInventory(); //
        ItemStack stack = inv.getSelectedStack();       //

        // Transformar Relva em Lavoura
        if (idAtual == VoxelPalette.GRASS_ID || idAtual == VoxelPalette.DIRT_ID) {
            vw.setBlock(cell.x, cell.y, cell.z, VoxelPalette.TILLED_ID);

            if (physicsSpace != null) {
                vw.rebuildDirtyChunks(physicsSpace);
            }
        }

        // Regra 2: Plantar Cenoura na Lavoura
        if (idAtual == VoxelPalette.TILLED_ID) {

            // Verifica se existe um stack selecionado e se não está vazio
            if (stack != null && !stack.isEmpty()) {

                // Verifica se o item é a cenoura
                // O CenouraItem define o nome como "cenoura" no construtor
                if (stack.getItem().getName().equals("cenoura")) {

                    // Planta a cenoura
                    vw.setBlock(cell.x, cell.y, cell.z, VoxelPalette.CARROT_ID);

                    // Consome 1 item do stack da mão
                    stack.remove(1);

                    // Atualiza o mundo
                    if (physicsSpace != null) {
                        vw.rebuildDirtyChunks(physicsSpace);
                    }
                }
            }
        }
    }
}