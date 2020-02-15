package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.permission.upmenu.AdminOptionsMenu;
import com.speedyg.btw.menu.permission.upmenu.MemberOptionsMenu;
import com.speedyg.btw.menu.permission.upmenu.ModeratorOptionsMenu;
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

public class MemberOptions extends Menu implements Listener {

    public MemberOptions(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        super(btw, p, team, returnMenu);
        this.inv = Bukkit.createInventory(null, main.getMenuOptions().getInt("MemberOptions.Menu_Size") > 0
                ? main.getMenuOptions().getInt("MemberOptions.Menu_Size")
                : 54, main.getMenuOptions().getString("MemberOptions.Menu_Name")
                != null ? main.getMenuOptions().getString("MemberOptions.Menu_Name")
                .replaceAll("&", "§")
                : "§8§nMember Control Menu");
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        this.inv.setItem(main.getMenuOptions().getInt("MemberOptions.Buttons.Close.Loc") - 1, this.close());
        this.inv.setItem(main.getMenuOptions().getInt("MemberOptions.Buttons.Back.Loc") - 1, this.back());
        this.inv.setItem(main.getMenuOptions().getInt("MemberOptions.Buttons.MemberOptionsMenuButton.Loc") - 1, this.memberOptions());
        this.inv.setItem(main.getMenuOptions().getInt("MemberOptions.Buttons.ModeratorOptionsMenuButton.Loc") - 1, this.modOptions());
        this.inv.setItem(main.getMenuOptions().getInt("MemberOptions.Buttons.AdminOptionsMenuButton.Loc") - 1, this.adminOptions());
    }

    private ItemStack memberOptions() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MemberOptions.Buttons.MemberOptionsMenuButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("MemberOptions.Buttons.MemberOptionsMenuButton.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("MemberOptions.Buttons.MemberOptionsMenuButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<memberSize>", String.valueOf(ABSTeam.getTeamByTeamUUID(this.team).getMembers().size())).replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack modOptions() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MemberOptions.Buttons.ModeratorOptionsMenuButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("MemberOptions.Buttons.ModeratorOptionsMenuButton.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("MemberOptions.Buttons.ModeratorOptionsMenuButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<modSize>", String.valueOf(ABSTeam.getTeamByTeamUUID(this.team).getMods().size())).replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack adminOptions() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MemberOptions.Buttons.AdminOptionsMenuButton.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("MemberOptions.Buttons.AdminOptionsMenuButton.Name").replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("MemberOptions.Buttons.AdminOptionsMenuButton.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("<adminSize>", String.valueOf(ABSTeam.getTeamByTeamUUID(this.team).getAdmins().size())).replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    private void inventoryClick(InventoryClickEvent e) {
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.memberOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_MEMBER_OPTIONS)) {
                                    MemberOptionsMenu menu = new MemberOptionsMenu(main, p, this, this.team);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.modOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_MODERATOR_OPTIONS)) {
                                    ModeratorOptionsMenu menu = new ModeratorOptionsMenu(main, p, this, this.team);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.adminOptions().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_ADMIN_OPTIONS)) {
                                    AdminOptionsMenu menu = new AdminOptionsMenu(main, p, this, this.team);
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
