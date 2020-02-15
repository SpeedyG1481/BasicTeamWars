package com.speedyg.btw.menu.efteam;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.menu.efteam.clickedteams.ShowMembersOfTeamMenu;
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

public class NeutralTeamsMenu extends PageMenu implements Listener {

    private List<String> teams;
    private UUID team;

    public NeutralTeamsMenu(BasicTeamWars main, Player p, UUID team, Menu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        teams = System.getAllTeams();
        for (String s : ABSTeam.getTeamByTeamUUID(team).getEnemyTeams())
            teams.remove(s);
        for (String s : ABSTeam.getTeamByTeamUUID(team).getFriendTeams())
            teams.remove(s);
        teams.remove(team.toString());
        this.totalMenu = (this.teams.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("NeutralTeams.Menu_Name") != null
                            ? main.getMenuOptions().getString("NeutralTeams.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Neutral Teams : Page §e" + (i + 1)));
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        if (currentMenu != 0)
            this.inv.get(currentMenu).setItem(45, this.previous());
        else
            this.inv.get(currentMenu).setItem(45, this.back());
        if (currentMenu < this.totalMenu - 1) {
            this.inv.get(currentMenu).setItem(53, this.next());
        } else {
            this.inv.get(currentMenu).setItem(53, this.close());
        }

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.teams.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(teams.get(queue)));
            queue++;
        }
    }


    private ItemStack readItem(String teamUUIDSTR) {
        ABSTeam uTeam = ABSTeam.getTeamByTeamUUID(UUID.fromString(teamUUIDSTR));
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("NeutralTeams.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("NeutralTeams.Buttons.ReadItem.Name").replaceAll("<teamName>", uTeam.getName())
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("NeutralTeams.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String add : tLore)
            rLore.add(add.replaceAll("<teamKills>", String.valueOf(uTeam.getTotalKills()))
                    .replaceAll("<teamWins>", String.valueOf(uTeam.getTotalVSWins()))
                    .replaceAll("<teamBalance>", String.valueOf(uTeam.getBalance()))
                    .replaceAll("<teamLevel>", String.valueOf(uTeam.getLevel()))
                    .replaceAll("<teamPower>", String.valueOf(uTeam.getPower()))
                    .replaceAll("<teamMembers>", String.valueOf(uTeam.getMembers().size()))
                    .replaceAll("<teamOwner>", uTeam.getTeamMaker().getName())
                    .replaceAll("<teamSize>", String.valueOf(uTeam.getSize()))
                    .replaceAll("&", "§"));
        iMeta.setLore(rLore);
        item.setItemMeta(iMeta);
        return item;
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inv.get(currentMenu))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.previous().getItemMeta().getDisplayName())) {
                                this.currentMenu--;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.next().getItemMeta().getDisplayName())) {
                                this.currentMenu++;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.readItem(this.teams.get((currentMenu * 45) + e.getSlot())).getItemMeta().getDisplayName())) {
                                UUID teamUUID = UUID.fromString(this.teams.get((currentMenu * 45) + e.getSlot()));
                                ABSTeam otherTeam = ABSTeam.getTeamByTeamUUID(teamUUID);
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (e.getClick().equals(ClickType.RIGHT)) {
                                    // Add Friend Team PERM CHECK
                                    if (team.hasPermission(p, Permission.SEND_FRIEND_TEAM_REQUEST)) {
                                        if (!team.getEnemyTeams().contains(teamUUID.toString())
                                                && !otherTeam.getEnemyTeams().contains(team.getTeamUUID().toString())) {
                                            if (!team.getFriendTeams().contains(teamUUID.toString())
                                                    && !otherTeam.getFriendTeams().contains(team.getTeamUUID().toString())) {
                                                if (!team.getTeamRequests().contains(teamUUID.toString())
                                                        && !otherTeam.getTeamRequests().contains(team.getTeamUUID().toString())) {
                                                    otherTeam.addTeamRequest(team.getTeamUUID());
                                                    otherTeam.saveTeam();
                                                    p.sendMessage(Messages.team_friend_request_sanded);
                                                    p.closeInventory();
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(Messages.already_have_request);
                                                }
                                            } else {
                                                p.sendMessage(Messages.already_friend_team);
                                                p.closeInventory();
                                            }
                                        } else {
                                            p.sendMessage(Messages.already_enemy_team);
                                            p.closeInventory();
                                        }
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.hasPermission(p, Permission.ADD_ENEMY_TEAM)) {
                                        if (!team.getEnemyTeams().contains(teamUUID.toString())) {
                                            if (!team.getFriendTeams().contains(teamUUID.toString())
                                                    && !otherTeam.getFriendTeams().contains(team.getTeamUUID().toString())) {
                                                if (!team.getTeamRequests().contains(teamUUID.toString())
                                                        && !otherTeam.getTeamRequests().contains(team.getTeamUUID().toString())) {
                                                    team.addEnemyTeam(teamUUID);
                                                    //otherTeam.addEnemyTeam(team.getTeamUUID());
                                                    team.saveTeam();
                                                    //otherTeam.saveTeam();
                                                    p.sendMessage(Messages.add_enemy_team_success);
                                                    p.closeInventory();
                                                } else {
                                                    p.closeInventory();
                                                    p.sendMessage(Messages.already_have_request);
                                                }
                                            } else {
                                                p.sendMessage(Messages.already_friend_team);
                                                p.closeInventory();
                                            }
                                        } else {
                                            p.sendMessage(Messages.already_enemy_team);
                                            p.closeInventory();
                                        }
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT) || e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    ShowMembersOfTeamMenu menu = new ShowMembersOfTeamMenu(main, p, teamUUID, this);
                                    menu.openMenu();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}