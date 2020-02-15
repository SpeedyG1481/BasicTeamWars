package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.kits.TeamSelectKitMenu;
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

public class WarOptionsMenu extends Menu implements Listener {

    public WarOptionsMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = (Bukkit.createInventory(null, main.getMenuOptions().getInt("WarOptionsMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("WarOptionsMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("WarOptionsMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("WarOptionsMenu.Menu_Name").replaceAll("&", "§")
                : "§8§nWar Options Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }


    private ItemStack warStatusChange() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        String status = team.isJoinedWar() ? main.getMenuOptions().getString("WarOptionsMenu.Status.Joined") : main.getMenuOptions().getString("WarOptionsMenu.Status.Not-Joined");
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarOptionsMenu.Buttons.StatusChangeButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("WarOptionsMenu.Buttons.StatusChangeButton.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("WarOptionsMenu.Buttons.StatusChangeButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§").replaceAll("<status>", status.replaceAll("&", "§")));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack kitOptions() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarOptionsMenu.Buttons.KitOptions.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("WarOptionsMenu.Buttons.KitOptions.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("WarOptionsMenu.Buttons.KitOptions.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("WarOptionsMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("WarOptionsMenu.Buttons.StatusChangeButton.Loc") - 1, this.warStatusChange());
        this.inv.setItem(main.getMenuOptions().getInt("WarOptionsMenu.Buttons.KitOptions.Loc") - 1, this.kitOptions());
        this.inv.setItem(main.getMenuOptions().getInt("WarOptionsMenu.Buttons.Back.Loc") - 1, this.back());

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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.warStatusChange().getItemMeta().getDisplayName())) {
                                team.setWarStatus(!team.isJoinedWar());
                                team.saveTeam();
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.kitOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.SELECT_WAR_KITS)) {
                                    TeamSelectKitMenu menu = new TeamSelectKitMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.closeInventory();
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
