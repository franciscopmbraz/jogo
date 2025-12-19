package jogo.craft;

import jogo.gameobject.item.*;
import jogo.voxel.VoxelPalette;

import java.util.ArrayList;
import java.util.List;

public class Craft {

    private static final List<Recipe> RECIPES = new ArrayList<>();

    // O Registo das receitas
    static {
        // Registar itens que já têm classe própria (extrai a receita deles)
        registerItemRecipe(new PickaxeItemStone());
        registerItemRecipe(new PickaxeItemWood());
        registerItemRecipe(new SwordItemStone());
        registerItemRecipe(new StickItem());

        // CORREÇÃO AQUI:
        // Como isto é uma receita manual (sem classe Item), adicionamos diretamente à lista:
        RECIPES.add(new Recipe(
                "troncop", 4,
                new Recipe.Ingredient[] {
                        new Recipe.Ingredient("tronco", 1)
                }
        ));
    }


    // Este método extrai a receita do item e guarda-a na lista
    public static void registerItemRecipe(Item item) {
        Recipe recipe = item.getRecipe();
        if (recipe != null) {
            RECIPES.add(recipe);
        }
    }

    public static List<Recipe> getRecipes() {
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

        // Consumir os ingredientes
        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            if (!inv.removeItem(ing.getItemName(), ing.getAmount())) {
                return false;
            }
        }

        // Criar o item resultante
        Item resultItem;
        String name = recipe.getResultName();

        if (name.equals("picaretap")) resultItem = new PickaxeItemStone();
        else if (name.equals("picaretam")) resultItem = new PickaxeItemWood();
        else if (name.equals("espadap")) resultItem = new SwordItemStone();
        else if (name.equals("pau")) resultItem = new StickItem();
            // O "troncop" vai cair aqui, criando um item simples. Está perfeito.
        else if (name.equals("troncop")) resultItem = new BlockItem("troncop", VoxelPalette.WOODPLANK_ID);
        else resultItem = new SimpleItem(name);

        Inventory.addInventory(resultItem, recipe.getResultAmount());
        return true;
    }
}