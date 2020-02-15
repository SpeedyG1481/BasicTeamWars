package com.speedyg.btw.menu.absclass;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.systems.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class VerificationMenu {

    public Inventory inventory;
    public Player p;
    public BasicTeamWars main;
    public Menu returnMenu;
    public UUID team;

    public VerificationMenu(BasicTeamWars basicTeamWars, Player p, Menu returnMenu, UUID team) {
        this.main = basicTeamWars;
        this.p = p;
        this.returnMenu = returnMenu;
        this.team = team;
    }

    public abstract ItemStack accept();

    public abstract ItemStack reject();

    public abstract void loadItems();

    public void openMenu() {
        this.loadItems();
        this.p.openInventory(this.inventory);
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
