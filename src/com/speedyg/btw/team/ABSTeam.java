package com.speedyg.btw.team;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.claim.Claim;
import com.speedyg.btw.team.player.TeamPlayer;
import com.speedyg.btw.war.WarTree;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * This class created for Basic Team Wars plugin. This class important class of plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 18.12.2019
 */
public class ABSTeam {

    public int MAX_TEAM_SIZE;
    private int MAX_POWER;
    private int MAX_LEVEL;

    private UUID teamUUID;
    private OfflinePlayer teamMaker;
    private String name;
    private int size;
    private String teamDescription;
    private String shortName;

    private List<String> claims;

    private List<String> members;
    private List<String> mods;
    private List<String> admins;

    private List<String> waitingInvites;
    private List<String> enemyTeams;
    private List<String> friendTeams;
    private List<String> teamRequests;
    private List<String> messageBox;
    private List<String> warriors;

    private List<String> memberPerms;
    private List<String> modPerms;
    private List<String> adminPerms;


    private long balance;
    private long makeTime;
    private int power;
    private int totalKills;
    private int totalVSWins;
    private int teamLevel;
    private Logo teamLogo;
    private File teamDataFile;
    private boolean isActivated;
    private Location memberSpawnLocation;
    private Location modSpawnLocation;
    private Location adminSpawnLocation;
    private boolean border;
    private boolean isJoinedWar;
    private String membersWarKit;
    private String moderatorsWarKit;
    private String adminsWarKit;

    public ABSTeam(OfflinePlayer teamMaker, String name, int size, long makeTime, Logo logo, UUID teamUUID) {
        this.teamMaker = teamMaker;
        this.makeTime = makeTime;
        if (name != null)
            this.name = name;
        else
            this.name = "&b" + teamMaker.getName() + "'s Team";
        if (logo != null)
            this.teamLogo = logo;
        else
            this.teamLogo = new Logo();
        this.size = size;
        this.members = new ArrayList<>();
        this.mods = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.enemyTeams = new ArrayList<>();
        this.friendTeams = new ArrayList<>();
        this.claims = new ArrayList<>();
        this.modPerms = new ArrayList<>();
        this.adminPerms = new ArrayList<>();
        this.memberPerms = new ArrayList<>();
        this.waitingInvites = new ArrayList<>();
        this.teamRequests = new ArrayList<>();
        this.messageBox = new ArrayList<>();
        this.warriors = new ArrayList<>();
        this.teamDescription = " ";
        this.shortName = "BTW";
        this.balance = 0;
        this.totalKills = 0;
        this.power = 100;
        this.totalVSWins = 0;
        this.teamLevel = 0;
        this.border = false;
        this.isActivated = false;
        this.isJoinedWar = false;
        this.memberSpawnLocation = null;
        this.adminSpawnLocation = null;
        this.modSpawnLocation = null;
        this.membersWarKit = null;
        this.adminsWarKit = null;
        this.moderatorsWarKit = null;
        if (teamUUID == null)
            this.teamUUID = UUID.randomUUID();
        else
            this.teamUUID = teamUUID;
        this.teamDataFile = new File(BasicTeamWars.getInstance().getDataFolder() + "/teams",
                this.teamUUID.toString() + ".json");
        MAX_POWER = BasicTeamWars.getInstance().getConfig().getInt("Max_Team_Power");
        MAX_LEVEL = BasicTeamWars.getInstance().getConfig().getInt("Max_Level");
        MAX_TEAM_SIZE = BasicTeamWars.getInstance().getConfig().getInt("Max_Team_Size");
    }

    public static ABSTeam getFileToTeam(File teamDataFile) {
        try {
            FileReader reader = new FileReader(teamDataFile);
            JSONParser oop = new JSONParser();
            JSONObject jo = (JSONObject) oop.parse(reader);
            reader.close();
            ABSTeam returnTeam = new ABSTeam(Bukkit.getOfflinePlayer(UUID.fromString((String) jo.get("Maker-UUID"))),
                    (String) jo.get("Name"), (int) (long) jo.get("Size"), (long) jo.get("MakeTime"),
                    Logo.stringToLogo((String) jo.get("Logo")), UUID.fromString((String) jo.get("Team-UUID")));
            returnTeam.setMembers((List<String>) jo.get("Members"));
            returnTeam.setAdmins((List<String>) jo.get("Admins"));
            returnTeam.setMods((List<String>) jo.get("Moderators"));
            returnTeam.setBalance((long) jo.get("Balance"));
            returnTeam.setPower((int) (long) jo.get("Power"));
            returnTeam.setTotalKills((int) (long) jo.get("TotalKills"));
            returnTeam.setTotalVSWins((int) (long) jo.get("TotalWins"));
            returnTeam.setActivated((boolean) jo.get("Active"));
            returnTeam.setLevel((int) (long) jo.get("Level"));
            returnTeam.setClaims((List<String>) jo.get("Claims"));
            returnTeam.setEnemyTeams((List<String>) jo.get("Enemy-Teams"));
            returnTeam.setFriendTeams((List<String>) jo.get("Friend-Teams"));
            returnTeam.setAdminPerms((List<String>) jo.get("Admin-Perms"));
            returnTeam.setModPerms((List<String>) jo.get("Mod-Perms"));
            returnTeam.setMemberPerms((List<String>) jo.get("Member-Perms"));
            returnTeam.setWaitingInvites((List<String>) jo.get("Invites"));
            returnTeam.setTeamRequests((List<String>) jo.get("Requests"));
            returnTeam.setMessageBox((List<String>) jo.get("Messages"));
            returnTeam.setShortName((String) jo.get("ShortName"));
            returnTeam.setBorder((boolean) jo.get("Border"));
            returnTeam.setJoinWar((boolean) jo.get("War"));
            returnTeam.setWarriors((List<String>) jo.get("Warriors"));
            returnTeam.setTeamDescription((String) jo.get("Description"));
            returnTeam.setMemberSpawnLocation(System.stringToLocation((String) jo.get("SpawnLocation-Member")));
            returnTeam.setAdminSpawnLocation(System.stringToLocation((String) jo.get("SpawnLocation-Admin")));
            returnTeam.setModSpawnLocation(System.stringToLocation((String) jo.get("SpawnLocation-Mod")));
            returnTeam.setAdminsWarKit((String) jo.get("WarKit-Admins"));
            returnTeam.setModeratorsWarKit((String) jo.get("WarKit-Mods"));
            returnTeam.setMembersWarKit((String) jo.get("WarKit-Members"));
            return returnTeam;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ABSTeam getTeamByTeamUUID(UUID teamUUID) {
        if (teamUUID != null)
            return ABSTeam.getFileToTeam(new File(BasicTeamWars.getInstance().getDataFolder() + "/teams", teamUUID.toString() + ".json"));
        else
            return null;
    }

    public void saveTeam() {
        this.isActivated = true;
        JSONObject json = new JSONObject();
        json.put("Maker-Name", teamMaker.getName());
        json.put("Maker-UUID", teamMaker.getUniqueId().toString());
        json.put("Name", this.name.replaceAll("§", "&"));
        json.put("Size", this.size);
        json.put("Members", this.members);
        json.put("Moderators", this.mods);
        json.put("Admins", this.admins);
        json.put("Balance", this.balance);
        json.put("MakeTime", this.makeTime);
        json.put("Description", this.teamDescription);
        json.put("Power", this.power);
        json.put("TotalKills", this.totalKills);
        json.put("Requests", this.teamRequests);
        json.put("TotalWins", this.totalVSWins);
        json.put("Level", this.teamLevel);
        json.put("Logo", teamLogo.toString());
        json.put("Active", isActivated);
        json.put("Team-UUID", this.teamUUID.toString());
        json.put("Claims", this.claims);
        json.put("Enemy-Teams", this.enemyTeams);
        json.put("Border", this.border);
        json.put("Warriors", this.warriors);
        json.put("Friend-Teams", this.friendTeams);
        json.put("Mod-Perms", this.modPerms);
        json.put("Admin-Perms", this.adminPerms);
        json.put("ShortName", this.shortName);
        json.put("Messages", this.messageBox);
        json.put("Member-Perms", this.memberPerms);
        json.put("Invites", this.waitingInvites);
        json.put("War", this.isJoinedWar);
        json.put("WarKit-Members", this.membersWarKit);
        json.put("WarKit-Mods", this.moderatorsWarKit);
        json.put("WarKit-Admins", this.adminsWarKit);
        json.put("SpawnLocation-Member", this.memberSpawnLocation == null ? null : System.locationToString(this.memberSpawnLocation));
        json.put("SpawnLocation-Admin", this.adminSpawnLocation == null ? null : System.locationToString(this.adminSpawnLocation));
        json.put("SpawnLocation-Mod", this.modSpawnLocation == null ? null : System.locationToString(this.modSpawnLocation));
        if (!teamDataFile.exists()) {
            try {
                teamDataFile.getParentFile().mkdirs();
                teamDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writer;
        try {
            writer = new FileWriter(teamDataFile);
            writer.write(json.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.saveAllClaims();
    }

    public String getNameWithoutColorCodes() {
        String teamName = this.name;
        for (int i = 0; i <= 9; i++) {
            teamName = teamName.replaceAll(("&" + i), "");
            teamName = teamName.replaceAll(("§" + i), "");
        }
        teamName = teamName.replaceAll("&a", "");
        teamName = teamName.replaceAll("&b", "");
        teamName = teamName.replaceAll("&c", "");
        teamName = teamName.replaceAll("&d", "");
        teamName = teamName.replaceAll("&e", "");
        teamName = teamName.replaceAll("&f", "");
        teamName = teamName.replaceAll("§a", "");
        teamName = teamName.replaceAll("§b", "");
        teamName = teamName.replaceAll("§c", "");
        teamName = teamName.replaceAll("§d", "");
        teamName = teamName.replaceAll("§e", "");
        teamName = teamName.replaceAll("§f", "");
        return teamName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String sName) {
        this.shortName = sName;
    }

    public String getTeamDescription() {
        return this.teamDescription;
    }

    public void setTeamDescription(String td) {
        this.teamDescription = td;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public boolean getBorder() {
        return border;
    }

    public void setMessageBox(List<String> messageBox) {
        this.messageBox = messageBox;
    }

    public List<String> getMessageBox() {
        return this.messageBox;
    }

    public void addMessage(String message) {
        this.messageBox.add("0:" + message);
    }

    public void removeMessage(String message) {
        this.messageBox.remove(message);
    }

    public void clearMessageBox() {
        this.messageBox.clear();
    }

    public List<String> getTeamRequests() {
        return this.teamRequests;
    }

    public void addTeamRequest(UUID teamUUID) {
        this.teamRequests.add(teamUUID.toString());
    }

    public void removeTeamRequest(UUID teamUUID) {
        this.teamRequests.remove(teamUUID.toString());
    }

    public void setTeamRequests(List<String> teamRequests) {
        this.teamRequests = teamRequests;
    }

    public boolean hasPermission(OfflinePlayer p, Permission permission) {
        TeamPlayer player = TeamPlayer.getPlayer(p);
        boolean isAdmin = this.getAdmins().contains(p.getUniqueId().toString());
        boolean isMod = this.getMods().contains(p.getUniqueId().toString());
        boolean isMember = this.getMembers().contains(p.getUniqueId().toString());
        boolean hasMemberPerm = isMember && this.getMemberPerms().contains(permission.toString());
        boolean hasModPerm = isMod && (this.getModPerms().contains(permission.toString()) || this.getMemberPerms().contains(permission.toString()));
        boolean hasAdminPerm = isAdmin && (this.getAdminPerms().contains(permission.toString()) || this.getMemberPerms().contains(permission.toString()));
        boolean haveSelfPermission = player.hasPermission(permission);
        boolean isTeamMaker = p.getUniqueId().equals(this.getTeamMaker().getUniqueId());

        if (hasAdminPerm || hasModPerm || hasMemberPerm || isTeamMaker || haveSelfPermission || p.isOp())
            return true;
        return false;
    }

    public void addInvite(UUID uuid) {
        this.waitingInvites.add(uuid.toString());
    }

    public List<String> getInvites() {
        return this.waitingInvites;
    }

    public void setWaitingInvites(List<String> invites) {
        this.waitingInvites = invites;
    }

    public void removeWaitingInvite(TeamPlayer player) {
        this.waitingInvites.remove(player.getPlayer().getUniqueId().toString());
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        if (size < MAX_TEAM_SIZE && size > 0)
            this.size = size;
    }

    public Location getMemberSpawnLocation() {
        return this.memberSpawnLocation;
    }

    public void setMemberSpawnLocation(Location loc) {
        this.memberSpawnLocation = loc;
    }

    public void setMemberSpawnLocation(World w, double x, double y, double z) {
        this.memberSpawnLocation = new Location(w, x, y, z);
    }

    public void setMemberSpawnLocation(World w, double x, double y, double z, float yaw, float pitch) {
        this.memberSpawnLocation = new Location(w, x, y, z, yaw, pitch);
    }

    public Location getAdminSpawnLocation() {
        return this.adminSpawnLocation;
    }

    public void setAdminSpawnLocation(Location loc) {
        this.adminSpawnLocation = loc;
    }

    public void setAdminSpawnLocation(World w, double x, double y, double z) {
        this.adminSpawnLocation = new Location(w, x, y, z);
    }

    public void setAdminSpawnLocation(World w, double x, double y, double z, float yaw, float pitch) {
        this.adminSpawnLocation = new Location(w, x, y, z, yaw, pitch);
    }

    public Location getModSpawnLocation() {
        return this.modSpawnLocation;
    }

    public void setModSpawnLocation(Location loc) {
        this.modSpawnLocation = loc;
    }

    public void setModSpawnLocation(World w, double x, double y, double z) {
        this.modSpawnLocation = new Location(w, x, y, z);
    }

    public void setModSpawnLocation(World w, double x, double y, double z, float yaw, float pitch) {
        this.modSpawnLocation = new Location(w, x, y, z, yaw, pitch);
    }

    public OfflinePlayer getTeamMaker() {
        return this.teamMaker;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setMembers(List<String> list) {
        members = list;
    }

    private void setAdmins(List<String> list) {
        admins = list;
    }

    private void setMods(List<String> list) {
        mods = list;
    }

    public void addMember(UUID p) {
        this.members.add(p.toString());
        TeamPlayer player = TeamPlayer.getPlayer(p);
        player.setTeam(this);
        player.savePlayer();

    }

    public void addModerator(UUID p) {
        this.mods.add(p.toString());

    }

    public void addAdmin(UUID p) {
        this.admins.add(p.toString());

    }

    public void removeMember(UUID p) {
        for (String px : this.members) {
            if (Bukkit.getOfflinePlayer(UUID.fromString(px)).isOnline()) {
                Player pOnline = (Player) Bukkit.getOfflinePlayer(UUID.fromString(px));
                pOnline.closeInventory();
            }
        }

        TeamPlayer player = TeamPlayer.getPlayer(p);
        player.clearPlayerData();

        if (BasicTeamWars.getInstance().getTimer() != null) {
            WarTree[] trees = BasicTeamWars.getInstance().getTimer().getWar().getWars();
            if (BasicTeamWars.getInstance().getTimer().getWar() != null) {
                for (WarTree tree : trees) {
                    if (tree != null) {
                        if ((tree.getTeam1_Players().contains(p.toString())) || (tree.getTeam2_Players().contains(p.toString()))) {
                            tree.removeTeam1Player(p.toString());
                            tree.removeTeam2Player(p.toString());
                        }
                    }
                }
            }
        }

        this.warriors.remove(p.toString());
        this.members.remove(p.toString());
        this.mods.remove(p.toString());
        this.admins.remove(p.toString());

    }

    public void removeModerator(UUID p) {
        for (String px : this.members) {
            if (Bukkit.getOfflinePlayer(UUID.fromString(px)).isOnline()) {
                Player pOnline = (Player) Bukkit.getOfflinePlayer(UUID.fromString(px));
                pOnline.closeInventory();
            }
        }

        this.mods.remove(p.toString());

    }

    public void removeAdmin(UUID p) {
        for (String px : this.members) {
            if (Bukkit.getOfflinePlayer(UUID.fromString(px)).isOnline()) {
                Player pOnline = (Player) Bukkit.getOfflinePlayer(UUID.fromString(px));
                pOnline.closeInventory();
            }
        }
        this.admins.remove(p.toString());

    }

    public boolean isFull() {
        return !(this.members.size() <= size);
    }

    private void setBalance(long balance) {
        this.balance = balance;

    }

    public void addBalance(long balance) {
        this.balance += balance;

    }

    public void removeBalance(long balance) {
        if (this.balance - balance <= 0)
            this.balance = 0;
        else
            this.balance -= balance;

    }

    private void setPower(int power) {
        this.power = power;

    }

    public void removePower(int power) {
        if (this.power - power <= 0) {
            this.power = 0;
            if (teamLevel - (Math.abs(this.power - power) / MAX_POWER) > 0) {
                this.teamLevel -= Math.abs(this.power - power) / MAX_POWER;
            } else {
                teamLevel = 0;
            }
            this.saveAllClaims();
            this.addMessage(Messages.your_team_level_down.replaceAll("<newLevel>", String.valueOf(teamLevel)));
        } else {
            this.power -= power;
        }
    }

    public void addPower(int power) {
        if (this.power + power >= MAX_POWER) {
            if (this.teamLevel + ((this.power + power) / MAX_POWER) < MAX_LEVEL) {
                this.teamLevel += ((this.power + power) / MAX_POWER);
            } else {
                teamLevel = MAX_LEVEL;
            }
            this.saveAllClaims();
            this.addMessage(Messages.your_team_level_up.replaceAll("<newLevel>", String.valueOf(teamLevel)));
            this.power = MAX_POWER / 5;
        } else {
            this.power += power;
        }
    }

    private void saveAllClaims() {
        for (String uuid : this.claims) {
            Claim claim = Claim.getClaim(UUID.fromString(uuid));
            claim.saveClaim();

        }
    }

    public void addKill() {
        this.totalKills++;
    }

    public void addKill(int kill) {
        this.totalKills += kill;
    }

    private void setTotalKills(int kills) {
        this.totalKills = kills;

    }

    public void addWin() {
        this.totalVSWins++;

    }

    public void addWin(int win) {
        this.totalVSWins += win;

    }

    private void setTotalVSWins(int totalVSWins) {
        this.totalVSWins = totalVSWins;

    }

    private void setActivated(boolean active) {
        this.isActivated = active;

    }

    public boolean isActivated() {
        return this.isActivated;
    }

    public long getMakeDate() {
        return this.makeTime;
    }

    public Logo getTeamLogo() {
        return this.teamLogo;
    }

    public void setTeamLogo(Logo logo) {
        this.teamLogo = logo;
    }

    public void setLevel(int level) {
        this.teamLevel = level;

    }

    public UUID getTeamUUID() {
        return teamUUID;
    }

    public void setTeamUUID(UUID uuid) {
        this.teamUUID = uuid;

    }

    public long getBalance() {
        return this.balance;
    }

    public List<String> getMembers() {
        return this.members;


    }

    public List<String> getAdmins() {
        return this.admins;
    }

    public List<String> getMods() {
        return this.mods;
    }

    public int getPower() {
        return this.power;
    }

    public int getTotalKills() {
        return this.totalKills;
    }

    public int getTotalVSWins() {
        return this.totalVSWins;
    }

    public int getLevel() {
        return this.teamLevel;
    }

    public List<String> getEnemyTeams() {
        return this.enemyTeams;
    }

    public List<String> getFriendTeams() {
        return this.friendTeams;
    }

    public void addEnemyTeam(UUID teamUUID) {
        ABSTeam otherTeam = ABSTeam.getTeamByTeamUUID(teamUUID);
        Date date = new Date();
        otherTeam.addMessage(Messages.team_added_you_as_an_enemy.replaceAll("<date>", date.toString().replaceAll(":", ".")).replaceAll("<teamName>", this.name.replaceAll("&", "§")));
        otherTeam.saveTeam();
        this.enemyTeams.add(teamUUID.toString());

    }

    public void addFriendTeam(UUID teamUUID) {
        ABSTeam otherTeam = ABSTeam.getTeamByTeamUUID(teamUUID);
        Date date = new Date();
        otherTeam.addMessage(Messages.team_added_you_as_an_friend.replaceAll("<date>", date.toString().replaceAll(":", ".")).replaceAll("<teamName>", this.name.replaceAll("&", "§")));
        otherTeam.saveTeam();
        this.friendTeams.add(teamUUID.toString());

    }

    public void removeEnemyTeam(UUID teamUUID) {
        ABSTeam otherTeam = ABSTeam.getTeamByTeamUUID(teamUUID);
        Date date = new Date();
        otherTeam.addMessage(Messages.team_removed_you_as_an_enemy.replaceAll("<date>", date.toString().replaceAll(":", ".")).replaceAll("<teamName>", this.name.replaceAll("&", "§")));
        otherTeam.saveTeam();
        this.enemyTeams.remove(teamUUID.toString());

    }

    public void removeFriendTeam(UUID teamUUID) {
        ABSTeam otherTeam = ABSTeam.getTeamByTeamUUID(teamUUID);
        Date date = new Date();
        otherTeam.addMessage(Messages.team_removed_you_as_an_friend.replaceAll("<date>", date.toString().replaceAll(":", ".")).replaceAll("<teamName>", this.name.replaceAll("&", "§")));
        otherTeam.saveTeam();
        this.friendTeams.remove(teamUUID.toString());

    }

    private void setFriendTeams(List<String> team) {
        this.friendTeams = team;

    }

    private void setEnemyTeams(List<String> team) {
        this.enemyTeams = team;

    }


    public List<String> getClaims() {
        return this.claims;
    }

    public void addClaim(String claim) {
        this.claims.add(claim);

    }

    public void removeClaim(String claim) {
        this.claims.remove(claim);

    }

    public List<String> getMemberPerms() {
        return this.memberPerms;
    }

    public void removeMemberPerm(Permission perm) {
        this.memberPerms.remove(perm.toString());

    }

    public void addMemberPerm(Permission perm) {
        this.memberPerms.add(perm.toString());

    }

    private void setMemberPerms(List<String> permsx) {
        this.memberPerms = permsx;

    }

    public List<String> getModPerms() {
        return this.modPerms;
    }

    public void removeModPerm(Permission perm) {
        this.modPerms.remove(perm.toString());

    }

    public void addModPerm(Permission perm) {
        this.modPerms.add(perm.toString());

    }

    private void setModPerms(List<String> permsx) {
        this.modPerms = permsx;

    }

    public List<String> getAdminPerms() {
        return this.adminPerms;
    }

    public void removeAdminPerm(Permission perm) {
        this.adminPerms.remove(perm.toString());

    }

    public void addAdminPerm(Permission perm) {
        this.adminPerms.add(perm.toString());

    }

    private void setAdminPerms(List<String> permsx) {
        this.adminPerms = permsx;

    }

    private void setClaims(List<String> list) {
        this.claims = list;

    }


    public boolean haveEnoughMoney(long cost) {
        return balance >= cost;
    }

    public File getTeamFile() {
        return this.teamDataFile;
    }

    public void removeTeam() {
        TeamPlayer owner = TeamPlayer.getPlayer(this.teamMaker);
        owner.clearPlayerData();
        for (String player : this.members) {
            TeamPlayer.getPlayer(UUID.fromString(player)).clearPlayerData();
            OfflinePlayer px = Bukkit.getPlayer(UUID.fromString(player));
            if (px.getPlayer().isOnline()) {
                Player pOnline = px.getPlayer();
                pOnline.closeInventory();
                pOnline.sendMessage(Messages.your_team_removed);
            }
        }
        for (String otherTeamsUUID : System.getAllTeams()) {
            ABSTeam otherTeams = ABSTeam.getTeamByTeamUUID(UUID.fromString(otherTeamsUUID));
            otherTeams.removeFriendTeam(this.getTeamUUID());
            otherTeams.removeTeamRequest(this.getTeamUUID());
            otherTeams.removeEnemyTeam(this.getTeamUUID());
            otherTeams.saveTeam();
        }
        if (this.teamDataFile.exists()) {
            this.teamDataFile.delete();
        }
    }

    /**
     * @param p OfflinePlayer player1
     * @param s UUID player2 UUID.
     * @return player1 permissions is bigger thane player 2?
     */
    public boolean isBigger(OfflinePlayer p, UUID s) {
        boolean pAdmin = this.admins.contains(p.getUniqueId().toString());
        boolean oPAdmin = this.admins.contains(s.toString());

        boolean pMod = this.mods.contains(p.getUniqueId().toString());
        boolean oPMod = this.mods.contains(s.toString());

        boolean isSuperUser = p.getUniqueId().toString().equalsIgnoreCase(this.teamMaker.getUniqueId().toString());

        if (isSuperUser)
            return true;

        if (pAdmin && !oPAdmin)
            return true;

        return pMod && !oPAdmin && !oPMod;
    }

    public List<String> getNeutralTeams() {
        List<String> allTeams = System.getAllTeams();
        for (String s : this.friendTeams)
            allTeams.remove(s);
        for (String s : this.enemyTeams)
            allTeams.remove(s);
        allTeams.remove(this.getTeamUUID().toString());
        return allTeams;
    }

    public void readMessage(String message) {
        if (messageBox.contains(message))
            this.messageBox.set(this.messageBox.indexOf(message), "1:" + message.split(":")[1]);
    }

    public void clearReadMessages() {
        List<String> willRemove = new ArrayList<>();
        for (String remove : this.messageBox) {
            if (remove.split(":")[0].equals("1")) {
                willRemove.add(remove);
            }
        }
        for (String remove : willRemove)
            this.messageBox.remove(remove);
    }

    public void markMessagesRead() {
        for (String change : this.messageBox) {
            if (Integer.parseInt(change.split(":")[0]) == 0) {
                this.messageBox.set(this.messageBox.indexOf(change), "1:" + change.split(":")[1]);
            }
        }
    }

    public int getBuildCost() {
        return BasicTeamWars.getInstance().getConfig().getInt("Prices_Of_Builds." + this.getMaxClaimSize());
    }

    public int getMaxClaimSize() {
        List<String> levels = new ArrayList<>();
        if (BasicTeamWars.getInstance().getConfig().getConfigurationSection("Claim_Size_Options") != null) {
            levels.addAll(BasicTeamWars.getInstance().getConfig().getConfigurationSection("Claim_Size_Options").getKeys(false));
        }
        for (String level : levels) {
            String[] split = level.split("-");
            int min = Integer.parseInt(split[0].replaceAll("#", String.valueOf(Integer.MAX_VALUE)));
            int max = Integer.parseInt(split[1].replaceAll("#", String.valueOf(Integer.MAX_VALUE)));
            if (teamLevel >= min && teamLevel <= max) {
                return BasicTeamWars.getInstance().getConfig().getInt("Claim_Size_Options." + level);
            }
        }
        return BasicTeamWars.getInstance().getConfig().getInt("Claim_Size_Options." + levels.get(0));
    }

    public boolean isJoinedWar() {
        return this.isJoinedWar;
    }

    private void setJoinWar(boolean b) {
        this.isJoinedWar = b;
    }

    public List<String> getWarriors() {
        if (!this.warriors.contains(this.teamMaker.getUniqueId().toString()))
            this.warriors.add(teamMaker.getUniqueId().toString());
        return this.warriors;
    }

    private void setWarriors(List<String> warriors) {
        this.warriors = warriors;
    }

    public void removeWarrior(String player) {
        this.warriors.remove(player);
    }

    public void addWarrior(String player) {
        this.warriors.add(player);
    }

    public void setWarStatus(boolean warStatus) {
        this.isJoinedWar = warStatus;
    }

    public String getMembersWarKit() {
        return membersWarKit;
    }

    public void setMembersWarKit(String membersWarKit) {
        this.membersWarKit = membersWarKit;
    }

    public String getModeratorsWarKit() {
        return moderatorsWarKit;
    }

    public void setModeratorsWarKit(String moderatorsWarKit) {
        this.moderatorsWarKit = moderatorsWarKit;
    }

    /**
     * @return Admin warriors kit's UUID.
     */
    public String getAdminsWarKit() {
        return adminsWarKit;
    }

    /**
     * @param adminsWarKit kit of admin warriors.
     */
    public void setAdminsWarKit(String adminsWarKit) {
        this.adminsWarKit = adminsWarKit;
    }

    /**
     * @return Member warriors UUIDs of team members.
     */
    public List<String> getMemberWarriors() {
        List<String> warriors = new ArrayList<>();
        for (String warrior : this.warriors) {
            if (!this.admins.contains(warrior) && !this.mods.contains(warrior) && members.contains(warrior)) {
                warriors.add(warrior);
            }
        }
        return warriors;
    }

    /**
     * @return Moderator warriors UUIDs of team members.
     */
    public List<String> getModeratorWarriors() {
        List<String> warriors = new ArrayList<>();
        for (String warrior : this.warriors) {
            if (this.mods.contains(warrior)) {
                warriors.add(warrior);
            }
        }
        return warriors;
    }

    /**
     * @return Admin Warriors UUIDs of team members.
     */
    public List<String> getAdminWarriors() {
        List<String> warriors = new ArrayList<>();
        for (String warrior : this.warriors) {
            if (this.admins.contains(warrior)) {
                warriors.add(warrior);
            }
        }
        warriors.add(teamMaker.getUniqueId().toString());
        return warriors;
    }

    /**
     * @param p Anyone player.
     * @return Boolean, Is the player a team member ?
     */
    public boolean isMember(OfflinePlayer p) {
        return members.contains(p.getUniqueId().toString()) || teamMaker.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString());
    }

    /**
     * @return Ratio of members size and total size.
     */
    public String getPercentage() {
        int percentage = (int) ((float) this.getMembers().size() / (float) this.getSize() * 100);
        return "" + percentage;
    }

    /**
     * @return Online members of this team.
     */
    public List<Player> getOnlineMembers() {
        List<Player> rt = new ArrayList<>();
        for (String member : members) {
            if (Bukkit.getPlayer(UUID.fromString(member)) != null) {
                rt.add(Bukkit.getPlayer(UUID.fromString(member)));
            }
        }
        if (teamMaker.isOnline() && Bukkit.getPlayer(teamMaker.getUniqueId()) != null)
            rt.add(Bukkit.getPlayer(teamMaker.getUniqueId()));
        return rt;
    }
}
