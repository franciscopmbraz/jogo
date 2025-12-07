package jogo.gameobject.character;

public class Enemy extends Character {

    private int damage = 10;
    private float speed = 4.0f; // Mais lento que o player (8.0f)

    public Enemy() {
        super("Inimigo");
        this.setPosition(137, 36, 130);
    }

    public int getDamage() { return damage; }
    public float getSpeed() { return speed; }
}