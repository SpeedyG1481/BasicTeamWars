package com.speedyg.btw.menu;

import com.google.common.collect.Lists;
import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.general.LogoMakerMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Logo;
import com.speedyg.btw.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TeamMakeMenu extends Menu implements Listener {

    private ABSTeam team;

    public TeamMakeMenu(BasicTeamWars btw, Player p, UUID team) {
        super(btw, p, team, (Menu) null);
        Date date = new Date();
        this.team = new ABSTeam(p, null, (short) 1, date.getTime(), new Logo(), null);
        this.inv = (Bukkit.createInventory(null, main.getMenuOptions().getInt("TeamMakeMenu.Menu_Size") > 0
                ? main.getMenuOptions().getInt("TeamMakeMenu.Menu_Size")
                : 54, main.getMenuOptions().getString("TeamMakeMenu.Menu_Name")
                != null ? main.getMenuOptions().getString("TeamMakeMenu.Menu_Name").replaceAll("&", "§")
                : "§8§nTeam Make Menu"));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("TeamMakeMenu.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("TeamMakeMenu.Buttons.NewTeamMake.Loc") - 1, this.newTeamButton());
        this.inv.setItem(main.getMenuOptions().getInt("TeamMakeMenu.Buttons.NewTeamSize.Loc") - 1, this.setMaxSizeTeam());
        this.inv.setItem(main.getMenuOptions().getInt("TeamMakeMenu.Buttons.NewTeamName.Loc") - 1, this.setNewTeamName());
        this.inv.setItem(main.getMenuOptions().getInt("TeamMakeMenu.Buttons.NewTeamLogo.Loc") - 1, this.setTeamLogo());
    }

    private ItemStack newTeamButton() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamMake.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamMake.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("TeamMakeMenu.Buttons.NewTeamMake.Lore");
        ArrayList<String> lore = new ArrayList<>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§").replaceAll("<price>",
                    String.valueOf(main.getConfig().getInt("Team_Make_Cost") + team.getSize() * main.getConfig().getInt("Team_Per_Person_Cost"))));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack setNewTeamName() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamName.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamName.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("TeamMakeMenu.Buttons.NewTeamName.Lore");
        ArrayList<String> lore = new ArrayList<>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§").replaceAll("<currentname>", team.getName()
                    != null ? team.getName().replaceAll("&", "§") : "NULL"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack setMaxSizeTeam() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamSize.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamSize.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("TeamMakeMenu.Buttons.NewTeamSize.Lore");
        ArrayList<String> lore = new ArrayList<>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§").replaceAll("<currentsize>", String.valueOf(team.getSize())));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack setTeamLogo() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamLogo.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("TeamMakeMenu.Buttons.NewTeamLogo.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("TeamMakeMenu.Buttons.NewTeamLogo.Lore");
        ArrayList<String> lore = new ArrayList<>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }


    @EventHandler
    private void inventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (e.getInventory() != null) {
                Player p = (Player) e.getWhoClicked();
                if (e.getInventory().equals(this.inv)) {
                    e.setCancelled(true);
                    if (e.getCurrentItem() != null) {
                        if (e.getCurrentItem().hasItemMeta()) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.close().getItemMeta().getDisplayName())) {
                                    p.closeInventory();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.newTeamButton().getItemMeta().getDisplayName())) {
                                    if (team.getTeamLogo() != null && team.getName() != null && team.getSize() > 0) {
                                        if (BasicTeamWars.getEconomy().getBalance(p) > main.getConfig().getInt("Team_Make_Cost")) {
                                            BasicTeamWars.getEconomy().withdrawPlayer(p, main.getConfig().getInt("Team_Make_Cost"));
                                            team.saveTeam();
                                            TeamPlayer player = TeamPlayer.getPlayer(p);
                                            player.setTeam(team);
                                            player.savePlayer();
                                            p.sendMessage(Messages.team_make_success);
                                        } else {
                                            p.sendMessage(Messages.not_enough_money_player);
                                        }
                                    } else {
                                        p.sendMessage(Messages.error_blank_name_or_logo);
                                    }
                                    p.closeInventory();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.setMaxSizeTeam().getItemMeta().getDisplayName())) {
                                    if (e.getClick().equals(ClickType.RIGHT)) {
                                        team.setSize((short) (team.getSize() + 1));
                                    } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                        team.setSize((short) (team.getSize() + 5));
                                    } else if (e.getClick().equals(ClickType.LEFT)) {
                                        team.setSize((short) (team.getSize() - 1));
                                    } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                        team.setSize((short) (team.getSize() - 5));
                                    }
                                    this.openMenu();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.setNewTeamName().getItemMeta().getDisplayName())) {
                                    p.closeInventory();
                                    BasicTeamWars.getInstance().getMenuFactory()
                                            .newMenu(p,
                                                    Lists.newArrayList("", "", Messages.new_name_message_1,
                                                            Messages.new_name_message_2))
                                            .reopenIfFail().response((player, strings) -> {
                                        if (strings[0] != null && strings[1] != null)
                                            if (strings[0].length() + strings[1].length() > 0) {
                                                String s = strings[0] + strings[1];
                                                team.setName(s);
                                                this.openMenu();
                                                return true;
                                            }
                                        return false;
                                    }).open();
                                } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.setTeamLogo().getItemMeta().getDisplayName())) {
                                    LogoMakerMenu menu = new LogoMakerMenu(main, p, team.getTeamUUID(), this, 0, team);
                                    menu.openMenu();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
