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
            Spatial s = createZombieVisual();
            s.setName(obj.getName());

            return s;
        } else if (obj instanceof Item) {
            Geometry g = new Geometry(obj.getName(), new Box(0.3f, 0.3f, 0.3f));
            g.setMaterial(colored(ColorRGBA.Yellow));
            return g;
        } else if (obj instanceof jogo.gameobject.character.NpcFazendeiro) {
            // Fazendeiro
            Spatial s = createFarmerVisual();
            s.setName(obj.getName());
            return s;

        } else if (obj instanceof jogo.gameobject.character.NpcEater) {
            //Comilap
            Spatial s = createEaterVisual();
            s.setName(obj.getName()); 
            return s;
        } else if (obj instanceof jogo.gameobject.character.TankEnemy) {
            Spatial s = createTankVisual();
            s.setName(obj.getName());
            return s;
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

        // Cabeça (Cubo)
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(mat);
        head.setLocalTranslation(0, 1.55f, 0); // No topo
        zombieNode.attachChild(head);

        // Corpo (Tronco)
        Geometry body = new Geometry("Body", new Box(0.3f, 0.45f, 0.15f));
        body.setMaterial(matClothes); // Camisola (ou pele)
        body.setLocalTranslation(0, 0.85f, 0);
        zombieNode.attachChild(body);

        // Braços (Esticados para a frente)
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

        //
        // (O BetterCharacterControl geralmente centraliza o objeto, mas visualmente isto ajuda)
        zombieNode.setLocalTranslation(0, -0.9f, 0);

        return zombieNode;
    }

    private Spatial createFarmerVisual() {
        Node node = new Node("FarmerVisual");

        // Materiais
        Material matSkin = colored(new ColorRGBA(0.96f, 0.76f, 0.62f, 1f)); // Pele
        Material matShirt = colored(ColorRGBA.Yellow);                      // Camisa Amarela
        Material matPants = colored(new ColorRGBA(0.4f, 0.25f, 0.1f, 1f));  // Calças Castanhas
        Material matHat = colored(new ColorRGBA(0.8f, 0.6f, 0.2f, 1f));     // Chapéu Palha

        // 1. Cabeça
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(matSkin);
        head.setLocalTranslation(0, 1.55f, 0);
        node.attachChild(head);

        // 2. Chapéu (Aba + Topo)
        Geometry hatBrim = new Geometry("HatBrim", new Box(0.35f, 0.02f, 0.35f));
        hatBrim.setMaterial(matHat);
        hatBrim.setLocalTranslation(0, 1.78f, 0); // Pouco acima da cabeça
        node.attachChild(hatBrim);

        Geometry hatTop = new Geometry("HatTop", new Box(0.20f, 0.12f, 0.20f));
        hatTop.setMaterial(matHat);
        hatTop.setLocalTranslation(0, 1.9f, 0);
        node.attachChild(hatTop);

        // 3. Corpo
        Geometry body = new Geometry("Body", new Box(0.3f, 0.45f, 0.15f));
        body.setMaterial(matShirt);
        body.setLocalTranslation(0, 0.85f, 0);
        node.attachChild(body);

        // 4. Braços (neutros, ao lado do corpo)
        Geometry armL = new Geometry("ArmL", new Box(0.12f, 0.45f, 0.12f));
        armL.setMaterial(matShirt);
        armL.setLocalTranslation(-0.42f, 0.85f, 0);
        node.attachChild(armL);

        Geometry armR = new Geometry("ArmR", new Box(0.12f, 0.45f, 0.12f));
        armR.setMaterial(matShirt);
        armR.setLocalTranslation(0.42f, 0.85f, 0);
        node.attachChild(armR);

        // 5. Pernas
        Geometry legL = new Geometry("LegL", new Box(0.13f, 0.45f, 0.13f));
        legL.setMaterial(matPants);
        legL.setLocalTranslation(-0.15f, 0.45f, 0); // Posição ajustada
        node.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.13f, 0.45f, 0.13f));
        legR.setMaterial(matPants);
        legR.setLocalTranslation(0.15f, 0.45f, 0);
        node.attachChild(legR);

        // Ajustar Pivot para os pés (0,0,0) ficarem no chão
        node.setLocalTranslation(0, -0.9f, 0);
        return node;
    }

    private Spatial createTankVisual() {
        Node node = new Node("GiantVisual");

        // Cores do Gigante
        Material matSkin = colored(new ColorRGBA(0.96f, 0.76f, 0.62f, 1f)); // Pele
        Material matCoat = colored(new ColorRGBA(0.6f, 0.4f, 0.2f, 1f));    // Casaco Castanho
        Material matPants = colored(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));   // Calças Cinza Escuro
        Material matBeard = colored(new ColorRGBA(0.3f, 0.15f, 0.05f, 1f)); // Barba/Cabelo

        float scale = 1.8f; // Escala Gigante

        // 1. Cabeça (Careca/Pele)
        Geometry head = new Geometry("Head", new Box(0.25f * scale, 0.28f * scale, 0.25f * scale));
        head.setMaterial(matSkin);
        head.setLocalTranslation(0, 1.65f * scale, 0);
        node.attachChild(head);

        // (Opcional) Costeletas/Barba castanha
        Geometry beard = new Geometry("Beard", new Box(0.26f * scale, 0.1f * scale, 0.15f * scale));
        beard.setMaterial(matBeard);
        beard.setLocalTranslation(0, 1.55f * scale, -0.1f * scale);
        node.attachChild(beard);

        // 2. Tronco (Casaco Castanho Largo)
        Geometry body = new Geometry("Body", new Box(0.5f * scale, 0.55f * scale, 0.35f * scale));
        body.setMaterial(matCoat);
        body.setLocalTranslation(0, 0.85f * scale, 0);
        node.attachChild(body);

        // 3. Braços (Ombros do casaco + Braços de pele)
        // Braço Esquerdo
        Geometry shoulderL = new Geometry("ShoulderL", new Box(0.2f * scale, 0.2f * scale, 0.2f * scale));
        shoulderL.setMaterial(matCoat);
        shoulderL.setLocalTranslation(-0.6f * scale, 1.2f * scale, 0);
        node.attachChild(shoulderL);

        Geometry armL = new Geometry("ArmL", new Box(0.18f * scale, 0.45f * scale, 0.18f * scale));
        armL.setMaterial(matSkin); // Braços nus
        armL.setLocalTranslation(-0.6f * scale, 0.6f * scale, 0.2f * scale); // Ligeiramente à frente
        // Rodar para parecer que vai dar um soco
        armL.setLocalRotation(new Quaternion().fromAngleAxis(-0.3f, Vector3f.UNIT_X));
        node.attachChild(armL);

        // Braço Direito
        Geometry shoulderR = new Geometry("ShoulderR", new Box(0.2f * scale, 0.2f * scale, 0.2f * scale));
        shoulderR.setMaterial(matCoat);
        shoulderR.setLocalTranslation(0.6f * scale, 1.2f * scale, 0);
        node.attachChild(shoulderR);

        Geometry armR = new Geometry("ArmR", new Box(0.18f * scale, 0.45f * scale, 0.18f * scale));
        armR.setMaterial(matSkin); // Braços nus
        armR.setLocalTranslation(0.6f * scale, 0.6f * scale, 0.2f * scale);
        armR.setLocalRotation(new Quaternion().fromAngleAxis(-0.3f, Vector3f.UNIT_X));
        node.attachChild(armR);

        // 4. Pernas (Curtas e grossas)
        Geometry legL = new Geometry("LegL", new Box(0.22f * scale, 0.35f * scale, 0.22f * scale));
        legL.setMaterial(matPants);
        legL.setLocalTranslation(-0.25f * scale, 0.35f * scale, 0);
        node.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.22f * scale, 0.35f * scale, 0.22f * scale));
        legR.setMaterial(matPants);
        legR.setLocalTranslation(0.25f * scale, 0.35f * scale, 0);
        node.attachChild(legR);

        // Ajuste Pivot
        node.setLocalTranslation(0, -0.9f, 0);
        return node;
    }
    private Spatial createEaterVisual() {
        Node node = new Node("EaterVisual");

        // Materiais
        Material matSkin = colored(new ColorRGBA(0.96f, 0.76f, 0.62f, 1f));
        Material matShirt = colored(ColorRGBA.Magenta); // A cor dele
        Material matPants = colored(ColorRGBA.Blue);    // Calças azuis

        // 1. Cabeça
        Geometry head = new Geometry("Head", new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(matSkin);
        head.setLocalTranslation(0, 1.55f, 0);
        node.attachChild(head);

        // 2. Corpo (Mais largo que o normal - 0.35f de largura em vez de 0.3f)
        Geometry body = new Geometry("Body", new Box(0.35f, 0.45f, 0.2f)); // Também mais profundo (0.2f)
        body.setMaterial(matShirt);
        body.setLocalTranslation(0, 0.85f, 0);
        node.attachChild(body);

        // 3. Braços (Levantados como se estivesse a comer ou à espera de algo)
        Geometry armL = new Geometry("ArmL", new Box(0.12f, 0.45f, 0.12f));
        armL.setMaterial(matShirt);
        // Roda um pouco para a frente
        armL.setLocalRotation(new Quaternion().fromAngleAxis(-0.5f, Vector3f.UNIT_X));
        armL.setLocalTranslation(-0.48f, 0.95f, 0.2f);
        node.attachChild(armL);

        Geometry armR = new Geometry("ArmR", new Box(0.12f, 0.45f, 0.12f));
        armR.setMaterial(matShirt);
        armR.setLocalRotation(new Quaternion().fromAngleAxis(-0.5f, Vector3f.UNIT_X));
        armR.setLocalTranslation(0.48f, 0.95f, 0.2f);
        node.attachChild(armR);

        // 4. Pernas
        Geometry legL = new Geometry("LegL", new Box(0.14f, 0.45f, 0.14f)); // Pernas um pouco mais grossas
        legL.setMaterial(matPants);
        legL.setLocalTranslation(-0.16f, 0.45f, 0);
        node.attachChild(legL);

        Geometry legR = new Geometry("LegR", new Box(0.14f, 0.45f, 0.14f));
        legR.setMaterial(matPants);
        legR.setLocalTranslation(0.16f, 0.45f, 0);
        node.attachChild(legR);

        node.setLocalTranslation(0, -0.9f, 0);
        return node;
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
