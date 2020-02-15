package com.speedyg.btw.menu.kits;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.kits.TeamKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class KitShowMenu extends Menu implements Listener {

    private ItemStack[] armors;
    private ItemStack[] contents;


    protected KitShowMenu(BasicTeamWars btw, Player p, UUID team, TeamKit kit, PageMenu returnMenu) {
        super(btw, p, team, returnMenu);
        this.armors = getItemStack(kit.getArmors());
        this.contents = getItemStack(kit.getContents());
        this.inv = (Bukkit.createInventory(null, 54, main.getMenuOptions().getString("KitShowMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("KitShowMenu.Menu_Name")
                .replaceAll("<name>", kit.getName().replaceAll("&", "§"))
                .replaceAll("&", "§")
                : "§8§nKit Show Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    private ItemStack[] getItemStack(List<String> list) {
        ItemStack[] item = new ItemStack[list.size()];
        int i = 0;
        for (String s : list) {
            item[i] = decodeItemStack(s);
            i++;
        }
        return item;
    }

    private ItemStack decodeItemStack(String s) {
        ItemStack item = null;
        if (s != null) {
            if (!s.equals("NULL")) {
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
        }
        return item;
    }

    private ItemStack glass() {
        ItemStack item = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 15);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(45, this.back());
        this.inv.setItem(53, this.close());
        for (int i = 0; i < armors.length; i++) {
            if (armors[i] != null)
                if (armors[i].getType().name().contains("HELMET")) {
                    this.inv.setItem(11, armors[i]);
                } else if (armors[i].getType().name().contains("CHESTPLATE")) {
                    this.inv.setItem(12, armors[i]);
                } else if (armors[i].getType().name().contains("LEGGINGS")) {
                    this.inv.setItem(14, armors[i]);
                } else if (armors[i].getType().name().contains("BOOTS")) {
                    this.inv.setItem(15, armors[i]);
                }
        }

        for (int i = 0; i < 45; i++) {
            if ((i != 11 && i != 12 && i != 14 && i != 15) && (!(i >= 28 && i <= 34))) {
                this.inv.setItem(i, this.glass());
            }
        }
        for (int i = 0; i < this.contents.length; i++) {
            this.inv.setItem(28 + i, this.contents[i]);
        }
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inv)) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnPageMenu.openMenu();
                            }
                        }
                    }
                }
            }
        }
    }
}
