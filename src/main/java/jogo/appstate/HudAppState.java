package jogo.appstate;


import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.ItemStack;
import java.io.*;


public class HudAppState extends BaseAppState {




    private static HudAppState instance;
    private static final String HIGHSCORE_FILE = "highscore.txt";


    private final Node guiNode;
    private final AssetManager assetManager;


    private BitmapText crosshair;
    private BitmapText vidaText;
    private BitmapText legendaText; // Texto para as falas dos NPCs
    private BitmapText timerText;
    private BitmapText missaoCumpridaText;
    private BitmapText highScoreText;




    private float gameTime = 0f;
    private boolean timerRunning = false;
    private boolean missaoAcabada = false;
    private float bestTime = -1f; // -1 significa sem recorde


    private BitmapFont font;
    private static final int HOTBAR_SLOTS = Inventory.HOTBAR_SIZE;
    private BitmapText[] hotbarTexts = new BitmapText[HOTBAR_SLOTS];


    // Temporizador para a mensagem desaparecer
    private float messageTimer = 0f;
    private float victoryTimer = 0f;


    public HudAppState(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
    }


    public static void iniciarTimer() {
        if (instance != null && !instance.timerRunning && !instance.missaoAcabada) {
            instance.timerRunning = true;
            instance.gameTime = 0f;
        }
    }


    public static void adicionarPenalidade(float segundos) {
        if (instance != null && instance.timerRunning) {
            instance.gameTime += segundos;
            instance.mostrarMensagem("Penalidade: +" + (int)segundos + "s");
        }
    }


    public static void finalizarMissao() {
        if (instance != null && instance.timerRunning) {
            instance.timerRunning = false;
            instance.missaoAcabada = true;
            instance.exibirVitoria();




        }
    }




    // Metodo para os NPCs chamarem
    public static void mostrarMensagem(String texto) {
        if (instance != null) {
            instance.exibirTexto(texto);
        }
    }


    @Override
    protected void initialize(Application app) {
        instance = this; // Regista esta instância


        font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        // Carregar Highscore do Ficheiro
        carregarHighscore();




        // --- Crosshair ---
        crosshair = new BitmapText(font, false);
        crosshair.setText("+");
        crosshair.setSize(font.getCharSet().getRenderedSize() * 2f);
        guiNode.attachChild(crosshair);
        centerCrosshair();


        // --- Vida ---
        vidaText = new BitmapText(font, false);
        vidaText.setSize(20f);
        guiNode.attachChild(vidaText);
        int w = app.getCamera().getWidth();
        int h = app.getCamera().getHeight();
        vidaText.setLocalTranslation(w - 125, h - 30, 0);


        //  Timer (Canto Superior Esquerdo)
        timerText = new BitmapText(font, false);
        timerText.setSize(20f);
        timerText.setColor(ColorRGBA.White);
        timerText.setLocalTranslation(20, h - 20, 0);
        timerText.setText("Tempo: 00:00");
        guiNode.attachChild(timerText);
        // Highscore (Debaixo do Timer)
        highScoreText = new BitmapText(font, false);
        highScoreText.setSize(16f);
        highScoreText.setColor(ColorRGBA.White);
        highScoreText.setLocalTranslation(20, h - 50, 0);
        guiNode.attachChild(highScoreText);
        atualizarTextoHighscore();


        // Texto de Missão Cumprida
        missaoCumpridaText = new BitmapText(font, false);
        missaoCumpridaText.setSize(50f);
        missaoCumpridaText.setColor(ColorRGBA.Green);
        missaoCumpridaText.setText(""); // Começa invisível
        guiNode.attachChild(missaoCumpridaText);


        //Legendas / Mensagens
        legendaText = new BitmapText(font, false);
        legendaText.setSize(17f);
        legendaText.setColor(ColorRGBA.Red); // Cor
        guiNode.attachChild(legendaText);
        // Posicionar no centro em baixo
        legendaText.setLocalTranslation(w / 2f - 100, h / 2f + 50, 0);
        legendaText.setText(""); // Começa vazio


        //Hotbar
        float slotWidth = 80f;
        float spacing   = 20f;
        float totalWidth = HOTBAR_SLOTS * slotWidth + (HOTBAR_SLOTS - 1) * spacing;
        float startX = (w - totalWidth) / 2f;
        float yHotbar = 40f;


        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            BitmapText txt = new BitmapText(font, false);
            txt.setSize(14f);
            txt.setLocalTranslation(startX + i * (slotWidth + spacing), yHotbar, 0);
            guiNode.attachChild(txt);
            hotbarTexts[i] = txt;
        }


        System.out.println("HudAppState initialized");
    }


    // Metodo interno para atualizar o texto visualmente
    private void exibirTexto(String texto) {
        if (legendaText != null) {
            legendaText.setText(texto);


            // Centrar o texto
            float textWidth = legendaText.getLineWidth();
            int screenW = getApplication().getCamera().getWidth();
            int screenH = getApplication().getCamera().getHeight();
            legendaText.setLocalTranslation((screenW - textWidth) / 2f, screenH / 2f + 100, 0);


            // tempo0 visivel
            messageTimer = 4.0f;
        }
    }
    private void exibirVitoria() {


        //Logica do score
        boolean novoRecorde = false;


        if (bestTime == -1 || gameTime < bestTime) {
            bestTime = gameTime;
            guardarHighscore(); //
            atualizarTextoHighscore();
            novoRecorde = true;
        }


        // Formatar o tempo final
        int minutos = (int) (gameTime / 60);
        int segundos = (int) (gameTime % 60);
        String tempoFinal = String.format("%02d:%02d", minutos, segundos);


        String msg = "MISSAO CUMPRIDA!\nTempo: " + tempoFinal;
        if (novoRecorde) {
            msg += "\nNOVO RECORDE!";
        }
        missaoCumpridaText.setText(msg);


        // Centrar no ecrã
        float width = missaoCumpridaText.getLineWidth();
        float height = missaoCumpridaText.getLineHeight();
        int screenW = getApplication().getCamera().getWidth();
        int screenH = getApplication().getCamera().getHeight();




        missaoCumpridaText.setLocalTranslation((screenW - width) / 2f, (screenH + height) / 2f, 0);


        victoryTimer = 5.0f;
    }
    private void carregarHighscore() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists()) {
            bestTime = -1f;
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                bestTime = Float.parseFloat(line);
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler highscore: " + e.getMessage());
            bestTime = -1f;
        }
    }
    private void guardarHighscore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.println(bestTime);
        } catch (IOException e) {
            System.err.println("Erro ao guardar highscore: " + e.getMessage());
        }
    }


    private void atualizarTextoHighscore() {
        if (bestTime < 0) {
            highScoreText.setText("Melhor: --:--");
        } else {
            int m = (int) (bestTime / 60);
            int s = (int) (bestTime % 60);
            highScoreText.setText(String.format("Melhor: %02d:%02d", m, s));
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


    @Override
    public void update(float tpf) {
        centerCrosshair();


        // Logica timer
        if (timerRunning) {
            gameTime += tpf;
            int minutos = (int) (gameTime / 60);
            int segundos = (int) (gameTime % 60);
            timerText.setText(String.format("Tempo: %02d:%02d", minutos, segundos));
        }


        // Atualiza Vida
        PlayerAppState pas = getApplication().getStateManager().getState(PlayerAppState.class);
        if (pas != null && pas.getPlayer() != null) {
            vidaText.setText("Vida: " + pas.getPlayer().getHealth());
        }


        // Atualiza Hotbar
        Inventory inv = Inventory.getInventory();
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            ItemStack stack = inv.getSlot(i);
            String label = (i + 1) + ": ";
            if (stack != null && !stack.isEmpty()) {
                label += stack.getItem().getName() + " x" + stack.getCount();
            } else {
                label += "-";
            }
            if (i == inv.getSelectedHotbarSlot()) {
                label = "[" + label + "]";
            }
            if (hotbarTexts[i] != null) hotbarTexts[i].setText(label);
        }


        //  Temporizador da Mensagem
        if (messageTimer > 0) {
            messageTimer -= tpf;
            if (messageTimer <= 0) {
                legendaText.setText(""); // Apaga o texto quando o tempo acaba
            }
        }
        // Timer Vitória
        if (victoryTimer > 0) {
            victoryTimer -= tpf;
            if (victoryTimer <= 0) {
                missaoCumpridaText.setText(""); // Apaga o texto da vitória
            }
        }


    }


    @Override
    protected void cleanup(Application app) {
        instance = null; // Limpar referência estática
        if (crosshair != null) crosshair.removeFromParent();
        if (vidaText != null) vidaText.removeFromParent();
        if (legendaText != null) legendaText.removeFromParent();
        if (hotbarTexts != null) {
            for (BitmapText t : hotbarTexts) if (t != null) t.removeFromParent();
        }
        if (timerText != null) timerText.removeFromParent();
        if (highScoreText != null) highScoreText.removeFromParent();
        if (missaoCumpridaText != null) missaoCumpridaText.removeFromParent();
    }


    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}

