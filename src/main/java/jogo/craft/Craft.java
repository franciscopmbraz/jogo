package jogo.craft;

import jogo.gameobject.item.*;
import jogo.voxel.VoxelPalette;

public class Craft {

    // TODAS AS RECEITAS DO JOGO
    private static final Recipe[] RECIPES = new Recipe[] {


            new Recipe(
                    "troncop", 4,
                    new Recipe.Ingredient[] {
                            new Recipe.Ingredient("tronco", 1)
                    }
            ),

            // Exemplo 2: 2 "Planks" -> 4 "Stick"
            new Recipe(
                    "pau", 4,
                    new Recipe.Ingredient[] {
                            new Recipe.Ingredient("troncop", 2)
                    }
            ),
            new Recipe(
                    "picaretap", 1,
                    new Recipe.Ingredient[] {
                            new Recipe.Ingredient("pedra", 3),
                            new Recipe.Ingredient("pau", 2)
                    }
            ),
            new Recipe(
                    "picaretam", 1,
                    new Recipe.Ingredient[] {
                            new Recipe.Ingredient("troncop", 3),
                            new Recipe.Ingredient("pau", 2)
                    }
            ),
            new Recipe(
                    "espadap",1,
                    new Recipe.Ingredient[]{
                            new Recipe.Ingredient("pau",1),
                            new Recipe.Ingredient("pedra",2)
                    }
            )

    };

    public static Recipe[] getRecipes() {
        return RECIPES;
    }

    public static boolean canCraft(Inventory inv, Recipe recipe) {
        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            if (!inv.hasItem(ing.getItemName(), ing.getAmount())) {
                return false;
            }
        }
        return true;
    }

    public static boolean craft(Inventory inv, Recipe recipe) {
        if (!canCraft(inv, recipe)) return false;

        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            if (!inv.removeItem(ing.getItemName(), ing.getAmount())) {
                return false;
            }
        }

        Item resultItem;
        String name = recipe.getResultName();

        if (name.equals("troncop")) {
            resultItem = new BlockItem("troncop", VoxelPalette.WOODPLANK_ID);
        } else if (name.equals("picaretap")) {
            resultItem = new PickaxeItemStone();
        } else if (name.equals("picaretam")) {
            resultItem = new PickaxeItemWood();
        } else if (name.equals("espadap")) {
            resultItem = new SwordItemStone();
        } else {
            resultItem = new SimpleItem(name);
        }

        Inventory.addInventory(resultItem, recipe.getResultAmount());
        return true;
    }
}
