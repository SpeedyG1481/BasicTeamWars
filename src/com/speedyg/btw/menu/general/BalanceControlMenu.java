package com.speedyg.btw.menu.general;

import com.google.common.collect.Lists;
import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BalanceControlMenu extends Menu implements Listener {


    public BalanceControlMenu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = Bukkit.createInventory(null, main.getMenuOptions().getInt("BalanceControlMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("BalanceControlMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("BalanceControlMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("BalanceControlMenu.Menu_Name")
                .replaceAll("&", "§")
                : "§8§nBalance Control Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("BalanceControlMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("BalanceControlMenu.Buttons.Back.Loc") - 1, this.back());
        this.inv.setItem(main.getMenuOptions().getInt("BalanceControlMenu.Buttons.AddFundsButton.Loc") - 1, this.addFunds());
        this.inv.setItem(main.getMenuOptions().getInt("BalanceControlMenu.Buttons.WithdrawFundsButton.Loc") - 1, this.withdrawFunds());
        this.inv.setItem(main.getMenuOptions().getInt("BalanceControlMenu.Buttons.InfoButton.Loc") - 1, this.infoButton());
        // balance control buttons and permission check
    }

    @Override
    public void openMenu() {
        this.loadItems();
        this.p.openInventory(this.inv);
    }

    private ItemStack addFunds() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("BalanceControlMenu.Buttons.AddFundsButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("BalanceControlMenu.Buttons.AddFundsButton.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("BalanceControlMenu.Buttons.AddFundsButton.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack withdrawFunds() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("BalanceControlMenu.Buttons.WithdrawFundsButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("BalanceControlMenu.Buttons.WithdrawFundsButton.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("BalanceControlMenu.Buttons.WithdrawFundsButton.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack infoButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("BalanceControlMenu.Buttons.InfoButton.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("BalanceControlMenu.Buttons.InfoButton.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("BalanceControlMenu.Buttons.InfoButton.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§")
                    .replaceAll("<p_balance>", String.valueOf(BasicTeamWars.getEconomy().getBalance(p)))
                    .replaceAll("<balance>", String.valueOf(ABSTeam.getTeamByTeamUUID(this.team).getBalance())));
        }
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                this.returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.addFunds().getItemMeta().getDisplayName())) {

                                if (team.hasPermission(p, Permission.ADD_FUND_TEAM)) {
                                    BasicTeamWars.getInstance().getMenuFactory()
                                            .newMenu(p,
                                                    Lists.newArrayList("", "", "",
                                                            ""))
                                            .reopenIfFail().response((player, strings) -> {
                                        if (strings[0] != null) {
                                            if (strings[0].length() > 0) {
                                                if (main.isInt(strings[0])) {
                                                    int requestedValue = Integer.parseInt(strings[0]);
                                                    if (requestedValue > 0) {
                                                        if (BasicTeamWars.getEconomy().getBalance(p) >= requestedValue) {
                                                            Bukkit.getScheduler().runTask(BasicTeamWars.getInstance(), () -> {
                                                                p.sendMessage(Messages.add_fund_success);
                                                                team.addBalance(requestedValue);
                                                                team.saveTeam();
                                                                BasicTeamWars.getEconomy().withdrawPlayer(p, requestedValue);
                                                            });
                                                            this.openMenu();
                                                        } else {
                                                            p.sendMessage(Messages.not_enough_money_player);
                                                        }
                                                    } else {
                                                        p.sendMessage(Messages.value_must_bigger_than_0);
                                                    }
                                                    return true;
                                                }
                                            }
                                        }

                                        return false;
                                    }).open();

                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.withdrawFunds().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.WITHDRAW_FUND_TEAM)) {
                                    BasicTeamWars.getInstance().getMenuFactory()
                                            .newMenu(p,
                                                    Lists.newArrayList("", "", "",
                                                            ""))
                                            .reopenIfFail().response((player, strings) -> {

                                        if (strings[0] != null) {
                                            if (strings[0].length() > 0) {
                                                if (main.isInt(strings[0])) {
                                                    int requestedValue = Integer.parseInt(strings[0]);
                                                    if (requestedValue > 0) {
                                                        if (team.getBalance() >= requestedValue) {
                                                            Bukkit.getScheduler().runTask(BasicTeamWars.getInstance(), () -> {
                                                                p.sendMessage(Messages.withdraw_success);
                                                                BasicTeamWars.getEconomy().depositPlayer(p, requestedValue);
                                                                team.removeBalance(requestedValue);
                                                                team.saveTeam();
                                                            });
                                                            this.openMenu();
                                                        } else {
                                                            p.sendMessage(Messages.not_enough_money);
                                                        }
                                                    } else {
                                                        p.sendMessage(Messages.value_must_bigger_than_0);
                                                    }
                                                    return true;
                                                }
                                            }
                                        }

                                        return false;
                                    }).open();
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
