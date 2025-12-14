package jogo.gameobject.item;

import jogo.voxel.VoxelPalette;

public class ItemFactory {

    public static Item createItem(String name) {
        if (name == null) return null;

        return switch (name) {
            // Ferramentas e Armas
            case "picaretap" -> new PickaxeItemStone();
            case "picaretam" -> new PickaxeItemWood();
            case "espadap" -> new SwordItemStone();

            // Consumíveis
            case "cenoura" -> new CenouraItem();

            // Blocos
            case "troncop" -> new BlockItem("troncop", VoxelPalette.WOODPLANK_ID);
            case "tronco" -> new BlockItem("tronco", VoxelPalette.WOOD_ID);
            case "pedra" -> new BlockItem("pedra", VoxelPalette.STONE_ID);
            case "relva" -> new BlockItem("relva", VoxelPalette.GRASS_ID);
            case "terra" -> new BlockItem("terra", VoxelPalette.DIRT_ID);
            case "bedrock" -> new BlockItem("bedrock", VoxelPalette.BEDROCK_ID);

            // Item genérico se não encontrar correspondência
            default -> new SimpleItem(name);
        };
    }
}