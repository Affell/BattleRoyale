package fr.couzcorp.battleroyale.models;

import fr.couzcorp.battleroyale.GameManager;
import fr.couzcorp.battleroyale.managers.ListManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PlayerObject {

    private Player p;
    private Team team;
    private int kills;
    private boolean alive;
    private int points;

    private PlayerObject(Player p) {
        this.p = p;
        this.alive = true;
        this.team = null;
        this.kills = 0;
        this.points = 0;
    }

    public Player getPlayer() {
        return p;
    }

    public void sendMessage(String msg) {
        p.sendMessage(msg);
    }

    // Team
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        Team teamBefore = this.team;
        this.team = team;
        if (team != null) {
            team.addMember(this);
        }
        if (teamBefore != null) {
            teamBefore.removeMember(this);
        }
    }

    // Kills
    int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
        this.getTeam().reloadGlobalKills();
    }

    // Alive
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        this.team.reloadAlive();
    }

    // Points
    int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        if (GameManager.mode.equals("solo"))
            this.points += points;
        else this.team.addPoints(points);
    }

    /*
        STATIC METHOD
     */
    public static ListManager<PlayerObject> toNewPlayerObjectList(Collection<? extends Player> playerList) {
        ListManager<PlayerObject> list = new ListManager<>();
        for (Player p : playerList) {
            list.add(getNewPlayerObject(p));
        }
        return list;
    }

    public static PlayerObject getNewPlayerObject(Player p) {
        PlayerObject player = new PlayerObject(p);
        ((CraftPlayer) p).setMaxHealth(40);
        AttributeInstance attributeInstance = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attributeInstance != null) attributeInstance.setBaseValue(10000);
        return player;
    }
}
