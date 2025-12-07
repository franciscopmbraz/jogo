package jogo.gameobject.item;

import jogo.gameobject.GameObject;

public abstract class Item extends GameObject {

    protected Item(String name) {
        super(name);
    }

    public void onInteract() {
        // Hook for interaction logic (engine will route interactions)
    }
    private int amount = 1; // Valor padrão

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
