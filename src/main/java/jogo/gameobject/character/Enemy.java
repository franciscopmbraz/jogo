package jogo.gameobject.character;

public class Enemy extends Character {

    private int damage = 10;
    private float speed = 4.0f; // playes 8.0f

    public Enemy() {
        super("Inimigo");
        this.setPosition(172, 47, 175   );
    }

    public int getDamage() { return damage; }
    public float getSpeed() { return speed; }
}