package fr.couzcorp.battleroyale.commands;

import fr.couzcorp.battleroyale.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CmdWorld implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.isOp()) {
                if (args.length == 0) {
                    return false;
                }
                if(Bukkit.getWorld(args[0]) != null) {
                    if(!Main.worldBlocked || !args[0].equals("world")) {
                        player.teleport(Objects.requireNonNull(Bukkit.getWorld(args[0])).getSpawnLocation().add(0.5, 0, 0.5));
                    }else{
                        player.sendMessage("§cLe monde est en réinitialisation !");
                    }
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        if(args.length == 1){
            List<String> list = new ArrayList<>();
            for(World world : Bukkit.getWorlds()){
                list.add(world.getName());
            }
            for(String s : list){
                if(s.toLowerCase().startsWith(args[0].toLowerCase()))tab.add(s);
            }
        }
        return tab;
    }
}
