package jogo.craft;

import jogo.gameobject.item.BlockItem;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.Item;
import jogo.gameobject.item.SimpleItem;
import jogo.voxel.VoxelPalette;

public class Craft {

    // TODAS AS RECEITAS DO JOGO
    // Troca os nomes conforme o que tens no teu jogo.
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
                    "picareta p", 1,
                    new Recipe.Ingredient[] {
                            new Recipe.Ingredient("pedra", 3),
                            new Recipe.Ingredient("pau", 2)
                    }
            ),
            new Recipe(
                    "picareta m", 1,
                    new Recipe.Ingredient[] {
                            new Recipe.Ingredient("troncop", 3),
                            new Recipe.Ingredient("pau", 2)
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
        // Verificar materiais
        if (!canCraft(inv, recipe)) return false;

        // Remover ingredientes
        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            boolean ok = inv.removeItem(ing.getItemName(), ing.getAmount());
            if (!ok) return false;
        }

        Item resultItem;

        String name = recipe.getResultName();

        if (name.equals("troncop")) {
            // aqui usas o ID de bloco certo para o troncop na tua paleta
            // TROCA VoxelPalette.WOOD_ID pelo ID correto do bloco "troncop"
            resultItem = new BlockItem("troncop", VoxelPalette.WOODPLANK_ID);
        } else {
            // restantes receitas: items normais
            resultItem = new SimpleItem(name);
        }

        Inventory.addToGlobal(resultItem, recipe.getResultAmount());
        return true;
    }
}
