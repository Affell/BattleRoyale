package fr.couzcorp.battleroyale.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Objects;

public class BlockListener implements Listener {

    @EventHandler
    private void onBreak(BlockBreakEvent e){
        if(Objects.equals(e.getBlock().getLocation().getWorld(), Bukkit.getWorld("hub"))){
            e.setCancelled(true);
            return;
        }
        e.setDropItems(false);
    }

    @EventHandler
    private void onPlace (BlockPlaceEvent e){
        if(Objects.equals(e.getBlock().getLocation().getWorld(), Bukkit.getWorld("hub"))){
            e.setCancelled(true);
        }
    }
}
