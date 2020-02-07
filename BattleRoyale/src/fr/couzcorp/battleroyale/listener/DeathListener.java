package fr.couzcorp.battleroyale.listener;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.GameManager;
import fr.couzcorp.battleroyale.models.Messages;
import fr.couzcorp.battleroyale.models.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class DeathListener implements Listener {

    public DeathListener() {
        this.main = Main.getInstance();
    }

    private Main main;

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        PlayerObject p = main.getPlayer(e.getEntity().getUniqueId().toString());
        if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity enDamager = ((EntityDamageByEntityEvent) Objects.requireNonNull(e.getEntity().getLastDamageCause())).getDamager();
            PlayerObject damager;
            if (enDamager instanceof Projectile) {
                enDamager = (Entity) ((Projectile) enDamager).getShooter();
            }
            assert enDamager != null;
            damager = main.getPlayer(enDamager.getUniqueId().toString());
            Entity finalEnDamager = enDamager;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                e.getEntity().spigot().respawn();
                e.getEntity().setGameMode(GameMode.SPECTATOR);
                e.getEntity().setSpectatorTarget(finalEnDamager);
            }, 5L);
            if (p != null && p.getTeam() != null && p.getTeam().isAlive()) {
                String reformatTitle = GameManager.bossBar.getTitle().replace(main.getGameManager().getBRPlayers().size()+"", (main.getGameManager().getBRPlayers().size()-1)+"");
                GameManager.bossBar.setTitle(reformatTitle);
                p.setAlive(false);
                if (damager != null) {
                    e.setDeathMessage(Messages.getMessage("player_kill").replace("%player%", p.getPlayer().getName()).replace("%killer%", Objects.requireNonNull(p.getPlayer().getKiller()).getName()));
                    damager.addKill();
                    damager.addPoints(main.getGameManager().getKillPoints());
                    Material m = Material.getMaterial(Objects.requireNonNull(main.getConfig().getString("loot.kill.material")));
                    int amount = main.getConfig().getInt("loot.kill.amount");
                    assert m != null;
                    damager.getPlayer().getInventory().addItem(new ItemStack(m, amount));
                    if (main.getGameManager().getTeamsAlive().size() == 1) {
                        main.getGameManager().finalKill(damager.getTeam());
                    }
                }
            }
        }else{
            if(e.getEntity().getLastDamageCause() != null && p.getTeam() != null){
                p.setAlive(false);
                String reformatTitle = GameManager.bossBar.getTitle().replace(main.getGameManager().getBRPlayers().size()+"", (main.getGameManager().getBRPlayers().size()-1)+"");
                GameManager.bossBar.setTitle(reformatTitle);
                e.setDeathMessage(null);

            }
        }
    }
}
