package com.speedyg.btw.menu.permission.upmenu;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.menu.permission.general.ChangeAdminPermissionsMenu;
import com.speedyg.btw.menu.permission.general.ChangeModeratorPermissionsMenu;
import com.speedyg.btw.menu.permission.self.ChangePlayerPrivatePermissionMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
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
import java.util.List;
import java.util.UUID;

public class AdminOptionsMenu extends PageMenu implements Listener {

    private UUID team;
    private List<String> admins;

    public AdminOptionsMenu(BasicTeamWars main, Player p, Menu returnMenu, UUID team) {
        super(main, p, returnMenu);
        this.team = team;
        this.admins = ABSTeam.getTeamByTeamUUID(this.team).getAdmins();
        this.currentMenu = 0;
        this.totalMenu = (admins.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++)
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("AdminOptionsListMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("AdminOptionsListMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Admin List : Page §e" + (i + 1)));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        this.admins = team.getAdmins();
        if (currentMenu != 0)
            this.inv.get(currentMenu).setItem(45, this.previous());
        else
            this.inv.get(currentMenu).setItem(45, this.back());
        if (currentMenu < this.totalMenu - 1) {
            this.inv.get(currentMenu).setItem(53, this.next());
        } else {
            this.inv.get(currentMenu).setItem(53, this.close());
        }

        this.inv.get(currentMenu).setItem(49, this.modSettings());

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.admins.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(UUID.fromString(admins.get(queue))));
            queue++;
        }
    }

    private ItemStack modSettings() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("AdminOptionsListMenu.Buttons.AdminPermissionSettings.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("AdminOptionsListMenu.Buttons.AdminPermissionSettings.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("AdminOptionsListMenu.Buttons.AdminPermissionSettings.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack readItem(UUID teamPlayer) {
        ItemStack item = Skull.getPlayerSkull(TeamPlayer.getPlayer(teamPlayer).getPlayer().getName());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("AdminOptionsListMenu.Buttons.ReadItem.Name")
                .replaceAll("&", "§")
                .replaceAll("<pName>", TeamPlayer.getPlayer(teamPlayer).getPlayer().getName()));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("AdminOptionsListMenu.Buttons.ReadItem.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
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
                            ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.modSettings().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_ADMINS_PERMISSIONS)) {
                                    ChangeAdminPermissionsMenu menu = new ChangeAdminPermissionsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.readItem(UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot()))).getItemMeta().getDisplayName())) {
                               /* if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    if (team.hasPermission(p, Permission.PROMOTE_ADMIN_PERMISSION)) {
                                        team.removeModerator(UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())));
                                        team.addAdmin(UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())));
                                        team.saveTeam();
                                        p.sendMessage(Messages.promote_success_to_admin);
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                    p.closeInventory();
                                } else*/
                                if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (team.hasPermission(p, Permission.KICK_PLAYER)) {
                                        if (team.isBigger(p, UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())))) {
                                            team.removeMember(UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())));
                                            team.saveTeam();
                                            p.sendMessage(Messages.kick_success);
                                        } else {
                                            p.sendMessage(Messages.insufficient_permissions);
                                        }
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                    p.closeInventory();
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.hasPermission(p, Permission.CHANGE_PLAYERS_SELF_PERMISSIONS)) {
                                        ChangePlayerPrivatePermissionMenu menu =
                                                new ChangePlayerPrivatePermissionMenu(main, p, this.team, this, UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())));
                                        menu.openMenu();
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (team.hasPermission(p, Permission.DEGRADE_ADMIN_TO_MOD_PERMISSION)) {
                                        team.addModerator(UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())));
                                        team.removeAdmin(UUID.fromString(this.admins.get((currentMenu * 45) + e.getSlot())));
                                        team.saveTeam();
                                        p.sendMessage(Messages.degrade_success_admin_to_mod);
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