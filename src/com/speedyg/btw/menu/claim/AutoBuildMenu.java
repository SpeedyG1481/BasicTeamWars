package com.speedyg.btw.menu.claim;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import com.speedyg.btw.team.claim.Claim;
import com.speedyg.btw.team.schematic.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AutoBuildMenu extends PageMenu implements Listener {


    private List<Schematic> schematicList;
    private UUID team;
    private Claim claim;
    private int rotateValue;

    public AutoBuildMenu(BasicTeamWars main, Player p, PageMenu returnMenu, UUID team, Claim claim) {
        super(main, p, returnMenu);
        this.team = team;
        this.claim = claim;
        this.schematicList = new ArrayList<>();
        for (File f : System.getAllSchematics()) {
            Schematic schematic = new Schematic(f);
            if (schematic.isSuitSchemaVersion())
                this.schematicList.add(schematic);
        }

        this.totalMenu = (schematicList.size() / 45) + 1;
        for (
                int i = 0;
                i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("AutoBuildMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("AutoBuildMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Schematics : Page §e" + (i + 1)));
        }
        this.currentMenu = 0;
        Bukkit.getServer().

                getPluginManager().

                registerEvents(this, main);

    }


    private ItemStack readItem(Schematic schematic, int number) {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("AutoBuildMenu.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("AutoBuildMenu.Buttons.ReadItem.Name")
                .replaceAll("<schematicNo>", String.valueOf((number + 1)))
                .replaceAll("<sName>", schematic.getName())
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("AutoBuildMenu.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String line : tLore)
            rLore.add(line.replaceAll("<rotate>", String.valueOf(rotateValue))
                    .replaceAll("<suitable>", String.valueOf(schematic.isSuitClaim(claim)))
                    .replaceAll("<price>", String.valueOf(schematic.getCost()))
                    .replaceAll("<min-level>", String.valueOf(schematic.getMinLevel()))
                    .replaceAll("<totalBlocks>", String.valueOf(schematic.getTotalBlocks()))
                    .replaceAll("<sName>", schematic.getName())
                    .replaceAll("&", "§"));
        iMeta.setLore(rLore);
        item.setItemMeta(iMeta);
        return item;
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
            if (queue >= this.schematicList.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(schematicList.get(queue), queue));
            queue++;
        }
    }

    @EventHandler
    @Deprecated
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
                                returnPageMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                    this.readItem(this.schematicList.get((currentMenu * 45) + e.getSlot()),
                                            (currentMenu * 45) + e.getSlot()).getItemMeta().getDisplayName())) {
                                Schematic schematic = this.schematicList.get((currentMenu * 45) + e.getSlot());
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.hasPermission(p, Permission.BUILD_A_SCHEMATIC_FOR_CLAIM)) {
                                        if (team.haveEnoughMoney(schematic.getCost())) {
                                            if (team.getLevel() >= schematic.getMinLevel()) {
                                                if (schematic.isSuitClaim(claim)) {
                                                    team.removeBalance(schematic.getCost());
                                                    team.saveTeam();
                                                    Random r = new Random();
                                                    int[] rand = {90, 180, 270, 360};
                                                    schematic.buildSchematicToClaim(p, claim, rotateValue = rand[r.nextInt(rand.length)]);
                                                    p.sendMessage(Messages.build_started);
                                                } else {
                                                    p.sendMessage(Messages.claim_is_not_suitable_of_schematic);
                                                }
                                            } else {
                                                p.sendMessage(Messages.team_level_not_enough);
                                            }
                                        } else {
                                            p.sendMessage(Messages.not_enough_money);
                                        }
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
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
}
