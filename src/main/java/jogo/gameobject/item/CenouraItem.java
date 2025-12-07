package jogo.gameobject.item;

public class CenouraItem extends SimpleItem {
    public CenouraItem() {
        super("cenoura");
    }

    public CenouraItem(int quantidade) {
        super("cenoura");
        this.setAmount(quantidade);
    }
}