package com.speedyg.btw.menu.efteam.clickedteams;

import com.speedyg.btw.BasicTeamWars;
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

public class ShowMembersOfTeamMenu extends PageMenu implements Listener {

    private List<String> playerList;
    private UUID team;

    public ShowMembersOfTeamMenu(BasicTeamWars main, Player p, UUID team, PageMenu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.playerList = ABSTeam.getTeamByTeamUUID(team).getMembers();
        this.totalMenu = (this.playerList.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("ShowMembersMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("ShowMembersMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Team Members : Page §e" + (i + 1)));
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
            if (queue >= this.playerList.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(playerList.get(queue)));
            queue++;
        }
    }

    private ItemStack readItem(String playerUUID) {
        TeamPlayer player = TeamPlayer.getPlayer(UUID.fromString(playerUUID));
        ItemStack item = Skull.getPlayerSkull(player.getPlayer().getName());
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("ShowMembersMenu.Buttons.ReadItem.Name")
                .replaceAll("&", "§").replaceAll("<playerName>", player.getPlayer().getName()));
        List<String> tLore = main.getMenuOptions().getStringList("ShowMembersMenu.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String add : tLore)
            rLore.add(add.replaceAll("<KD>", String.valueOf((((float) player.getPlayerTotalKill() / (float) player.getPlayerTotalDeaths()) * 100)))
                    .replaceAll("<playerKills>", String.valueOf(player.getPlayerTotalKill()))
                    .replaceAll("&", "§")
                    .replaceAll("<playerDeaths>", String.valueOf(player.getPlayerTotalDeaths())));
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
                                returnPageMenu.openMenu();
                            }
                        }
                    }
                }
            }
        }
    }

}
