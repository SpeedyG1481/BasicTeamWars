package com.speedyg.btw.menu.absclass;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.systems.Skull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class PageMenu {

    public HashMap<Integer, Inventory> inv = new HashMap<>();
    public int currentMenu;
    public int totalMenu;
    public Player p;
    public BasicTeamWars main;
    public Menu returnMenu;
    public PageMenu returnPageMenu;

    public PageMenu(BasicTeamWars main, Player p, Menu returnMenu) {
        this.main = main;
        this.currentMenu = 0;
        this.totalMenu = 0;
        this.returnMenu = returnMenu;
        this.p = p;
    }

    public PageMenu(BasicTeamWars main, Player p, PageMenu returnMenu) {
        this.main = main;
        this.currentMenu = 0;
        this.totalMenu = 0;
        this.returnPageMenu = returnMenu;
        this.p = p;
    }

    protected abstract void loadItems();

    public void openMenu() {
        this.loadItems();
        this.p.openInventory(this.inv.get(currentMenu));
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
