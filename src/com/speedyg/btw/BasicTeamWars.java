package com.speedyg.btw;

import com.speedyg.btw.commands.Basic_Team_Wars_Command;
import com.speedyg.btw.events.Events;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.license.License;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.placeholder.MVdWPlaceHolderAPI;
import com.speedyg.btw.placeholder.PlaceHolderAPI;
import com.speedyg.btw.systems.SignMenuFactory;
import com.speedyg.btw.timer.Timer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class BasicTeamWars extends JavaPlugin {

    private static BasicTeamWars instance;
    private static Economy economy;
    private File messageFile;
    private FileConfiguration messageOptions;
    private File menuFile;
    private FileConfiguration menuOptions;
    private File spawnFile;
    private FileConfiguration spawnFileOptions;
    private SignMenuFactory menuFactory;
    private Timer timer;
    private File logFile;

    public BasicTeamWars() {
        instance = this;
    }

    public static BasicTeamWars getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Date date = new Date();
        String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        this.logFile = new File(this.getDataFolder() + "/logs", date.toString()
                .replaceAll(":", " ") + ".txt");

        if (!this.checkLicense()) {
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §6§lWarning! §cNot found licence! Please buy this plugin!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cIf you have licence but you receive this error,");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cyou can send a message to this address. §eDiscord: Yusuf#7761");
            this.log(LogLevel.ERROR, "Warning! Not found licence! Please buy this plugin!");
            this.log(LogLevel.ERROR, "If you have licence but you receive this error," +
                    " you can send a message to this address. Discord: Yusuf#7761");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §aLicense found!");
            this.log(LogLevel.INFO, "License activated!");
        }

        if (this.getServerVersion().equals(Version.UNSUPPORTED)) {
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cUnsupported version detected! §bYour Version: §e" + version);
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §6We have support, §e1.9.x, 1.10.x, 1.11.x, 1.12.x, 1.14.x, 1.15.x");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            this.log(LogLevel.ERROR, "Plugin disabled...");
            this.log(LogLevel.ERROR, "Unsupported version detected!");
            return;
        } else {
            this.log(LogLevel.INFO, "Plugin enabling...");
        }

        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cDepended plugins not found!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cPlugin was disabled!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §cDepended Plugins: §6" + this.getDescription().getDepend().toString());
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            this.log(LogLevel.ERROR, "Plugin disabled...");
            this.log(LogLevel.ERROR, "Depended plugins not found..!");
            this.log(LogLevel.ERROR, "Depended Plugins: " + this.getDescription().getDepend().toString());
            return;
        }

        this.getCommand("basicteamwars").setExecutor(new Basic_Team_Wars_Command(this));
        this.loadYMLFiles();
        new Events(this);
        this.loadDefaultSchematics();
        if (this.getConfig().getBoolean("Weekly_War_Active"))
            this.timer = new Timer();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPI().register();
            this.log(LogLevel.INFO, "PlaceholderAPI founded! Placeholders registered!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §aPlaceholderAPI founded! Placeholders registered.");
        }

        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            MVdWPlaceHolderAPI api = new MVdWPlaceHolderAPI();
            api.registerPlaceHolders();
            this.log(LogLevel.INFO, "MVdWPlaceHolderAPI founded! Placeholders registered!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §aMVdWPlaceHolderAPI founded! Placeholders registered.");
        }
        this.menuFactory = new SignMenuFactory(this);
        {
            int teamSize = System.getAllTeams().size();
            int claimSize = System.getAllClaims().size();
            int kitSize = System.getAllKits().size();
            int schematicSize = System.getAllSchematics().size();
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §aPlugin successfully enabled!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §b" + teamSize + "§7 teams loaded!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §b" + claimSize + "§7 claims loaded!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §b" + kitSize + "§7 kits loaded!");
            Bukkit.getConsoleSender().sendMessage("§e[§bBTW§e] §b" + schematicSize + "§7 schematics loaded!");

            this.log(LogLevel.INFO, "Plugin successfully enabled.");
            this.log(LogLevel.INFO, "Server version: " + this.getServerVersion().toString().toUpperCase());
            this.log(LogLevel.INFO, teamSize + " teams loaded!");
            this.log(LogLevel.INFO, claimSize + " claims loaded!");
            this.log(LogLevel.INFO, kitSize + " kits loaded!");
            this.log(LogLevel.INFO, schematicSize + " schematics loaded!");
        }
    }


    private boolean checkLicense() {
        License license;
        try {
            license = new License(InetAddress.getLocalHost().getHostAddress(), this.getDescription().getName());
            return license.getControl();
        } catch (IOException e) {
            return false;
        }
    }

    private void loadDefaultSchematics() {
        File sDir = new File(this.getDataFolder() + "/schematics");
        if (!sDir.exists()) {
            sDir.mkdirs();
            saveResource("schematics/house.obs", false);
            saveResource("schematics/kale1.obs", false);
            saveResource("schematics/kale2.obs", false);
            saveResource("schematics/kale3.obs", false);
            saveResource("schematics/kale4.obs", false);
            saveResource("schematics/Orman.obs", false);
            saveResource("schematics/unknown1.obs", false);
        }
    }

    public void log(LogLevel level, String log) {
        Date date = new Date();
        try {
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFile, true));
            writer.newLine();
            writer.write("[" + date.toString() + "/" + level.toString().toUpperCase() + "] " + log);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void loadYMLFiles() {
        this.saveDefaultConfig();
        this.messageFile = new File(this.getDataFolder(), "messages.yml");
        this.menuFile = new File(this.getDataFolder(), "menu_options.yml");
        this.spawnFile = new File(this.getDataFolder(), "spawn_options.yml");
        if (!this.messageFile.exists()) {
            this.messageFile.getParentFile().mkdirs();
            try {
                this.messageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!this.menuFile.exists()) {
            this.menuFile.getParentFile().mkdirs();
            saveResource("menu_options.yml", false);
        }
        if (!this.spawnFile.exists()) {
            this.spawnFile.getParentFile().mkdirs();
            saveResource("spawn_options.yml", false);
        }

        this.messageOptions = new YamlConfiguration();
        this.menuOptions = new YamlConfiguration();
        this.spawnFileOptions = new YamlConfiguration();
        try {
            this.messageOptions.load(this.messageFile);
            this.menuOptions.load(this.menuFile);
            this.spawnFileOptions.load(this.spawnFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Messages.loadMessages();
    }

    public FileConfiguration getMessageOptions() {
        return this.messageOptions;
    }

    public FileConfiguration getMenuOptions() {
        return this.menuOptions;
    }

    public FileConfiguration getSpawnFileOptions() {
        return this.spawnFileOptions;
    }

    public SignMenuFactory getMenuFactory() {
        return this.menuFactory;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Economy getEconomy() {
        return economy;
    }

    public void saveMessageFile() {
        try {
            this.messageOptions.save(this.messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSpawnOptionsFile() {
        try {
            this.spawnFileOptions.save(this.spawnFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfiguration() {
        this.reloadConfig();
        try {
            this.messageOptions.load(this.messageFile);
            this.menuOptions.load(this.menuFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Timer getTimer() {
        return timer;
    }

    public Version getServerVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        if (version.contains("1_9"))
            return Version.V1_9;
        else if (version.contains("1_10"))
            return Version.V1_10;
        else if (version.contains("1_11"))
            return Version.V1_11;
        else if (version.contains("1_12"))
            return Version.V1_12;
        else if (version.contains("1_14"))
            return Version.V1_14;
        else if (version.contains("1_15"))
            return Version.V1_15;
        else if (version.contains("1_16"))
            return Version.V1_16;
        else
            return Version.UNSUPPORTED;
    }
}
