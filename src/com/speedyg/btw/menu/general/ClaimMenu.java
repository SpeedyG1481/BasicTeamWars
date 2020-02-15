package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.claim.TeamClaimsMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
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

public class ClaimMenu extends Menu implements Listener {

    public ClaimMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = (Bukkit.createInventory(null, main.getMenuOptions().getInt("ClaimMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("ClaimMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("ClaimMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("ClaimMenu.Menu_Name").replaceAll("&", "§")
                : "§8§nClaim Options Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("ClaimMenu.Buttons.NewClaim.Loc") - 1, this.takeNewClaim());
        this.inv.setItem(main.getMenuOptions().getInt("ClaimMenu.Buttons.CurrentClaims.Loc") - 1, this.currentClaims());
        this.inv.setItem(main.getMenuOptions().getInt("ClaimMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("ClaimMenu.Buttons.Back.Loc") - 1, this.back());
        if (main.getConfig().getBoolean("Border_Show_System"))
            this.inv.setItem(main.getMenuOptions().getInt("ClaimMenu.Buttons.BorderSettings.Loc") - 1, this.borderSettings());

    }


    private ItemStack takeNewClaim() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("ClaimMenu.Buttons.NewClaim.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("ClaimMenu.Buttons.NewClaim.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("ClaimMenu.Buttons.NewClaim.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack currentClaims() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("ClaimMenu.Buttons.CurrentClaims.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("ClaimMenu.Buttons.CurrentClaims.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("ClaimMenu.Buttons.CurrentClaims.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<count>", String.valueOf(ABSTeam.getTeamByTeamUUID(this.team).getClaims().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack borderSettings() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("ClaimMenu.Buttons.BorderSettings.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("ClaimMenu.Buttons.BorderSettings.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("ClaimMenu.Buttons.BorderSettings.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<status>", String.valueOf(ABSTeam.getTeamByTeamUUID(this.team).getBorder())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    @EventHandler
    @Deprecated
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.currentClaims().getItemMeta().getDisplayName())) {
                                TeamClaimsMenu menu = new TeamClaimsMenu(main, p, this.team, this);
                                menu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.borderSettings().getItemMeta().getDisplayName())) {
                                team.setBorder(!team.getBorder());
                                team.saveTeam();
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.takeNewClaim().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.TAKE_NEW_CLAIM)) {
                                    if (team.getClaims().size() < main.getConfig().getInt("Max_Takeable_Claims")) {
                                        if (!main.getConfig().getStringList("Banned_Claim_Worlds").contains(p.getWorld().getName())) {
                                            if (team.haveEnoughMoney(main.getConfig().getInt("Team_Take_Claim_Cost"))) {
                                                if (team.getLevel() >= main.getConfig().getInt("Team_Take_Claim_Min_Level")) {
                                                    Claim claim = new Claim(team, null, p.getLocation());
                                                    if (!claim.isCollideOtherClaim()) {
                                                        team.addClaim(claim.getClaimUUID().toString());
                                                        team.removeBalance(main.getConfig().getInt("Team_Take_Claim_Cost"));
                                                        claim.saveClaim();
                                                        team.saveTeam();
                                                        if (main.getConfig().getBoolean("Show_New_Claim_Area"))
                                                            claim.showNewClaim();
                                                        p.closeInventory();
                                                        p.sendMessage(Messages.claim_take_success);
                                                    } else {
                                                        p.sendMessage(Messages.the_area_has_owner);
                                                        p.closeInventory();
                                                    }
                                                } else {
                                                    p.sendMessage(Messages.team_level_not_enough);
                                                    p.closeInventory();
                                                }
                                            } else {
                                                p.sendMessage(Messages.not_enough_money);
                                                p.closeInventory();
                                            }
                                        } else {
                                            p.sendMessage(Messages.claim_banned_world);
                                            p.closeInventory();
                                        }
                                    } else {
                                        p.sendMessage(Messages.your_team_has_been_reached_max_claims_size);
                                        p.closeInventory();
                                    }
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
