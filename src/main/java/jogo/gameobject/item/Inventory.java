package jogo.gameobject.item;

public class Inventory {

    public static final int HOTBAR_SIZE = 9;      // 9 slots hotbar (1–9)

    private static final Inventory GLOBAL_INVENTORY = new Inventory();

    public static Inventory getGlobalInventory() {
        return GLOBAL_INVENTORY;
    }

    public static void addToGlobal(Item item, int amount) {
        GLOBAL_INVENTORY.addItem(item, amount);
    }

    private final ItemStack[] slots = new ItemStack[HOTBAR_SIZE];
    private int selectedHotbarSlot = 0;           // índice 0..8

    public Inventory() {
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            slots[i] = ItemStack.empty();
        }
    }

    public ItemStack getSlot(int index) {
        if (index < 0 || index >= HOTBAR_SIZE) return null;
        return slots[index];
    }

    public void setSlot(int index, ItemStack stack) {
        if (index < 0 || index >= HOTBAR_SIZE) return;
        slots[index] = (stack == null ? ItemStack.empty() : stack);
    }

    public int getSelectedHotbarSlot() {
        return selectedHotbarSlot;
    }

    public void setSelectedHotbarSlot(int index) {
        if (index >= 0 && index < HOTBAR_SIZE) {
            selectedHotbarSlot = index;
        }
    }

    public ItemStack getSelectedStack() {
        return getSlot(selectedHotbarSlot);
    }


    public boolean addItem(Item item, int amount) {
        // 1º tentar juntar com stack já existente compatível
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack s = slots[i];
            if (s == null || s.isEmpty()) continue;

            Item existing = s.getItem();
            if (canStack(existing, item)) {
                s.add(amount);
                return true;
            }
        }

        // 2º procurar um slot vazio
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack s = slots[i];
            if (s == null || s.isEmpty()) {
                slots[i] = new ItemStack(item, amount);
                return true;
            }
        }

        // inventário cheio
        return false;
    }

    // decide se dois itens podem ser empilhados
    private boolean canStack(Item a, Item b) {
        if (a == null || b == null) return false;

        // têm de ser da MESMA classe (SimpleItem com SimpleItem, BlockItem com BlockItem, etc.)
        if (!a.getClass().equals(b.getClass())) return false;

        // têm de ter o mesmo nome
        if (!a.getName().equals(b.getName())) return false;

        // se forem BlockItem, também têm de ter o mesmo blockId
        if (a instanceof BlockItem ba && b instanceof BlockItem bb) {
            return ba.getBlockId() == bb.getBlockId();
        }

        // para os restantes tipos (espadas, picaretas, SimpleItem), nome+classe chegam
        return true;
    }

    // procura item pelo nome usado para o craft
    public boolean hasItem(String name, int amount) {
        int total = 0;

        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack stack = slots[i];
            if (stack == null || stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (item != null && item.getName().equals(name)) {
                total += stack.getCount();
                if (total >= amount) return true;
            }
        }
        return false;
    }

    // remove o item pelo nome usado para o craft
    public boolean removeItem(String name, int amount) {
        int toRemove = amount;

        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack stack = slots[i];
            if (stack == null || stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (item != null && item.getName().equals(name)) {
                int inStack = stack.getCount();
                int removeNow = Math.min(inStack, toRemove);
                stack.remove(removeNow);
                toRemove -= removeNow;

                if (toRemove <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
