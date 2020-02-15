package com.speedyg.btw.placeholder;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.player.TeamPlayer;
import com.speedyg.btw.war.WarTree;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Date;

public class PlaceHolderAPI extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }


    @Override
    public String getAuthor() {
        return BasicTeamWars.getInstance().getDescription().getAuthors().get(0);
    }


    @Override
    public String getIdentifier() {
        return "btw";
    }


    @Override
    public String getVersion() {
        return BasicTeamWars.getInstance().getDescription().getVersion();
    }


    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        TeamPlayer p = TeamPlayer.getPlayer(player);
        ABSTeam playerTeam = p.getTeam();

        if (identifier.contains("team_short_name")) {
            // %btw_team_short_name%
            if (playerTeam != null)
                return playerTeam.getShortName();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_level")) {
            // %btw_team_level%
            if (playerTeam != null)
                return "" + playerTeam.getLevel();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_power")) {
            // %btw_team_power%
            if (playerTeam != null)
                return "" + playerTeam.getPower();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_kills")) {
            // %btw_team_kills%
            if (playerTeam != null)
                return "" + playerTeam.getTotalKills();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_wins")) {
            // %btw_team_wins%
            if (playerTeam != null)
                return "" + playerTeam.getTotalVSWins();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_balance")) {
            // %btw_team_balance%
            if (playerTeam != null)
                return "" + playerTeam.getBalance();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_size")) {
            // %btw_team_size%
            if (playerTeam != null)
                return "" + playerTeam.getSize();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_members_size")) {
            // %btw_team_members_size%
            if (playerTeam != null)
                return "" + (playerTeam.getMembers().size() + 1);
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_make_time")) {
            // %btw_team_make_time%
            if (playerTeam != null)
                return "" + new Date(playerTeam.getMakeDate()).toString();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_owner")) {
            // %btw_team_owner%
            if (playerTeam != null)
                return playerTeam.getTeamMaker().getName();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_description")) {
            // %btw_team_description%
            if (playerTeam != null)
                return playerTeam.getTeamDescription();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_name")) {
            // %btw_team_name%
            if (playerTeam != null)
                return playerTeam.getNameWithoutColorCodes();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_messages_size")) {
            // %btw_team_messages_size%
            if (playerTeam != null)
                return "" + playerTeam.getMessageBox().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_warriors_size")) {
            // %btw_team_warriors_size%
            if (playerTeam != null)
                return "" + playerTeam.getWarriors().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_member_warriors_size")) {
            // %btw_team_member_warriors_size%
            if (playerTeam != null)
                return "" + playerTeam.getMemberWarriors().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_mod_warriors_size")) {
            // %btw_team_mod_warriors_size%
            if (playerTeam != null)
                return "" + playerTeam.getModeratorWarriors().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_admin_warriors_size")) {
            // %btw_team_admin_warriors_size%
            if (playerTeam != null)
                return "" + playerTeam.getAdminWarriors().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_enemy_teams_size")) {
            // %btw_team_enemy_teams_size%
            if (playerTeam != null)
                return "" + playerTeam.getEnemyTeams().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_friend_teams_size")) {
            // %btw_team_friend_teams_size%
            if (playerTeam != null)
                return "" + playerTeam.getFriendTeams().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_neutral_teams_size")) {
            // %btw_team_neutral_teams_size%
            if (playerTeam != null)
                return "" + playerTeam.getNeutralTeams().size();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("team_size_indicator")) {
            // %btw_team_size_indicator%
            if (playerTeam != null)
                return playerTeam.getPercentage();
            else
                return Messages.null_pointer;
        } else if (identifier.contains("player_kills")) {
            // %btw_player_kills%
            return "" + p.getPlayerTotalKill();
        } else if (identifier.contains("player_deaths")) {
            // %btw_player_deaths%
            return "" + p.getPlayerTotalDeaths();
        } else if (identifier.contains("player_kd")) {
            // %btw_player_kd%
            return p.KD();
        } else if (identifier.contains("claim_owner_name")) {
            // %btw_claim_owner_name%
            if (player.isOnline() && Bukkit.getPlayer(player.getUniqueId()) != null) {
                Player pOnline = Bukkit.getPlayer(player.getUniqueId());
                ABSTeam cOwner = System.getHasTeamOnLocation(pOnline.getLocation());
                if (cOwner != null) {
                    return cOwner.getNameWithoutColorCodes();
                } else {
                    return Messages.wilderness;
                }
            } else {
                return Messages.null_pointer;
            }
        } else if (identifier.contains("claim_owner_short_name")) {
            // %btw_claim_owner_short_name%
            if (player.isOnline() && Bukkit.getPlayer(player.getUniqueId()) != null) {
                Player pOnline = Bukkit.getPlayer(player.getUniqueId());
                ABSTeam cOwner = System.getHasTeamOnLocation(pOnline.getLocation());
                if (cOwner != null) {
                    return cOwner.getShortName();
                } else {
                    return Messages.wilderness;
                }
            } else {
                return Messages.null_pointer;
            }
        } else if (identifier.contains("server_total_teams")) {
            // %btw_server_total_teams%
            return "" + System.getAllTeams().size();
        } else if (identifier.contains("server_total_claims")) {
            // %btw_server_total_claims%
            return "" + System.getAllClaims().size();
        } else if (identifier.contains("server_next_war_time")) {
            // %btw_server_next_war_time%
            if (BasicTeamWars.getInstance().getConfig().getBoolean("Weekly_War_Active"))
                if (BasicTeamWars.getInstance().getTimer() != null) {
                    return BasicTeamWars.getInstance().getTimer().nextWarDate().toString();
                } else {
                    return Messages.no_war;
                }

            else
                return Messages.no_war;
        } else if (identifier.contains("team_next_war_time")) {
            // %btw_team_next_war_time%
            if (playerTeam != null) {
                if (BasicTeamWars.getInstance().getConfig().getBoolean("Weekly_War_Active")) {
                    if (BasicTeamWars.getInstance().getTimer() != null) {
                        WarTree tree = BasicTeamWars.getInstance().getTimer().getWar().findTree(playerTeam.getTeamUUID());
                        if (tree != null) {
                            Date date = new Date();
                            long time = (tree.getWarTime().getTime() - date.getTime()) / 1000;
                            if (time > 0) {
                                return System.timeSplitter(time);
                            } else {
                                return Messages.war_time_passed;
                            }
                        } else {
                            return Messages.no_war;
                        }
                    } else {
                        return Messages.no_war;
                    }
                } else {
                    return Messages.no_war;
                }
            } else {
                return Messages.null_pointer;
            }
        }

        return null;
    }
}
