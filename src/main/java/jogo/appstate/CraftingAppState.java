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
import java.util.List;

public class CraftingAppState extends BaseAppState implements ActionListener {

    private Node guiNode;
    private BitmapFont guiFont;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Camera cam;

    private boolean menuOpen = false;
    private BitmapText title;
    private BitmapText help;

    private List<Recipe> recipes; // Lista de todas as receitas
    private BitmapText[] recipeTexts;  // Textos visuais de cada receita

    // zonas clicáveis
    private int[] btnX, btnY, btnW, btnH;

    @Override
    protected void initialize(Application app) {
        SimpleApplication sapp = (SimpleApplication) app;

        guiNode = sapp.getGuiNode();
        assetManager = sapp.getAssetManager();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        inputManager = sapp.getInputManager();
        cam = sapp.getCamera();

        recipes = Craft.getRecipes(); // vai buscar as receitas

        //  Menu de craft
        title = new BitmapText(guiFont);
        title.setText("MENU DE CRAFT");
        title.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);

        help = new BitmapText(guiFont);
        help.setText("C: fechar | TAB: rato | Clique: craftar");

        // preparar os arrays para os botões e textos
        int totalRecipes = recipes.size();

        recipeTexts = new BitmapText[totalRecipes];
        btnX = new int[totalRecipes];
        btnY = new int[totalRecipes];
        btnW = new int[totalRecipes];
        btnH = new int[totalRecipes];

        for (int i = 0; i < totalRecipes; i++) {
            recipeTexts[i] = new BitmapText(guiFont);
        }

        // Inputs do craft
        inputManager.addMapping("CraftToggle", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("CraftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "CraftToggle", "CraftClick");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

        if (!isPressed) return;

        if (name.equals("CraftToggle")) {  // se clicar C abre o menu de craft
            toggleMenu();
        }
        else if (name.equals("CraftClick")) {
            // Só aceita cliques se o menu estiver aberto e o rato visível
            if (menuOpen && inputManager.isCursorVisible()) {
                handleClick();
            }
        }
    }

    private void toggleMenu() { // abre o menu se estiver aberto fecha
        menuOpen = !menuOpen;

        if (menuOpen) openMenu();
        else closeMenu();
    }

    private void openMenu() {
        // Adiciona os textos à "Node" da interface
        guiNode.attachChild(title);
        guiNode.attachChild(help);

        for (BitmapText t : recipeTexts) guiNode.attachChild(t);

        layoutTexts(); // Calcula posições
        refreshTexts(); // Atualiza o conteúdo (se tens materiais ou não)
    }

    private void closeMenu() { // fecha o menu
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
        int spacing = 40;  // espaçamento entre linhas
        int baseX = 60;
        int width = screenW / 2;

        for (int i = 0; i < recipes.size(); i++) {
            int y = startY - i * spacing;  //aqui o espaçamento

            // Posiciona o texto
            recipeTexts[i].setLocalTranslation(baseX + 5, y + 25, 0);

            // Guarda as coordenadas do "botão" correspondente a este texto
            btnX[i] = baseX;
            btnY[i] = y;
            btnW[i] = width;
            btnH[i] = 30;
        }
    }

    private void refreshTexts() {
        Inventory inv = Inventory.getInventory(); // ve o inventário

        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            // Pergunta à lógica se é possível craftar
            boolean can = Craft.canCraft(inv, r);


            StringBuilder sb = new StringBuilder();
            sb.append(r.getResultName());  // pega no nome
            sb.append(" x").append(r.getResultAmount()); // pega na quantidade
            //Se can for verdadeiro, escreve "OK". Se for falso, escreve "FALTA"
            sb.append("  [").append(can ? "OK" : "FALTA").append("]  - ");

            for (Recipe.Ingredient ing : r.getIngredients()) {
                //Adiciona à frase todos os materiais necessários
                // para que o jogador saiba o que precisa apanhar.
                sb.append(ing.getItemName());
                sb.append(" x").append(ing.getAmount());
                sb.append("  ");
            }
            // pega em tudo e envia para o objeto de texto (BitmapText) que está visível no ecrã.
            recipeTexts[i].setText(sb.toString());

        }
    }

    private void handleClick() {
        // ve onde está o rato
        Vector2f cursor = inputManager.getCursorPosition();
        float mx = cursor.x, my = cursor.y;

        for (int i = 0; i < recipes.size(); i++) {
            // Verifica se o rato está dentro do retângulo do botão i
            int x = btnX[i];
            int y = btnY[i];
            int w = btnW[i];
            int h = btnH[i];

            if (mx >= x && mx <= x + w && my >= y && my <= y + h) {
                // Se estiver, tenta craftar a receita i
                tryCraft(i);
                break;
            }
        }
    }

    private void tryCraft(int index) {
        Inventory inv = Inventory.getInventory();
        Recipe r = recipes.get(index);
        // Tenta executar o craft (remover ingredientes, adicionar produto)
        if (Craft.craft(inv, r)) {
            System.out.println("Crafted: " + r.getResultName());
            refreshTexts(); // Atualiza o ecrã (os materiais  existentes mudam)
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
