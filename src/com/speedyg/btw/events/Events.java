package com.speedyg.btw.events;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.filesystem.System;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.Permission;
import com.speedyg.btw.team.player.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class Events implements Listener {

    private BasicTeamWars main;

    public Events(BasicTeamWars main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void respawnEvent(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        ABSTeam team = TeamPlayer.getPlayer(p).getTeam();
        if (team != null) {
            boolean isAdmin = team.getAdmins().contains(p.getUniqueId().toString()) && team.getTeamMaker().getUniqueId().equals(p.getUniqueId());
            boolean isMod = team.getMods().contains(p.getUniqueId().toString());

            if (isAdmin && team.getAdminSpawnLocation() != null)
                e.setRespawnLocation(team.getAdminSpawnLocation());
            else if (isMod && team.getModSpawnLocation() != null)
                e.setRespawnLocation(team.getModSpawnLocation());
            else if (team.getMemberSpawnLocation() != null)
                e.setRespawnLocation(team.getMemberSpawnLocation());
        }
    }


    @EventHandler
    private void deathEvent(PlayerDeathEvent e) {
        if (e.getEntity() != null) {
            if (e.getEntity().getKiller() != null) {
                TeamPlayer killer = TeamPlayer.getPlayer(e.getEntity().getKiller());
                TeamPlayer other = TeamPlayer.getPlayer(e.getEntity());
                killer.addKill();
                other.addDeath();
                if (killer.getTeam() != null && other.getTeam() != null) {
                    boolean upgrade = System.getHasTeamOnLocation(Bukkit.getPlayer(other.getPlayer().getUniqueId()).getLocation()) != null
                            && System.getHasTeamOnLocation(Bukkit.getPlayer(other.getPlayer().getUniqueId()).getLocation()).getTeamUUID().equals(other.getTeam().getTeamUUID());
                    if (killer.getTeam().getEnemyTeams().contains(other.getTeam().getTeamUUID().toString())) {
                        if (upgrade)
                            killer.getTeam().addPower(main.getConfig().getInt("Enemy_Team_Member_Kill_In_Enemy_Claim_Point"));
                        else
                            killer.getTeam().addPower(main.getConfig().getInt("Enemy_Team_Member_Kill_Point"));
                        other.getTeam().removePower(main.getConfig().getInt("Enemy_Team_Member_Death_Point"));
                    } else {
                        if (upgrade)
                            killer.getTeam().addPower(main.getConfig().getInt("Neutral_Team_Member_Kill_In_Enemy_Claim_Point"));
                        else
                            killer.getTeam().addPower(main.getConfig().getInt("Neutral_Team_Member_Kill_Point"));
                        other.getTeam().removePower(main.getConfig().getInt("Neutral_Team_Member_Death_Point"));
                    }

                    killer.getTeam().addKill();
                    killer.getTeam().saveTeam();
                    other.getTeam().saveTeam();
                }
                killer.savePlayer();
                other.savePlayer();
            }
        }
    }


    @EventHandler(priority = EventPriority.NORMAL)
    private void playerJoinEvent(PlayerJoinEvent event) {
        if (System.getPlayerFile(event.getPlayer()) == null) {
            TeamPlayer player = new TeamPlayer(event.getPlayer());
            player.savePlayer();
        }
    }


    @EventHandler
    @Deprecated
    private void playerMoveEvent(PlayerMoveEvent e) {
        ABSTeam fromLoc = System.getHasTeamOnLocation(e.getFrom());
        ABSTeam toLoc = System.getHasTeamOnLocation(e.getTo());
        if (!System.compareTeams(fromLoc, toLoc)) {
            String title;
            String desc;
            if (toLoc != null) {
                title = Messages.move_title.replaceAll("<teamName>", toLoc.getName().replaceAll("&", "§"));
                desc = Messages.move_description.replaceAll("<teamDescription>", toLoc.getTeamDescription().replaceAll("&", "§"));
            } else {
                title = Messages.wilderness_title;
                desc = Messages.wilderness_description;
            }
            e.getPlayer().sendTitle(title, desc);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreakEvent(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        ABSTeam team = System.getHasTeamOnLocation(e.getBlock().getLocation());
        if (team != null) {
            if (!team.hasPermission(e.getPlayer(), Permission.BLOCK_BREAK_CLAIMS)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.insufficient_permissions);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void itemDropEvent(PlayerDropItemEvent e) {
        if (e.isCancelled())
            return;

        ABSTeam team = System.getHasTeamOnLocation(e.getPlayer().getLocation());
        if (team != null) {
            if (!team.hasPermission(e.getPlayer(), Permission.DROP_ITEM_IN_CLAIM)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.insufficient_permissions);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockPlaceEvent(BlockPlaceEvent e) {
        ABSTeam team = System.getHasTeamOnLocation(e.getBlock().getLocation());
        if (team != null) {
            if (!team.hasPermission(e.getPlayer(), Permission.BLOCK_PLACE_CLAIMS)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.insufficient_permissions);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockExplodeEvent(EntityExplodeEvent e) {
        boolean cancelExplosions = main.getConfig().getBoolean("Close_Explosion_In_Claims");
        if (cancelExplosions) {
            for (Block loc : e.blockList()) {
                ABSTeam team = System.getHasTeamOnLocation(loc.getLocation());
                if (team != null) {
                    e.blockList().clear();
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockIgniteEvent(BlockIgniteEvent e) {
        boolean cancelSpreadFire = main.getConfig().getBoolean("Close_Spread_Fire_In_Claims");
        if (cancelSpreadFire) {
            if (e.getCause().equals(BlockIgniteEvent.IgniteCause.SPREAD)) {
                ABSTeam team = System.getHasTeamOnLocation(e.getBlock().getLocation());
                if (team != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void armorStandEvent(PlayerArmorStandManipulateEvent e) {
        ABSTeam team = System.getHasTeamOnLocation(e.getRightClicked().getLocation());
        if (team != null) {
            if (!team.hasPermission(e.getPlayer(), Permission.USE_ARMOR_STAND_IN_CLAIMS)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.insufficient_permissions);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void playerFishEvent(PlayerFishEvent e) {
        ABSTeam team = System.getHasTeamOnLocation(e.getPlayer().getLocation());
        if (team != null) {
            if (!team.hasPermission(e.getPlayer(), Permission.USE_FISHING_ROD_IN_CLAIMS)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Messages.insufficient_permissions);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void interactEvent(PlayerInteractEvent e) {
        if (e.isCancelled())
            return;

        if (e.getClickedBlock() != null) {
            ABSTeam team = System.getHasTeamOnLocation(e.getClickedBlock().getLocation());
            if (team != null) {
                if (e.getClickedBlock().getType().equals(Material.getMaterial("CHEST"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_CHEST_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("ENDER_CHEST"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_ENDER_CHEST_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("WORKBENCH"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_WORKBENCH_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("ENCHANTMENT_TABLE"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_ENCHANTMENT_TABLE_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("MOB_SPAWNER"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_MOB_SPAWNER_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("FURNACE"))
                        || e.getClickedBlock().getType().equals(Material.getMaterial("BURNING_FURNACE"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_FURNACE_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("BREWING_STAND"))
                        || e.getClickedBlock().getType().equals(Material.getMaterial("BREWING_STAND_ITEM"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_BREWING_STAND_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("BED"))
                        || e.getClickedBlock().getType().equals(Material.getMaterial("BED_BLOCK"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_BED_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("TNT"))
                        || e.getClickedBlock().getType().equals(Material.getMaterial("EXPLOSIVE_MINECART"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_TNT_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getClickedBlock().getType().equals(Material.getMaterial("HOPPER"))
                        || e.getClickedBlock().getType().equals(Material.getMaterial("HOPPER_MINECART"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_HOPPER_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else {
                    if (e.getItem() != null) {
                        if (e.getItem().getType().equals(Material.getMaterial("FISHING_ROD"))) {
                            if (!team.hasPermission(e.getPlayer(), Permission.USE_FISHING_ROD_IN_CLAIMS)) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(Messages.insufficient_permissions);
                            }
                        } else if (e.getItem().getType().equals(Material.getMaterial("FIREBALL"))
                                || e.getItem().getType().equals(Material.getMaterial("FLINT_AND_STEEL"))) {
                            if (!team.hasPermission(e.getPlayer(), Permission.USE_FLINT_AND_STEEL_IN_CLAIMS)) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(Messages.insufficient_permissions);
                            }
                        } else if (e.getItem().getType().equals(Material.getMaterial("MINECART"))
                                || e.getItem().getType().equals(Material.getMaterial("POWERED_MINECART"))
                                || e.getItem().getType().equals(Material.getMaterial("HOPPER_MINECART"))
                                || e.getItem().getType().equals(Material.getMaterial("COMMAND_MINECART"))
                                || e.getItem().getType().equals(Material.getMaterial("EXPLOSIVE_MINECART"))
                                || e.getItem().getType().equals(Material.getMaterial("STORAGE_MINECART"))) {
                            if (!team.hasPermission(e.getPlayer(), Permission.USE_MINECART_IN_CLAIMS)) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(Messages.insufficient_permissions);
                            }
                        } else if (e.getItem().getType().equals(Material.getMaterial("ARMOR_STAND"))) {
                            if (!team.hasPermission(e.getPlayer(), Permission.USE_ARMOR_STAND_IN_CLAIMS)) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(Messages.insufficient_permissions);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void projectileEvent(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            if (e.getEntityType().equals(EntityType.valueOf("ENDER_PEARL"))) {
                ABSTeam team = System.getHasTeamOnLocation(p.getLocation());
                if (team != null) {
                    if (!team.hasPermission(p, Permission.USE_ENDER_PEARL_IN_CLAIMS)) {
                        e.setCancelled(true);
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                }
            }
        }
    }


    @EventHandler
    private void vehicleDestroyEvent(VehicleDestroyEvent e) {
        if (e.getAttacker() instanceof Player) {
            Player p = (Player) e.getAttacker();
            if (e.getVehicle().getType().name().contains("MINECART")) {
                ABSTeam team = System.getHasTeamOnLocation(p.getLocation());
                if (team != null) {
                    if (!team.hasPermission(p, Permission.USE_MINECART_IN_CLAIMS)) {
                        e.setCancelled(true);
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                }
            }
        }
    }

    @EventHandler
    private void blockPistonExtendEvent(BlockPistonExtendEvent e) {
        boolean cancelBlockExtendWithPiston = main.getConfig().getBoolean("Close_Piston_Block_Extend_To_Claim");
        if (cancelBlockExtendWithPiston) {
            for (Block loc : e.getBlocks()) {
                ABSTeam team = System.getHasTeamOnLocation(loc.getLocation().add(-1, 0, 0));
                ABSTeam team1 = System.getHasTeamOnLocation(loc.getLocation().add(1, 0, 0));
                ABSTeam team2 = System.getHasTeamOnLocation(loc.getLocation().add(0, 0, -1));
                ABSTeam team3 = System.getHasTeamOnLocation(loc.getLocation().add(0, 0, 1));
                ABSTeam isMemberPlace = System.getHasTeamOnLocation(e.getBlock().getLocation());
                if ((team != null || team1 != null || team2 != null || team3 != null) && isMemberPlace == null) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    private void blockPistonExtendEvent(BlockPistonRetractEvent e) {
        boolean cancelBlockExtendWithPiston = main.getConfig().getBoolean("Close_Piston_Block_Retract_To_Claim");
        if (cancelBlockExtendWithPiston) {
            for (Block loc : e.getBlocks()) {
                ABSTeam team = System.getHasTeamOnLocation(loc.getLocation());
                ABSTeam isMemberPlace = System.getHasTeamOnLocation(e.getBlock().getLocation());
                if (team != null && isMemberPlace == null) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }


    @EventHandler
    private void playerBucketEvent(PlayerBucketEmptyEvent e) {
        if (e.isCancelled())
            return;

        if (e.getBlockClicked() != null) {
            ABSTeam team = System.getHasTeamOnLocation(e.getBlockClicked().getLocation());
            if (team != null) {
                if (e.getBucket().equals(Material.getMaterial("LAVA"))
                        || e.getBucket().equals(Material.getMaterial("LAVA_BUCKET"))
                        || e.getBucket().equals(Material.getMaterial("STATIONARY_LAVA"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_LAVA_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getBucket().equals(Material.getMaterial("WATER"))
                        || e.getBucket().equals(Material.getMaterial("WATER_BUCKET"))
                        || e.getBucket().equals(Material.getMaterial("STATIONARY_WATER"))
                        || e.getBucket().equals(Material.getMaterial("WATER_LILY"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_WATER_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                }
            }
        }
    }

    @EventHandler
    private void playerBucketEvent(PlayerBucketFillEvent e) {
        if (e.isCancelled())
            return;

        if (e.getBlockClicked() != null) {
            ABSTeam team = System.getHasTeamOnLocation(e.getBlockClicked().getLocation());
            if (team != null) {
                if (e.getBucket().equals(Material.getMaterial("LAVA"))
                        || e.getBucket().equals(Material.getMaterial("LAVA_BUCKET"))
                        || e.getBucket().equals(Material.getMaterial("STATIONARY_LAVA"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_LAVA_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getBucket().equals(Material.getMaterial("WATER"))
                        || e.getBucket().equals(Material.getMaterial("WATER_BUCKET"))
                        || e.getBucket().equals(Material.getMaterial("STATIONARY_WATER"))
                        || e.getBucket().equals(Material.getMaterial("WATER_LILY"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_WATER_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                }
            }
        }
    }


    @EventHandler
    private void interactEntityEvent(PlayerInteractEntityEvent e) {
        if (e.isCancelled())
            return;

        if (e.getRightClicked() != null) {
            ABSTeam team = System.getHasTeamOnLocation(e.getRightClicked().getLocation());
            if (team != null) {
                if (e.getRightClicked().getType().equals(EntityType.valueOf("MINECART"))
                        || e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_CHEST"))
                        || e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_COMMAND"))
                        || e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_FURNACE"))
                        || e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_HOPPER"))
                        || e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_MOB_SPAWNER"))
                        || e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_TNT"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_MINECART_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getRightClicked().getType().equals(EntityType.valueOf("MINECART_HOPPER"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_HOPPER_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                } else if (e.getRightClicked().getType().equals(EntityType.valueOf("ARMOR_STAND"))) {
                    if (!team.hasPermission(e.getPlayer(), Permission.USE_ARMOR_STAND_IN_CLAIMS)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Messages.insufficient_permissions);
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            ABSTeam team = System.getHasTeamOnLocation(e.getEntity().getLocation());
            Player p = (Player) e.getDamager();
            if (team != null) {
                if (e.getEntity() instanceof ArmorStand) {
                    if (!team.hasPermission(p, Permission.USE_ARMOR_STAND_IN_CLAIMS)) {
                        e.setCancelled(true);
                        p.sendMessage(Messages.insufficient_permissions);
                    }
                }
            }
        } else {
            boolean entityProtect = main.getConfig().getBoolean("Close_Explosion_In_Claims");
            if (entityProtect) {
                ABSTeam team = System.getHasTeamOnLocation(e.getEntity().getLocation());
                if (team != null) {
                    if (e.getEntity() instanceof ArmorStand ||
                            e.getEntity() instanceof HopperMinecart ||
                            e.getEntity() instanceof StorageMinecart) {
                        e.setCancelled(true);
                    }
                }
            }
        }

    }

}
