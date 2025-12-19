package jogo.gameobject.item;

import jogo.craft.Recipe;

public class StickItem extends SimpleItem {

    public StickItem() {
        super("pau");
    }

    @Override
    public Recipe getRecipe() {
        return new Recipe(
                "pau",
                4, // Cria 4 paus de uma vez
                new Recipe.Ingredient[] {
                        new Recipe.Ingredient("troncop", 2)
                }
        );
    }
}