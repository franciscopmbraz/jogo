package jogo.gameobject.character;

import jogo.appstate.HudAppState;
import jogo.gameobject.item.CenouraItem;
import jogo.gameobject.item.Inventory;
import jogo.interaction.Interactable;

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
            HudAppState.mostrarMensagem("Toma esta cenoura! Planta-a na terra lavrada.|\n Depois colhe (20 cenouras) e leva ao meu amigo aqui ao lado.\n Nao te esqueças de matar os inimigos!!");
            HudAppState.iniciarTimer();
            Inventory.addInventory(new CenouraItem(), 1);


            deuSemente = true;
        } else {
            HudAppState.mostrarMensagem("Já plantaste? O meu amigo está à espera da comida.");
        }
    }
}