package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import jogo.gameobject.character.Player;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.ItemStack;


public class HudAppState extends BaseAppState {

    private final Node guiNode;
    private final AssetManager assetManager;
    private BitmapText crosshair;
    private BitmapText vidaText;
    private BitmapFont font;
    private static final int HOTBAR_SLOTS = Inventory.HOTBAR_SIZE;
    private BitmapText[] hotbarTexts = new BitmapText[HOTBAR_SLOTS];


    public HudAppState(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
    }

    @Override
    protected void initialize(Application app) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        crosshair = new BitmapText(font, false);
        crosshair.setText("+");
        crosshair.setSize(font.getCharSet().getRenderedSize() * 2f);
        guiNode.attachChild(crosshair);
        centerCrosshair();
        System.out.println("HudAppState initialized: crosshair attached");


        // Aparece a vida do player
        vidaText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
        vidaText.setSize(20f); // tamanho da fonte
        guiNode.attachChild(vidaText);
        int screenWidth = app.getCamera().getWidth();  // largura da camera
        int screenHeight = app.getCamera().getHeight(); // altura da camera
        float x = screenWidth -125;
        float y = screenHeight - 30;
        vidaText.setLocalTranslation( x,y, 0); // posição inicial

        // Aparece a hotbar

        float slotWidth = 80f;   // antes 60f — mais espaço horizontal
        float spacing   = 20f;    // antes 10f — mais espaço entre slots
        float totalWidth = HOTBAR_SLOTS * slotWidth + (HOTBAR_SLOTS - 1) * spacing;
        float startX = (screenWidth - totalWidth) / 2f;
        float yHotbar = 40f; // distância do fundo

        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            BitmapText txt = new BitmapText(font, false);
            txt.setSize(18f); // se ainda estiver muito apertado, podes baixar para 16f
            txt.setLocalTranslation(startX + i * (slotWidth + spacing), yHotbar, 0);
            guiNode.attachChild(txt);
            hotbarTexts[i] = txt;
        }


    }

    private void centerCrosshair() {
        SimpleApplication sapp = (SimpleApplication) getApplication();
        int w = sapp.getCamera().getWidth();
        int h = sapp.getCamera().getHeight();
        float x = (w - crosshair.getLineWidth()) / 2f;
        float y = (h + crosshair.getLineHeight()) / 2f;
        crosshair.setLocalTranslation(x, y, 0);
    }

    @Override  // classe que da update
    public void update(float tpf) {
        // keep centered (cheap)
        centerCrosshair();

        // vai dar a vida do player caso nao seja nula
        Player player = getApplication().getStateManager().getState(PlayerAppState.class).getPlayer();
        if (player != null) {
            vidaText.setText("Vida: "+ player.getHealth());
        }
        Inventory inv = player.getInventory();
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            ItemStack stack = inv.getSlot(i);
            String label = (i + 1) + ": ";

            if (stack != null && !stack.isEmpty()) {
                label += stack.getItem().getName() + " x" + stack.getCount();
            } else {
                label += "-";
            }

            // destacar o slot selecionado
            if (i == inv.getSelectedHotbarSlot()) {
                label = "[" + label + "]";
            }

            if (hotbarTexts[i] != null) {
                hotbarTexts[i].setText(label);
            }
        }

    }

    @Override
    protected void cleanup(Application app) {
        if (crosshair != null) crosshair.removeFromParent();
        if (vidaText != null) vidaText.removeFromParent();
        if (hotbarTexts != null) {
            for (BitmapText t : hotbarTexts) {
                if (t != null) t.removeFromParent();
            }
        }
    }


    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}

