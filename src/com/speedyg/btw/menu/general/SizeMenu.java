package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import org.bukkit.Bukkit;
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

public class SizeMenu extends Menu implements Listener {

    private int newSize;

    public SizeMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.newSize = 0;
        this.inv = Bukkit.createInventory(null, main.getMenuOptions().getInt("SizeMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("SizeMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("SizeMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("SizeMenu.Menu_Name")
                .replaceAll("&", "§")
                : "§8§nSize Settings Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("SizeMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("SizeMenu.Buttons.Back.Loc") - 1, this.back());
        this.inv.setItem(main.getMenuOptions().getInt("SizeMenu.Buttons.UpgradeNewSize.Loc") - 1, this.upgradeNewSize());
        this.inv.setItem(main.getMenuOptions().getInt("SizeMenu.Buttons.SaveNewSize.Loc") - 1, this.saveNewSize());
    }

    private ItemStack saveNewSize() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("SizeMenu.Buttons.SaveNewSize.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("SizeMenu.Buttons.SaveNewSize.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("SizeMenu.Buttons.SaveNewSize.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack upgradeNewSize() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("SizeMenu.Buttons.UpgradeNewSize.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("SizeMenu.Buttons.UpgradeNewSize.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("SizeMenu.Buttons.UpgradeNewSize.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§")
                    .replaceAll("<cost>", String.valueOf(newSize * main.getConfig().getInt("Team_Per_Person_Cost")))
                    .replaceAll("<teamBalance>", String.valueOf(team.getBalance()))
                    .replaceAll("<sizeDiff>", String.valueOf(newSize))
                    .replaceAll("<upgradeSize>", String.valueOf(newSize + team.getSize()))
                    .replaceAll("<teamSize>", String.valueOf(team.getSize())));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inv)) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                            ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.upgradeNewSize().getItemMeta().getDisplayName())) {
                                if (e.getClick().equals(ClickType.LEFT)) {
                                    if (newSize + 1 <= team.MAX_TEAM_SIZE)
                                        newSize++;
                                } else if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (newSize > 0)
                                        newSize--;
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (newSize + 5 <= team.MAX_TEAM_SIZE)
                                        newSize += 5;
                                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    if (newSize - 5 >= 0) {
                                        newSize -= 5;
                                    }
                                }
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.saveNewSize().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_TEAM_SIZE)) {
                                    if (team.haveEnoughMoney(newSize * main.getConfig().getInt("Team_Per_Person_Cost"))) {
                                        team.removeBalance(newSize * main.getConfig().getInt("Team_Per_Person_Cost"));
                                        team.setSize(team.getSize() + newSize);
                                        team.saveTeam();
                                        p.sendMessage(Messages.team_size_changed);
                                    } else {
                                        p.sendMessage(Messages.not_enough_money);
                                    }
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                                p.closeInventory();
                            }
                        }
                    }

                }
            }
        }
    }
}
