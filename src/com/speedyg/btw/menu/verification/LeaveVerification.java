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

public class LeaveVerification extends VerificationMenu implements Listener {

    public LeaveVerification(BasicTeamWars basicTeamWars, Player p, Menu returnMenu, UUID team) {
        super(basicTeamWars, p, returnMenu, team);
        this.inventory = Bukkit.createInventory(null, main.getMenuOptions().getInt("Verification.Leave.Menu_Size") > 0
                        ? main.getMenuOptions().getInt("Verification.Leave.Menu_Size") : 18,
                main.getMenuOptions().getString("Verification.Leave.Menu_Name") != null ?
                        main.getMenuOptions().getString("Verification.Leave.Menu_Name").replaceAll("&", "§") :
                        "§8§nLeave Confirmation");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    public ItemStack accept() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Leave.Buttons.Accept.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Leave.Buttons.Accept.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Leave.Buttons.Accept.Lore");
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
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Leave.Buttons.Reject.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Leave.Buttons.Reject.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Leave.Buttons.Reject.Lore");
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
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Leave.Buttons.Close.Loc") - 1, this.close());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Leave.Buttons.Back.Loc") - 1, this.back());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Leave.Buttons.Accept.Loc") - 1, this.accept());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Leave.Buttons.Reject.Loc") - 1, this.reject());
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
                                team.removeMember(p.getUniqueId());
                                team.saveTeam();
                                main.log(LogLevel.PLAYER_INFO, p.getName() + " is leaved own team! Team UUID: " + team.getTeamUUID().toString());
                                p.closeInventory();
                                p.sendMessage(Messages.leave_team_success);
                            }
                        }
                    }
                }
            }
        }
    }
}
