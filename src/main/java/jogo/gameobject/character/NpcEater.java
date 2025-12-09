package jogo.gameobject.character;

import jogo.interaction.Interactable;
import jogo.gameobject.item.Inventory;
import jogo.appstate.HudAppState;

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

            HudAppState.finalizarMissao();


        } else {
            HudAppState.mostrarMensagem("O Fazendeiro prometeu-me 20 cenouras... Ainda nada?");
        }
    }
}