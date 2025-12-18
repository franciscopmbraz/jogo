package jogo.gameobject.item;

public class Inventory {

    public static final int HOTBAR_SIZE = 9;      // 9 slots hotbar (1–9)

    private static final Inventory INVENTORY = new Inventory();

    public static Inventory getInventory() {
        return INVENTORY;
    }

    // adiciona itens
    public static void addInventory(Item item, int amount) {
        INVENTORY.addItem(item, amount);
    }
    // Um array que guarda os itens
    private final ItemStack[] slots = new ItemStack[HOTBAR_SIZE];

    private int selectedHotbarSlot = 0;           // índice 0..8

    public Inventory() {
        // cria um inventario
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

    public void clear() {
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            slots[i] = ItemStack.empty();
        }
        System.out.println("Inventário limpo!");
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
        // tentar juntar com stack já existente compatível
        // Percorre todos os 9 slots da Hotbar
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack s = slots[i];
            // se dor null ou estiver fazio continua
            if (s == null || s.isEmpty()) continue;

            // se existir e der para juntar adicionamos
            Item existing = s.getItem();
            if (canStack(existing, item)) {
                s.add(amount);
                return true;
            }
        }
        // se nao procuramos um slot vazio
        // precorre os slots
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack s = slots[i];
            // se estiver fazio adicionamos
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

        // para os restantes tipos (espadas, picaretas, SimpleItem), nome+classe
        return true;
    }

    // procura item pelo nome usado para o craft
    public boolean hasItem(String name, int amount) {
        //  Começamos com 0. Vamos somar aqui quantos itens encontramos.
        int total = 0;
        // precorre todos os slots
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack stack = slots[i];
            // se tiver fazio ignora
            if (stack == null || stack.isEmpty()) continue;

            Item item = stack.getItem();
            // Verifica se o item existe E se o nome é igual ao que procuramos
            if (item != null && item.getName().equals(name)) {
                // Soma a quantidade deste monte ao total geral
                total += stack.getCount();
                // Se já temos o suficiente paramos de procurar
                if (total >= amount) return true;
            }
        }
        return false;
    }

    // remove o item pelo nome usado para o craft
    public boolean removeItem(String name, int amount) {
        // quantidade que queremos remover
        int toRemove = amount;

        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack stack = slots[i];
            // Se o slot estiver vazio ignora
            if (stack == null || stack.isEmpty()) continue;

            Item item = stack.getItem();
            // Verifica se é o item que queremos remover
            if (item != null && item.getName().equals(name)) {
                // quantos existem no total
                int inStack = stack.getCount();

                // // Tiramos parte ou tudo o que precisamos
                int removeNow = Math.min(inStack, toRemove);
                // Remove do monte atual
                stack.remove(removeNow);
                // tiramos o que acabamos de remover
                toRemove -= removeNow;

                // Se a dívida chegou a 0 acabámos
                if (toRemove <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
