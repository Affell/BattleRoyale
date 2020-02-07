package fr.couzcorp.battleroyale;

import fr.couzcorp.battleroyale.commands.CmdStart;
import fr.couzcorp.battleroyale.commands.CmdStop;
import fr.couzcorp.battleroyale.commands.CmdTeam;
import fr.couzcorp.battleroyale.commands.CmdWorld;
import fr.couzcorp.battleroyale.listener.*;
;import fr.couzcorp.battleroyale.managers.InventoryManager;
import fr.couzcorp.battleroyale.managers.ListManager;
import fr.couzcorp.battleroyale.managers.RequestManager;
import fr.couzcorp.battleroyale.models.PlayerObject;
import fr.couzcorp.battleroyale.timers.PluginTimer;
import net.minecraft.server.v1_14_R1.BossBattleCustom;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin {

    private static Main instance;
    public static float coef;
    private InventoryManager inventoryManager;
    private GameManager gameManager;
    private RequestManager requestManager;
    private PluginTimer pluginTimer;
    public static String absolutePath = "/home/gameserver/servers/serveur45/minecraft/";
    public static boolean worldBlocked = false;

    private ListManager<PlayerObject> onlinePlayers;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        instance = this;
        List<NamespacedKey> l = new ArrayList<>();
        for (BossBattleCustom b : ((CraftServer) getServer()).getServer().getBossBattleCustomData().getBattles()) {
            System.out.println(b.getBukkitEntity().getTitle());
            l.add(NamespacedKey.minecraft(b.getKey().getKey()));
        }
        for (NamespacedKey k : l){
            Bukkit.removeBossBar(k);
        }

        // Fields
        onlinePlayers = PlayerObject.toNewPlayerObjectList(Bukkit.getOnlinePlayers());
        gameManager = new GameManager();
        GameManager.bossBar.setVisible(false);
        coef = getConfig().getInt("coef");
        requestManager = new RequestManager();
        inventoryManager = new InventoryManager();

        //Commands
        Objects.requireNonNull(getCommand("team")).setExecutor(new CmdTeam());
        Objects.requireNonNull(getCommand("start")).setExecutor(new CmdStart());
        Objects.requireNonNull(getCommand("stop")).setExecutor(new CmdStop());
        Objects.requireNonNull(getCommand("world")).setExecutor(new CmdWorld());

        //Listeners
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new PvpListener(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new FireBlockListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);

        //Timer(s)
        pluginTimer = new PluginTimer();

        createWorlds();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        gameManager.getBRPlayers().get().clear();
        gameManager.stop();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        coef = getConfig().getInt("coef");
        if(gameManager != null) {
            gameManager.refreshPoints();
        }

    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public int runPluginTimer(){
        return Bukkit.getScheduler().runTaskTimer(this,pluginTimer,0, 20 / (long) Main.coef).getTaskId();
    }

    public void cancelPluginTimer(int id){
        Bukkit.getScheduler().cancelTask(id);
    }

    private void createWorlds() {
        List<String> l = new ArrayList<>(Arrays.asList("creative", "hub"));

        for (World w : Bukkit.getWorlds()) {
            String name = w.getName();
            l.remove(name);
        }

        for (String n : l) {
            WorldCreator worldCreator = new WorldCreator(n);
            worldCreator.createWorld();
        }
    }

    public ListManager<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    public ListManager<PlayerObject> getBRPlayers() {
        ListManager<PlayerObject> list = new ListManager<>();
        for (PlayerObject p : onlinePlayers.get()) {
            if (p.getPlayer().getGameMode() == GameMode.SURVIVAL) list.add(p);
        }
        return list.removeDuplicates();
    }

    public PlayerObject getPlayer(String uuid) {
        for (PlayerObject p : onlinePlayers.get()) {
            if (p.getPlayer().getUniqueId().toString().equals(uuid)) return p;
        }
        return null;
    }

    public void addPlayer(Player p) {
        boolean b = false;
        for (PlayerObject o : onlinePlayers.get()) {
            if (o.getPlayer().equals(p)) {
                b = true;
                break;
            }
        }
        if (!b)
            onlinePlayers.add(PlayerObject.getNewPlayerObject(p));
    }
}
