package com.speedyg.btw.team.player;

import com.speedyg.btw.*;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class created for Basic Team Wars plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 18.12.2019
 */

public class TeamPlayer {

    private OfflinePlayer player;
    private ABSTeam team;
    private List<String> permissions;
    private File playerFile;
    private int playerTotalKill;
    private int playerTotalDeaths;

    public TeamPlayer(OfflinePlayer player) {
        this.permissions = new ArrayList<>();
        this.player = player;
        this.team = null;
        this.playerTotalDeaths = 0;
        this.playerTotalKill = 0;
        this.playerFile = new File(BasicTeamWars.getInstance().getDataFolder() + "/players", player.getUniqueId().toString() + ".json");
    }

    public static TeamPlayer getPlayer(File playerFile) {
        try {
            FileReader reader = new FileReader(playerFile);
            JSONParser oop = new JSONParser();
            JSONObject jo = (JSONObject) oop.parse(reader);
            reader.close();
            TeamPlayer player = new TeamPlayer(Bukkit.getOfflinePlayer(UUID.fromString((String) jo.get("UUID"))));
            player.setPermissions((List<String>) jo.get("Permissions"));
            player.setPlayerTotalKill((int) (long) jo.get("Kills"));
            player.setPlayerTotalDeaths((int) (long) jo.get("Deaths"));
            if (jo.get("Team") != null)
                player.setTeam(ABSTeam.getTeamByTeamUUID(UUID.fromString((String) jo.get("Team"))));
            return player;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static TeamPlayer getPlayer(UUID playerUUID) {
        if (playerUUID != null) {
            File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/players",
                    playerUUID.toString() + ".json");
            if (file.exists())
                return TeamPlayer.getPlayer(file);
            else
                return new TeamPlayer(Bukkit.getOfflinePlayer(playerUUID));
        } else {
            return null;
        }
    }

    public static TeamPlayer getPlayer(OfflinePlayer player) {
        return TeamPlayer.getPlayer(player.getUniqueId());
    }


    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public ABSTeam getTeam() {
        return this.team;
    }

    public void savePlayer() {
        JSONObject json = new JSONObject();
        json.put("Username", player.getName());
        json.put("UUID", player.getUniqueId().toString());
        json.put("Kills", playerTotalKill);
        json.put("Deaths", playerTotalDeaths);
        json.put("Team", team != null ? team.getTeamUUID().toString() : null);
        json.put("TeamFile", team != null ? team.getTeamFile().getName() : null);
        json.put("Permissions", permissions);
        if (!playerFile.exists()) {
            try {
                playerFile.getParentFile().mkdirs();
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writer;
        try {
            writer = new FileWriter(playerFile);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addKill(int kill) {
        this.playerTotalKill += kill;
    }

    public void addKill() {
        this.playerTotalKill++;
    }

    public int getPlayerTotalKill() {
        return this.playerTotalKill;
    }

    public void setPlayerTotalKill(int totalKill) {
        this.playerTotalKill = totalKill;
    }

    public void addDeath(int death) {
        this.playerTotalDeaths += death;
    }

    public void addDeath() {
        this.playerTotalDeaths++;
    }

    public int getPlayerTotalDeaths() {
        return this.playerTotalDeaths;
    }

    public void setPlayerTotalDeaths(int totalDeath) {
        this.playerTotalDeaths = totalDeath;
    }

    public void addPermission(Permission perm) {
        this.permissions.add(perm.toString());
    }

    public void removePermission(Permission perm) {
        this.permissions.remove(perm.toString());
    }

    public void setTeam(ABSTeam team) {
        this.team = team;
    }

    public void setPermissions(List<String> perms) {
        this.permissions = perms;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    @Override
    public String toString() {
        return "\"" + player.getUniqueId().toString() + "\""; //player.getUniqueId();
    }


    public String KD() {
        double per = ((double) this.playerTotalKill / (double) (this.playerTotalDeaths > 0 ? this.playerTotalDeaths : 1));
        return new DecimalFormat("##.##").format(per).replace(",", ".") + "%";
    }


    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission.toString());
    }

    public void clearPlayerData() {
        permissions.clear();
        team = null;
        this.savePlayer();
    }
}
