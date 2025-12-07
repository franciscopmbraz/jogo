package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import jogo.engine.GameRegistry;
import jogo.engine.RenderIndex;
import jogo.framework.math.Vec3;
import jogo.gameobject.GameObject;
import jogo.gameobject.character.Player;
import jogo.gameobject.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

public class RenderAppState extends BaseAppState {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final GameRegistry registry;
    private final RenderIndex renderIndex;

    private Node gameNode;
    private final Map<GameObject, Spatial> instances = new HashMap<>();

    public RenderAppState(Node rootNode, AssetManager assetManager, GameRegistry registry, RenderIndex renderIndex) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.registry = registry;
        this.renderIndex = renderIndex;
    }

    @Override
    protected void initialize(Application app) {
        gameNode = new Node("GameObjects");
        rootNode.attachChild(gameNode);
    }

    @Override
    public void update(float tpf) {
        // Ensure each registered object has a spatial and sync position
        var current = registry.getAll();
        Set<GameObject> alive = new HashSet<>(current);

        for (GameObject obj : current) {
            Spatial s = instances.get(obj);
            if (s == null) {
                s = createSpatialFor(obj);
                if (s != null) {
                    gameNode.attachChild(s);
                    instances.put(obj, s);
                    renderIndex.register(s, obj);
                }
            }
            if (s != null) {
                Vec3 p = obj.getPosition();
                s.setLocalTranslation(new Vector3f(p.x, p.y, p.z));
            }
        }

        // Cleanup: remove spatials for objects no longer in registry
        var it = instances.entrySet().iterator();
        while (it.hasNext()) {
            var e = it.next();
            if (!alive.contains(e.getKey())) {
                Spatial s = e.getValue();
                renderIndex.unregister(s);
                if (s.getParent() != null) s.removeFromParent();
                it.remove();
            }
        }
    }

    private Spatial createSpatialFor(GameObject obj) {
        //TODO This could be set inside each GameObject!
        if (obj instanceof Player) {
            Geometry g = new Geometry(obj.getName(), new Cylinder(16, 16, 0.35f, 1.4f, true));
            g.setMaterial(colored(ColorRGBA.Green));
            return g;
        } else if (obj instanceof jogo.gameobject.character.Enemy) {
            // Inimigo
            return createZombieVisual();
        } else if (obj instanceof Item) {
            Geometry g = new Geometry(obj.getName(), new Box(0.3f, 0.3f, 0.3f));
            g.setMaterial(colored(ColorRGBA.Yellow));
            return g;
        }
        return null;
    }

    private Material colored(ColorRGBA color) {
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", color.clone());
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 8f);
        return m;
    }
    private Spatial createZombieVisual() {
        Node zombieNode = new Node("ZombieVisual");

        // Cor Verde Escuro para o Zombie
        Material mat = colored(new ColorRGBA(0.0f, 0.5f, 0.0f, 1.0f));
        Material matClothes = colored(new ColorRGBA(0.0f, 0.0f, 0.6f, 1.0f)); // Azul para as calças

        // 1. Cabeça (Cubo)
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(mat);
        head.setLocalTranslation(0, 1.55f, 0); // No topo
        zombieNode.attachChild(head);

        // 2. Corpo (Tronco)
        Geometry body = new Geometry("Body", new Box(0.3f, 0.45f, 0.15f));
        body.setMaterial(matClothes); // Camisola (ou pele)
        body.setLocalTranslation(0, 0.85f, 0);
        zombieNode.attachChild(body);

        // 3. Braços (Esticados para a frente)
        // Braço Esquerdo
        Geometry armL = new Geometry("ArmL", new Box(0.12f, 0.45f, 0.12f));
        armL.setMaterial(mat);
        // Rodar 90 graus para a frente (X axis)
        armL.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        armL.setLocalTranslation(-0.42f, 1.15f, 0.45f); // Deslocado para o ombro e para a frente
        zombieNode.attachChild(armL);

        // Braço Direito
        Geometry armR = new Geometry("ArmR", new Box(0.12f, 0.45f, 0.12f));
        armR.setMaterial(mat);
        armR.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        armR.setLocalTranslation(0.42f, 1.15f, 0.45f);
        zombieNode.attachChild(armR);

        // 4. Pernas
        Geometry legL = new Geometry("LegL", new Box(0.13f, 0.45f, 0.13f));
        legL.setMaterial(matClothes);
        legL.setLocalTranslation(-0.15f, 0.0f, 0); // Pés no chão (offset tratado pelo physics)
        // Nota: O BetterCharacterControl centra a física, vamos subir as pernas um pouco para o total dar ~1.8m
        legL.move(0, 0.45f, 0);
        zombieNode.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.13f, 0.45f, 0.13f));
        legR.setMaterial(matClothes);
        legR.move(0.15f, 0.45f, 0);
        zombieNode.attachChild(legR);

        // Opcional: Ajustar o pivot para os pés ficarem em baixo
        // (O BetterCharacterControl geralmente centraliza o objeto, mas visualmente isto ajuda)
        zombieNode.setLocalTranslation(0, -0.9f, 0);

        return zombieNode;
    }

    @Override
    protected void cleanup(Application app) {
        if (gameNode != null) {
            gameNode.removeFromParent();
            gameNode = null;
        }
        instances.clear();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
