package fr.couzcorp.battleroyale.models;

import fr.couzcorp.battleroyale.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {

    public Messages(){
        Main main = Main.getInstance();
        FileConfiguration config = main.getConfig();
    }
    public static String getMessage(String name){
        return Main.getInstance().getConfig().getString("messages."+name);
    }

}
