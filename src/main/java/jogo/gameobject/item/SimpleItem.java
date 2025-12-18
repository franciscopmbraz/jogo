package jogo.gameobject.item;

public class SimpleItem extends Item {

    public SimpleItem(String name) {
        super(name);
    }
    // Maneira de guardar objetos sem propriedades
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleItem other)) return false;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
