package jogo.gameobject.character;

import jogo.interaction.Interactable;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.CenouraItem;

public class NpcFazendeiro extends Character implements Interactable {

    private boolean deuSemente = false;

    public NpcFazendeiro() {
        super("Fazendeiro");
        // Ajusta a posição conforme o teu mapa
        this.setPosition(138, 35, 136);
    }

    @Override
    public void onInteract() {
        if (!deuSemente) {
            System.out.println("Fazendeiro: Olá! Toma esta cenoura. Planta-a na terra lavrada!");
            System.out.println("Fazendeiro: Depois colhe o resultado (5 cenouras) e leva ao meu amigo Comilão.");

            Inventory.addInventory(new CenouraItem(), 1);

            deuSemente = true;
        } else {
            System.out.println("Fazendeiro: Já plantaste? O meu amigo está à espera da comida.");
        }
    }
}