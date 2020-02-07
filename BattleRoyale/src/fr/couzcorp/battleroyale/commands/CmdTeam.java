package fr.couzcorp.battleroyale.commands;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.GameManager;
import fr.couzcorp.battleroyale.managers.RequestManager;
import fr.couzcorp.battleroyale.models.Messages;
import fr.couzcorp.battleroyale.models.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdTeam implements CommandExecutor, TabCompleter {

    private Main main = Main.getInstance();
    private RequestManager requestManager = main.getRequestManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !GameManager.isStarted) {
            PlayerObject p = main.getPlayer(((Player) sender).getUniqueId().toString());
            if (args.length == 1) {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer != null && targetPlayer != p.getPlayer()) {
                    PlayerObject target = main.getPlayer(targetPlayer.getUniqueId().toString());
                    if (p.getTeam() == null || p.getTeam().getMembers().size() == 1) {
                        requestManager.addRequest(p.getPlayer().getUniqueId(), target.getPlayer().getUniqueId());
                        target.sendMessage(Messages.getMessage("team_invite_toTarget").replace("%player%", p.getPlayer().getName()));
                        p.sendMessage(Messages.getMessage("team_invite_toSender").replace("%player%", target.getPlayer().getName()));
                    } else {
                        p.sendMessage(Messages.getMessage("team_full"));
                    }

                } else if (args[0].equalsIgnoreCase("leave")) {
                    if(p.getTeam()!=null){
                        p.setTeam(null);
                        p.sendMessage(Messages.getMessage("team_leave"));
                    }
                }
            } else if (args.length == 2) {
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer != null && targetPlayer != p.getPlayer()) {
                    PlayerObject target = main.getPlayer(targetPlayer.getUniqueId().toString());
                    if (args[0].equalsIgnoreCase("accept")) {
                        if (requestManager.containsRequest(target.getPlayer().getUniqueId(), p.getPlayer().getUniqueId())) {
                            if (target.getTeam() == null || target.getTeam().getMembers().size() == 1) {
                                requestManager.removeRequest(targetPlayer.getUniqueId(), p.getPlayer().getUniqueId());
                                if(target.getTeam() == null)target.setTeam(main.getGameManager().createTeam());
                                p.setTeam(target.getTeam());
                                p.sendMessage(Messages.getMessage("team_accept_toPlayerWhoAccept").replace("%player%", target.getPlayer().getName()));
                                target.sendMessage(Messages.getMessage("team_accept_toPlayerWhoAsk").replace("%player%", p.getPlayer().getName()));
                            } else {
                                p.sendMessage(Messages.getMessage("team_full"));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("deny")) {
                        if (requestManager.containsRequest(target.getPlayer().getUniqueId(), p.getPlayer().getUniqueId())) {
                            requestManager.removeRequest(target.getPlayer().getUniqueId(), p.getPlayer().getUniqueId());
                            p.sendMessage(Messages.getMessage("team_deny_toPlayerWhoAccept").replace("%player%", target.getPlayer().getName()));
                            target.sendMessage(Messages.getMessage("team_deny_toPlayerWhoAsk").replace("%player%", p.getPlayer().getName()));
                        }
                    }
                }
            } else {
                if(p.getTeam()!=null){
                    Bukkit.broadcastMessage(p.getTeam().getMembers().get().get(0).getPlayer().getName() + p.getTeam().getMembers().get().get(1).getPlayer().getName());
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        List<String> list = new ArrayList<>();
        if (!GameManager.isStarted && sender instanceof Player) {
            PlayerObject p = main.getPlayer(((Player) sender).getUniqueId().toString());
            if (args.length == 1) {
                list.addAll(main.getGameManager().getPlayersNames().removeReturn(p.getPlayer().getName()).get());
                if(p.getTeam()!=null)list.add("leave");
                if (requestManager.getRequestersNameForPlayer(p.getPlayer().getUniqueId()).size() > 0) {
                    list.addAll(Arrays.asList("accept","deny"));
                }

                for (String s : list) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) tab.add(s);
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                    list.addAll(requestManager.getRequestersNameForPlayer(p.getPlayer().getUniqueId()).get());
                    for (String s : list) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) tab.add(s);
                    }
                }
            }
        }
        return tab;
    }
}