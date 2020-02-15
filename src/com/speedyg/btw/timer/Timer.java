package com.speedyg.btw.timer;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.player.TeamPlayer;
import com.speedyg.btw.war.WarTree;
import com.speedyg.btw.war.WeeklyWar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.UUID;

public class Timer extends BukkitRunnable {

    private WeeklyWar war;
    private int teamSize;
    private Date nowDate;
    private Date otherWarDate;

    public Timer() {
        this.war = new WeeklyWar();
        this.nowDate = new Date();
        this.teamSize = (war.getSize() / 2) + 1;
        this.otherWarDate = new Date();
        this.otherWarDate.setTime(nowDate.getTime() + (1000 * war.getTimeBetween() * teamSize));
        this.runTaskTimer(BasicTeamWars.getInstance(), 0, 20);
    }

    public WeeklyWar getWar() {
        return war;
    }

    public Date nextWarDate() {
        return otherWarDate;
    }

    @Override
    @Deprecated
    public void run() {
        Date date = new Date();
        int minWarSize = BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Options.Minimum_Team_For_Start_War");
        if (war.getWars().length > minWarSize) {
            for (WarTree war : war.getWars()) {
                if (war != null) {
                    if (war.getWarTime().toString().equals(date.toString())) {
                        war.startWar();
                    } else {
                        int lastSeconds = (int) (war.getWarTime().getTime() - date.getTime()) / 1000;
                        int lastTimer = BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Options.Coundown_Timer") > 0 ? BasicTeamWars.getInstance().getConfig().getInt("Weekly_War_Options.Coundown_Timer") : 30;
                        if (lastSeconds > 0 && lastSeconds <= lastTimer) {
                            if (war.getTeam1_Players() != null) {
                                for (String uuid : war.getTeam1_Players()) {
                                    Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                                    if (player != null) {
                                        player.sendTitle(Messages.out_war_starting, Messages.countdown_war_timer.replaceAll("<time>", this.timeSplitter(lastSeconds)));
                                    }
                                }
                            }
                            if (war.getTeam2_Players() != null) {
                                for (String uuid : war.getTeam2_Players()) {
                                    Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                                    if (player != null) {
                                        player.sendTitle(Messages.out_war_starting, Messages.countdown_war_timer.replaceAll("<time>", this.timeSplitter(lastSeconds)));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (date.toString().equals(otherWarDate.toString()) || date.getTime() >= otherWarDate.getTime()) {
                this.war = new WeeklyWar();
                this.nowDate = new Date();
                this.teamSize = (war.getSize() / 2) + 1;
                this.otherWarDate = new Date();
                this.otherWarDate.setTime(nowDate.getTime() + (1000 * war.getTimeBetween() * teamSize) + 10);
            }
        }
    }

    private String timeSplitter(int t) {
        int time = t;
        int minute;
        int hour;
        int second;
        hour = (time / (3600));
        time -= (3600) * hour;
        minute = (time / 60);
        time -= minute * 60;
        second = time;

        String hourS = Messages.hour;
        String minS = Messages.minute;
        String secS = Messages.second;

        if (hour > 0) {
            return hour + " " + hourS + ", " + minute + " " + minS + " ," + second + " " + secS;
        } else if (minute > 0) {
            return minute + " " + minS + " ," + second + " " + secS;
        } else {
            return second + " " + secS;
        }
    }

}
