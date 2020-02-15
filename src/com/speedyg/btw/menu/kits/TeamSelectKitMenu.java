package com.speedyg.btw.menu.kits;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.absclass.Menu;
import com.speedyg.btw.menu.absclass.PageMenu;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.Skull;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.kits.TeamKit;
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

public class TeamSelectKitMenu extends PageMenu implements Listener {

    private List<String> kits;
    private UUID team;

    public TeamSelectKitMenu(BasicTeamWars main, Player p, UUID team, Menu returnMenu) {
        super(main, p, returnMenu);
        this.kits = System.getAllKits();
        this.team = team;
        this.totalMenu = (this.kits.size() / 45) + 1;
        for (int i = 0; i < totalMenu; i++) {
            this.inv.put(i, Bukkit.createInventory(null, 54,
                    main.getMenuOptions().getString("KitSelectionMenu.Menu_Name") != null
                            ? main.getMenuOptions().getString("KitSelectionMenu.Menu_Name")
                            .replaceAll("&", "§").replaceAll("<page>", String.valueOf(i + 1))
                            : "§8Kit Selection Menu : Page §e" + (i + 1)));
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
            if (queue >= this.kits.size()) {
                break;
            }
            this.inv.get(currentMenu).setItem(a, this.readItem(kits.get(queue), queue));
            queue++;
        }
    }

    private ItemStack readItem(String kitUUID, int queue) {
        ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
        TeamKit kit = TeamKit.getTeamKit(UUID.fromString(kitUUID));
        int perPersonCost = kit.getCost();
        String name = kit.getName().replaceAll("&", "§");
        int minLevel = kit.getMinLevel();
        int generalCost = 0;
        boolean isAdminKit = team.getAdminsWarKit() != null && team.getAdminsWarKit().equals(kitUUID);
        boolean isModeratorKit = team.getModeratorsWarKit() != null && team.getModeratorsWarKit().equals(kitUUID);
        boolean isMemberKit = team.getMembersWarKit() != null && team.getMembersWarKit().equals(kitUUID);
        if (isAdminKit) {
            generalCost += team.getAdminWarriors().size() * perPersonCost;
        }
        if (isModeratorKit) {
            generalCost += team.getModeratorWarriors().size() * perPersonCost;
        }
        if (isMemberKit) {
            generalCost += team.getMemberWarriors().size() * perPersonCost;
        }
        ItemStack item = Skull.getCustomSkull(main.getMenuOptions().getString("KitSelectionMenu.Buttons.ReadItem.Icon"));
        ItemMeta iMeta = item.getItemMeta();
        iMeta.setDisplayName(main.getMenuOptions().getString("KitSelectionMenu.Buttons.ReadItem.Name")
                .replaceAll("<id>", String.valueOf(queue + 1))
                .replaceAll("&", "§"));
        List<String> tLore = main.getMenuOptions().getStringList("KitSelectionMenu.Buttons.ReadItem.Lore");
        ArrayList<String> rLore = new ArrayList<>();
        for (String add : tLore)
            rLore.add(add
                    .replaceAll("<reqTeamLevel>", String.valueOf(minLevel))
                    .replaceAll("<kitCostGenerally>", String.valueOf(generalCost))
                    .replaceAll("<kitCostPerPerson>", String.valueOf(perPersonCost))
                    .replaceAll("<kitName>", name)
                    .replaceAll("&", "§"));
        if (isAdminKit || isMemberKit || isModeratorKit) {
            if (isMemberKit)
                rLore.add(main.getMenuOptions().getString("KitSelectionMenu.Status.Member-Kit").replaceAll("&", "§"));
            if (isModeratorKit)
                rLore.add(main.getMenuOptions().getString("KitSelectionMenu.Status.Mod-Kit").replaceAll("&", "§"));
            if (isAdminKit)
                rLore.add(main.getMenuOptions().getString("KitSelectionMenu.Status.Admin-Kit").replaceAll("&", "§"));
        }
        iMeta.setLore(rLore);
        item.setItemMeta(iMeta);
        return item;
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
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.back().getItemMeta().getDisplayName())) {
                                returnMenu.openMenu();
                            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.readItem(this.kits.get((currentMenu * 45) + e.getSlot()), (currentMenu * 45) + e.getSlot()).getItemMeta().getDisplayName())) {
                                UUID kitUUID = UUID.fromString(this.kits.get((currentMenu * 45) + e.getSlot()));
                                ABSTeam team = ABSTeam.getTeamByTeamUUID(this.team);
                                if (e.getClick().equals(ClickType.RIGHT)) {
                                    if (team.getLevel() >= TeamKit.getTeamKit(kitUUID).getMinLevel()) {
                                        if (team.getModeratorsWarKit() != null && team.getModeratorsWarKit().equalsIgnoreCase(kitUUID.toString()))
                                            team.setModeratorsWarKit(null);
                                        else
                                            team.setModeratorsWarKit(kitUUID.toString());
                                        team.saveTeam();
                                        this.openMenu();
                                    } else {
                                        p.sendMessage(Messages.team_level_not_enough);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.LEFT)) {
                                    if (team.getLevel() >= TeamKit.getTeamKit(kitUUID).getMinLevel()) {
                                        if (team.getMembersWarKit() != null && team.getMembersWarKit().equalsIgnoreCase(kitUUID.toString()))
                                            team.setMembersWarKit(null);
                                        else
                                            team.setMembersWarKit(kitUUID.toString());
                                        team.saveTeam();
                                        this.openMenu();
                                    } else {
                                        p.sendMessage(Messages.team_level_not_enough);
                                        p.closeInventory();
                                    }
                                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                    KitShowMenu menu = new KitShowMenu(main, p, this.team, TeamKit.getTeamKit(kitUUID), this);
                                    menu.openMenu();
                                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                                    if (team.getLevel() >= TeamKit.getTeamKit(kitUUID).getMinLevel()) {
                                        if (team.getAdminsWarKit() != null && team.getAdminsWarKit().equalsIgnoreCase(kitUUID.toString()))
                                            team.setAdminsWarKit(null);
                                        else
                                            team.setAdminsWarKit(kitUUID.toString());
                                        team.saveTeam();
                                        this.openMenu();
                                    } else {
                                        p.sendMessage(Messages.team_level_not_enough);
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
