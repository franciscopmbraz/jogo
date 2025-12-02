package jogo.gameobject.item;

public class ItemStack {

    private final Item item;
    private int count;

    public ItemStack(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    // Slot vazio
    public static ItemStack empty() {

        return new ItemStack(null, 0);
    }

    public boolean isEmpty() {
        return item == null || count <= 0;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void add(int amount) {
        count += amount;
    }

    public void remove(int amount) {
        count -= amount;
        if (count < 0) count = 0;
    }
}
