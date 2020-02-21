package com.speedyg.btw.filesystem;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.systems.BookUtil;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.claim.Claim;
import com.speedyg.btw.team.kits.TeamKit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class System {

    public static List<UUID> getWarJoinedTeams() {
        List<UUID> returnList = new ArrayList<>();
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/teams");
        if (file.isDirectory())
            if (file.listFiles() != null)
                for (File teamFile : file.listFiles()) {
                    ABSTeam team = ABSTeam.getFileToTeam(teamFile);
                    if (team.isJoinedWar())
                        returnList.add(team.getTeamUUID());
                }
        returnList.sort((o1, o2) -> {
            Integer data1;
            int data2;
            ABSTeam team1 = ABSTeam.getTeamByTeamUUID(o1);
            ABSTeam team2 = ABSTeam.getTeamByTeamUUID(o2);
            switch (Matching.valueOf(BasicTeamWars.getInstance().getConfig().getString("Weekly_War_Team_Matching"))) {
                case TEAM_SIZE:
                    data1 = team1.getSize();
                    data2 = team2.getSize();
                    break;
                case AUTO:
                    data1 = (team1.getLevel() + 1) * (team1.getSize()) * (team1.getWarriors().size());
                    data2 = (team2.getLevel() + 1) * (team2.getSize()) * (team2.getWarriors().size());
                    break;
                case TEAM_WARRIORS:
                    data1 = (team1.getWarriors().size());
                    data2 = (team2.getWarriors().size());
                    break;
                default:
                    data1 = team1.getLevel();
                    data2 = team2.getLevel();
                    break;


            }
            return data1.compareTo(data2);
        });
        Collections.reverse(returnList);
        return returnList;
    }

    public static List<String> getAllClaims() {
        List<String> returnList = new ArrayList<>();
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/claims");
        if (file.isDirectory())
            if (file.listFiles() != null)
                for (File claimFile : Objects.requireNonNull(file.listFiles())) {
                    returnList.add(Claim.getClaim(claimFile).getClaimUUID().toString());
                }
        return returnList;
    }

    public static List<String> getAllKits() {
        List<String> returnList = new ArrayList<>();
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/kits");
        if (file.isDirectory())
            if (file.listFiles() != null)
                for (File kitFile : Objects.requireNonNull(file.listFiles())) {
                    returnList.add(TeamKit.getTeamKit(kitFile).getUUID().toString());
                }
        return returnList;
    }

    public static List<File> getAllSchematics() {
        List<File> returnList = new ArrayList<>();
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/schematics");
        if (file.isDirectory())
            if (file.listFiles() != null)
                returnList.addAll(Arrays.asList(Objects.requireNonNull(file.listFiles())));
        return returnList;
    }

    public static List<String> getAllTeams() {
        List<String> returnList = new ArrayList<>();
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/teams");
        if (file.isDirectory())
            if (file.listFiles() != null)
                for (File teamFile : file.listFiles()) {
                    returnList.add(ABSTeam.getFileToTeam(teamFile).getTeamUUID().toString());
                }
        return returnList;
    }


    public static List<String> getAllTeams(int level, OrderType type) {
        List<String> returnList = new ArrayList<>();
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/teams");
        if (file.isDirectory())
            if (file.listFiles() != null)
                for (File teamFile : file.listFiles()) {
                    ABSTeam team = ABSTeam.getFileToTeam(teamFile);
                    if (team != null)
                        if (team.getLevel() >= level) {
                            returnList.add(team.getTeamUUID().toString());
                        }
                }

        Collections.sort(returnList, (o1, o2) -> {
            ABSTeam team1 = ABSTeam.getTeamByTeamUUID(UUID.fromString(o1));
            ABSTeam team2 = ABSTeam.getTeamByTeamUUID(UUID.fromString(o2));
            switch (type) {
                case DATE:
                    Date data1 = new Date(team1.getMakeDate());
                    Date data2 = new Date(team2.getMakeDate());
                    return data1.compareTo(data2);
                case TEAM_MAKER_NAME:
                    String name1 = team1.getTeamMaker().getName();
                    String name2 = team2.getTeamMaker().getName();
                    return name1.compareTo(name2);
                case WINS:
                    Integer win_1 = team1.getTotalVSWins();
                    Integer win_2 = team2.getTotalVSWins();
                    return win_1.compareTo(win_2);
                case KILLS:
                    Integer kill_1 = team1.getTotalKills();
                    Integer kill_2 = team2.getTotalKills();
                    return kill_1.compareTo(kill_2);
                case TEAM_NAME:
                    String name_1 = team1.getName();
                    String name_2 = team2.getName();
                    return name_1.compareTo(name_2);
            }
            return 0;
        });
        Collections.reverse(returnList);

        return returnList;
    }

    public static File getPlayerFile(OfflinePlayer player) {
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/players", player.getUniqueId().toString() + ".json");
        if (file.exists()) {
            return file;
        }
        return null;
    }


    public static File getPlayerTeamFile(OfflinePlayer player) {
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/players", player.getUniqueId().toString() + ".json");
        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                JSONParser oop = new JSONParser();
                JSONObject jo = (JSONObject) oop.parse(reader);
                reader.close();
                if (jo.get("TeamFile") != null)
                    return new File(BasicTeamWars.getInstance().getDataFolder() + "/teams", (String) jo.get("TeamFile"));
                else
                    return null;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getPlayerTeamFile(UUID player) {
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/players", player.toString() + ".json");
        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                JSONParser oop = new JSONParser();
                JSONObject jo = (JSONObject) oop.parse(reader);
                reader.close();
                if (jo.get("TeamFile") != null)
                    return new File(BasicTeamWars.getInstance().getDataFolder() + "/teams", (String) jo.get("TeamFile"));
                else
                    return null;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String colorChanger(short logo) {
        switch (logo) {
            case 1:
                return "6";
            case 3:
                return "b";
            case 4:
                return "e";
            case 5:
                return "a";
            case 6:
                return "d";
            case 7:
                return "8";
            case 8:
                return "7";
            case 9:
                return "3";
            case 10:
                return "5";
            case 11:
                return "1";
            case 13:
                return "2";
            case 14:
                return "4";
            default:
                return "f";
        }

    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location stringToLocation(String locationString) {
        if (locationString == null)
            return null;
        String[] splitNodes = locationString.split(",");
        double x = Double.parseDouble(splitNodes[1]);
        double y = Double.parseDouble(splitNodes[2]);
        double z = Double.parseDouble(splitNodes[3]);
        double yaw = Double.parseDouble(splitNodes[4]);
        double pitch = Double.parseDouble(splitNodes[5]);
        return new Location(Bukkit.getWorld(splitNodes[0]), x, y, z, (float) yaw, (float) pitch);
    }

    @Deprecated
    public static void openBook(Player p, String message) {
        String[] messages = new String[(message.split(":")[1].split(" ").length / 45) + 1];
        int i = 0;
        int z = 0;
        for (String add : message.split(":")[1].split(" ")) {
            if (z == 0)
                messages[i] = add + " ";
            else
                messages[i] += add + " ";
            z++;
            if (z == 45) {
                i++;
                z = 0;
            }
        }
        ItemStack book = new ItemStack(Material.getMaterial("WRITTEN_BOOK"));
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.addPage(messages);
        book.setItemMeta(meta);
        BookUtil.openBook(book, p);
    }


    public static ABSTeam getHasTeamOnLocation(Location location) {
        File file = new File(BasicTeamWars.getInstance().getDataFolder() + "/claims");
        if (file.isDirectory())
            if (file.listFiles() != null)
                for (File claimFile : file.listFiles()) {
                    Claim claim = Claim.getClaim(claimFile);
                    if (claim != null) {
                        int x = location.getBlockX();
                        int z = location.getBlockZ();
                        if ((claim.getMaxX() >= x && claim.getMinX() <= x)
                                &&
                                (claim.getMaxZ() >= z && claim.getMinZ() <= z))
                            return claim.getOwner();
                    }
                }
        return null;
    }

    public static boolean compareTeams(ABSTeam s1, ABSTeam s2) {
        if (s1 != null && s2 != null)
            return s1.getTeamUUID().equals(s2.getTeamUUID());
        else if (s1 != null)
            return false;
        else if (s2 != null)
            return false;
        else
            return true;
    }

    public static String timeSplitter(long t) {
        long time = t;
        long minute;
        long hour;
        long second;
        long day;

        day = time / 86400;
        time -= 84600 * day;
        hour = (time / (3600));
        time -= (3600) * hour;
        minute = (time / 60);
        time -= minute * 60;
        second = time;

        String hourS = Messages.hour;
        String minS = Messages.minute;
        String secS = Messages.second;
        String dayS = Messages.day;

        if (day > 0) {
            return day + " " + dayS + ", " + hour + " " + hourS + ", " + minute + " " + minS + " ," + second + " " + secS;
        } else if (hour > 0) {
            return hour + " " + hourS + ", " + minute + " " + minS + " ," + second + " " + secS;
        } else if (minute > 0) {
            return minute + " " + minS + " ," + second + " " + secS;
        } else {
            return second + " " + secS;
        }
    }

}


enum Matching {
    TEAM_SIZE, TEAM_WARRIORS, AUTO;
}

