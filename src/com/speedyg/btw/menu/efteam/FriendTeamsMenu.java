package com.speedyg.btw.menu.efteam;

import com.speedyg.btw.BasicTeamWars;
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

public class FriendTeamsMenu extends PageMenu implements Listener {

    private List<String> teams;
    private UUID team;

    public FriendTeamsMenu(BasicTeamWars main, Player p, UUID team, Menu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.teams = ABSTeam.getTeamByTeamUUID(team).getFriendTeams();
        this.totalMenu = (this.teams.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("FriendTeams.Menu_Name") != null
                            ? main.getMenuOptions().getString("FriendTeams.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Friend Teams : Page §e" + (i + 1)));
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
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("FriendTeams.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("FriendTeams.Buttons.ReadItem.Name").replaceAll("<teamName>", uTeam.getName())
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("FriendTeams.Buttons.ReadItem.Lore");
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
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    if (team.hasPermission(p, Permission.REMOVE_FRIEND_TEAM)) {
                                        ABSTeam otherTeam = ABSTeam.getTeamByTeamUUID(teamUUID);
                                        otherTeam.removeFriendTeam(team.getTeamUUID());
                                        otherTeam.saveTeam();
                                        team.removeFriendTeam(teamUUID);
                                        team.saveTeam();
                                        p.closeInventory();
                                        p.sendMessage(Messages.remove_friend_team_success);
                                    } else {
                                        p.closeInventory();
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    ShowMembersOfTeamMenu teamMenu = new ShowMembersOfTeamMenu(main, p, teamUUID, this);
                                    teamMenu.openMenu();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
