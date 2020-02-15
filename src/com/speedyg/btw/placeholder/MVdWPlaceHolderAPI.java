package com.speedyg.btw.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.player.TeamPlayer;
import com.speedyg.btw.war.WarTree;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

public class MVdWPlaceHolderAPI {

    public void registerPlaceHolders() {
        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_short_name", e -> {
            // {btw_team_short_name}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return playerTeam.getShortName();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_level", e -> {
            // {btw_team_level}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getLevel();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_power", e -> {
            // {btw_team_power}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getPower();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_kills", e -> {
            // {btw_team_kills}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getTotalKills();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_wins", e -> {
            // {btw_team_wins}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getTotalVSWins();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_balance", e -> {
            // {btw_team_balance}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getBalance();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_size", e -> {
            // {btw_team_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getSize();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_members_size", e -> {
            // {btw_team_members_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + (playerTeam.getMembers().size() + 1);
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_make_time", e -> {
            // {btw_team_make_time}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + new Date(playerTeam.getMakeDate()).toString();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_owner", e -> {
            // {btw_team_owner}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return playerTeam.getTeamMaker().getName();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_description", e -> {
            // {btw_team_description}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return playerTeam.getTeamDescription();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_name", e -> {
            // {btw_team_name}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return playerTeam.getNameWithoutColorCodes();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_messages_size", e -> {
            // {btw_team_messages_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getMessageBox().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_warriors_size", e -> {
            // {btw_team_warriors_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getWarriors().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_member_warriors_size", e -> {
            // {btw_team_member_warriors_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getMemberWarriors().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_mod_warriors_size", e -> {
            // {btw_team_mod_warriors_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getModeratorWarriors().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_admin_warriors_size", e -> {
            // {btw_team_admin_warriors_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getAdminWarriors().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_enemy_teams_size", e -> {
            // {btw_team_enemy_teams_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getEnemyTeams().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_friend_teams_size", e -> {
            // {btw_team_friend_teams_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getFriendTeams().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_neutral_teams_size", e -> {
            // {btw_team_neutral_teams_size}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return "" + playerTeam.getNeutralTeams().size();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_size_indicator", e -> {
            // {btw_team_size_indicator}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer player = TeamPlayer.getPlayer(e.getOfflinePlayer());
                ABSTeam playerTeam = player.getTeam();
                if (playerTeam != null)
                    return playerTeam.getPercentage();
                else
                    return Messages.null_pointer;
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_player_kills", e -> {
            // {btw_player_kills}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer p = TeamPlayer.getPlayer(e.getOfflinePlayer());
                return "" + p.getPlayerTotalKill();
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_player_deaths", e -> {
            // {btw_player_deaths}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer p = TeamPlayer.getPlayer(e.getOfflinePlayer());
                return "" + p.getPlayerTotalDeaths();
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_player_kd", e -> {
            // {btw_player_kd}
            if (e.getOfflinePlayer() != null) {
                TeamPlayer p = TeamPlayer.getPlayer(e.getOfflinePlayer());
                return p.KD();
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_server_total_teams", e -> {
            // {btw_server_total_teams}
            return "" + System.getAllTeams().size();
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_server_total_claims", e -> {
            // {btw_server_total_claims}
            return "" + System.getAllClaims().size();
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_claim_owner_name", e -> {
            // {btw_location_claim_owner_name}
            if (e.getOfflinePlayer() != null) {
                if (e.getPlayer().isOnline() && Bukkit.getPlayer(e.getPlayer().getUniqueId()) != null) {
                    Player pOnline = Bukkit.getPlayer(e.getPlayer().getUniqueId());
                    ABSTeam cOwner = System.getHasTeamOnLocation(pOnline.getLocation());
                    if (cOwner != null) {
                        return cOwner.getNameWithoutColorCodes();
                    } else {
                        return Messages.wilderness;
                    }
                } else {
                    return Messages.null_pointer;
                }
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_claim_owner_short_name", e -> {
            // {btw_claim_owner_short_name}
            if (e.getOfflinePlayer() != null) {
                if (e.getPlayer().isOnline() && Bukkit.getPlayer(e.getPlayer().getUniqueId()) != null) {
                    Player pOnline = Bukkit.getPlayer(e.getPlayer().getUniqueId());
                    ABSTeam cOwner = System.getHasTeamOnLocation(pOnline.getLocation());
                    if (cOwner != null) {
                        return cOwner.getShortName();
                    } else {
                        return Messages.wilderness;
                    }
                } else {
                    return Messages.null_pointer;
                }
            } else {
                return Messages.null_pointer;
            }
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_server_next_war_time", e -> {
            // {btw_server_next_war_time}
            if (BasicTeamWars.getInstance().getConfig().getBoolean("Weekly_War_Active"))
                if (BasicTeamWars.getInstance().getTimer() != null) {
                    return BasicTeamWars.getInstance().getTimer().nextWarDate().toString();
                } else {
                    return Messages.no_war;
                }
            else
                return Messages.no_war;
        });

        PlaceholderAPI.registerPlaceholder(BasicTeamWars.getInstance(), "btw_team_next_war_time", e -> {
            // {btw_team_next_war_time}
            TeamPlayer p = TeamPlayer.getPlayer(e.getOfflinePlayer());
            ABSTeam playerTeam = p.getTeam();
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
        });
    }


}
