package com.speedyg.btw.war;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WeeklyWar {

    private List<UUID> teamList;

    private int size;

    private WarTree[] wars;

    private int timeBetween;

    private Location team1_Loc;
    private Location team2_Loc;
    private Location spawn_loc;

    public WeeklyWar() {
        this.teamList = System.getWarJoinedTeams();
        this.size = teamList.size();
        this.wars = new WarTree[(this.teamList.size() / 2) + 1];
        this.team1_Loc = stringToLoc(BasicTeamWars.getInstance().getSpawnFileOptions().getString("Team1_Spawn_Location"));
        this.team2_Loc = stringToLoc(BasicTeamWars.getInstance().getSpawnFileOptions().getString("Team2_Spawn_Location"));
        this.spawn_loc = stringToLoc(BasicTeamWars.getInstance().getSpawnFileOptions().getString("Spawn_Location"));
        this.timeBetween = (BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Between_War_Time") > 0 ? BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Between_War_Time") : 1800);
        if (team1_Loc != null && team2_Loc != null && spawn_loc != null)
            this.makeWarTree();
        else {
            Bukkit.getConsoleSender().sendMessage("§cWar is cancelled!");
            Bukkit.getConsoleSender().sendMessage("§cPlease set spawn locations and reload plugin for start war!");
        }
    }

    public int getTimeBetween() {
        return timeBetween;
    }

    private Location stringToLoc(String string) {
        String[] spl = string.split(",");
        World w = Bukkit.getWorld(spl[0]);
        double x = Double.parseDouble(spl[1]);
        double y = Double.parseDouble(spl[2]);
        double z = Double.parseDouble(spl[3]);
        float yaw = (float) Double.parseDouble(spl[4]);
        float pitch = (float) Double.parseDouble(spl[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public WarTree[] getWars() {
        return wars;
    }

    public WarTree findTree(UUID teamUUID) {
        if (wars != null) {
            for (WarTree tree : wars) {
                if (tree.getTeam1() != null && tree.getTeam1().getTeamUUID().equals(teamUUID)) {
                    return tree;
                } else if (tree.getTeam2() != null && tree.getTeam2().getTeamUUID().equals(teamUUID)) {
                    return tree;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Deprecated
    private void makeWarTree() {
        int i = 0;
        Date date;
        while (!teamList.isEmpty()) {
            UUID team1 = null;
            UUID team2 = null;
            date = new Date();
            date.setTime((date.getTime() + ((i + 1) * timeBetween * 1000)));
            if (0 < teamList.size())
                team1 = teamList.get(0);
            if (1 < teamList.size())
                team2 = teamList.get(1);

            this.wars[i] = new WarTree(team1, team2, date, this);

            if (team1 != null)
                this.teamList.remove(team1);
            if (team2 != null)
                this.teamList.remove(team2);
            i++;
        }
    }

    Location getTeam2_Loc() {
        return team2_Loc;
    }

    Location getTeam1_Loc() {
        return team1_Loc;
    }

    Location spawnLoc() {
        return this.spawn_loc;
    }

    public int getSize() {
        return size;
    }

    public List<UUID> getTeamList() {
        return this.teamList;
    }

}

