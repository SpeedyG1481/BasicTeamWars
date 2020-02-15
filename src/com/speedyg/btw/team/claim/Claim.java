package com.speedyg.btw.team.claim;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.team.ABSTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * This class created for Basic Team Wars plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 25.12.2019
 */

public class Claim {

    private int MAX_EXPANSION_SIZE;

    private UUID claimUUID;
    private int totalBlocks;
    private ABSTeam claimOwner;
    private File claimFile;
    private Location center;

    public Claim(ABSTeam team, UUID claimUUID, Location center) {
        this.claimOwner = team;
        this.center = center;
        if (claimUUID == null)
            this.claimUUID = UUID.randomUUID();
        else
            this.claimUUID = claimUUID;
        this.claimFile = new File(BasicTeamWars.getInstance().getDataFolder() + "/claims", this.claimUUID.toString() + ".json");
        MAX_EXPANSION_SIZE = BasicTeamWars.getInstance().getConfig().getInt("Max_Expansion_Size");
        this.totalBlocks = (int) Math.pow(team.getMaxClaimSize(), 2) * center.getWorld().getMaxHeight();
    }


    public static Claim getClaim(UUID uuid) {
        return Claim.getClaim(new File(BasicTeamWars.getInstance().getDataFolder() + "/claims", uuid.toString() + ".json"));
    }

    public static Claim getClaim(File claimFile) {
        try {
            FileReader reader = new FileReader(claimFile);
            JSONParser oop = new JSONParser();
            JSONObject jo = (JSONObject) oop.parse(reader);
            reader.close();
            Location loc = new Location(Bukkit.getWorld(((String) jo.get("Center")).split(",")[0]),
                    Integer.parseInt(((String) jo.get("Center")).split(",")[1]),
                    Integer.parseInt(((String) jo.get("Center")).split(",")[2]),
                    Integer.parseInt(((String) jo.get("Center")).split(",")[3]));
            Claim claim = new Claim(ABSTeam.getTeamByTeamUUID(UUID.fromString((String) jo.get("Owner-Team"))), UUID.fromString((String) jo.get("UUID")), loc);
            return claim;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveClaim() {
        JSONObject json = new JSONObject();
        json.put("Center", center.getWorld().getName() + "," + center.getBlockX() + "," + center.getBlockY() + "," + center.getBlockZ());
        json.put("UUID", claimUUID.toString());
        json.put("Total-Block-Count", totalBlocks);
        json.put("Size", this.claimOwner.getMaxClaimSize());
        json.put("Owner-Team", claimOwner.getTeamUUID().toString());
        if (!claimFile.exists()) {
            try {
                claimFile.getParentFile().mkdirs();
                claimFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writer;
        try {
            writer = new FileWriter(claimFile);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMaxX() {
        return this.center.getBlockX() + this.claimOwner.getMaxClaimSize();
    }

    public int getMinX() {
        return this.center.getBlockX() - this.claimOwner.getMaxClaimSize();
    }

    public int getMaxZ() {
        return this.center.getBlockZ() + this.claimOwner.getMaxClaimSize();
    }

    public int getMinZ() {
        return this.center.getBlockZ() - this.claimOwner.getMaxClaimSize();
    }


    public boolean isCollideOtherClaim() {
        int thisMaxX = this.center.getBlockX() + MAX_EXPANSION_SIZE;
        int thisMaxZ = this.center.getBlockZ() + MAX_EXPANSION_SIZE;
        int thisMinX = this.center.getBlockX() - MAX_EXPANSION_SIZE;
        int thisMinZ = this.center.getBlockZ() - MAX_EXPANSION_SIZE;

        for (String uuidStr : System.getAllClaims()) {
            Claim claim = Claim.getClaim(UUID.fromString(uuidStr));
            int otherMaxX = claim.getCenter().getBlockX() + MAX_EXPANSION_SIZE;
            int otherMaxZ = claim.getCenter().getBlockZ() + MAX_EXPANSION_SIZE;
            int otherMinX = claim.getCenter().getBlockX() - MAX_EXPANSION_SIZE;
            int otherMinZ = claim.getCenter().getBlockZ() - MAX_EXPANSION_SIZE;

            boolean otherTeamXMaxIn = thisMaxX >= otherMaxX && thisMinX <= otherMaxX;
            boolean otherTeamXMinIn = thisMaxX >= otherMinX && thisMinX <= otherMinX;
            boolean otherTeamZMaxIn = thisMaxZ >= otherMaxZ && thisMinZ <= otherMaxZ;
            boolean otherTeamZMinIn = thisMaxZ >= otherMinZ && thisMinZ <= otherMinZ;


            boolean thisTeamXMaxIn = otherMaxX >= thisMaxX && otherMaxX <= thisMinX;
            boolean thisTeamXMinIn = otherMinX >= thisMaxX && otherMinX <= thisMinX;
            boolean thisTeamZMaxIn = otherMaxZ >= thisMaxZ && otherMaxZ <= thisMinZ;
            boolean thisTeamZMinIn = otherMinZ >= thisMaxZ && otherMinZ <= thisMinZ;


            if ((otherTeamXMaxIn && otherTeamZMaxIn)
                    || (otherTeamXMinIn && otherTeamZMinIn)
                    || (thisTeamXMaxIn && thisTeamZMaxIn)
                    || (thisTeamXMinIn && thisTeamZMinIn)
                    || (thisTeamXMinIn && thisTeamZMaxIn)
                    || (thisTeamXMaxIn && thisTeamZMinIn)
                    || (otherTeamXMaxIn && otherTeamZMinIn)
                    || (otherTeamXMinIn && otherTeamZMaxIn)) {
                if (!claim.claimOwner.getBorder())
                    this.borderEffect(new Location(claim.getCenter().getWorld(), otherMaxX, 0, otherMaxZ), new Location(claim.getCenter().getWorld(), otherMinX, 0, otherMinZ));
                return true;
            }
        }
        return false;
    }

    public Location getCenter() {
        return this.center;
    }

    @Deprecated
    public void showNewClaim() {
        int maxX = this.getMaxX();
        int maxZ = this.getMaxZ();

        int minX = this.getMinX();
        int minZ = this.getMinZ();
        try {
            int x = minX;
            int z = minZ;
            int time = 0;
            int timerScheduler = ((maxX - minX) * 2) + ((maxZ - minZ) * 2);
            int counterX_Z = 0;
            while (time < timerScheduler) {
                if (x < maxX && z == minZ) {
                    x++;
                } else if (x == maxX && z < maxZ) {
                    z++;
                } else if (minX < x && z == maxZ) {
                    x--;
                } else if (x == minX && minZ < z) {
                    z--;
                }

                if (counterX_Z >= this.claimOwner.getMaxClaimSize() * 2)
                    counterX_Z = 0;

                int exY = 0;
                for (int y = 9; y >= 0; y--) {
                    short color = 0;
                    if (counterX_Z >= ((this.claimOwner.getMaxClaimSize() * 2) - claimOwner.getTeamLogo().getLogo().length) / 2
                            && (counterX_Z < ((((this.claimOwner.getMaxClaimSize() * 2) - claimOwner.getTeamLogo().getLogo().length) / 2)
                            + claimOwner.getTeamLogo().getLogo().length))) {
                        color = claimOwner.getTeamLogo().getLogo()[y][counterX_Z % (((this.claimOwner.getMaxClaimSize() * 2) - claimOwner.getTeamLogo().getLogo().length) / 2)];
                    }
                    Block b = this.center.getWorld().getBlockAt(x, this.center.getWorld().getHighestBlockYAt(x, z) + exY, z);
                    b.setType(Material.getMaterial("STAINED_GLASS_PANE"));
                    b.getState().setRawData((byte) color);

                    exY++;
                }
                counterX_Z++;
                time++;
            }
        } catch (Exception e) {
        }
    }


    public int getAverageY() {
        int totalY = 0;
        for (int x = getMinX(); x < getMaxX(); x++) {
            for (int z = getMinZ(); z < getMaxZ(); z++) {
                totalY += center.getWorld().getHighestBlockYAt(x, z);
            }
        }
        return (totalY / ((getMaxX() - getMinX()) * (getMaxZ() - getMinZ())));
    }

    private void clearEffect(Location loc1, Location loc2) {
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        try {
            int x = minX;
            int z = minZ;
            int time = 0;
            int timerScheduler = ((maxX - minX) * 2) + ((maxZ - minZ) * 2);
            while (time < timerScheduler) {
                if (x < maxX && z == minZ) {
                    x++;
                } else if (x == maxX && z < maxZ) {
                    z++;
                } else if (minX < x && z == maxZ) {
                    x--;
                } else if (x == minX && minZ < z) {
                    z--;
                }
                int exY = 0;
                for (int y = 9; y >= 0; y--) {
                    Block b = this.center.getWorld().getBlockAt(x, this.center.getWorld().getHighestBlockYAt(x, z) + exY, z);
                    if (b.getType().equals(Material.getMaterial("STAINED_GLASS_PANE")))
                        b.setType(Material.getMaterial("AIR"));
                    exY++;
                }
                time++;
            }
        } catch (Exception e) {
        }
    }

    @Deprecated
    private void borderEffect(Location loc1, Location loc2) {
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        try {
            int x = minX;
            int z = minZ;
            int time = 0;
            int timerScheduler = ((maxX - minX) * 2) + ((maxZ - minZ) * 2);
            int counterX_Z = 0;
            while (time < timerScheduler) {
                if (x < maxX && z == minZ) {
                    x++;
                } else if (x == maxX && z < maxZ) {
                    z++;
                } else if (minX < x && z == maxZ) {
                    x--;
                } else if (x == minX && minZ < z) {
                    z--;
                }

                if (counterX_Z >= this.claimOwner.getMaxClaimSize() * 2)
                    counterX_Z = 0;

                int exY = 0;
                for (int y = 9; y >= 0; y--) {
                    short color = 0;
                    if (counterX_Z >= ((this.claimOwner.getMaxClaimSize() * 2) - claimOwner.getTeamLogo().getLogo().length) / 2
                            && (counterX_Z < ((((this.claimOwner.getMaxClaimSize() * 2) - claimOwner.getTeamLogo().getLogo().length) / 2)
                            + claimOwner.getTeamLogo().getLogo().length))) {
                        color = claimOwner.getTeamLogo().getLogo()[y][counterX_Z % (((this.claimOwner.getMaxClaimSize() * 2) - claimOwner.getTeamLogo().getLogo().length) / 2)];
                    }
                    Block b = this.center.getWorld().getBlockAt(x, this.center.getWorld().getHighestBlockYAt(x, z) + exY, z);
                    if (b.getType().equals(Material.getMaterial("AIR"))) {
                        b.setType(Material.getMaterial("STAINED_GLASS_PANE"));
                        b.getState().setRawData((byte) color);
                    }

                    exY++;
                }
                counterX_Z++;
                time++;
            }
            BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> {
                this.clearEffect(loc1, loc2);
            }, 3 * 20L);
        } catch (Exception ignored) {
            return;
        }

    }

    private int colorToRed(short color) {
        switch (color) {
            case 3:
            case 13:
            case 11:
            case 5:
            case 9:
                return 0;
            case 7:
                return 192;
            case 8:
            case 10:
                return 128;
            default:
                return 255;
        }
    }

    private int colorToBlue(short color) {
        switch (color) {
            case 1:
            case 14:
            case 13:
            case 5:
            case 4:
                return 0;
            case 7:
                return 192;
            case 8:
            case 10:
            case 9:
                return 128;
            default:
                return 255;
        }
    }

    private int colorToGreen(short color) {
        switch (color) {
            case 1:
                return 127;
            case 6:
            case 14:
            case 11:
            case 10:
                return 0;
            case 7:
                return 192;
            case 8:
            case 13:
            case 9:
                return 128;
            default:
                return 255;
        }
    }

    public UUID getClaimUUID() {
        return this.claimUUID;
    }

    public ABSTeam getOwner() {
        return this.claimOwner;
    }

    public int getSize() {
        return this.claimOwner.getMaxClaimSize();
    }

    public int getTotalBlocksSize() {
        return this.totalBlocks;
    }

    public File getFile() {
        return this.claimFile;
    }

    public void removeClaim() {
        if (this.claimFile.exists())
            this.claimFile.delete();
    }
}
