package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import jogo.craft.Craft;
import jogo.craft.Recipe;
import jogo.gameobject.item.Inventory;

public class CraftingAppState extends BaseAppState implements ActionListener {

    private Node guiNode;
    private BitmapFont guiFont;
    private AssetManager assetManager;  // <-- Faltava isto
    private InputManager inputManager;
    private Camera cam;

    private boolean menuOpen = false;

    private Recipe[] recipes;
    private BitmapText title;
    private BitmapText help;
    private BitmapText[] recipeTexts;

    // zonas clicáveis
    private int[] btnX, btnY, btnW, btnH;

    @Override
    protected void initialize(Application app) {
        SimpleApplication sapp = (SimpleApplication) app;

        guiNode = sapp.getGuiNode();
        assetManager = sapp.getAssetManager(); // <-- AGORA FUNCIONA
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        inputManager = sapp.getInputManager();
        cam = sapp.getCamera();

        recipes = Craft.getRecipes();

        title = new BitmapText(guiFont);
        title.setText("MENU DE CRAFT");
        title.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);

        help = new BitmapText(guiFont);
        help.setText("C: fechar | TAB: rato | Clique: craftar");

        recipeTexts = new BitmapText[recipes.length];
        btnX = new int[recipes.length];
        btnY = new int[recipes.length];
        btnW = new int[recipes.length];
        btnH = new int[recipes.length];

        for (int i = 0; i < recipes.length; i++) {
            recipeTexts[i] = new BitmapText(guiFont);
        }

        // Inputs
        inputManager.addMapping("CraftToggle", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("CraftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "CraftToggle", "CraftClick");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) return;

        if (name.equals("CraftToggle")) {
            toggleMenu();
        }
        else if (name.equals("CraftClick")) {

            if (menuOpen && inputManager.isCursorVisible()) {
                handleClick();
            }
        }
    }

    private void toggleMenu() {
        menuOpen = !menuOpen;

        if (menuOpen) openMenu();
        else closeMenu();
    }

    private void openMenu() {
        guiNode.attachChild(title);
        guiNode.attachChild(help);

        for (BitmapText t : recipeTexts) guiNode.attachChild(t);

        layoutTexts();
        refreshTexts();
    }

    private void closeMenu() {
        title.removeFromParent();
        help.removeFromParent();

        for (BitmapText t : recipeTexts) t.removeFromParent();
    }

    private void layoutTexts() {
        int screenW = cam.getWidth();
        int screenH = cam.getHeight();

        title.setLocalTranslation(50, screenH - 50, 0);
        help.setLocalTranslation(50, 80, 0);

        int startY = screenH - 120;
        int spacing = 40;
        int baseX = 60;
        int width = screenW / 2;

        for (int i = 0; i < recipes.length; i++) {
            int y = startY - i * spacing;

            recipeTexts[i].setLocalTranslation(baseX + 5, y + 25, 0);

            btnX[i] = baseX;
            btnY[i] = y;
            btnW[i] = width;
            btnH[i] = 30;
        }
    }

    private void refreshTexts() {
        Inventory inv = Inventory.getInventory();

        for (int i = 0; i < recipes.length; i++) {
            Recipe r = recipes[i];
            boolean can = Craft.canCraft(inv, r);

            StringBuilder sb = new StringBuilder();
            sb.append(r.getResultName())
                    .append(" x").append(r.getResultAmount())
                    .append("  [").append(can ? "OK" : "FALTA").append("]  - ");

            for (Recipe.Ingredient ing : r.getIngredients()) {
                sb.append(ing.getItemName())
                        .append(" x").append(ing.getAmount())
                        .append("  ");
            }

            recipeTexts[i].setText(sb.toString());
        }
    }

    private void handleClick() {
        Vector2f cursor = inputManager.getCursorPosition();
        float mx = cursor.x, my = cursor.y;

        for (int i = 0; i < recipes.length; i++) {
            int x = btnX[i];
            int y = btnY[i];
            int w = btnW[i];
            int h = btnH[i];

            if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
                tryCraft(i);
                break;
            }
        }
    }

    private void tryCraft(int index) {
        Inventory inv = Inventory.getInventory();
        Recipe r = recipes[index];

        if (Craft.craft(inv, r)) {
            System.out.println("Crafted: " + r.getResultName());
            refreshTexts();
        } else {
            System.out.println("Não tens materiais suficientes para: " + r.getResultName());
        }
    }

    @Override
    protected void cleanup(Application app) {
        closeMenu();
        inputManager.deleteMapping("CraftToggle");
        inputManager.deleteMapping("CraftClick");
        inputManager.removeListener(this);
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}
