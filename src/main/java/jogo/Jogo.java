package jogo;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import jogo.appstate.*;
import jogo.engine.GameRegistry;
import jogo.engine.RenderIndex;


/**
 * Main application entry.
 */
public class Jogo extends SimpleApplication {

    public static void main(String[] args) {
        Jogo app = new Jogo();
        app.setShowSettings(true); // show settings dialog
        AppSettings settings = new AppSettings(true);
        settings.setTitle("IscteCraft");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setGammaCorrection(true); // enable sRGB gamma-correct rendering
        app.setSettings(settings);
        app.start();
    }

    private BulletAppState bulletAppState;

    @Override
    public void simpleInitApp() {
        // disable flyCam, we manage camera ourselves
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(false);
        viewPort.setBackgroundColor(new ColorRGBA(0.6f, 0.75f, 1f, 1f)); // sky-like

        // Physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false); // toggle off later
        PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();

        // AppStates (order matters a bit: input -> world -> render -> interaction -> player)
        InputAppState input = new InputAppState();
        stateManager.attach(input);

        WorldAppState world = new WorldAppState(rootNode, assetManager, physicsSpace, cam, input);
        stateManager.attach(world);

        // Engine registry and render layers
        GameRegistry registry = new GameRegistry();
        input.setRegistry(registry);
        RenderIndex renderIndex = new RenderIndex();
        stateManager.attach(new RenderAppState(rootNode, assetManager, registry, renderIndex));
        stateManager.attach(new InteractionAppState(rootNode, cam, input, renderIndex, world));

        // Demo objects
        // Chest chest = new Chest();
        // chest.setPosition(26.5f, world.getRecommendedSpawnPosition().y - 2f, 26.5f);
        // registry.add(chest);

        PlayerAppState player = new PlayerAppState(rootNode, assetManager, cam, input, physicsSpace, world,registry);
        stateManager.attach(player);
        EnemyAppState enemyState = new EnemyAppState(rootNode, registry, physicsSpace, player,world);
        stateManager.attach(enemyState);
        TankAppState tankState = new TankAppState(rootNode, registry, physicsSpace, player, world);
        stateManager.attach(tankState);
        registry.add(new jogo.gameobject.character.NpcFazendeiro());
        registry.add(new jogo.gameobject.character.NpcEater());


        // Post-processing: SSAO for subtle contact shadows
        try {
            FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
            Class<?> ssaoCls = Class.forName("com.jme3.post.ssao.SSAOFilter");
            Object ssao = ssaoCls.getConstructor(float.class, float.class, float.class, float.class)
                    .newInstance(2.1f, 0.6f, 0.5f, 0.02f); // radius, intensity, scale, bias
            // Add filter via reflection to avoid compile-time dependency
            java.lang.reflect.Method addFilter = FilterPostProcessor.class.getMethod("addFilter", Class.forName("com.jme3.post.Filter"));
            addFilter.invoke(fpp, ssao);
            viewPort.addProcessor(fpp);
        } catch (Exception e) {
            System.out.println("SSAO not available (effects module missing?): " + e.getMessage());
        }

        // HUD (just a crosshair for now)
        stateManager.attach(new HudAppState(guiNode, assetManager));
        // Menu de Crafting (abre com C)
        stateManager.attach(new CraftingAppState());
        // ataques a inimigos
        stateManager.attach(new CombatAppState(rootNode, cam, input, renderIndex));
        if (jogo.engine.GameSaver.loadGame(player.getPlayer(), registry)) {
            player.warpToPlayerPosition();
            System.out.println("Save carregado automaticamente!");
        }
    }
}
