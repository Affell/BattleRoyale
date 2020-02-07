package fr.couzcorp.battleroyale.commands;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.GameManager;
import fr.couzcorp.battleroyale.models.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdStart implements CommandExecutor, TabCompleter {

    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (player.isOp()) {
            if (args.length == 1) {

                if (!GameManager.isStarted) {
                    if(args[0].equalsIgnoreCase("solo")){
                        if(main.getGameManager().start("solo"))
                        player.sendMessage(Messages.getMessage("battle_start_toPlayer"));
                    }else if(args[0].equalsIgnoreCase("duo")){
                        if(main.getGameManager().start("duo"))
                        player.sendMessage(Messages.getMessage("battle_start_toPlayer"));
                    }else{
                        return false;
                    }
                } else {
                    player.sendMessage(Messages.getMessage("battle_alreadyStarted_toPlayer"));
                }
            } else {
                return false;
            }
        }else{
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] strings) {
        List<String> tab = new ArrayList<>();
        if (strings.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("solo");
            list.add("duo");
            for (String s : list){
                if(s.toLowerCase().startsWith(strings[0].toLowerCase())){
                    tab.add(s);
                }
            }

        }
        return tab;
    }
}
