package com.speedyg.btw.menu;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.OrderType;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ListTeamsMenu extends PageMenu implements Listener {

    private List<String> teams;

    public ListTeamsMenu(BasicTeamWars main, Player p, Menu returnMenu) {
        super(main, p, returnMenu);
        this.teams = System.getAllTeams(0, OrderType.DATE);
        this.totalMenu = (teams.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("TeamListMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("TeamListMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Team List : Page §e" + (i + 1)));
        }
        this.currentMenu = 0;
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    private ItemStack orderByName() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByName.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByName.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("TeamListMenu.Buttons.OrderByName.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack orderByDate() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByDate.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByDate.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("TeamListMenu.Buttons.OrderByDate.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack orderByWins() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByWins.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByWins.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("TeamListMenu.Buttons.OrderByWins.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack orderByKills() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByKills.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByKills.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("TeamListMenu.Buttons.OrderByKills.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack orderByTeamMaker() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByTeamMaker.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("TeamListMenu.Buttons.OrderByTeamMaker.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("TeamListMenu.Buttons.OrderByTeamMaker.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void loadItems() {
        if (currentMenu != 0)
            this.inv.get(currentMenu).setItem(45, this.previous());
        if (currentMenu < this.totalMenu - 1) {
            this.inv.get(currentMenu).setItem(53, this.next());
        } else {
            this.inv.get(currentMenu).setItem(53, this.close());
        }

        this.inv.get(currentMenu).setItem(47, this.orderByDate());
        this.inv.get(currentMenu).setItem(48, this.orderByKills());
        this.inv.get(currentMenu).setItem(49, this.orderByName());
        this.inv.get(currentMenu).setItem(50, this.orderByTeamMaker());
        this.inv.get(currentMenu).setItem(51, this.orderByWins());

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.teams.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(teams.get(queue), queue));
            queue++;
        }
    }

    private ItemStack readItem(String teamUUID, int number) {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(UUID.fromString(teamUUID));
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamListMenu.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("TeamListMenu.Buttons.ReadItem.Name")
                .replaceAll("<number>", String.valueOf((number + 1)))
                .replaceAll("<name>", team.getName().replaceAll("&", "§"))
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("TeamListMenu.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String line : tLore)
            rLore.add(line.replaceAll("<name>", team.getName().replaceAll("&", "§"))
                    .replaceAll("%percentage%", (((float) team.getMembers().size() / (float) team.getSize()) * 100) + "%")
                    .replaceAll("<members>", String.valueOf(team.getMembers().size()))
                    .replaceAll("<owner>", team.getTeamMaker().getName())
                    .replaceAll("<size>", String.valueOf(team.getSize()))
                    .replaceAll("<balance>", String.valueOf(team.getBalance()))
                    .replaceAll("<cDate>", new Date(team.getMakeDate()).toString())
                    .replaceAll("<tKills>", String.valueOf(team.getTotalKills()))
                    .replaceAll("<level>", String.valueOf(team.getLevel()))
                    .replaceAll("<power>", String.valueOf(team.getPower()))
                    .replaceAll("<tWins>", String.valueOf(team.getTotalVSWins())).replaceAll("&", "§"));
        iMeta.setLore(rLore);
        item.setItemMeta(iMeta);
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
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.next().getItemMeta().getDisplayName())) {
                                currentMenu++;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.previous().getItemMeta().getDisplayName())) {
                                currentMenu--;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.orderByDate().getItemMeta().getDisplayName())) {
                                this.teams = System.getAllTeams(0, OrderType.DATE);
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.orderByKills().getItemMeta().getDisplayName())) {
                                this.teams = System.getAllTeams(0, OrderType.KILLS);
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.orderByName().getItemMeta().getDisplayName())) {
                                this.teams = System.getAllTeams(0, OrderType.TEAM_NAME);
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.orderByTeamMaker().getItemMeta().getDisplayName())) {
                                this.teams = System.getAllTeams(0, OrderType.TEAM_MAKER_NAME);
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.orderByWins().getItemMeta().getDisplayName())) {
                                this.teams = System.getAllTeams(0, OrderType.WINS);
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName()
                                    .equals(this.readItem(teams.get((currentMenu * 45) + e.getSlot()), (currentMenu * 45) + e.getSlot()).getItemMeta().getDisplayName())) {
                                ABSTeam clickedTeam = ABSTeam.getTeamByTeamUUID(UUID.fromString(teams.get(((currentMenu * 45) + e.getSlot()))));
                                if (clickedTeam.getTeamFile().exists()) {
                                    if (!clickedTeam.getMembers().contains(p.getUniqueId().toString())
                                            && !clickedTeam.getTeamMaker().getUniqueId().toString().equals(p.getUniqueId().toString())) {
                                        if (System.getPlayerTeamFile(p) == null) {
                                            if (!clickedTeam.getInvites().contains(p.getUniqueId().toString())) {
                                                if (!clickedTeam.isFull()) {
                                                    clickedTeam.addInvite(p.getUniqueId());
                                                    clickedTeam.saveTeam();
                                                    p.sendMessage(Messages.invite_sanded);
                                                } else {
                                                    p.sendMessage(Messages.team_size_full);
                                                }
                                            } else {
                                                p.sendMessage(Messages.you_already_send_invite);
                                            }
                                        } else {
                                            p.sendMessage(Messages.have_team_message);
                                        }
                                    } else {
                                        p.sendMessage(Messages.already_your_team);
                                    }
                                } else {
                                    p.sendMessage(Messages.team_already_removed);
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
