package fr.couzcorp.battleroyale.models;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.GameManager;
import fr.couzcorp.battleroyale.managers.ListManager;
import org.bukkit.Bukkit;

public class Team {

    private int globalKills;
    private ListManager<PlayerObject> members;
    private boolean alive;
    private int top;
    private int points;
    private int id;

    public Team(int id) {
        this.globalKills = 0;
        this.alive = true;
        this.top = 0;
        this.points = 0;
        this.members = new ListManager<>();
        this.id = id;
    }

    // Members
    public ListManager<PlayerObject> getMembers() {
        return members;
    }
    public ListManager<String> getMembersName(){
        ListManager<String> list = new ListManager<>();
        for (PlayerObject p : members.get()){
            list.add(p.getPlayer().getName());
        }
        return list;
    }
    boolean removeMember(PlayerObject player) {
        boolean b = members.remove(player);
        if(members.size() == 0){
            Main.getInstance().getGameManager().getTeams().remove(this);
        }else {
            members.get().get(0).sendMessage(Messages.getMessage("team_leave_toOtherPlayer").replace("%player%", player.getPlayer().getName()));
        }
        return b;
    }
    public boolean addMember(PlayerObject player) {
        return members.add(player);
    }

    // Global Kills
    public int getGlobalKills() {
        return globalKills;
    }
    public void setGlobalKills(int globalKills) {
        this.globalKills = globalKills;
    }
    public void reloadGlobalKills() {
        globalKills = 0;
        for (PlayerObject p : members.get()) {
            globalKills += p.getKills();
        }
    }

    // Alive
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public void reloadAlive() {
        alive = false;
        for (PlayerObject p : members.get()) {
            if (p.isAlive()) {
                alive = true;
                break;
            }
        }
        if(!alive){
            setTop(Main.getInstance().getGameManager().getTeamsAlive().size()+1);
            Bukkit.broadcastMessage(Messages.getMessage("team_kill").replace("%players%", String.join(" et ", getMembersName().get())));
        }
    }

    // Top
    public int getTop() {
        return top;
    }
    public void setTop(int top) {
        this.top = top;
    }

    // Points
    public int getPoints() {
        if(GameManager.mode.equals("solo")){
            points = members.get().get(0).getPoints();
        }
        return points;
    }
    public void addPoints(int points) {
        this.points += points;
    }

    // ID
    public int getId() {
        return id;
    }
}
