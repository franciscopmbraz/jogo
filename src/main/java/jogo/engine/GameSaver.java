package jogo.engine;

import com.jme3.math.Vector3f;
import jogo.gameobject.GameObject;
import jogo.gameobject.character.Enemy;
import jogo.gameobject.character.Player;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.Item;
import jogo.gameobject.item.ItemFactory;
import jogo.gameobject.item.ItemStack;
import jogo.voxel.Chunk;
import jogo.voxel.VoxelWorld;

import java.io.*;
import java.util.Map;

public class GameSaver {

    private static final String SAVE_FILE = "savegame.dat";

    // --- GUARDAR ---
    public static void saveGame(Player player, GameRegistry registry, VoxelWorld world) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(SAVE_FILE))) {

            // 1. Player Data
            dos.writeFloat(player.getPosition().x);
            dos.writeFloat(player.getPosition().y);
            dos.writeFloat(player.getPosition().z);
            dos.writeInt(player.getHealth());

            // 2. Inventory
            Inventory inv = player.getInventory();
            // Em vez de getItems().length, usamos a constante de tamanho
            dos.writeInt(Inventory.HOTBAR_SIZE);

            for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
                // AQUI: Usamos getSlot(i) em vez de iterar sobre um array
                ItemStack stack = inv.getSlot(i);

                if (stack != null && !stack.isEmpty()) {
                    dos.writeBoolean(true);
                    dos.writeUTF(stack.getItem().getName());
                    // Nota: Se não adicionaste o getQuantity() no ItemStack, usa getCount()
                    dos.writeInt(stack.getCount());
                } else {
                    dos.writeBoolean(false);
                }
            }

            // 3. Enemies & NPCs
            // Vamos contar quantos inimigos e NPCs existem para saber quantos ler depois
            // Filtramos apenas os que queremos salvar (excluir Player, Items no chão se for complexo, etc)
            var allObjects = registry.getAll();
            long enemyCount = allObjects.stream().filter(obj -> obj instanceof Enemy).count();

            dos.writeInt((int) enemyCount);

            for (GameObject obj : allObjects) {
                if (obj instanceof Enemy) {
                    Enemy e = (Enemy) obj;
                    // Guardar Tipo (classe), Posição e Vida
                    // Usamos o nome da classe para saber qual instanciar (ZombieEnemy vs TankEnemy)
                    dos.writeUTF(e.getClass().getName());
                    dos.writeFloat(e.getPosition().x);
                    dos.writeFloat(e.getPosition().y);
                    dos.writeFloat(e.getPosition().z);
                    dos.writeInt(e.getHealth());
                }
            }

            // 4. World Data (Chunks)
            Map<VoxelWorld.Vector3i, Chunk> chunks = world.getChunks();
            dos.writeInt(chunks.size()); // Quantos chunks temos

            for (Map.Entry<VoxelWorld.Vector3i, Chunk> entry : chunks.entrySet()) {
                VoxelWorld.Vector3i pos = entry.getKey();
                Chunk chunk = entry.getValue();

                // Coordenadas do Chunk
                dos.writeInt(pos.x);
                dos.writeInt(pos.y);
                dos.writeInt(pos.z);

                // Dados do Chunk (os blocos)
                byte[] data = chunk.getData();
                dos.writeInt(data.length); // Tamanho (normalmente 4096)
                dos.write(data); // Os bytes em si
            }

            System.out.println("Jogo guardado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao guardar o jogo.");
        }
    }

    // --- CARREGAR ---
    public static boolean loadGame(Player player, GameRegistry registry, VoxelWorld world) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return false;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(SAVE_FILE))) {

            // 1. Player Data
            float px = dis.readFloat();
            float py = dis.readFloat();
            float pz = dis.readFloat();
            int health = dis.readInt();

            player.setPosition(px, py, pz);
            player.setHealth(health);

            // 2. Inventory
            player.getInventory().clear();
            int invSize = dis.readInt();
            for (int i = 0; i < invSize; i++) {
                boolean hasItem = dis.readBoolean();
                if (hasItem) {
                    String itemName = dis.readUTF();
                    int qty = dis.readInt();
                    Item item = ItemFactory.createItem(itemName);
                    if (item != null) {
                        player.getInventory().addItem(item, qty);
                    }
                }
            }

            // 3. Enemies (Limpar e Recriar)
            // Removemos TODOS os inimigos atuais do registo antes de carregar os do save
            registry.getAll().removeIf(obj -> obj instanceof Enemy);

            int enemyCount = dis.readInt();
            for (int i = 0; i < enemyCount; i++) {
                String className = dis.readUTF();
                float ex = dis.readFloat();
                float ey = dis.readFloat();
                float ez = dis.readFloat();
                int eHealth = dis.readInt();

                try {
                    // Reflection para criar a classe certa (ZombieEnemy ou TankEnemy)
                    Class<?> clazz = Class.forName(className);
                    // Assume que o construtor aceita (x, y, z) como definimos antes
                    java.lang.reflect.Constructor<?> ctor = clazz.getConstructor(float.class, float.class, float.class);
                    Enemy enemy = (Enemy) ctor.newInstance(ex, ey, ez);
                    enemy.setHealth(eHealth);

                    registry.add(enemy);

                } catch (Exception e) {
                    System.err.println("Erro ao carregar inimigo: " + className);
                }
            }

            // 4. World Data
            world.clearChunks(); // Limpa o mundo atual (memória)
            int chunkCount = dis.readInt();

            for (int i = 0; i < chunkCount; i++) {
                int cx = dis.readInt();
                int cy = dis.readInt();
                int cz = dis.readInt();

                int dataLen = dis.readInt();
                byte[] data = new byte[dataLen];
                dis.readFully(data); // Lê todos os bytes do bloco

                // Recria o chunk no VoxelWorld
                world.restoreChunk(cx, cy, cz, data);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}