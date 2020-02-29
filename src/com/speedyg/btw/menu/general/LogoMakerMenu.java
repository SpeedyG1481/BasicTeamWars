package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.Version;
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
    // Current color iki taraflı buga giriyor o düzelticek

    private ItemStack currentColor;
    private long changeCost;
    private ABSTeam team;

    public LogoMakerMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu, long changeCost, ABSTeam absTeam) {
        super(btw, p, team, returnMenu);
        this.team = absTeam;
        if (this.team.getTeamLogo() != null)
            temporaryLogo = new Logo(this.team.getTeamLogo().getLogo());
        else
            temporaryLogo = new Logo();
        currentColor = colorChanger(null);
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
        Material mat;
        ItemStack item;
        if (BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_14) || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_15) || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_16)) {
            item = glassChanger(data);
        } else {
            mat = Material.getMaterial("STAINED_GLASS_PANE");
            item = new ItemStack(mat, 1, data);
        }

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(null);
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack glassChanger(short data) {
        ItemStack item;
        switch (data) {
            case 1:
                item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
                break;
            case 3:
                item = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                break;
            case 4:
                item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                break;
            case 5:
                item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                break;
            case 6:
                item = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
                break;
            case 7:
                item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                break;
            case 8:
                item = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                break;
            case 9:
                item = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
                break;
            case 10:
                item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
                break;
            case 11:
                item = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
                break;
            case 13:
                item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                break;
            case 14:
                item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                break;
            default:
                item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                break;
        }

        return item;
    }

    private ItemStack woolChanger(short data) {
        ItemStack item;
        switch (data) {
            case 1:
                item = new ItemStack(Material.ORANGE_WOOL);
                break;
            case 3:
                item = new ItemStack(Material.LIGHT_BLUE_WOOL);
                break;
            case 4:
                item = new ItemStack(Material.YELLOW_WOOL);
                break;
            case 5:
                item = new ItemStack(Material.LIME_WOOL);
                break;
            case 6:
                item = new ItemStack(Material.PINK_WOOL);
                break;
            case 7:
                item = new ItemStack(Material.GRAY_WOOL);
                break;
            case 8:
                item = new ItemStack(Material.LIGHT_GRAY_WOOL);
                break;
            case 9:
                item = new ItemStack(Material.CYAN_WOOL);
                break;
            case 10:
                item = new ItemStack(Material.PURPLE_WOOL);
                break;
            case 11:
                item = new ItemStack(Material.BLUE_WOOL);
                break;
            case 13:
                item = new ItemStack(Material.GREEN_WOOL);
                break;
            case 14:
                item = new ItemStack(Material.RED_WOOL);
                break;
            default:
                item = new ItemStack(Material.WHITE_WOOL);
                break;
        }

        return item;
    }


    private ItemStack woolItem(short data) {
        Material mat;
        ItemStack item;
        if (BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_14) || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_15) || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_16)) {
            item = woolChanger(data);
        } else {
            mat = Material.getMaterial("WOOL");
            item = new ItemStack(mat, 1, data);
        }
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

                                } else if (e.getCurrentItem().getType().name().contains("WOOL")) {
                                    currentColor = colorChanger(e.getCurrentItem());
                                    this.openMenu();
                                } else if (e.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE")) {
                                    e.setCurrentItem(currentColor);
                                    for (int i = 0; i < (temporaryLogo.getLogo().length / 2); i++) {
                                        for (int j = 0; j < (temporaryLogo.getLogo().length / 2); j++) {
                                            short data;
                                            if (BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_14)
                                                    || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_15)
                                                    || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_16))
                                                data = durabilityChanger(e.getInventory().getItem((i * 9) + j));
                                            else
                                                data = e.getInventory().getItem((i * 9) + j).getDurability();
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

    private short durabilityChanger(ItemStack item) {
        switch (item.getType()) {
            case ORANGE_STAINED_GLASS_PANE:
                return 1;
            case LIGHT_BLUE_STAINED_GLASS_PANE:
                return 3;
            case YELLOW_STAINED_GLASS_PANE:
                return 4;
            case LIME_STAINED_GLASS_PANE:
                return 5;
            case PINK_STAINED_GLASS_PANE:
                return 6;
            case GRAY_STAINED_GLASS_PANE:
                return 8;
            case LIGHT_GRAY_STAINED_GLASS_PANE:
                return 7;
            case CYAN_STAINED_GLASS_PANE:
                return 9;
            case PURPLE_STAINED_GLASS_PANE:
                return 10;
            case BLUE_STAINED_GLASS_PANE:
                return 11;
            case GREEN_STAINED_GLASS_PANE:
                return 13;
            case RED_STAINED_GLASS_PANE:
                return 14;
            default:
                return 0;
        }
    }

    private ItemStack colorChanger(ItemStack type) {
        if (type != null) {
            if (BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_14)
                    || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_15)
                    || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_16)) {
                switch (type.getType()) {
                    case ORANGE_WOOL:
                        return new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
                    case LIGHT_BLUE_WOOL:
                        return new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                    case YELLOW_WOOL:
                        return new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                    case LIME_WOOL:
                        return new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                    case PINK_WOOL:
                        return new ItemStack(Material.PINK_STAINED_GLASS_PANE);
                    case GRAY_WOOL:
                        return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    case LIGHT_GRAY_WOOL:
                        return new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                    case CYAN_WOOL:
                        return new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
                    case PURPLE_WOOL:
                        return new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
                    case BLUE_WOOL:
                        return new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
                    case GREEN_WOOL:
                        return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                    case RED_WOOL:
                        return new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    default:
                        return new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                }
            } else if (type.getType().name().contains("WOOL")) {
                return new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, type.getDurability());
            }
        } else {
            if (BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_14)
                    || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_15)
                    || BasicTeamWars.getInstance().getServerVersion().equals(Version.V1_16)) {
                return new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
            } else {
                return new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 0);
            }
        }
        return null;
    }


}
