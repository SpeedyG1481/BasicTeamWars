package com.speedyg.btw.menu.permission.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.PageMenu;
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

public class ChangeModeratorPermissionsMenu extends PageMenu implements Listener {

    private List<Permission> permissionList;
    private UUID team;

    public ChangeModeratorPermissionsMenu(BasicTeamWars main, Player p, UUID team, PageMenu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.currentMenu = 0;
        permissionList = new ArrayList<>();
        for (Permission add : Permission.values())
            permissionList.add(add);
        this.totalMenu = ((permissionList.size() / 45) + 1);
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("ChangeModeratorPermissions.Menu_Name") != null
                            ? main.getMenuOptions().getString("ChangeModeratorPermissions.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Moderator Permissions : Page §e" + (i + 1)));
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
            if (queue >= this.permissionList.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(permissionList.get(queue)));
            queue++;
        }
    }

    private ItemStack readItem(Permission perm) {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        String url = "";
        if (team.getModPerms().contains(perm.toString())) {
            url = main.getMenuOptions().getString("ChangeModeratorPermissions.Buttons.ReadItem.True-Icon");
        } else {
            url = main.getMenuOptions().getString("ChangeModeratorPermissions.Buttons.ReadItem.False-Icon");
        }
        ItemStack item = Skull.getCustomSkull(url);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(main.getMenuOptions().getString("ChangeModeratorPermissions.Buttons.ReadItem.Name")
                .replaceAll("&", "§").replaceAll("<permName>", perm.getPermissionLoader().getName())
                .replaceAll("<hasPermission>", String.valueOf(team.getModPerms().contains(perm.toString()))));
        ArrayList<String> rLore = new ArrayList<>();
        List<String> tLore = main.getMenuOptions().getStringList("ChangeModeratorPermissions.Buttons.ReadItem.Lore");
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§").replaceAll("<description>", perm.getPermissionLoader().getDescription())
                    .replaceAll("<hasPermission>", String.valueOf(team.getModPerms().contains(perm.toString()))));
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
                                this.returnPageMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.previous().getItemMeta().getDisplayName())) {
                                currentMenu--;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.next().getItemMeta().getDisplayName())) {
                                currentMenu++;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.readItem(this.permissionList.get((currentMenu * 45) + e.getSlot())).getItemMeta().getDisplayName())) {
                                Permission perm = this.permissionList.get((currentMenu * 45) + e.getSlot());
                                if (team.getModPerms().contains(perm.toString()))
                                    team.removeModPerm(perm);
                                else
                                    team.addModPerm(perm);
                                team.saveTeam();
                                this.openMenu();
                            }
                        }
                    }
                }
            }
        }
    }

}