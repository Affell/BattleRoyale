package fr.couzcorp.battleroyale.commands;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.models.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdStop implements CommandExecutor, TabCompleter {

    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player)sender;

        if(args.length == 0){
            if(player.isOp()) {
                if (main.getGameManager().stop()) {
                    player.sendMessage(Messages.getMessage("battle_stop_toPlayer"));
                } else {
                    player.sendMessage(Messages.getMessage("battle_notStarted_toPlayer"));
                }
            }else{
                player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande");
            }
        }else{
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
