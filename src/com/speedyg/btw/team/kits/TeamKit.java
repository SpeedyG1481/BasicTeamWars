package com.speedyg.btw.team.kits;

import com.speedyg.btw.BasicTeamWars;
import com.sun.istack.internal.NotNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This class created for Basic Team Wars plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 10.02.2020
 */
public class TeamKit {

    private int cost;
    private List<String> armors;
    private List<String> contents;
    private String kitName;
    private UUID kitUUID;
    private File kitFile;
    private int minLevel;

    public TeamKit(String name, UUID uuid) {
        armors = new ArrayList<>();
        contents = new ArrayList<>();
        cost = 0;
        if (name != null) {
            kitName = name;
        } else {
            kitName = "";
        }
        if (uuid != null) {
            kitUUID = uuid;
        } else {
            kitUUID = UUID.randomUUID();
        }
        minLevel = 0;
        kitFile = new File(BasicTeamWars.getInstance().getDataFolder() + "/kits", kitUUID.toString() + ".json");
    }

    public static TeamKit getTeamKit(UUID uuid) {
        if (uuid != null)
            return TeamKit.getTeamKit(new File(BasicTeamWars.getInstance().getDataFolder() + "/kits", uuid.toString() + ".json"));
        else
            return new TeamKit(null, null);
    }

    public static TeamKit getTeamKit(File file) {
        try {
            FileReader reader = new FileReader(file);
            JSONParser oop = new JSONParser();
            JSONObject jo = (JSONObject) oop.parse(reader);
            reader.close();
            TeamKit kit = new TeamKit((String) jo.get("Name"), UUID.fromString((String) jo.get("UUID")));
            kit.setCost((int) (long) jo.get("Cost"));
            kit.setMinimumLevel((int) (long) jo.get("Min-Level"));
            kit.setArmors((List<String>) jo.get("Armors"));
            kit.setContents((List<String>) jo.get("Contents"));
            return kit;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setArmors(List<String> armors) {
        this.armors = armors;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }


    public void setMinimumLevel(int minimumLevel) {
        this.minLevel = minimumLevel;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setKitName(String name) {
        this.kitName = name;
    }

    public void setKitUUID(UUID uuid) {
        this.kitUUID = uuid;
    }


    public void saveKit() {
        JSONObject json = new JSONObject();
        json.put("Name", this.kitName);
        json.put("UUID", this.kitUUID.toString());
        json.put("Cost", this.cost);
        json.put("Min-Level", this.minLevel);
        json.put("Armors", armors);
        json.put("Contents", contents);

        if (!kitFile.exists()) {
            try {
                kitFile.getParentFile().mkdirs();
                kitFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writer;
        try {
            writer = new FileWriter(kitFile);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {
        return this.kitUUID;
    }

    public int getCost() {
        return this.cost;
    }

    public String getName() {
        return this.kitName;
    }

    public int getMinLevel() {
        return this.minLevel;
    }

    public List<String> getArmors() {
        return this.armors;
    }

    public List<String> getContents() {
        return this.contents;
    }

    public ItemStack[] listToItemStack(@NotNull List<String> list) {
        ItemStack[] returnList = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++) {
            returnList[i] = decodeItemStack(list.get(i));
        }
        return returnList;
    }

    private ItemStack decodeItemStack(String s) {
        ItemStack item = null;
        if (s != null && !s.equals("NULL")) {
            String[] first = s.split("&");
            Material type = Material.getMaterial(first[0]);
            short data = (short) Integer.parseInt(first[1]);
            int amount = Integer.parseInt(first[2]);
            String displayName;
            if (!first[3].equals("null"))
                displayName = first[3];
            else
                displayName = null;
            List<String> lore;
            if (!first[4].equals("null"))
                if (first[4].contains(","))
                    lore = Arrays.asList(first[4].split(","));
                else
                    lore = Arrays.asList(first[4]);
            else
                lore = null;
            List<String> enchantsSplitter;
            if (!first[5].equals("null"))
                if (first[5].contains("#"))
                    enchantsSplitter = Arrays.asList(first[5].split("#"));
                else
                    enchantsSplitter = Arrays.asList(first[5]);
            else
                enchantsSplitter = null;
            item = new ItemStack(type, amount, data);
            ItemMeta itemMeta = item.getItemMeta();
            if (displayName != null)
                itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            if (enchantsSplitter != null)
                for (String splitter : enchantsSplitter) {
                    Enchantment ench = Enchantment.getByName(splitter.split("-")[0]);
                    int enchPower = Integer.parseInt(splitter.split("-")[1]);
                    itemMeta.addEnchant(ench, enchPower, true);
                }
            item.setItemMeta(itemMeta);
        }
        return item;
    }

}
