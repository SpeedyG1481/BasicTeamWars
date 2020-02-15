package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.efteam.EFRelRequestsMenu;
import com.speedyg.btw.menu.efteam.EnemyTeamsMenu;
import com.speedyg.btw.menu.efteam.FriendTeamsMenu;
import com.speedyg.btw.menu.efteam.NeutralTeamsMenu;
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

public class EFTeamControlMenu extends Menu implements Listener {

    public EFTeamControlMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = Bukkit.createInventory(null, main.getMenuOptions().getInt("EFTeamControl.Menu_Size") > 0
                ? main.getMenuOptions().getInt("EFTeamControl.Menu_Size")
                : 54, main.getMenuOptions().getString("EFTeamControl.Menu_Name")
                != null ? main.getMenuOptions().getString("EFTeamControl.Menu_Name")
                .replaceAll("&", "§")
                : "§8§nEF Team Control Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }


    private ItemStack enemyTeams() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("EFTeamControl.Buttons.EnemyTeams.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("EFTeamControl.Buttons.EnemyTeams.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("EFTeamControl.Buttons.EnemyTeams.Lore");
        for (String lore : tLore)
            rLore.add(lore.replaceAll("&", "§"));
        imeta.setLore(rLore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack friendTeams() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("EFTeamControl.Buttons.FriendTeams.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("EFTeamControl.Buttons.FriendTeams.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("EFTeamControl.Buttons.FriendTeams.Lore");
        for (String lore : tLore)
            rLore.add(lore.replaceAll("&", "§"));
        imeta.setLore(rLore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack neutralTeams() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("EFTeamControl.Buttons.NeutralTeams.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("EFTeamControl.Buttons.NeutralTeams.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("EFTeamControl.Buttons.NeutralTeams.Lore");
        for (String lore : tLore)
            rLore.add(lore.replaceAll("&", "§"));
        imeta.setLore(rLore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack requests() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("EFTeamControl.Buttons.RelationRequests.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("EFTeamControl.Buttons.RelationRequests.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("EFTeamControl.Buttons.RelationRequests.Lore");
        for (String lore : tLore)
            rLore.add(lore.replaceAll("&", "§"));
        imeta.setLore(rLore);
        item.setItemMeta(imeta);
        return item;
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("EFTeamControl.Buttons.FriendTeams.Loc") - 1, this.friendTeams());
        this.inv.setItem(main.getMenuOptions().getInt("EFTeamControl.Buttons.EnemyTeams.Loc") - 1, this.enemyTeams());
        this.inv.setItem(main.getMenuOptions().getInt("EFTeamControl.Buttons.RelationRequests.Loc") - 1, this.requests());
        this.inv.setItem(main.getMenuOptions().getInt("EFTeamControl.Buttons.NeutralTeams.Loc") - 1, this.neutralTeams());
        this.inv.setItem(main.getMenuOptions().getInt("EFTeamControl.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("EFTeamControl.Buttons.Back.Loc") - 1, this.back());
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
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.friendTeams().getItemMeta().getDisplayName())) {
                                // Open Friend Teams
                                if (team.hasPermission(p, Permission.CHECK_FRIEND_TEAMS)) {
                                    FriendTeamsMenu menu = new FriendTeamsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.enemyTeams().getItemMeta().getDisplayName())) {
                                // Open Enemy Teams
                                if (team.hasPermission(p, Permission.CHECK_ENEMY_TEAMS)) {
                                    EnemyTeamsMenu menu = new EnemyTeamsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.requests().getItemMeta().getDisplayName())) {
                                // Requests
                                if (team.hasPermission(p, Permission.CHECK_AND_CONTROL_TEAM_REL_REQUESTS)) {
                                    EFRelRequestsMenu menu = new EFRelRequestsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.neutralTeams().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHECK_NEUTRAL_TEAMS)) {
                                    NeutralTeamsMenu menu = new NeutralTeamsMenu(main, p, this.team, this);
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
