package jogo.gameobject.item;

import jogo.craft.Recipe;

public class PickaxeItemWood extends PickaxeItem {
    public PickaxeItemWood() {
        super("picaretap", 1.7f);
    }

    @Override
    public Recipe getRecipe() {
        // Define que para criar ESTE item, precisas destes ingredientes
        return new Recipe(
                this.getName(),
                1,              // Cria 1 unidade
                new Recipe.Ingredient[] {
                        new Recipe.Ingredient("troncop", 3),
                        new Recipe.Ingredient("pau", 2)
                }
        );
    }
}

