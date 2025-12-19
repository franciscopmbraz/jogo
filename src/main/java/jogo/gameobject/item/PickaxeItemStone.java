package jogo.gameobject.item;

import jogo.craft.Recipe;

public class PickaxeItemStone extends PickaxeItem {
    public PickaxeItemStone() {
        super("picaretap", 3.0f);
    }
    @Override
    public Recipe getRecipe() {
        // Define que para criar ESTE item, precisas destes ingredientes
        return new Recipe(
                this.getName(), // O nome do resultado é o nome deste item ("picaretap")
                1,              // Cria 1 unidade
                new Recipe.Ingredient[] {
                        new Recipe.Ingredient("pedra", 3),
                        new Recipe.Ingredient("pau", 2)
                }
        );
    }
}