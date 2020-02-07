package fr.couzcorp.battleroyale.listener;

import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {

    @EventHandler
    private void onMobSpawn (EntitySpawnEvent e){
        if(e.getEntity() instanceof Mob){
            e.setCancelled(true);
        }
    }
}