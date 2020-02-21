package com.speedyg.btw.menu.verification;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.LogLevel;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.VerificationMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.claim.Claim;
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

public class ClaimRemoveVerification extends VerificationMenu implements Listener {

    private Claim claim;

    public ClaimRemoveVerification(BasicTeamWars basicTeamWars, Player p, Menu returnMenu, UUID team, Claim claim) {
        super(basicTeamWars, p, returnMenu, team);
        this.inventory = Bukkit.createInventory(null, main.getMenuOptions().getInt("Verification.Claim.Menu_Size") > 0
                        ? main.getMenuOptions().getInt("Verification.Claim.Menu_Size") : 18,
                main.getMenuOptions().getString("Verification.Claim.Menu_Name") != null ?
                        main.getMenuOptions().getString("Verification.Claim.Menu_Name").replaceAll("&", "§") :
                        "§8§nClaim Remove Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
        this.claim = claim;
    }

    @Override
    public ItemStack accept() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Claim.Buttons.Accept.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Claim.Buttons.Accept.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Claim.Buttons.Accept.Lore");
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
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Claim.Buttons.Reject.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Claim.Buttons.Reject.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Claim.Buttons.Reject.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    public ItemStack info() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("Verification.Claim.Buttons.Info.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("Verification.Claim.Buttons.Info.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("Verification.Claim.Buttons.Info.Lore");
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
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Claim.Buttons.Close.Loc") - 1, this.close());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Claim.Buttons.Back.Loc") - 1, this.back());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Claim.Buttons.Accept.Loc") - 1, this.accept());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Claim.Buttons.Reject.Loc") - 1, this.reject());
        this.inventory.setItem(main.getMenuOptions().getInt("Verification.Claim.Buttons.Info.Loc") - 1, this.info());
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
                                team.removeClaim(claim.getClaimUUID().toString());
                                claim.removeClaim();
                                main.log(LogLevel.PLAYER_INFO, p.getName() + " is removed team claim! Claim UUID:" + claim.getClaimUUID().toString());
                                team.saveTeam();
                                p.closeInventory();
                                p.sendMessage(Messages.claim_removed);
                            }
                        }
                    }
                }
            }
        }
    }
}