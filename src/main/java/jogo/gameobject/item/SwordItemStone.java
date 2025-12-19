package jogo.gameobject.item;

import jogo.craft.Recipe;

public class SwordItemStone extends SwordItem {
    public SwordItemStone() {
        super("espadap", 20);
    }

    @Override
    public Recipe getRecipe() {
        // Define que para criar ESTE item, precisas destes ingredientes
        return new Recipe(
                this.getName(), // O nome do resultado é o nome deste item ("picaretap")
                1,              // Cria 1 unidade
                new Recipe.Ingredient[] {
                        new Recipe.Ingredient("pedra", 2),
                        new Recipe.Ingredient("pau", 1)
                }
        );
    }
}