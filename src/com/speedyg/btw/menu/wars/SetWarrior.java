package com.speedyg.btw.menu.wars;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.player.TeamPlayer;
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

public class SetWarrior extends PageMenu implements Listener {

    private UUID team;
    private List<String> members;

    public SetWarrior(BasicTeamWars main, Player p, UUID team, Menu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.members = ABSTeam.getTeamByTeamUUID(this.team).getMembers();
        this.totalMenu = ((this.members.size() / 45) + 1);
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("SetWarriorsMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("SetWarriorsMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Members : Page §e" + (i + 1)));
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        this.members = team.getMembers();
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
            if (queue >= this.members.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(UUID.fromString(members.get(queue))));
            queue++;
        }
    }

    private ItemStack readItem(UUID teamPlayer) {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getPlayerSkull(TeamPlayer.getPlayer(teamPlayer).getPlayer().getName());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("SetWarriorsMenu.Buttons.ReadItem.Name")
                .replaceAll("&", "§")
                .replaceAll("<pName>", TeamPlayer.getPlayer(teamPlayer).getPlayer().getName()));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("SetWarriorsMenu.Buttons.ReadItem.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<status>", String.valueOf(team.getWarriors().contains(teamPlayer.toString()))).replaceAll("&", "§"));
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
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.readItem(UUID.fromString(this.members.get((currentMenu * 45) + e.getSlot()))).getItemMeta().getDisplayName())) {
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (team.getTeamFile().exists()) {
                                    if (team.getWarriors().contains(this.members.get((currentMenu * 45) + e.getSlot())))
                                        team.removeWarrior(this.members.get((currentMenu * 45) + e.getSlot()));
                                    else
                                        team.addWarrior(this.members.get((currentMenu * 45) + e.getSlot()));
                                    team.saveTeam();
                                } else {
                                    p.sendMessage(Messages.team_already_removed);
                                }
                                this.openMenu();
                            }
                        }
                    }
                }
            }
        }
    }

}
