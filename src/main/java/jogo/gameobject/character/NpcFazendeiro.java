package jogo.gameobject.character;

import jogo.interaction.Interactable;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.CenouraItem;
import jogo.appstate.HudAppState;

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
            HudAppState.mostrarMensagem("Toma esta cenoura! Planta-a na terra lavrada.|\n Depois colhe o resultado (20 cenouras) e leva ao meu amigo aqui ao lado.");
            HudAppState.iniciarTimer();
            Inventory.addInventory(new CenouraItem(), 1);


            deuSemente = true;
        } else {
            HudAppState.mostrarMensagem("Já plantaste? O meu amigo está à espera da comida.");
        }
    }
}