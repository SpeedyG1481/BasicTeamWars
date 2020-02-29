package com.speedyg.btw.menu;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.Version;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.kits.TeamKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamKitsEditMenu extends Menu implements Listener {

    private int MAX_LEVEL;
    private TeamKit kit;
    private int cost = 0;
    private int minLevel = 0;
    private ItemStack[] armors;
    private ItemStack[] contents;

    public TeamKitsEditMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu, TeamKit kit) {
        super(btw, p, team, returnMenu);
        this.kit = kit;
        this.inv = (Bukkit.createInventory(null, 54, main.getMenuOptions().getString("KitEditMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("KitEditMenu.Menu_Name").replaceAll("&", "§")
                : "§8§nEdit This Kit"));
        MAX_LEVEL = main.getConfig().getInt("Max_Level");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
        this.contents = new ItemStack[7];
        this.armors = new ItemStack[4];
    }

    private ItemStack glass() {
        Material mat;
        if (BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_14) || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_15) || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_16)) {
            mat = Material.getMaterial("STAINED_GLASS_PANE", true);
        } else {
            mat = Material.getMaterial("STAINED_GLASS_PANE");
        }
        ItemStack item = new ItemStack(mat, 1, (short) 15);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack saveButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("KitEditMenu.Buttons.SaveButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("KitEditMenu.Buttons.SaveButton.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("KitEditMenu.Buttons.SaveButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setCost() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("KitEditMenu.Buttons.CostButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("KitEditMenu.Buttons.CostButton.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("KitEditMenu.Buttons.CostButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<value>", String.valueOf(cost)).replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setLevel() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("KitEditMenu.Buttons.LevelButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("KitEditMenu.Buttons.LevelButton.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("KitEditMenu.Buttons.LevelButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<value>", String.valueOf(minLevel)).replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(49, this.saveButton());
        this.inv.setItem(47, this.setCost());
        this.inv.setItem(51, this.setLevel());
        for (int i = 0; i < 45; i++) {
            if ((i != 11 && i != 12 && i != 14 && i != 15) && (!(i >= 28 && i <= 34))) {
                this.inv.setItem(i, this.glass());
            }
        }
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inv)) {
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.setCost().getItemMeta().getDisplayName())) {
                                e.setCancelled(true);
                                if (e.getClick().equals(ClickType.LEFT)) {
                                    this.cost += 10;
                                } else if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (cost - 10 >= 0)
                                        this.cost -= 10;
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    this.cost += 1000;
                                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    if (cost - 1000 >= 0)
                                        this.cost -= 1000;
                                }
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.setLevel().getItemMeta().getDisplayName())) {
                                e.setCancelled(true);
                                if (e.getClick().equals(ClickType.LEFT)) {
                                    if (minLevel + 1 <= MAX_LEVEL)
                                        this.minLevel += 1;
                                    else
                                        this.minLevel = MAX_LEVEL;
                                } else if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (minLevel - 1 >= 0)
                                        this.minLevel -= 1;
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (minLevel + 10 <= MAX_LEVEL)
                                        this.minLevel += 10;
                                    else
                                        this.minLevel = MAX_LEVEL;
                                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    if (minLevel - 10 >= 0)
                                        this.minLevel -= 10;
                                }
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.saveButton().getItemMeta().getDisplayName())) {
                                saveItems();
                                e.setCancelled(true);
                                kit.setArmors(itemStackToString(armors));
                                kit.setContents(itemStackToString(contents));
                                kit.setMinimumLevel(minLevel);
                                kit.setCost(cost);
                                kit.saveKit();
                                p.closeInventory();
                                p.sendMessage(Messages.kit_saved);

                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.glass().getItemMeta().getDisplayName())) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void saveItems() {
        armors[0] = this.inv.getItem(11);
        armors[1] = this.inv.getItem(12);
        armors[2] = this.inv.getItem(14);
        armors[3] = this.inv.getItem(15);
        for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = this.inv.getItem(i + 28);
        }
    }


    private List<String> itemStackToString(ItemStack[] items2) {
        List<String> list = new ArrayList<>(items2.length);
        for (ItemStack item : items2) {
            if (item != null) {
                StringBuilder enchants = new StringBuilder("null");
                StringBuilder lore = new StringBuilder("null");
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasLore()) {
                        int x = 0;
                        for (String add : item.getItemMeta().getLore()) {
                            if (x == 0)
                                lore = new StringBuilder();
                            if (x == item.getItemMeta().getLore().size() - 1) {
                                lore.append(add);
                            } else {
                                lore.append(add).append(",");
                            }
                            x++;
                        }
                    }
                }
                int x = 0;
                ItemMeta iMeta = item.getItemMeta();
                for (Enchantment enc : iMeta.getEnchants().keySet()) {
                    if (x == 0)
                        enchants = new StringBuilder();
                    if (x == iMeta.getEnchants().keySet().size() - 1)
                        enchants.append(enc.getName()).append("-").append(iMeta.getEnchants().get(enc).shortValue());
                    else
                        enchants.append(enc.getName()).append("-").append(iMeta.getEnchants().get(enc).shortValue()).append("#");
                    x++;
                }

                String pw = item.getType().name() + "&" + item.getDurability() + "&" + item.getAmount() + "&" + item.getItemMeta().getDisplayName() + "&" + lore + "&" + enchants;
                list.add(pw);
            } else {
                list.add("NULL");
            }
        }
        return list;
    }

}
