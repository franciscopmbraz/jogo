package jogo.gameobject.item;

public abstract class SwordItem extends Item {

    private final int damage;

    public SwordItem(String name, int damage) {
        super(name);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}