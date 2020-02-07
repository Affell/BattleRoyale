package fr.couzcorp.battleroyale.listener;

import fr.couzcorp.battleroyale.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PvpListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(GameManager.invincibility) {
            if (e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
    }
}
