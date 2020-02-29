package com.speedyg.btw.menu.verification;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.LogLevel;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.VerificationMenu;
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
import java.util.List;
import java.util.UUID;

public class AbandonVerification extends VerificationMenu implements Listener {

    public AbandonVerification(BasicTeamWars basicTeamWars, Player p, Menu returnMenu, UUID team) {
        super(basicTeamWars, p, returnMenu, team);
        this.inventory = Bukkit.createInventory(null, main.getMenuOptions().getInt("Verification.Abandon.Menu_Size") > 0
                        ? main.getMenuOptions().getInt("Verification.Abandon.Menu_Size") : 18,
                main.getMenuOptions().getString("Verification.Abandon.Menu_Name") != null ?
                        main.getMenuOptions().getString("Verification.Abandon.Menu_Name").replaceAll("&", "§") :
                        "§8§nTeam Remove Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    public ItemStack accept() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Abandon.Buttons.Accept.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Abandon.Buttons.Accept.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Abandon.Buttons.Accept.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    @Override
    public ItemStack reject() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Abandon.Buttons.Reject.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Abandon.Buttons.Reject.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Abandon.Buttons.Reject.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    @Override
    public void loadItems() {
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Abandon.Buttons.Close.Loc") - 1, this.close());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Abandon.Buttons.Back.Loc") - 1, this.back());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Abandon.Buttons.Accept.Loc") - 1, this.accept());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Abandon.Buttons.Reject.Loc") - 1, this.reject());
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inventory)) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {

                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                this.p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.reject().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.accept().getItemMeta().getDisplayName())) {
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (main.getTimer() != null) {
                                    if (!main.getTimer().getWar().getTeamList().contains(team.getTeamUUID())) {
                                        team.removeTeam();
                                        main.log(LogLevel.INFO, "Team has been removed!.. Removed Team UUID: " + team.getTeamUUID().toString());
                                        p.sendMessage(Messages.team_deleted);
                                    } else {
                                        p.sendMessage(Messages.team_remove_abandoned);
                                    }
                                } else {
                                    team.removeTeam();
                                    main.log(LogLevel.INFO, "Team has been removed!.. Removed Team UUID: " + team.getTeamUUID().toString());
                                    p.sendMessage(Messages.team_deleted);
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
