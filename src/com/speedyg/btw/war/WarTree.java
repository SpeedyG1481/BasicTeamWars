package com.speedyg.btw.war;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.LogLevel;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.kits.TeamKit;
import com.speedyg.btw.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class WarTree implements Listener {

    private WeeklyWar war;

    private ABSTeam team1;
    private ABSTeam team2;
    private Date warTime;
    private boolean warStatus = false;
    private int maxTime = 0;
    private ABSTeam winner = null;
    private ABSTeam loser = null;

    private List<String> team1_Players;
    private List<String> team2_Players;

    private long team_1Point = 0;
    private long team_2Point = 0;

    @Deprecated
    public WarTree(UUID team, UUID team1, Date warTime, WeeklyWar war) {
        this.team1 = ABSTeam.getTeamByTeamUUID(team);
        this.team2 = ABSTeam.getTeamByTeamUUID(team1);
        this.warTime = warTime;
        this.war = war;
        this.team1_Players = new ArrayList<>();
        this.team2_Players = new ArrayList<>();


        if (this.team1 == null && this.team2 != null) {
            this.winner = this.team2;
            ABSTeam teamWinner = ABSTeam.getTeamByTeamUUID(winner.getTeamUUID());
            teamWinner.addWin();
            teamWinner.addPower(BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Player_Options.Winner_Team_Point"));
            teamWinner.saveTeam();
            return;
        } else if (this.team1 != null && this.team2 == null) {
            this.winner = this.team1;
            ABSTeam teamWinner = ABSTeam.getTeamByTeamUUID(winner.getTeamUUID());
            teamWinner.addWin();
            teamWinner.addPower(BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Player_Options.Winner_Team_Point"));
            teamWinner.saveTeam();
            return;
        }

        this.team1_Players = this.team1.getWarriors();
        this.team2_Players = this.team2.getWarriors();
        List<String> team1_Player_names = new ArrayList<>(team1_Players.size());
        List<String> team2_Player_names = new ArrayList<>(team2_Players.size());

        for (String s : team1_Players)
            team1_Player_names.add(Bukkit.getOfflinePlayer(UUID.fromString(s)).getName());
        for (String s : team2_Players)
            team2_Player_names.add(Bukkit.getOfflinePlayer(UUID.fromString(s)).getName());

        this.team1.addMessage(Messages.you_have_a_new_war
                .replaceAll("<enemyTeam>", this.team2.getName().replaceAll("&", "§"))
                .replaceAll("<warriors>", String.valueOf(team1_Player_names))
                .replaceAll("<date>", warTime.toLocaleString().replaceAll(":", ".")));
        this.team1.saveTeam();
        this.team2.addMessage(Messages.you_have_a_new_war
                .replaceAll("<enemyTeam>", this.team1.getName().replaceAll("&", "§"))
                .replaceAll("<warriors>", String.valueOf(team2_Player_names))
                .replaceAll("<date>", warTime.toLocaleString().replaceAll(":", ".")));
        this.team2.saveTeam();
        Bukkit.getServer().getPluginManager().registerEvents(this, BasicTeamWars.getInstance());
    }

    public ABSTeam getTeam1() {
        return this.team1;
    }

    public ABSTeam getTeam2() {
        return this.team2;
    }

    @Deprecated
    public boolean startWar() {
        this.controlAndFinishWar();
        if (team1 == null && team2 != null) {
            winner = team2;
            return false;
        } else if (team1 == null) {
            winner = null;
            return false;
        } else if (team2 == null) {
            winner = team1;
            return false;
        }

        if (war.getTeam1_Loc() == null || war.getTeam2_Loc() == null) {
            team1.addMessage(Messages.team_war_error);
            team2.addMessage(Messages.team_war_error);
            BasicTeamWars.getInstance().log(LogLevel.ERROR, "Team war arena spawn location null! War is abandoned!");
            return false;
        }
        List<String> removablePlayer = new ArrayList<>();

        for (String pUUID : this.team1_Players) {
            if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> {
                    if (p.isDead())
                        p.spigot().respawn();
                    p.teleport(war.getTeam1_Loc());
                    p.setHealth(p.getMaxHealth());
                    p.setGameMode(GameMode.SURVIVAL);
                    this.sendEquipmentTeamPlayer(p);
                    p.closeInventory();
                }, 1L);
            } else {
                removablePlayer.add(pUUID);
            }
        }

        for (String pUUID : this.team2_Players) {
            if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> {
                    if (p.isDead())
                        p.spigot().respawn();
                    p.teleport(war.getTeam2_Loc());
                    p.setHealth(p.getMaxHealth());
                    p.setGameMode(GameMode.SURVIVAL);
                    this.sendEquipmentTeamPlayer(p);
                    p.closeInventory();
                }, 1L);
            } else {
                removablePlayer.add(pUUID);
            }
        }


        for (String remove : removablePlayer) {
            team1_Players.remove(remove);
            team2_Players.remove(remove);
        }

        this.warStatus = true;

        return true;
    }

    private void sendEquipmentTeamPlayer(Player p) {
        ABSTeam team1 = ABSTeam.getTeamByTeamUUID(this.team1.getTeamUUID());
        ABSTeam team2 = ABSTeam.getTeamByTeamUUID(this.team2.getTeamUUID());
        boolean status = false;
        TeamKit kit = null;
        if (team1.isMember(p)) {
            boolean isAdminWarrior = team1.getAdminWarriors().contains(p.getUniqueId().toString());
            boolean isModeratorWarrior = team1.getModeratorWarriors().contains(p.getUniqueId().toString());
            boolean isMemberWarrior = team1.getMemberWarriors().contains(p.getUniqueId().toString());

            boolean haveAdminKit = team1.getAdminsWarKit() != null;
            boolean haveModKit = team1.getModeratorsWarKit() != null;
            boolean haveMemberKit = team1.getMembersWarKit() != null;
            if (isAdminWarrior && haveAdminKit) {
                kit = TeamKit.getTeamKit(UUID.fromString(team1.getAdminsWarKit()));
                if (team1.getBalance() >= kit.getCost()) {
                    team1.removeBalance(kit.getCost());
                    status = true;
                }
            } else if (isModeratorWarrior && haveModKit) {
                kit = TeamKit.getTeamKit(UUID.fromString(team1.getModeratorsWarKit()));
                if (team1.getBalance() >= kit.getCost()) {
                    team1.removeBalance(kit.getCost());
                    status = true;
                }
            } else if (isMemberWarrior && haveMemberKit) {
                kit = TeamKit.getTeamKit(UUID.fromString(team1.getMembersWarKit()));
                if (team1.getBalance() >= kit.getCost()) {
                    team1.removeBalance(kit.getCost());
                    status = true;
                }
            }

        } else if (team2.isMember(p)) {
            boolean isAdminWarrior = team2.getAdminWarriors().contains(p.getUniqueId().toString());
            boolean isModeratorWarrior = team2.getModeratorWarriors().contains(p.getUniqueId().toString());
            boolean isMemberWarrior = team2.getMemberWarriors().contains(p.getUniqueId().toString());

            boolean haveAdminKit = team2.getAdminsWarKit() != null;
            boolean haveModKit = team2.getModeratorsWarKit() != null;
            boolean haveMemberKit = team2.getMembersWarKit() != null;
            if (isAdminWarrior && haveAdminKit) {
                kit = TeamKit.getTeamKit(UUID.fromString(team2.getAdminsWarKit()));
                if (team2.getBalance() >= kit.getCost()) {
                    team2.removeBalance(kit.getCost());
                    status = true;
                }
            } else if (isModeratorWarrior && haveModKit) {
                kit = TeamKit.getTeamKit(UUID.fromString(team2.getModeratorsWarKit()));
                if (team2.getBalance() >= kit.getCost()) {
                    team2.removeBalance(kit.getCost());
                    status = true;
                }
            } else if (isMemberWarrior && haveMemberKit) {
                kit = TeamKit.getTeamKit(UUID.fromString(team2.getMembersWarKit()));
                if (team2.getBalance() >= kit.getCost()) {
                    team2.removeBalance(kit.getCost());
                    status = true;
                }
            }
        }

        if (kit != null && status) {
            this.setEquipments(p, kit);
        }
        team1.saveTeam();
        team2.saveTeam();
    }

    private void setEquipments(Player p, TeamKit kit) {
        ItemStack[] armors = kit.listToItemStack(kit.getArmors());
        ItemStack[] contents = kit.listToItemStack(kit.getContents());
        for (int i = 0; i < armors.length; i++) {
            if (armors[i] != null) {
                if (armors[i].getType().name().contains("HELMET")) {
                    p.getEquipment().setHelmet(armors[i]);
                } else if (armors[i].getType().name().contains("CHESTPLATE")) {
                    p.getEquipment().setChestplate(armors[i]);
                } else if (armors[i].getType().name().contains("LEGGINGS")) {
                    p.getEquipment().setLeggings(armors[i]);
                } else if (armors[i].getType().name().contains("BOOTS")) {
                    p.getEquipment().setBoots(armors[i]);
                }
            }
        }

        for (int i = 0; i < contents.length; i++) {
            p.getInventory().setItem(i, contents[i]);
        }
    }

    private void controlAndFinishWar() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            String timeSTR = System.timeSplitter(BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Between_War_Time"));

            @Override
            public void run() {
                try {
                    maxTime++;
                    if (maxTime >= 5) {
                        if (maxTime == 6) {
                            for (String pUUID : team1_Players) {
                                if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                                    Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                                    p.sendTitle(Messages.start_war_title.replaceAll("<time>", timeSTR), Messages.start_war_sub_title.replaceAll("<time>", timeSTR));
                                }
                            }
                            for (String pUUID : team2_Players) {
                                if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                                    Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                                    p.sendTitle(Messages.start_war_title.replaceAll("<time>", timeSTR), Messages.start_war_sub_title.replaceAll("<time>", timeSTR));
                                }
                            }
                        }

                        if (team1 == null && team2 == null) {
                            winner = null;
                            this.cancel();
                        }


                        if (maxTime >= (war.getTimeBetween() + 1)) {
                            if (team_1Point > team_2Point) {
                                winner = team1;
                            } else if (team_2Point > team_1Point) {
                                winner = team2;
                            } else {
                                Random r = new Random();
                                if (team1.getLevel() == team2.getLevel()) {
                                    if (r.nextInt(100) > 50)
                                        winner = team1;
                                    else
                                        winner = team2;
                                } else {
                                    winner = team1.getLevel() < team2.getLevel() ? team1 : team2;
                                }
                            }
                            loser = winner.getTeamUUID().equals(team1.getTeamUUID()) ? team2 : team1;

                            ABSTeam teamWinner = ABSTeam.getTeamByTeamUUID(winner.getTeamUUID());
                            teamWinner.addWin();
                            teamWinner.addPower(BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Player_Options.Winner_Team_Point"));
                            teamWinner.addPower((int) (winner.getTeamUUID().toString().equals(team1.getTeamUUID().toString()) ? team_1Point : team_2Point));
                            teamWinner.saveTeam();
                        }

                        if (winner != null && loser != null) {
                            warStatus = false;


                            for (String pUUID : team1_Players) {
                                if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                                    BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
                                    scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> {
                                        Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                                        if (p.isDead())
                                            p.spigot().respawn();
                                        boolean isAdmin = team1.getAdmins().contains(p.getUniqueId().toString()) && team1.getTeamMaker().getUniqueId().equals(p.getUniqueId());
                                        boolean isMod = team1.getMods().contains(p.getUniqueId().toString());
                                        if (isAdmin && team1.getAdminSpawnLocation() != null)
                                            p.teleport(team1.getAdminSpawnLocation());
                                        else if (isMod && team1.getModSpawnLocation() != null)
                                            p.teleport(team1.getModSpawnLocation());
                                        else if (team1.getMemberSpawnLocation() != null)
                                            p.teleport(team1.getMemberSpawnLocation());
                                        else
                                            p.teleport(war.spawnLoc());
                                        p.sendMessage(Messages.war_finished_player_message);
                                    }, 1L);
                                }
                            }

                            for (String pUUID : team2_Players) {
                                if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                                    BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
                                    scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> {
                                        Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                                        if (p.isDead())
                                            p.spigot().respawn();
                                        boolean isAdmin = team2.getAdmins().contains(p.getUniqueId().toString()) && team2.getTeamMaker().getUniqueId().equals(p.getUniqueId());
                                        boolean isMod = team2.getMods().contains(p.getUniqueId().toString());
                                        if (isAdmin && team2.getAdminSpawnLocation() != null)
                                            p.teleport(team2.getAdminSpawnLocation());
                                        else if (isMod && team2.getModSpawnLocation() != null)
                                            p.teleport(team2.getModSpawnLocation());
                                        else if (team2.getMemberSpawnLocation() != null)
                                            p.teleport(team2.getMemberSpawnLocation());
                                        else
                                            p.teleport(war.spawnLoc());
                                        p.sendMessage(Messages.war_finished_player_message);
                                    }, 1L);
                                }
                            }

                            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                                p.sendTitle(Messages.war_finished_title, Messages.war_finished_sub_title
                                        .replaceAll("<loser>", loser.getName().replaceAll("&", "§"))
                                        .replaceAll("<winner>", winner.getName().replaceAll("&", "§")));
                            }
                            Bukkit.broadcastMessage(Messages.war_finished
                                    .replaceAll("<loser>", loser.getName().replaceAll("&", "§"))
                                    .replaceAll("<winner>", winner.getName().replaceAll("&", "§")));
                        }

                        if (!warStatus)
                            this.cancel();
                    } else {
                        for (String pUUID : team2_Players) {
                            if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                                Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                                p.sendTitle(Messages.in_war_starting.replaceAll("<time>", String.valueOf(5 - maxTime)), "");
                            }
                        }
                        for (String pUUID : team1_Players) {
                            if (Bukkit.getPlayer(UUID.fromString(pUUID)) != null) {
                                Player p = Bukkit.getPlayer(UUID.fromString(pUUID));
                                p.sendTitle(Messages.in_war_starting.replaceAll("<time>", String.valueOf(5 - maxTime)), "");
                            }
                        }
                    }

                } catch (Exception e) {
                    this.cancel();
                }
            }
        };
        timer.schedule(task, 0, 1000);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void commandEvent(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().isOp() && warStatus && (team1_Players.contains(e.getPlayer().getUniqueId().toString()) || team2_Players.contains(e.getPlayer().getUniqueId().toString()))) {
            List<String> usableCommands = BasicTeamWars.getInstance().getConfig().getStringList("Weekly_War_Options.Usable_Commands");
            if (!usableCommands.contains(e.getMessage())) {
                e.getPlayer().sendMessage(Messages.you_can_not_do_this_in_war);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void cancelDamageTeamMembers(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player player_1 = (Player) e.getEntity();
            Player player_2 = (Player) e.getDamager();
            if (warStatus && (team1_Players.contains(player_1.getUniqueId().toString()) ||
                    team2_Players.contains(player_1.getUniqueId().toString())) && (team1_Players.contains(player_2.getUniqueId().toString()) ||
                    team2_Players.contains(player_2.getUniqueId().toString()))) {
                if (team1_Players.contains(player_1.getUniqueId().toString()) && team1_Players.contains(player_2.getUniqueId().toString())) {
                    e.setCancelled(true);
                } else if (team2_Players.contains(player_1.getUniqueId().toString()) && team2_Players.contains(player_2.getUniqueId().toString())) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    private void teleportEvent(AsyncPlayerChatEvent e) {
        if (!BasicTeamWars.getInstance().getConfig().getBoolean("Weekly_War_Options.Use_Chat"))
            if (!e.getPlayer().isOp() && warStatus && (team1_Players.contains(e.getPlayer().getUniqueId().toString()) || team2_Players.contains(e.getPlayer().getUniqueId().toString()))) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.you_can_not_do_this_in_war);
            }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void joinEvent(PlayerJoinEvent e) {
        if (warStatus && (team1_Players.contains(e.getPlayer().getUniqueId().toString()) || team2_Players.contains(e.getPlayer().getUniqueId().toString()))) {
            if (team1_Players.contains(e.getPlayer().getUniqueId().toString()))
                e.getPlayer().teleport(war.getTeam1_Loc());
            else
                e.getPlayer().teleport(war.getTeam2_Loc());
            this.sendEquipmentTeamPlayer(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void quitEvent(PlayerQuitEvent e) {
        if (warStatus && (team1_Players.contains(e.getPlayer().getUniqueId().toString()) || team2_Players.contains(e.getPlayer().getUniqueId().toString()))) {
            if (war.spawnLoc() != null)
                e.getPlayer().teleport(war.spawnLoc());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void deathEvent(PlayerDeathEvent e) {
        if (e.getEntity() != null) {
            if (warStatus && (team1_Players.contains(e.getEntity().getUniqueId().toString()) || team2_Players.contains(e.getEntity().getUniqueId().toString()))) {
                if (e.getEntity().getKiller() != null) {
                    Player killer = e.getEntity().getKiller();
                    if ((team1_Players.contains(killer.getUniqueId().toString()) || team2_Players.contains(killer.getUniqueId().toString()))) {
                        int point = BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Player_Options.Killer_Player_Point") > 0 ? BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Player_Options.Killer_Player_Point") : 5;
                        int addPoint = point * ((Math.abs(team1.getLevel() - team2.getLevel())) > 0 ? (Math.abs(team1.getLevel() - team2.getLevel())) : 1);
                        TeamPlayer player = TeamPlayer.getPlayer(killer);
                        player.getTeam().addPower(addPoint);
                        player.getTeam().saveTeam();
                        if (team1_Players.contains(killer.getUniqueId().toString())) {
                            this.team_1Point += addPoint;
                        } else {
                            this.team_2Point += addPoint;
                        }
                    }
                }

                e.getEntity().spigot().respawn();
                if (team1_Players.contains(e.getEntity().getUniqueId().toString())) {
                    e.getEntity().teleport(war.getTeam1_Loc());
                } else {
                    e.getEntity().teleport(war.getTeam2_Loc());
                }
                BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> this.sendEquipmentTeamPlayer(e.getEntity()), 4L);
                if (BasicTeamWars.getInstance().getConfig().getBoolean("Weekly_War_Player_Options.Death_Drops_Clear"))
                    e.getDrops().clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void moveEvent(PlayerMoveEvent e) {
        if ((this.maxTime < 5) && warStatus && (team1_Players.contains(e.getPlayer().getUniqueId().toString()) || team2_Players.contains(e.getPlayer().getUniqueId().toString()))) {
            e.setCancelled(true);
        }
    }


    public Date getWarTime() {
        return warTime;
    }

    public List<String> getTeam1_Players() {
        return this.team1_Players;
    }

    public List<String> getTeam2_Players() {
        return this.team2_Players;
    }

    public void removeTeam1Player(String uuid) {
        this.team1_Players.remove(uuid);
    }

    public void removeTeam2Player(String uuid) {
        this.team2_Players.remove(uuid);
    }
}
