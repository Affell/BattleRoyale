package fr.couzcorp.battleroyale.managers;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.models.Messages;
import fr.couzcorp.battleroyale.models.PlayerObject;
import fr.couzcorp.battleroyale.models.Team;
import org.bukkit.Bukkit;

public class MessagesManager {

    public void update(int phase, int timer, int currentTiming) {
        /*switch (phase){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:*/
                countdown(phase, timer, currentTiming);
        /*}*/
    }

    public void sendFinalKillMessage(Team lastSquad) {
        String names = String.join(" et ", lastSquad.getMembersName().get());
        Bukkit.broadcastMessage(Messages.getMessage("final_kill_message").replace("%players%", names).replace("%points%", "" + lastSquad.getPoints()));
        for (PlayerObject p : Main.getInstance().getGameManager().getBRPlayers().get()){
            TitleManager.sendTitle(p.getPlayer(),
                    Messages.getMessage("final_kill_title")
                            .replace("%players%", names)
                            .replace("%verbe%", (lastSquad.getMembers().size() > 1 ? "ont" : "a")),
                    Messages.getMessage("final_kill_subtitle")
                            .replace("%players%", names)
                            .replace("%verbe%", (lastSquad.getMembers().size() > 1 ? "ont" : "a"))
                    );
        }
    }

    private void countdown(int phase, int timer, int currentTiming) {
        if (currentTiming - timer <= 10) {
            switch (phase) {
                case 0:
                    Bukkit.broadcastMessage(Messages.getMessage("broadcast_compteur").replace("%timer%", String.valueOf(currentTiming - timer)).replace("%s%", (10 - timer > 1 ? "s" : "")));
                    break;
                case 1:
                case 3:
                case 5:
                case 7:
                case 9:
                    Bukkit.broadcastMessage(Messages.getMessage("broadcast_zone_incoming").replace("%timer%", String.valueOf(currentTiming - timer)).replace("%s%", (10 - timer > 1 ? "s" : "")));
                    break;
                default:
                    Bukkit.broadcastMessage(Messages.getMessage("broadcast_zone_ending").replace("%timer%", String.valueOf(currentTiming - timer)).replace("%s%", (10 - timer > 1 ? "s" : "")));
            }
        }
    }
}
