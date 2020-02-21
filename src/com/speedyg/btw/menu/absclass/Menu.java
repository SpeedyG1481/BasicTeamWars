package com.speedyg.btw.menu.absclass;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.systems.Skull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Menu {

    public UUID team;
    public Menu returnMenu;
    public PageMenu returnPageMenu;
    protected Player p;
    protected Inventory inv;
    protected BasicTeamWars main;

    protected Menu(BasicTeamWars btw, Player p, UUID team, Menu returnMenu) {
        this.main = btw;
        this.team = team;
        this.returnMenu = returnMenu;
        this.p = p;
    }

    protected Menu(BasicTeamWars btw, Player p, UUID team, PageMenu returnMenu) {
        this.main = btw;
        this.team = team;
        this.returnPageMenu = returnMenu;
        this.p = p;
    }

    protected abstract void loadItems();

    public void openMenu() {
        this.loadItems();
        Bukkit.getScheduler().runTask(BasicTeamWars.getInstance(), () -> {
            this.p.openInventory(this.inv);
        });

    }

    protected ItemStack previous() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("General.Buttons.Previous.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("General.Buttons.Previous.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("General.Buttons.Previous.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    protected ItemStack next() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("General.Buttons.Next.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("General.Buttons.Next.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("General.Buttons.Next.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    protected ItemStack close() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("General.Buttons.Close.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("General.Buttons.Close.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("General.Buttons.Close.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    protected ItemStack back() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("General.Buttons.Back.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("General.Buttons.Back.Name").replaceAll("&", "§"));
        List<String> tlore = main.getMenuOptions().getStringList("General.Buttons.Back.Lore");
        ArrayList<String> lore = new ArrayList<String>();
        for (String add : tlore) {
            lore.add(add.replaceAll("&", "§"));
        }
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }
}
