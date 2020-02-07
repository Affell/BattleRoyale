package fr.couzcorp.battleroyale.listener;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Main.getInstance().addPlayer(e.getPlayer());
        if(!GameManager.isStarted){
            e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
            e.getPlayer().teleport(Objects.requireNonNull(Bukkit.getWorld("hub")).getSpawnLocation().add(0.5,5.5,0.5));
        }
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent e){
        e.setCancelled(true);
        if(e.getEntity() instanceof Player)((Player)e.getEntity()).setFoodLevel(20);
    }
}
