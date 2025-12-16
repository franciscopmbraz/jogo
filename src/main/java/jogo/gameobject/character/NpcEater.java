package jogo.gameobject.character;

import jogo.appstate.HudAppState;
import jogo.gameobject.item.Inventory;
import jogo.interaction.Interactable;

public class NpcEater extends Character implements Interactable {

    public NpcEater() {
        super("Eater")
        ;
        this.setPosition(143, 35, 136 );
    }

    @Override
    public void onInteract() {
        Inventory inv = Inventory.getInventory();

        if (jogo.appstate.HudAppState.getMonstrosMortos() < 2) {
            jogo.appstate.HudAppState.mostrarMensagem("Tens de matar os 2 monstros\nantes de entregar as cenouras!");
            return;
        }
        // Verifica se cenouras

        if (inv.hasItem("cenoura", 20)) {

            inv.removeItem("cenoura", 20);

            HudAppState.finalizarMissao();


        } else {
            HudAppState.mostrarMensagem("O Fazendeiro prometeu-me 20 cenouras... Ainda nada?");
        }
    }
}