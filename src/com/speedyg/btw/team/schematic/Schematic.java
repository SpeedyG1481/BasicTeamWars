package com.speedyg.btw.team.schematic;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.Version;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class created for Basic Team Wars plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 18.12.2019
 */

public class Schematic {

    private int xSize;
    private int ySize;
    private int zSize;

    private String name;

    private long totalBlocks;

    private Materials[][][] materials;
    private long cost;
    private int minLevel;

    private Version schemaVersion;

    public Schematic(String schematicName) {
        File schemaFile = new File(BasicTeamWars.getInstance().getDataFolder() + "/schematics", schematicName + ".obs");
        try {
            FileReader reader = new FileReader(schemaFile);
            JSONParser oop = new JSONParser();
            JSONObject jo = (JSONObject) oop.parse(reader);
            reader.close();
            this.totalBlocks = (int) (long) jo.get("TotalBlocks");
            this.xSize = (int) (long) jo.get("xSize");
            this.ySize = (int) (long) jo.get("ySize");
            this.zSize = (int) (long) jo.get("zSize");
            this.materials = this.stringToSchema((String) jo.get("Schema"), (int) (long) jo.get("xSize"), (int) (long) jo.get("ySize"), (int) (long) jo.get("zSize"));
            this.name = (String) jo.get("SchemaName");
            this.cost = (long) jo.get("Cost");
            this.minLevel = (int) (long) jo.get("Min-Level");
            if (jo.get("Version") != null)
                this.schemaVersion = Version.valueOf((String) jo.get("Version"));
            else
                this.schemaVersion = Version.UNSUPPORTED;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Schematic(File schematicFile) {
        try {
            FileReader reader = new FileReader(schematicFile);
            JSONParser oop = new JSONParser();
            JSONObject jo = (JSONObject) oop.parse(reader);
            reader.close();
            this.totalBlocks = (int) (long) jo.get("TotalBlocks");
            this.xSize = (int) (long) jo.get("xSize");
            this.ySize = (int) (long) jo.get("ySize");
            this.zSize = (int) (long) jo.get("zSize");
            this.materials = this.stringToSchema((String) jo.get("Schema"), (int) (long) jo.get("xSize"), (int) (long) jo.get("ySize"), (int) (long) jo.get("zSize"));
            this.name = (String) jo.get("SchemaName");
            this.cost = (long) jo.get("Cost");
            this.minLevel = (int) (long) jo.get("Min-Level");
            if (jo.get("Version") != null)
                this.schemaVersion = Version.valueOf((String) jo.get("Version"));
            else
                this.schemaVersion = Version.UNSUPPORTED;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    public long getCost() {
        return this.cost;
    }


    public int getMinLevel() {
        return this.minLevel;
    }

    public boolean isSuitClaim(Claim claim) {
        return xSize <= (claim.getMaxX() - claim.getMinX()) && zSize <= (claim.getMaxZ() - claim.getMinZ());
    }

    public boolean isSuitSchemaVersion() {
        return BasicTeamWars.getInstance().getServerVersion().equals(this.schemaVersion);
    }

    @Deprecated
    public void buildSchematicToClaim(Player p, Claim claim, int rotateValue) {
        if (this.isSuitClaim(claim) && isSuitSchemaVersion()) {
            int startY = claim.getAverageY();
            int startX = claim.getMinX();
            int startZ = claim.getMinZ();
            new BukkitRunnable() {
                int y = 0;

                @Override
                public void run() {
                    try {
                        for (int x = 0; x < xSize; x++) {
                            for (int z = 0; z < zSize; z++) {
                                Block b;
                                Materials mat;
                                b = claim.getCenter().getWorld().getBlockAt(startX + x, startY + y, startZ + z);
                                if (rotateValue % 360 == 90) {
                                    mat = materials[z][y][x];
                                } else if (rotateValue % 360 == 180) {
                                    mat = materials[x][y][z];
                                } else if (rotateValue % 360 == 270) {
                                    mat = materials[z][y][x];
                                } else {
                                    mat = materials[x][y][z];
                                }
                                b.setType(mat.getMaterial());
                                b.getState().setRawData((byte) mat.getData());
                            }
                        }
                        if (Bukkit.getPlayer(p.getUniqueId()) != null)
                            if (Bukkit.getPlayer(p.getUniqueId()).isOnline())
                                p.sendMessage(Messages.build_loading.replaceAll("<percentage>", (int) (((double) y / (double) ySize) * 100) + "%"));
                        y++;
                    } catch (Exception e) {
                        this.cancel();
                        if (Bukkit.getPlayer(p.getUniqueId()) != null)
                            if (Bukkit.getPlayer(p.getUniqueId()).isOnline())
                                p.sendMessage(Messages.build_failed);
                        e.printStackTrace();
                    }
                    if (!(y < ySize)) {
                        if (Bukkit.getPlayer(p.getUniqueId()) != null)
                            if (Bukkit.getPlayer(p.getUniqueId()).isOnline()) {
                                p.sendMessage(Messages.build_loading.replaceAll("<percentage>", (int) (((double) y / (double) ySize) * 100) + "%"));
                                p.sendMessage(Messages.build_success);
                            }
                        this.cancel();
                    }
                }
            }.runTaskTimer(BasicTeamWars.getInstance(), 0L, 20L);
        }
    }


    private Materials[][][] stringToSchema(String schema, int xSize, int ySize, int zSize) {
        Materials[][][] returnMat = new Materials[xSize][ySize][zSize];
        int x = 0;
        String[] xSplitter = schema.split(":");
        for (String xStr : xSplitter) {
            int y = 0;
            String[] ySplitter = xStr.split(";");
            for (String yStr : ySplitter) {
                int z = 0;
                String[] zSplitter = yStr.split("-");
                for (String zStr : zSplitter) {
                    returnMat[x][y][z] = new Materials(Material.getMaterial(zStr.split("&")[0]), Integer.parseInt(zStr.split("&")[1]));
                    z++;
                }
                y++;
            }
            x++;
        }
        return returnMat;
    }

    public String getName() {
        return this.name;
    }

    public long getTotalBlocks() {
        return this.totalBlocks;
    }
}

class Materials {

    private Material name;
    private int data;

    public Materials(Material material, int data) {
        if (material != null)
            this.name = material;
        else
            this.name = Material.getMaterial("AIR");
        this.data = data;
    }

    public Material getMaterial() {
        return name;
    }

    public void setName(Material name) {
        this.name = name;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

}
