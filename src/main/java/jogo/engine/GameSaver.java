package jogo.engine;

import jogo.gameobject.GameObject;
import jogo.gameobject.character.Player;
import jogo.gameobject.item.Inventory;
import jogo.gameobject.item.Item;
import jogo.gameobject.item.ItemFactory;
import jogo.gameobject.item.ItemStack;

import java.io.*;
import java.util.Locale;

public class GameSaver {

    private static final String SAVE_FILE = "savegame.txt";

    // --- GUARDAR ---
    public static void saveGame(Player player, GameRegistry registry) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            // 1. Jogador (Vida e Posição)
            writer.println("HEALTH:" + player.getHealth());
            writer.println(String.format(Locale.US, "POS:%.2f,%.2f,%.2f",
                    player.getPosition().x, player.getPosition().y, player.getPosition().z));

            // 2. Inventário
            Inventory inv = player.getInventory();
            for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
                ItemStack stack = inv.getSlot(i);
                if (stack != null && !stack.isEmpty()) {
                    writer.println("SLOT_" + i + ":" + stack.getItem().getName() + ":" + stack.getCount());
                }
            }

            // 3. NPCs (Guarda a posição de todos os objetos que não sejam o Player)
            if (registry != null) {
                for (GameObject obj : registry.getAll()) {
                    // Ignora o Player (já guardado) e guarda NPCs pelo nome
                    if (!(obj instanceof Player)) {
                        writer.println(String.format(Locale.US, "NPC:%s:%.2f,%.2f,%.2f",
                                obj.getName(),
                                obj.getPosition().x, obj.getPosition().y, obj.getPosition().z));
                    }
                }
            }

            System.out.println("Jogo guardado!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- CARREGAR ---
    public static boolean loadGame(Player player, GameRegistry registry) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("Nenhum save encontrado. A iniciar novo mundo...");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            player.getInventory().clear();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");

                if (parts[0].equals("HEALTH")) {
                    player.setHealth(Integer.parseInt(parts[1]));

                } else if (parts[0].equals("POS")) {
                    String[] coords = parts[1].split(",");
                    player.setPosition(
                            Float.parseFloat(coords[0]),
                            Float.parseFloat(coords[1]),
                            Float.parseFloat(coords[2])
                    );

                } else if (parts[0].startsWith("SLOT_")) {
                    int slot = Integer.parseInt(parts[0].substring(5));
                    Item item = ItemFactory.createItem(parts[1]);
                    int qtd = Integer.parseInt(parts[2]);
                    if (item != null) player.getInventory().setSlot(slot, new ItemStack(item, qtd));

                } else if (parts[0].equals("NPC")) {
                    // Formato -> NPC : Nome : X,Y,Z
                    if (registry != null) {
                        String npcName = parts[1];
                        String[] coords = parts[2].split(",");
                        float x = Float.parseFloat(coords[0]);
                        float y = Float.parseFloat(coords[1]);
                        float z = Float.parseFloat(coords[2]);

                        // Procura o NPC na lista e atualiza a posição
                        for (GameObject obj : registry.getAll()) {
                            if (obj.getName().equals(npcName)) {
                                obj.setPosition(x, y, z);
                            }
                        }
                    }
                }
            }
            System.out.println("Save carregado (Player + NPCs)!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}