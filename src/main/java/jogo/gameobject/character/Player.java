package jogo.gameobject.character;

import jogo.gameobject.item.Inventory;

public class Player extends Character {
    private final Inventory inventory = Inventory.getGlobalInventory();

    public Player() {
        super("Player");
    }
    public Inventory getInventory() {
        return inventory;
    }



    @Override
    public int getHealth() {
        return super.getHealth();
    }
    @Override
    public void setHealth(int health) {
        super.setHealth(health);
    }
}
