package fr.couzcorp.battleroyale.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;

import java.util.Objects;

public class FireBlockListener implements Listener {

    @EventHandler
    public void onFireBlock(BlockIgniteEvent e){
        if(e.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL){
            Objects.requireNonNull(e.getIgnitingBlock()).setType(Material.AIR);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent e){
        e.setCancelled(true);
    }
}
