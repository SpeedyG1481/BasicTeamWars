package com.speedyg.btw;

import com.speedyg.btw.commands.Basic_Team_Wars_Command;
import com.speedyg.btw.events.Events;
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

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

    public BasicTeamWars() {
        instance = this;
    }

    public static BasicTeamWars getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("§cDepended plugins not found!");
            Bukkit.getConsoleSender().sendMessage("§cPlugin was disabled!");
            Bukkit.getConsoleSender().sendMessage("§cDepended Plugins: §6" + this.getDescription().getDepend().toString());
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getCommand("basicteamwars").setExecutor(new Basic_Team_Wars_Command(this));
        this.loadYMLFiles();
        this.menuFactory = new SignMenuFactory(this);
        new Events(this);
        this.loadDefaultSchematics();
        if (this.getConfig().getBoolean("Weekly_War_Active"))
            this.timer = new Timer();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPI().register();
        }

        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            MVdWPlaceHolderAPI api = new MVdWPlaceHolderAPI();
            api.registerPlaceHolders();
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

    public void log(Level level, String log) {
        Logger logger = Logger.getLogger("Logger");
        FileHandler fh;

        try {
            fh = new FileHandler(this.getDataFolder() + "/log/log.txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.log(level, log);

        } catch (SecurityException | IOException e) {
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
}
