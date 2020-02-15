package com.speedyg.btw.menu.wars;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.claim.Claim;
import com.speedyg.btw.team.player.TeamPlayer;
import com.speedyg.btw.war.Siege;
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

public class SiegeClaims extends PageMenu implements Listener {

    private UUID team;
    private List<String> claims;

    SiegeClaims(BasicTeamWars main, Player p, UUID team, PageMenu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.currentMenu = 0;
        this.claims = ABSTeam.getTeamByTeamUUID(this.team).getClaims();
        this.totalMenu = ((this.claims.size() / 45) + 1);
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("SiegeClaims.Menu_Name") != null
                            ? main.getMenuOptions().getString("SiegeClaims.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Claims : Page §e" + (i + 1)));
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

    private ItemStack readItem(String claimUUID, int number) {
        Claim claim = Claim.getClaim(UUID.fromString(claimUUID));
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("SiegeClaims.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("SiegeClaims.Buttons.ReadItem.Name")
                .replaceAll("<id>", String.valueOf((number + 1)))
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("SiegeClaims.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String line : tLore)
            rLore.add(line
                    .replaceAll("<centerX>", String.valueOf(claim.getCenter().getBlockX()))
                    .replaceAll("<world>", String.valueOf(claim.getCenter().getWorld().getName()))
                    .replaceAll("<centerY>", String.valueOf(claim.getCenter().getBlockY()))
                    .replaceAll("<centerZ>", String.valueOf(claim.getCenter().getBlockZ()))
                    .replaceAll("<size>", String.valueOf(claim.getSize()))
                    .replaceAll("<claimOwner>", claim.getOwner().getName())
                    .replaceAll("&", "§"));
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnPageMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName()
                                    .equals(this.readItem(claims.get((currentMenu * 45) + e.getSlot()), (currentMenu * 45) + e.getSlot()).getItemMeta().getDisplayName())) {
                                Claim clickedClaim = Claim.getClaim(UUID.fromString(claims.get(((currentMenu * 45) + e.getSlot()))));
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (team.getTeamFile().exists()) {
                                    if (clickedClaim.getFile().exists()) {
                                        Siege siege = new Siege(TeamPlayer.getPlayer(p).getTeam(), clickedClaim);
                                        siege.startSiege();
                                    } else {
                                        p.sendMessage(Messages.claim_already_removed);
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
