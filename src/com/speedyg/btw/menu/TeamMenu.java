package com.speedyg.btw.menu;

import com.google.common.collect.Lists;
import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.general.*;
import com.speedyg.btw.menu.verification.AbandonVerification;
import com.speedyg.btw.menu.verification.LeaveVerification;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamMenu extends Menu implements Listener {


    public TeamMenu(BasicTeamWars btw, Player p, UUID team) {
        super(btw, p, team, (Menu) null);
        this.inv = (Bukkit.createInventory(null, main.getMenuOptions().getInt("TeamEditMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("TeamEditMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("TeamEditMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("TeamEditMenu.Menu_Name").replaceAll("&", "§")
                : "§8§nTeam Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.LogoButton.Loc") - 1,
                this.logoInfoButton());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.NameButton.Loc") - 1,
                this.nameButton());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.SizeButton.Loc") - 1,
                this.sizeButton());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.BalanceButton.Loc") - 1,
                this.balanceControlMenu());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.MemberControl.Loc") - 1,
                this.memberControlMenu());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.GeneralInfoButton.Loc") - 1,
                this.otherInfoButton());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.WarOptions.Loc") - 1,
                this.warOptionsMenu());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.EnemyFriendsTeam.Loc") - 1,
                this.enenmyFriendTeamOptions());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.SpawnPoint.Loc") - 1,
                this.spawnPointOptions());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.ClaimOptions.Loc") - 1,
                this.claimOptions());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.LeaveTeam.Loc") - 1,
                this.leaveTeam());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.RemoveTeam.Loc") - 1,
                this.abandonTeam());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.Invites.Loc") - 1,
                this.checkInvites());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.MessageBox.Loc") - 1,
                this.messageBox());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.DescriptionButton.Loc") - 1,
                this.descriptionButton());
        this.inv.setItem(main.getMenuOptions().getInt("TeamEditMenu.Buttons.ShortNameButton.Loc") - 1,
                this.shortName());
    }

    private ItemStack abandonTeam() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.RemoveTeam.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.RemoveTeam.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.RemoveTeam.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<claims>", String.valueOf(team.getClaims().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }


    private ItemStack messageBox() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.MessageBox.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.MessageBox.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.MessageBox.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<claims>", String.valueOf(team.getClaims().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack logoInfoButton() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.LogoButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.LogoButton.Name").replaceAll("&", "§"));
        String logox = "";
        ArrayList<String> lore = new ArrayList<>();
        for (short i = 0; i < team.getTeamLogo().getLogo().length; i++) {
            for (short j = 0; j < team.getTeamLogo().getLogo().length; j++) {
                logox += "§" + System.colorChanger(team.getTeamLogo().getLogo()[i][j]) + main.getMenuOptions().getString("TeamEditMenu.Buttons.LogoButton.Logo-Box");
            }
            lore.add(logox);
            logox = "";
        }
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.LogoButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§")
                    .replaceAll("<team_change_cost>", String.valueOf(main.getConfig().getInt("Team_Change_Logo_Cost"))));
        imeta.setLore(lore);
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack nameButton() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.NameButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.NameButton.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.NameButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<name_change_cost>",
                    String.valueOf(main.getConfig().getInt("Team_Change_Name_Cost"))).replaceAll("<name>",
                    team.getName().replaceAll("&", "§")));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack descriptionButton() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.DescriptionButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.DescriptionButton.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.DescriptionButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<description_change_cost>",
                    String.valueOf(main.getConfig().getInt("Team_Change_Description_Cost"))).replaceAll("<description>",
                    team.getTeamDescription().replaceAll("&", "§")));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack shortName() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.ShortNameButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.ShortNameButton.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.ShortNameButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<shortname_change_cost>",
                    String.valueOf(main.getConfig().getInt("Team_Change_Short_Name_Cost"))).replaceAll("<shortName>",
                    team.getShortName().replaceAll("&", "§")));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack sizeButton() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.SizeButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.SizeButton.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.SizeButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<size>", String.valueOf(team.getMembers().size()))
                    .replaceAll("%percentage%", new DecimalFormat("##.##")
                            .format(((float) team.getMembers().size() / (float) team.getSize()) * 100)
                            .replace(",", ".") + "%")
                    .replaceAll("<max-size>",
                            String.valueOf(team.getSize())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack balanceControlMenu() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.BalanceButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.BalanceButton.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.BalanceButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<balance>",
                    String.valueOf(team.getBalance())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack memberControlMenu() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.MemberControl.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.MemberControl.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.MemberControl.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<admins-size>", String.valueOf(team.getAdmins().size()))
                    .replaceAll("<mods-size>", String.valueOf(team.getMods().size())).replaceAll("<members-size>",
                            String.valueOf(team.getMembers().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack otherInfoButton() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.GeneralInfoButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.GeneralInfoButton.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.GeneralInfoButton.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("<team-id>", team.getTeamUUID().toString())
                    .replaceAll("<team-level>", String.valueOf(team.getLevel()))
                    .replaceAll("<total-wins>", String.valueOf(team.getTotalVSWins()))
                    .replaceAll("<total-kills>", String.valueOf(team.getTotalKills()))
                    .replaceAll("<team-power>", String.valueOf(team.getPower()))
                    .replaceAll("<team-balance>", String.valueOf(team.getBalance()))
                    .replaceAll("<team-name>", team.getName().replaceAll("&", "§"))
                    .replaceAll("&", "§").replaceAll("<owner-name>", team.getTeamMaker().getName())
                    .replaceAll("<admins-size>", String.valueOf(team.getAdmins().size()))
                    .replaceAll("<mods-size>", String.valueOf(team.getMods().size()))
                    .replaceAll("<members-size>",
                            String.valueOf(team.getMembers().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }


    private ItemStack warOptionsMenu() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.WarOptions.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.WarOptions.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.WarOptions.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack enenmyFriendTeamOptions() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.EnemyFriendsTeam.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.EnemyFriendsTeam.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.EnemyFriendsTeam.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<neutrals>", String.valueOf(team.getNeutralTeams().size()))
                    .replaceAll("<friends>", String.valueOf(team.getFriendTeams().size()))
                    .replaceAll("<enemies>", String.valueOf(team.getEnemyTeams().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack spawnPointOptions() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.SpawnPoint.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.SpawnPoint.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.SpawnPoint.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack claimOptions() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.ClaimOptions.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.ClaimOptions.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.ClaimOptions.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<claims>", String.valueOf(team.getClaims().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack leaveTeam() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.LeaveTeam.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.LeaveTeam.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.LeaveTeam.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§").replaceAll("<claims>", String.valueOf(team.getClaims().size())));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack checkInvites() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamEditMenu.Buttons.Invites.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamEditMenu.Buttons.Invites.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("TeamEditMenu.Buttons.Invites.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.balanceControlMenu().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.BALANCE_CONTROL)) {
                                    BalanceControlMenu menu = new BalanceControlMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.claimOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CLAIM_OPTIONS)) {
                                    ClaimMenu menu = new ClaimMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.enenmyFriendTeamOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.ENEMY_FRIEND_TEAM_CONTROL)) {
                                    EFTeamControlMenu menu = new EFTeamControlMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.warOptionsMenu().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.WAR_OPTIONS)) {
                                    WarMenu menu = new WarMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.logoInfoButton().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_TEAM_LOGO)) {
                                    LogoMakerMenu menu = new LogoMakerMenu(main, p, this.team, this, main.getConfig().getInt("Team_Change_Logo_Cost"), team);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.memberControlMenu().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.MEMBER_OPTIONS)) {
                                    MemberOptions menu = new MemberOptions(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.leaveTeam().getItemMeta().getDisplayName())) {
                                if (p.getUniqueId().equals(team.getTeamMaker().getUniqueId())) {
                                    p.sendMessage(Messages.not_leave_your_team);
                                    p.closeInventory();
                                } else {
                                    LeaveVerification menu = new LeaveVerification(main, p, this, this.team);
                                    menu.openMenu();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.messageBox().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHECK_MESSAGE_BOX)) {
                                    MessageBox menu = new MessageBox(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.abandonTeam().getItemMeta().getDisplayName())) {
                                if (team.getTeamMaker().getUniqueId().toString().equals(p.getUniqueId().toString())) {
                                    AbandonVerification menu = new AbandonVerification(main, p, this, this.team);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.checkInvites().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHECK_TEAM_JOIN_REQUESTS)) {
                                    InviteCheckMenu menu = new InviteCheckMenu(main, p, this, this.team);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.nameButton().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_TEAM_NAME)) {
                                    BasicTeamWars.getInstance().getMenuFactory()
                                            .newMenu(p,
                                                    Lists.newArrayList("", "", Messages.new_name_message_1,
                                                            Messages.new_name_message_2))
                                            .reopenIfFail().response((player, strings) -> {
                                        if (strings[0] != null && strings[1] != null)
                                            if (strings[0].length() + strings[1].length() > 0) {
                                                String s = strings[0] + strings[1];
                                                if (team.haveEnoughMoney(main.getConfig().getInt("Team_Change_Name_Cost"))) {
                                                    p.sendMessage(Messages.team_name_changed);
                                                    team.setName(s);
                                                    team.removeBalance(main.getConfig().getInt("Team_Change_Name_Cost"));
                                                    team.saveTeam();
                                                } else {
                                                    p.sendMessage(Messages.not_enough_money);
                                                    p.closeInventory();
                                                }
                                                this.openMenu();
                                                return true;
                                            }
                                        return false;
                                    }).open();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.descriptionButton().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_TEAM_DESCRIPTION)) {
                                    BasicTeamWars.getInstance().getMenuFactory()
                                            .newMenu(p,
                                                    Lists.newArrayList("", "", "",
                                                            Messages.new_description_line))
                                            .reopenIfFail().response((player, strings) -> {
                                        if (strings[0] != null && strings[1] != null && strings[2] != null)
                                            if (strings[0].length() + strings[1].length() + strings[2].length() > 0) {
                                                String s = strings[0] + strings[1] + strings[2];
                                                if (team.haveEnoughMoney(main.getConfig().getInt("Team_Change_Description_Cost"))) {
                                                    p.sendMessage(Messages.team_description_changed);
                                                    team.setTeamDescription(s);
                                                    team.removeBalance(main.getConfig().getInt("Team_Change_Description_Cost"));
                                                    team.saveTeam();
                                                } else {
                                                    p.sendMessage(Messages.not_enough_money);
                                                    p.closeInventory();
                                                }
                                                this.openMenu();
                                                return true;
                                            }
                                        return false;
                                    }).open();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.shortName().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_TEAM_SHORT_NAME)) {
                                    BasicTeamWars.getInstance().getMenuFactory()
                                            .newMenu(p,
                                                    Lists.newArrayList("", Messages.new_short_name_line_1, Messages.new_short_name_line_2,
                                                            Messages.new_short_name_line_3))
                                            .reopenIfFail().response((player, strings) -> {
                                        if (strings[0] != null)
                                            if (strings[0].length() > 0 && strings[0].length() <= 3) {
                                                String s = strings[0];
                                                if (team.haveEnoughMoney(main.getConfig().getInt("Team_Change_Short_Name_Cost"))) {
                                                    p.sendMessage(Messages.team_description_changed);
                                                    team.setShortName(s);
                                                    team.removeBalance(main.getConfig().getInt("Team_Change_Short_Name_Cost"));
                                                    team.saveTeam();
                                                } else {
                                                    p.sendMessage(Messages.not_enough_money);
                                                    p.closeInventory();
                                                }
                                                this.openMenu();
                                                return true;
                                            }
                                        return false;
                                    }).open();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.sizeButton().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_TEAM_SIZE)) {
                                    SizeMenu menu = new SizeMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.spawnPointOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_SPAWN_POINT)) {
                                    SpawnSettingsMenu menu = new SpawnSettingsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
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
