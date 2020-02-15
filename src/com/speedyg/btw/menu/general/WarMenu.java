package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.wars.SetWarrior;
import com.speedyg.btw.menu.wars.SiegeTeams;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarMenu extends Menu implements Listener {


    public WarMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = (Bukkit.createInventory(null, main.getMenuOptions().getInt("WarMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("WarMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("WarMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("WarMenu.Menu_Name").replaceAll("&", "§")
                : "§8§nWar Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }


    private ItemStack newSiege() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarMenu.Buttons.NewSiege.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("WarMenu.Buttons.NewSiege.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("WarMenu.Buttons.NewSiege.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack weeklyWarMenu() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarMenu.Buttons.WeeklyWar.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("WarMenu.Buttons.WeeklyWar.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("WarMenu.Buttons.WeeklyWar.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setWarriors() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarMenu.Buttons.SetWarriors.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("WarMenu.Buttons.SetWarriors.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("WarMenu.Buttons.SetWarriors.Lore");
        for (String add : tLore)
            rLore.add(add
                    .replaceAll("<size>", String.valueOf(team.getWarriors().size()))
                    .replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void loadItems() {
        if (main.getConfig().getBoolean("Siege_System"))
            this.inv.setItem(main.getMenuOptions().getInt("WarMenu.Buttons.NewSiege.Loc") - 1, this.newSiege());
        this.inv.setItem(main.getMenuOptions().getInt("WarMenu.Buttons.WeeklyWar.Loc") - 1, this.weeklyWarMenu());
        this.inv.setItem(main.getMenuOptions().getInt("WarMenu.Buttons.SetWarriors.Loc") - 1, this.setWarriors());
        this.inv.setItem(main.getMenuOptions().getInt("WarMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("WarMenu.Buttons.Back.Loc") - 1, this.back());
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.newSiege().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.START_NEW_SIEGE)) {
                                    SiegeTeams menu = new SiegeTeams(main, p, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.weeklyWarMenu().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.WAR_OPTIONS)) {
                                    WarOptionsMenu menu = new WarOptionsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.setWarriors().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.SET_WARRIORS_OF_TEAM)) {
                                    SetWarrior menu = new SetWarrior(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
