package com.speedyg.btw.menu.permission.upmenu;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.menu.permission.general.ChangeMemberPermissionsMenu;
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

public class MemberOptionsMenu extends PageMenu implements Listener {

    private UUID team;
    private List<String> members;

    public MemberOptionsMenu(BasicTeamWars main, Player p, Menu returnMenu, UUID team) {
        super(main, p, returnMenu);
        this.team = team;
        this.members = ABSTeam.getTeamByTeamUUID(this.team).getMembers();
        for (String remove : ABSTeam.getTeamByTeamUUID(this.team).getMods())
            members.remove(remove);
        for (String remove : ABSTeam.getTeamByTeamUUID(this.team).getAdmins())
            members.remove(remove);
        this.currentMenu = 0;
        this.totalMenu = (members.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++)
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("MemberOptionListMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("MemberOptionListMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Member List : Page §e" + (i + 1)));
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    @Override
    protected void loadItems() {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        this.members = team.getMembers();
        if (currentMenu != 0)
            this.inv.get(currentMenu).setItem(45, this.previous());
        else
            this.inv.get(currentMenu).setItem(45, this.back());
        if (currentMenu < this.totalMenu - 1) {
            this.inv.get(currentMenu).setItem(53, this.next());
        } else {
            this.inv.get(currentMenu).setItem(53, this.close());
        }

        this.inv.get(currentMenu).setItem(49, this.memberSettings());

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.members.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(UUID.fromString(members.get(queue))));
            queue++;
        }
    }

    private ItemStack memberSettings() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MemberOptionListMenu.Buttons.MemberPermissionSettings.Icon"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("MemberOptionListMenu.Buttons.MemberPermissionSettings.Name")
                .replaceAll("&", "§"));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("MemberOptionListMenu.Buttons.MemberPermissionSettings.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        meta.setLore(rLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack readItem(UUID teamPlayer) {
        ItemStack item = Skull.getPlayerSkull(TeamPlayer.getPlayer(teamPlayer).getPlayer().getName());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("MemberOptionListMenu.Buttons.ReadItem.Name")
                .replaceAll("&", "§")
                .replaceAll("<pName>", TeamPlayer.getPlayer(teamPlayer).getPlayer().getName()));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("MemberOptionListMenu.Buttons.ReadItem.Lore");
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.memberSettings().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CHANGE_MEMBERS_PERMISSIONS)) {
                                    ChangeMemberPermissionsMenu menu = new ChangeMemberPermissionsMenu(main, p, this.team, this);
                                    menu.openMenu();
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                    p.closeInventory();
                                }
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.readItem(UUID.fromString(this.members.get((currentMenu * 45) + e.getSlot()))).getItemMeta().getDisplayName())) {
                                if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    if (team.hasPermission(p, Permission.PROMOTE_MODERATOR_PERMISSION)) {
                                        team.addModerator(UUID.fromString(this.members.get((currentMenu * 45) + e.getSlot())));
                                        team.saveTeam();
                                        p.sendMessage(Messages.promote_success_to_moderator);
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                    p.closeInventory();
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (team.hasPermission(p, Permission.KICK_PLAYER)) {
                                        if (team.isBigger(p, UUID.fromString(this.members.get((currentMenu * 45) + e.getSlot())))) {
                                            team.removeMember(UUID.fromString(this.members.get((currentMenu * 45) + e.getSlot())));
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
                                        ChangePlayerPrivatePermissionMenu menu = new ChangePlayerPrivatePermissionMenu(main, p, this.team, this, UUID.fromString(this.members.get((currentMenu * 45) + e.getSlot())));
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
}
