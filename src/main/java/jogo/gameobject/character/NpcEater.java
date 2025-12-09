package jogo.gameobject.character;

import jogo.interaction.Interactable;
import jogo.gameobject.item.Inventory;

public class NpcEater extends Character implements Interactable {

    public NpcEater() {
        super("Comilao")
        ;
        this.setPosition(143, 35, 136 );
    }

    @Override
    public void onInteract() {
        Inventory inv = Inventory.getInventory();

        // Verifica se tens 5 cenouras
        if (inv.hasItem("cenoura", 20)) {

            inv.removeItem("cenoura", 20);

            System.out.println("Comilão: Mmmm! Cenouras frescas! Obrigado!");
            System.out.println("(Missão Completa)");

        } else {
            System.out.println("Comilão: Estou cheio de fome... O Fazendeiro não te deu nada?");
        }
    }
}