package jogo.craft;

public class Recipe {

    // Ingrediente de uma receita
    public static class Ingredient {
        private final String itemName;
        private final int amount;

        public Ingredient(String itemName, int amount) {
            this.itemName = itemName;
            this.amount = amount;
        }

        public String getItemName() {
            return itemName;
        }

        public int getAmount() {
            return amount;
        }
    }

    private final String resultName;
    private final int resultAmount;
    private final Ingredient[] ingredients;

    public Recipe(String resultName, int resultAmount, Ingredient[] ingredients) {
        this.resultName = resultName;
        this.resultAmount = resultAmount;
        this.ingredients = ingredients;
    }

    public String getResultName() {
        return resultName;
    }

    public int getResultAmount() {
        return resultAmount;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }
}