package com.speedyg.btw.menu;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.war.WarTree;
import com.speedyg.btw.war.WeeklyWar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Wars extends PageMenu implements Listener {

    private WeeklyWar war;
    private WarTree[] wars;

    public Wars(BasicTeamWars main, Player p, Menu returnMenu) {
        super(main, p, returnMenu);
        this.war = main.getTimer().getWar();
        this.wars = this.war.getWars();
        this.totalMenu = (this.wars.length / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("WarsMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("WarsMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Wars : Page §e" + (i + 1)));
        }
        Bukkit.getServer().getPluginManager().registerEvents(this, main);
    }

    private ItemStack readItem(WarTree tree, int i) {
        if (tree != null) {
            if (tree.getTeam1() != null && tree.getTeam2() != null) {
                Date now = new Date();
                boolean isPass = (now.getTime() > ((war.getTimeBetween() * 1000)) + tree.getWarTime().getTime());
                boolean isProgress = now.getTime() < (war.getTimeBetween() * 1000) + tree.getWarTime().getTime() && now.getTime() > tree.getWarTime().getTime();
                boolean isWaiting = now.getTime() < tree.getWarTime().getTime();
                String status = isPass ? main.getMenuOptions().getString("WarsMenu.Status.Pass") : isProgress ? main.getMenuOptions().getString("WarsMenu.Status.In-War") : isWaiting ? main.getMenuOptions().getString("WarsMenu.Status.Waiting") : "NULL";
                ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarsMenu.Buttons.ReadItem.Icon"));
                ItemMeta iMeta = item.getItemMeta();
                iMeta.setDisplayName(main.getMenuOptions().getString("WarsMenu.Buttons.ReadItem.Name").replaceAll("<warNo>", String.valueOf(i + 1))
                        .replaceAll("&", "§"));
                List<String> tLore = main.getMenuOptions().getStringList("WarsMenu.Buttons.ReadItem.Lore");
                ArrayList<String> rLore = new ArrayList<>();
                for (String add : tLore)
                    rLore.add(add
                            .replaceAll("<warTime>", tree.getWarTime().toString())
                            .replaceAll("<name_1>", tree.getTeam1().getName())
                            .replaceAll("<name_2>", tree.getTeam2().getName())
                            .replaceAll("<level_1>", String.valueOf(tree.getTeam1().getLevel()))
                            .replaceAll("<level_2>", String.valueOf(tree.getTeam2().getLevel()))
                            .replaceAll("<status>", status.replaceAll("&", "§"))
                            .replaceAll("&", "§"));
                iMeta.setLore(rLore);
                item.setItemMeta(iMeta);
                return item;
            }
        }
        return null;
    }

    private ItemStack infoOtherWar() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("WarsMenu.Buttons.OtherWarInfo.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("WarsMenu.Buttons.OtherWarInfo.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("WarsMenu.Buttons.OtherWarInfo.Lore");
        ArrayList<String> lore = new ArrayList<>();
        for (String add : tlore) {
            lore.add(add
                    .replaceAll("<date>", main.getTimer().nextWarDate().toString())
                    .replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    @Override
    protected void loadItems() {
        if (currentMenu != 0)
            this.inv.get(currentMenu).setItem(45, this.previous());
        else
            this.inv.get(currentMenu).setItem(45, this.close());
        if (currentMenu < this.totalMenu - 1) {
            this.inv.get(currentMenu).setItem(53, this.next());
        } else {
            this.inv.get(currentMenu).setItem(53, this.close());
        }

        this.inv.get(currentMenu).setItem(49, this.infoOtherWar());

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.wars.length) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(wars[queue], queue));
            queue++;
        }
    }


    @EventHandler
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
                            }
                        }
                    }
                }
            }
        }
    }

}
