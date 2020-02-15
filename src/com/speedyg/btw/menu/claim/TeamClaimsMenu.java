package com.speedyg.btw.menu.claim;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.TeamMenu;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.menu.verification.ClaimRemoveVerification;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import com.speedyg.btw.team.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class TeamClaimsMenu extends PageMenu implements Listener {

    private List<String> claims;
    private UUID team;

    public TeamClaimsMenu(BasicTeamWars main, Player p, UUID team, Menu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.claims = ABSTeam.getTeamByTeamUUID(team).getClaims();
        this.totalMenu = (this.claims.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("TeamClaimsMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("TeamClaimsMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Team Claims : Page §e" + (i + 1)));
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
            if (queue >= this.claims.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(claims.get(queue), queue));
            queue++;
        }
    }


    private ItemStack readItem(String teamUUIDSTR, int queue) {
        Claim claim = Claim.getClaim(UUID.fromString(teamUUIDSTR));
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamClaimsMenu.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("TeamClaimsMenu.Buttons.ReadItem.Name").replaceAll("<claimNo>", String.valueOf(queue + 1))
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("TeamClaimsMenu.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String add : tLore)
            rLore.add(add.replaceAll("<x>", String.valueOf(claim.getCenter().getBlockX()))
                    .replaceAll("<y>", String.valueOf(claim.getCenter().getBlockY()))
                    .replaceAll("<z>", String.valueOf(claim.getCenter().getBlockZ()))
                    .replaceAll("<world>", claim.getCenter().getWorld().getName())
                    .replaceAll("<size>", String.valueOf(claim.getSize()))
                    .replaceAll("<totalBlocks>", String.valueOf(claim.getTotalBlocksSize()))
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                    this.readItem(this.claims.get((currentMenu * 45) + e.getSlot()),
                                            (currentMenu * 45) + e.getSlot()).getItemMeta().getDisplayName())) {
                                UUID claimUUID = UUID.fromString(this.claims.get((currentMenu * 45) + e.getSlot()));
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (team.hasPermission(p, Permission.TELEPORT_CLAIMS)) {
                                        Location tpLoc = Claim.getClaim(claimUUID).getCenter();
                                        tpLoc.setY(tpLoc.getWorld().getHighestBlockYAt(tpLoc.getBlockX(), tpLoc.getBlockZ()));
                                        p.teleport(tpLoc);
                                        p.sendMessage(Messages.teleport_success_to_claim);
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                    p.closeInventory();
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (team.hasPermission(p, Permission.REMOVE_CLAIM)) {
                                        ClaimRemoveVerification menu = new ClaimRemoveVerification(main, p, new TeamMenu(main, p, this.team), this.team, Claim.getClaim(claimUUID));
                                        menu.openMenu();
                                    } else {
                                        p.closeInventory();
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.hasPermission(p, Permission.OPEN_AUTO_BUILD_CLAIM_MENU)) {
                                        AutoBuildMenu menu = new AutoBuildMenu(main, p, this, team.getTeamUUID(), Claim.getClaim(claimUUID));
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
}