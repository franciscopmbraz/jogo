package jogo.gameobject.item;

import jogo.craft.Recipe;

public abstract class SwordItem extends Item {

    private final int damage;

    public SwordItem(String name, int damage) {
        super(name);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public Recipe getRecipe() {
        return null;
    }
}