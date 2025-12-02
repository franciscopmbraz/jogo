package jogo.gameobject.item;

public class BlockItem extends Item {

    private final byte blockId; // id do bloco na VoxelPalette

    public BlockItem(String name, byte blockId) {
        super(name);
        this.blockId = blockId;
    }

    public byte getBlockId() {
        return blockId;
    }
}

