package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import com.speedyg.btw.team.player.TeamPlayer;
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

public class InviteCheckMenu extends PageMenu implements Listener {

    private List<String> requests;
    private UUID team;

    public InviteCheckMenu(BasicTeamWars main, Player p, Menu returnMenu, UUID team) {
        super(main, p, returnMenu);
        this.team = team;
        this.requests = ABSTeam.getTeamByTeamUUID(this.team).getInvites();
        this.totalMenu = (this.requests.size() / 45) + 1;
        this.currentMenu = 0;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("InviteCheckMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("InviteCheckMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Team List : Page §e" + (i + 1)));
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        if (currentMenu != 0)
            this.inv.get(currentMenu).setItem(45, this.previous());
        else
            this.inv.get(currentMenu).setItem(45, this.back());
        if (currentMenu < this.totalMenu - 1)
            this.inv.get(currentMenu).setItem(53, this.next());
        else
            this.inv.get(currentMenu).setItem(53, this.close());

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.requests.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(requests.get(queue)));
            queue++;
        }
    }

    private ItemStack readItem(String playerUUID) {
        TeamPlayer player = TeamPlayer.getPlayer(UUID.fromString(playerUUID));
        ItemStack item = Skull.getPlayerSkull(player.getPlayer().getName());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("InviteCheckMenu.Buttons.ReadItem.Name")
                .replaceAll("&", "§").replaceAll("<pName>", player.getPlayer().getName()));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("InviteCheckMenu.Buttons.ReadItem.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<KD>", player.getPlayerTotalDeaths() != 0
                    ? ((float) player.getPlayerTotalKill() / (float) player.getPlayerTotalDeaths() * 100) + "%"
                    : "0%")
                    .replaceAll("<pDeaths>", String.valueOf(player.getPlayerTotalDeaths()))
                    .replaceAll("<pKills>", String.valueOf(player.getPlayerTotalKill()))
                    .replaceAll("&", "§")
                    .replaceAll("<currentTeam>", player.getTeam() != null ?
                            player.getTeam().getName().replaceAll("&", "§") :
                            "NULL"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }


    @EventHandler
    private void inventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inv.get(currentMenu))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                            ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.previous().getItemMeta().getDisplayName())) {
                                this.currentMenu--;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.next().getItemMeta().getDisplayName())) {
                                this.currentMenu++;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.readItem(
                                    this.requests.get((currentMenu * 45) + e.getSlot())).getItemMeta().getDisplayName())) {
                                if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (team.hasPermission(p, Permission.ACCEPT_TEAMJOIN_REQUESTS)) {
                                        if (System.getPlayerTeamFile(UUID.fromString(this.requests.get((currentMenu * 45) + e.getSlot()))) == null) {
                                            team.addMember(UUID.fromString(this.requests.get((currentMenu * 45) + e.getSlot())));
                                            team.removeWaitingInvite(TeamPlayer.getPlayer(UUID.fromString(
                                                    this.requests.get(((currentMenu * 45) + e.getSlot())))));
                                            returnMenu.openMenu();
                                        } else {
                                            p.closeInventory();
                                            team.removeWaitingInvite(TeamPlayer.getPlayer(UUID.fromString(
                                                    this.requests.get(((currentMenu * 45) + e.getSlot())))));
                                            p.sendMessage(Messages.player_have_team);
                                        }
                                        team.saveTeam();
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.hasPermission(p, Permission.REJECT_TEAMJOIN_REQUESTS)) {
                                        team.removeWaitingInvite(TeamPlayer.getPlayer(UUID.fromString(this.requests.get((currentMenu * 45) + e.getSlot()))));
                                        team.saveTeam();
                                        returnMenu.openMenu();
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
}
