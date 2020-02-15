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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnSettingsMenu extends Menu implements Listener {

    public SpawnSettingsMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = Bukkit.createInventory(null, main.getMenuOptions().getInt("SpawnSettingsMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("SpawnSettingsMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("SpawnSettingsMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("SpawnSettingsMenu.Menu_Name")
                .replaceAll("&", "§")
                : "§8§nSpawn Settings Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("SpawnSettingsMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("SpawnSettingsMenu.Buttons.Back.Loc") - 1, this.back());
        this.inv.setItem(main.getMenuOptions().getInt("SpawnSettingsMenu.Buttons.SetMemberLocation.Loc") - 1, this.setMemberSpawnLocation());
        this.inv.setItem(main.getMenuOptions().getInt("SpawnSettingsMenu.Buttons.SetModLocation.Loc") - 1, this.setModSpawnLocation());
        this.inv.setItem(main.getMenuOptions().getInt("SpawnSettingsMenu.Buttons.SetAdminLocation.Loc") - 1, this.setAdminSpawnLocation());

    }

    private ItemStack setMemberSpawnLocation() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("SpawnSettingsMenu.Buttons.SetMemberLocation.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("SpawnSettingsMenu.Buttons.SetMemberLocation.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("SpawnSettingsMenu.Buttons.SetMemberLocation.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§").replaceAll("<spawnLocation>", team.getMemberSpawnLocation() != null
                    ? team.getMemberSpawnLocation().getWorld().getName() +
                    "," + team.getMemberSpawnLocation().getBlockX() +
                    "," + team.getMemberSpawnLocation().getBlockY() +
                    "," + team.getMemberSpawnLocation().getBlockZ() :
                    "NULL"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setModSpawnLocation() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("SpawnSettingsMenu.Buttons.SetModLocation.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("SpawnSettingsMenu.Buttons.SetModLocation.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("SpawnSettingsMenu.Buttons.SetModLocation.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§").replaceAll("<spawnLocation>", team.getModSpawnLocation() != null
                    ? team.getModSpawnLocation().getWorld().getName() +
                    "," + team.getModSpawnLocation().getBlockX() +
                    "," + team.getModSpawnLocation().getBlockY() +
                    "," + team.getModSpawnLocation().getBlockZ() :
                    "NULL"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setAdminSpawnLocation() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("SpawnSettingsMenu.Buttons.SetAdminLocation.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("SpawnSettingsMenu.Buttons.SetAdminLocation.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("SpawnSettingsMenu.Buttons.SetAdminLocation.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§").replaceAll("<spawnLocation>", team.getAdminSpawnLocation() != null
                    ? team.getAdminSpawnLocation().getWorld().getName() +
                    "," + team.getAdminSpawnLocation().getBlockX() +
                    "," + team.getAdminSpawnLocation().getBlockY() +
                    "," + team.getAdminSpawnLocation().getBlockZ() :
                    "NULL"));
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(
                                    this.setMemberSpawnLocation().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_SPAWN_POINT_MEMBER)) {
                                    team.setMemberSpawnLocation(p.getLocation());
                                    team.saveTeam();
                                    this.openMenu();
                                    p.sendMessage(Messages.member_spawn_location_changed);
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(
                                    this.setModSpawnLocation().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_SPAWN_POINT_MODERATOR)) {
                                    team.setModSpawnLocation(p.getLocation());
                                    team.saveTeam();
                                    this.openMenu();
                                    p.sendMessage(Messages.mod_spawn_location_changed);
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(
                                    this.setAdminSpawnLocation().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_SPAWN_POINT_ADMIN)) {
                                    team.setAdminSpawnLocation(p.getLocation());
                                    team.saveTeam();
                                    this.openMenu();
                                    p.sendMessage(Messages.admin_spawn_location_changed);
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
