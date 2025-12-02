package jogo.gameobject.item;

public abstract class PickaxeItem extends Item {

    private final float speedMultiplier; // 1.0f = normal, 2.0f = 2x normal

    public PickaxeItem(String name, float speedMultiplier) {
        super(name);
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}
