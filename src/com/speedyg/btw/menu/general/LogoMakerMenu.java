package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Logo;
import com.speedyg.btw.team.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogoMakerMenu extends Menu implements Listener {

    private Logo temporaryLogo;
    private int right = 0;
    private int bottom = 0;
    private short currentColor = 0;
    private long changeCost;
    private ABSTeam team;

    public LogoMakerMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu, long changeCost, ABSTeam absTeam) {
        super(btw, p, team, returnMenu);
        this.team = absTeam;
        if (this.team.getTeamLogo() != null)
            temporaryLogo = new Logo(this.team.getTeamLogo().getLogo());
        else
            temporaryLogo = new Logo();
        this.changeCost = changeCost;
        this.inv = (Bukkit.createInventory(null, 54, main.getMenuOptions().getString("LogoMakerMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("LogoMakerMenu.Menu_Name")
                .replaceAll("&", "§")
                .replaceAll("<version>", main.getDescription().getVersion())
                : "§8§nTeam Make Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    public void loadItems() {
        for (int i = 0; i < (temporaryLogo.getLogo().length / 2); i++) {
            for (int j = 0; j < (temporaryLogo.getLogo().length / 2); j++) {
                this.inv.setItem((i * 9) + j, glassItem(temporaryLogo.getLogo()[i + bottom][j + right]));
            }
        }

        int z = 6;
        for (int i = 0; i <= 15; i++) {
            if (i != 15 && i != 12 && i != 2) {
                this.inv.setItem(z, woolItem((short) i));
                z++;
                if (z % 9 == 0)
                    z += 6;
            }
        }

        this.inv.setItem(45, this.goTopLeftButton());
        this.inv.setItem(46, this.goBottomLeftButton());
        this.inv.setItem(47, this.goTopRightButton());
        this.inv.setItem(48, this.goBottomRightButton());
        this.inv.setItem(49, this.infoItem());

        this.inv.setItem(53, this.close());
        this.inv.setItem(52, this.saveLogo());
        this.inv.setItem(51, this.back());

    }

    private ItemStack glassItem(short data) {
        ItemStack item = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, data);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(null);
        item.setItemMeta(itemMeta);
        return item;
    }


    private ItemStack woolItem(short data) {
        ItemStack item = new ItemStack(Material.getMaterial("WOOL"), 1, data);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(null);
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack infoItem() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("LogoMakerMenu.Buttons.InfoItem.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("LogoMakerMenu.Buttons.InfoItem.Name")
                .replaceAll("&", "§"));
        String logox = "";
        List<String> lore = new ArrayList<>();
        for (short i = 0; i < temporaryLogo.getLogo().length; i++) {
            for (short j = 0; j < temporaryLogo.getLogo().length; j++) {
                logox += "§" + System.colorChanger(temporaryLogo.getLogo()[i][j]) + main.getMenuOptions().getString("LogoMakerMenu.Buttons.InfoItem.Logo-Box");
            }
            lore.add(logox);
            logox = "";
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack saveLogo() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("LogoMakerMenu.Buttons.SaveLogo.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("LogoMakerMenu.Buttons.SaveLogo.Name")
                .replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<String>();
        List<String> tlore = main.getMenuOptions().getStringList("LogoMakerMenu.Buttons.SaveLogo.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack goTopRightButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("LogoMakerMenu.Buttons.TopRight.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("LogoMakerMenu.Buttons.TopRight.Name")
                .replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<String>();
        List<String> tlore = main.getMenuOptions().getStringList("LogoMakerMenu.Buttons.TopRight.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack goTopLeftButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("LogoMakerMenu.Buttons.TopLeft.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("LogoMakerMenu.Buttons.TopLeft.Name")
                .replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<String>();
        List<String> tlore = main.getMenuOptions().getStringList("LogoMakerMenu.Buttons.TopLeft.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack goBottomRightButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("LogoMakerMenu.Buttons.BottomRight.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("LogoMakerMenu.Buttons.BottomRight.Name")
                .replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<String>();
        List<String> tlore = main.getMenuOptions().getStringList("LogoMakerMenu.Buttons.BottomRight.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack goBottomLeftButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("LogoMakerMenu.Buttons.BottomLeft.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("LogoMakerMenu.Buttons.BottomLeft.Name")
                .replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<String>();
        List<String> tlore = main.getMenuOptions().getStringList("LogoMakerMenu.Buttons.BottomLeft.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (e.getInventory() != null) {
                Player p = (Player) e.getWhoClicked();
                if (e.getInventory().equals(this.inv)) {
                    e.setCancelled(true);
                    if (e.getCurrentItem() != null) {
                        if (e.getCurrentItem().hasItemMeta()) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                        this.close().getItemMeta().getDisplayName())) {
                                    p.closeInventory();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                        this.goTopLeftButton().getItemMeta().getDisplayName())) {
                                    this.right = 0;
                                    this.bottom = 0;
                                    this.openMenu();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                        this.goTopRightButton().getItemMeta().getDisplayName())) {
                                    this.right = 5;
                                    this.bottom = 0;
                                    this.openMenu();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                        this.goBottomLeftButton().getItemMeta().getDisplayName())) {
                                    this.right = 0;
                                    this.bottom = 5;
                                    this.openMenu();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                        this.goBottomRightButton().getItemMeta().getDisplayName())) {
                                    this.right = 5;
                                    this.bottom = 5;
                                    this.openMenu();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName()
                                        .equals(this.back().getItemMeta().getDisplayName())) {
                                    if (team.getTeamFile() != null)
                                        if (team.getTeamFile().exists())
                                            if (ABSTeam.getFileToTeam(team.getTeamFile()) != null)
                                                team.setTeamLogo(ABSTeam.getFileToTeam(team.getTeamFile()).getTeamLogo());
                                    returnMenu.openMenu();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(
                                        this.saveLogo().getItemMeta().getDisplayName())) {
                                    if (team.hasPermission(p, Permission.CHANGE_TEAM_LOGO)) {
                                        if (team.haveEnoughMoney(this.changeCost)) {
                                            team.setTeamLogo(this.temporaryLogo);
                                            if (changeCost > 0) {
                                                team.removeBalance(changeCost);
                                                team.saveTeam();
                                            }
                                            p.sendMessage(Messages.logo_change_success);
                                            returnMenu.openMenu();
                                        } else {
                                            p.sendMessage(Messages.not_enough_money);
                                            p.closeInventory();
                                        }
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                        p.closeInventory();
                                    }

                                } else if (e.getCurrentItem().getType().equals(Material.getMaterial("WOOL"))) {
                                    currentColor = e.getCurrentItem().getDurability();
                                    this.openMenu();
                                } else if (e.getCurrentItem().getType().equals(Material.getMaterial("STAINED_GLASS_PANE"))) {
                                    e.getCurrentItem().setDurability(currentColor);
                                    for (int i = 0; i < (temporaryLogo.getLogo().length / 2); i++) {
                                        for (int j = 0; j < (temporaryLogo.getLogo().length / 2); j++) {
                                            short data = e.getInventory().getItem((i * 9) + j).getDurability();
                                            temporaryLogo.getLogo()[i + bottom][j + right] = data;
                                        }
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


}
