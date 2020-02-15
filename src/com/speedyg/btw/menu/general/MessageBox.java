package com.speedyg.btw.menu.general;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
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

public class MessageBox extends PageMenu implements Listener {

    private List<String> messages;
    private UUID team;

    public MessageBox(BasicTeamWars main, Player p, UUID team, Menu returnMenu) {
        super(main, p, returnMenu);
        this.team = team;
        this.messages = ABSTeam.getTeamByTeamUUID(this.team).getMessageBox();
        this.totalMenu = (this.messages.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("MessageBox.Menu_Name") != null
                            ? main.getMenuOptions().getString("MessageBox.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Team Messages : Page §e" + (i + 1)));
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

        this.inv.get(currentMenu).setItem(50, this.markAllRead());
        this.inv.get(currentMenu).setItem(49, this.clearMessageBox());
        this.inv.get(currentMenu).setItem(48, this.clearReadMessage());

        int queue = 45 * currentMenu;
        for (int a = 0; a < this.inv.get(currentMenu).getSize() - 9; a++) {
            if (queue >= this.messages.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(messages.get(queue), queue));
            queue++;
        }
    }

    private ItemStack clearMessageBox() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MessageBox.Buttons.Clear.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("MessageBox.Buttons.Clear.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("MessageBox.Buttons.Clear.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack markAllRead() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MessageBox.Buttons.MarkRead.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("MessageBox.Buttons.MarkRead.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("MessageBox.Buttons.MarkRead.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }

    private ItemStack clearReadMessage() {
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("MessageBox.Buttons.ClearReadMessage.Icon"));
        ItemMeta imeta = item.getItemMeta();
        imeta.setDisplayName(main.getMenuOptions().getString("MessageBox.Buttons.ClearReadMessage.Name").replaceAll("&", "§"));
        ArrayList<String> lore = new ArrayList<>();
        List<String> tlore = main.getMenuOptions().getStringList("MessageBox.Buttons.ClearReadMessage.Lore");
        for (String add : tlore)
            lore.add(add.replaceAll("&", "§"));
        imeta.setLore(lore);
        item.setItemMeta(imeta);
        return item;
    }


    private ItemStack readItem(String message, int queue) {
        ItemStack item;
        if (message.split(":")[0].equals("0")) {
            item = Skull.getCustomSkull(main.getMenuOptions().getString("MessageBox.Buttons.ReadItem.Read-Icon"));
        } else {
            item = Skull.getCustomSkull(main.getMenuOptions().getString("MessageBox.Buttons.ReadItem.Unread-Icon"));
        }

        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("MessageBox.Buttons.ReadItem.Name").replaceAll("<messageNo>", String.valueOf(queue + 1))
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("MessageBox.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String add : tLore)
            rLore.add(add.replaceAll("&", "§"));
        iMeta.setLore(rLore);
        item.setItemMeta(iMeta);
        return item;
    }

    @EventHandler
    @Deprecated
    private void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getInventory().equals(this.inv.get(currentMenu))) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().hasItemMeta()) {
                        if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                            ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.close().getItemMeta().getDisplayName())) {
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.previous().getItemMeta().getDisplayName())) {
                                this.currentMenu--;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.next().getItemMeta().getDisplayName())) {
                                this.currentMenu++;
                                this.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.clearMessageBox().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CLEAR_ALL_MESSAGES_OF_TEAM)) {
                                    if (!team.getMessageBox().isEmpty()) {
                                        team.clearMessageBox();
                                        team.saveTeam();
                                        p.sendMessage(Messages.message_box_has_been_cleared);
                                    } else {
                                        p.sendMessage(Messages.already_empty_messages);
                                    }
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.clearReadMessage().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.CLEAR_READ_MESSAGES_OF_TEAM)) {
                                    if (!team.getMessageBox().isEmpty()) {
                                        team.clearReadMessages();
                                        team.saveTeam();
                                        p.sendMessage(Messages.clear_read_messages_success);
                                    } else {
                                        p.sendMessage(Messages.already_empty_messages);
                                    }
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.markAllRead().getItemMeta().getDisplayName())) {
                                if (team.hasPermission(p, Permission.MARK_MESSAGES_READ)) {
                                    if (!team.getMessageBox().isEmpty()) {
                                        team.markMessagesRead();
                                        team.saveTeam();
                                        p.sendMessage(Messages.mark_messages_read_success);
                                    } else {
                                        p.sendMessage(Messages.already_empty_messages);
                                    }
                                } else {
                                    p.sendMessage(Messages.insufficient_permissions);
                                }
                                p.closeInventory();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                                    this.readItem(this.messages.get((currentMenu * 45) + e.getSlot()), (currentMenu * 45) + e.getSlot()).getItemMeta().getDisplayName())) {
                                String message = this.messages.get((currentMenu * 45) + e.getSlot());
                                if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (team.hasPermission(p, Permission.READ_TEAM_MESSAGES)) {
                                        System.openBook(p, message);
                                        team.readMessage(message);
                                        team.saveTeam();
                                    } else {
                                        p.sendMessage(Messages.insufficient_permissions);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.hasPermission(p, Permission.MARK_AS_READ)) {
                                        team.readMessage(message);
                                        team.saveTeam();
                                        this.openMenu();
                                    } else {
                                        p.closeInventory();
                                        p.sendMessage(Messages.insufficient_permissions);
                                    }
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (team.hasPermission(p, Permission.REMOVE_TEAM_MESSAGES)) {
                                        team.removeMessage(message);
                                        team.saveTeam();
                                        p.sendMessage(Messages.message_removed);
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
