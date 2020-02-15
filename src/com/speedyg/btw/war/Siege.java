package com.speedyg.btw.war;

import com.speedyg.btw.BasicTeamWars;
import com.speedyg.btw.messages.Messages;
import com.speedyg.btw.team.ABSTeam;
import com.speedyg.btw.team.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class Siege {

    private ABSTeam attacker;
    private ABSTeam defensive;

    private List<String> warriors;

    private Claim attackedClaim;

    public Siege(ABSTeam attacker, Claim attackedClaim) {
        this.attackedClaim = attackedClaim;
        this.attacker = attacker;
        this.defensive = attackedClaim.getOwner();
        this.warriors = attacker.getWarriors();
        this.defensive.addMessage(Messages.your_claim_under_attack.replaceAll("<date>", new Date().toString()).replaceAll("<attackerTeam>", attacker.getName().replaceAll("&", "§")));
    }


    public void startSiege() {
        List<Location> random = new ArrayList<>();
        random.add(new Location(attackedClaim.getCenter().getWorld(), attackedClaim.getMaxX(), attackedClaim.getCenter().getWorld().getHighestBlockYAt(attackedClaim.getMaxX(), attackedClaim.getMaxZ() - attackedClaim.getMinZ()), attackedClaim.getMaxZ() - attackedClaim.getMinZ()));
        random.add(new Location(attackedClaim.getCenter().getWorld(), attackedClaim.getMinX(), attackedClaim.getCenter().getWorld().getHighestBlockYAt(attackedClaim.getMinX(), attackedClaim.getMaxZ() - attackedClaim.getMinZ()), attackedClaim.getMaxZ() - attackedClaim.getMinZ()));
        random.add(new Location(attackedClaim.getCenter().getWorld(), attackedClaim.getMaxX() - attackedClaim.getMinX(), attackedClaim.getCenter().getWorld().getHighestBlockYAt(attackedClaim.getMaxX() - attackedClaim.getMinX(), attackedClaim.getMaxZ()), attackedClaim.getMaxZ()));
        random.add(new Location(attackedClaim.getCenter().getWorld(), attackedClaim.getMaxX() - attackedClaim.getMinX(), attackedClaim.getCenter().getWorld().getHighestBlockYAt(attackedClaim.getMaxX() - attackedClaim.getMinX(), attackedClaim.getMinZ()), attackedClaim.getMinZ()));
        Random r = new Random();
        for (String warrior : warriors) {
            if (Bukkit.getPlayer(UUID.fromString(warrior)) != null) {
                Player p = Bukkit.getPlayer(UUID.fromString(warrior));
                BukkitScheduler scheduler = BasicTeamWars.getInstance().getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(BasicTeamWars.getInstance(), () -> {
                    p.teleport(random.get(r.nextInt(random.size())));
                    p.closeInventory();
                }, 1L);
            }
        }
    }

    public Claim getAttackedClaim() {
        return attackedClaim;
    }

    public ABSTeam getDefensive() {
        return defensive;
    }

    public ABSTeam getAttacker() {
        return attacker;
    }

}
