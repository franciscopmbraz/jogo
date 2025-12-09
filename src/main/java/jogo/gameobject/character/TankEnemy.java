package jogo.gameobject.character;

public class TankEnemy extends Character {

    private int damage = 25;
    private float speed = 2f;

    public TankEnemy() {
        super("Tank");
        this.setPosition(170, 47, 170);
        this.setHealth(200); // 200 de Vida
    }

    public int getDamage() { return damage; }
    public float getSpeed() { return speed; }
}