package com.speedyg.btw.commands;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.menu.*;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Logo;
import com.speedyg.btw.team.kits.TeamKit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class Basic_Team_Wars_Command implements CommandExecutor {

    private BasicTeamWars main;

    public Basic_Team_Wars_Command(BasicTeamWars basicTeamWars) {
        this.main = basicTeamWars;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("yardim")) {
                    p.sendMessage("§8§l*---------------------------*");
                    p.sendMessage("§7/" + label + " §amake §8*§7 This command opens the team make menu.");
                    p.sendMessage("§7/" + label + " §ateam §8*§7 This command opens the team menu.");
                    p.sendMessage("§7/" + label + " §ateams §8*§7 This command opens the team list menu.");
                    p.sendMessage("§7/" + label + " §awars §8*§7 This command opens the wars menu.");
                    if (p.isOp() || p.hasPermission("btw.admin")) {
                        p.sendMessage("§7/" + label + " §areload §8*§7 Command will be reload config, menu options, messages...");
                        p.sendMessage("§7/" + label + " §asetspawn §8*§7 This command save end of war teleport location.");
                        p.sendMessage("§7/" + label + " §asetspawn §bteam_1 §8*§7 This command save start of war teleport location for team_1.");
                        p.sendMessage("§7/" + label + " §asetspawn §bteam_2 §8*§7 This command save start of war teleport location for team_2.");
                        p.sendMessage("§7/" + label + " §akit <kitName> §8*§7 This command will be open the kit edit menu.");
                    }
                    p.sendMessage("§8§l*---------------------------*");
                } else if (args[0].equalsIgnoreCase("maketeam")
                        || args[0].equalsIgnoreCase("make")
                        || args[0].equalsIgnoreCase("olustur")) {
                    if (System.getPlayerTeamFile(p) == null) {
                        TeamMakeMenu menu = new TeamMakeMenu(main, p, null);
                        menu.openMenu();
                    } else {
                        p.sendMessage(Messages.have_team_message);
                    }
                } else if (args[0].equalsIgnoreCase("team")
                        || args[0].equalsIgnoreCase("takim")
                        || args[0].equalsIgnoreCase("ekip")) {
                    if (System.getPlayerTeamFile(p) != null) {
                        TeamMenu menu = new TeamMenu(main, p, ABSTeam.getFileToTeam(System.getPlayerTeamFile(p)).getTeamUUID());
                        menu.openMenu();
                    } else {
                        p.sendMessage(Messages.not_have_team_message);
                    }
                } else if (args[0].equalsIgnoreCase("teams")
                        || args[0].equalsIgnoreCase("takimlar")
                        || args[0].equalsIgnoreCase("ekipler")) {
                    ListTeamsMenu menu = new ListTeamsMenu(main, p, null);
                    menu.openMenu();
                } else if (args[0].equalsIgnoreCase("wars")
                        || args[0].equalsIgnoreCase("savaslar")
                        || args[0].equalsIgnoreCase("savas")) {
                    if (main.getConfig().getBoolean("Weekly_War_Active")) {
                        Wars menu = new Wars(main, p, null);
                        menu.openMenu();
                    } else {
                        p.sendMessage(Messages.wars_disabled);
                    }
                } else if (args[0].equalsIgnoreCase("reload")
                        || args[0].equalsIgnoreCase("yenile")) {
                    if (p.hasPermission("btw.admin")) {
                        main.reloadConfiguration();
                        p.sendMessage(Messages.reload_success);
                    } else {
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                } else if (args[0].equalsIgnoreCase("setspawn")
                        || args[0].equalsIgnoreCase("spawnbelirle")) {
                    if (p.hasPermission("btw.admin")) {
                        String w = p.getLocation().getWorld().getName();
                        double x = p.getLocation().getX();
                        double y = p.getLocation().getY();
                        double z = p.getLocation().getZ();
                        double yaw = p.getLocation().getYaw();
                        double pitch = p.getLocation().getPitch();
                        final String value = w + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
                        main.getSpawnFileOptions().set("Spawn_Location", value);
                        main.saveSpawnOptionsFile();
                        p.sendMessage(Messages.spawn_location_saved);
                    } else {
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                } else {
                    p.sendMessage(Messages.wrong_usage);
                }
            } else if (args.length == 2) {
                String w = p.getLocation().getWorld().getName();
                double x = p.getLocation().getX();
                double y = p.getLocation().getY();
                double z = p.getLocation().getZ();
                double yaw = p.getLocation().getYaw();
                double pitch = p.getLocation().getPitch();
                final String value = w + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
                if (args[0].equalsIgnoreCase("setspawn") && args[1].equalsIgnoreCase("team_1")) {
                    if (p.hasPermission("btw.admin")) {
                        main.getSpawnFileOptions().set("Team1_Spawn_Location", value);
                        main.saveSpawnOptionsFile();
                        p.sendMessage(Messages.spawn_location_saved);
                    } else {
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                } else if (args[0].equalsIgnoreCase("setspawn") && args[1].equalsIgnoreCase("team_2")) {
                    if (p.hasPermission("btw.admin")) {
                        main.getSpawnFileOptions().set("Team2_Spawn_Location", value);
                        main.saveSpawnOptionsFile();
                        p.sendMessage(Messages.spawn_location_saved);
                    } else {
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                } else if (args[0].equalsIgnoreCase("kit") || args[0].equalsIgnoreCase("ekipman")) {
                    if (p.hasPermission("btw.admin")) {
                        TeamKit kit = new TeamKit(args[1], null);
                        TeamKitsEditMenu menu = new TeamKitsEditMenu(main, p, null, null, kit);
                        menu.openMenu();
                    } else {
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                } else {
                    p.sendMessage(Messages.wrong_usage);
                }
            } else {
                p.sendMessage(Messages.wrong_usage);
            }
        } else {
            sender.sendMessage(Messages.just_player);
        }
        return false;
    }

}
