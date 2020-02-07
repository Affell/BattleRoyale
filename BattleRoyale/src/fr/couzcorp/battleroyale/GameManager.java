package fr.couzcorp.battleroyale;

import fr.couzcorp.battleroyale.managers.ListManager;
import fr.couzcorp.battleroyale.managers.MessagesManager;
import fr.couzcorp.battleroyale.models.Messages;
import fr.couzcorp.battleroyale.models.PlayerObject;
import fr.couzcorp.battleroyale.models.Team;
import fr.couzcorp.battleroyale.timers.PluginTimer;
import fr.couzcorp.battleroyale.utils.FileUtil;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/*@SuppressWarnings("ALL")*/
public class GameManager {

    private Main main;
    private MessagesManager messagesManager;
    public static boolean isStarted;
    public static boolean invincibility;
    public static String mode;
    public static BossBar bossBar;
    private int phase;
    private int[] timings;
    private int currentTiming;
    private int size;
    private int pluginTimerID;
    private WorldBorder worldBorder;
    private ListManager<PlayerObject> BRPlayers;
    private ListManager<Team> teams;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private int KILL_POINTS, STEP_1_POINTS, STEP_2_POINTS, STEP_3_POINTS, STEP_4_POINTS, STEP_5_POINTS, STEP_6_POINTS, STEP_7_POINTS, STEP_8_POINTS, STEP_9_POINTS, STEP_10_POINTS, VICTORY_POINTS;

    public GameManager() {
        main = Main.getInstance();
        messagesManager = new MessagesManager();
        BRPlayers = main.getBRPlayers();
        teams = new ListManager<>();
        mode = "";
        bossBar = Bukkit.createBossBar(NamespacedKey.minecraft("players"), "§cLe jeu n'est pas commencé", BarColor.GREEN, BarStyle.SOLID);
        phase = 0;
        timings = new int[]{10, 60, 420, 60, 120, 90, 60, 150, 25, 60, -1};
        isStarted = false;
        invincibility = true;
        size = 650 * 2;
        worldBorder = Objects.requireNonNull(Bukkit.getWorld("world")).getWorldBorder();
        worldBorder.setCenter(632, 499);
        worldBorder.setSize(size);
        worldBorder.setDamageBuffer(0.1);
        worldBorder.setWarningDistance(5);
        refreshPoints();
    }

    public boolean start(String mode) {
        main.reloadConfig();
        BRPlayers = main.getBRPlayers();
        GameManager.mode = mode;
        if (!isStarted) {
            if (mode.equals("solo")) {
                this.splitTeams();
            } else if (mode.equals("duo")) {
                this.balanceTeams();
            } else {
                GameManager.mode = "";
                return false;
            }
            Bukkit.broadcastMessage(Messages.getMessage("broadcast_mode").replace("%mode%", GameManager.mode.toUpperCase(Locale.ENGLISH)));
            isStarted = true;
            pluginTimerID = main.runPluginTimer();
            bossBar.setTitle("§9Joueurs restants : §a" + BRPlayers.size());
            for (PlayerObject p : BRPlayers.get()) {
                bossBar.addPlayer(p.getPlayer());
            }
            bossBar.setVisible(true);
        }
        return true;
    }


    public boolean stop() {
        if (isStarted) {
            isStarted = false;
            main.cancelPluginTimer(pluginTimerID);
            invincibility = true;
            currentTiming = 0;
            PluginTimer.timer = 0;
            phase = 0;
            worldBorder.setSize(size);
            Main.getInstance().reloadConfig();
            bossBar.setVisible(false);
            clearAndTpPlayers();
            resetMap();
            return true;
        }
        return false;
    }

    public void finalKill(Team lastSquad) {
        messagesManager.sendFinalKillMessage(lastSquad);
        lastSquad.addPoints(VICTORY_POINTS);
        stop();
    }

    public void update(int timer) {
        currentTiming = 0;
        for (int i = 0; i <= phase; i++) {
            currentTiming += timings[i];
        }
        if (timer == currentTiming) {
            phase++;
            currentTiming += timings[phase];
            onPhaseChanging();
            try {
                Field f = this.getClass().getDeclaredField("STEP_" + phase + "_POINTS");
                givePoints(f.getInt(this));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (phase == 10) {
            dealFinalDamages();
        }
        messagesManager.update(phase, timer, currentTiming);
    }

    private void onPhaseChanging() {
        switch (phase) {
            case 1:
                setInventory();
                Bukkit.broadcastMessage(Messages.getMessage("broadcast_start"));
                spawnPlayers();
                updateInvincibility(true);
                break;
            case 2:
                clearElytras();
                updateInvincibility(false);
                updateZone(822, timings[phase]);
                spawnChest(false);
                break;
            case 3:
                healPlayers();
                break;
            case 4:
                updateZone(411, timings[phase]);
                spawnChest(true);
            case 5:
                healPlayers();
                break;
            case 6:
                updateZone(60, timings[phase]);
                break;
            case 8:
                updateZone(10, timings[phase]);
                break;
            case 10:
                worldBorder.setSize(size);
                sendToOps("§cDebug : Final Phase");
                break;
        }
    }

    private void dealFinalDamages() {
        for (PlayerObject player : getPlayersAlive().get()) player.getPlayer().damage(3.0);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void resetMap() {
        Main.worldBlocked = true;
        Bukkit.unloadWorld("world", false);
        Bukkit.getWorlds().remove(Bukkit.getWorld("world"));
        Bukkit.reload();
        File src = new File(Main.absolutePath + "creative");
        File target = new File(Main.absolutePath + "world");
        File backup = new File(Main.absolutePath + "world_backup");
        if (backup.exists()) {
            backup.delete();
        }
        target.renameTo(backup);
        try {
            FileUtil.copyFolder(src, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().reload();
        Bukkit.broadcastMessage("§c§lReset de la map terminée !");
        WorldCreator w = new WorldCreator("world");
        w.copy(Objects.requireNonNull(Bukkit.getWorld("creative")));
        w.createWorld();
        Main.worldBlocked = false;
    }

    private void spawnPlayers() {
        sendToOps("§cDebug : spawnPlayers()");

        int radius = 970 / 2;
        for (Team team : getTeams().get()) {
            Random random = new Random();
            int x = random.nextInt(radius) - random.nextInt(radius * 2);
            int z = random.nextInt(radius) - random.nextInt(radius * 2);
            double cx = 501;
            double cz = 499;
            double distance = radius + 1;
            while (distance > radius) {
                x = random.nextInt(radius) - random.nextInt(radius * 2);
                z = random.nextInt(radius) - random.nextInt(radius * 2);
                distance = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(z - cz, 2));
            }
            for (PlayerObject player : team.getMembers().get()) {
                player.getPlayer().teleport(new Location(Bukkit.getWorld("world"), x, 300, z));
            }
        }
    }

    private void spawnChest(boolean betterLoot) {
        sendToOps("§cDebug : spawnChests() §7, betterLoot = " + betterLoot);

        Random random = new Random();
        int radius = 970 / 2;

        for (int i = 0; i < getPlayersAlive().size() * 15; i++) {
            int x = random.nextInt(radius) - random.nextInt(radius * 2);
            int z = random.nextInt(radius) - random.nextInt(radius * 2);
            double cx = 501;
            double cz = 499;
            double distance = radius + 1;
            while (distance > radius) {
                x = random.nextInt(radius) - random.nextInt(radius * 2);
                z = random.nextInt(radius) - random.nextInt(radius * 2);
                distance = Math.sqrt(Math.pow(x - cx, 2) + Math.pow(z - cz, 2));
            }

            boolean bool = true;
            Location loc;
            int y = 19;
            do {
                loc = new Location(Bukkit.getWorld("world"), x, y, z);
                if (!loc.getBlock().getType().equals(Material.AIR)) {
                    bool = false;
                } else {
                    y--;
                }
                if (y == 0) {
                    sendToOps("§cDebug : Error -> spawnChest() §7, no ground at : " + x + " " + y + " " + z);
                    bool = false;
                }
            } while (bool);

            loc = new Location(Bukkit.getWorld("world"), x, y + 1, z);
            sendToOps("§cDebug : spawnChest() §7, block placed at : " + x + " " + y + " " + z);
            loc.getBlock().setType(Material.CHEST);
            Chest chest = (Chest) loc.getBlock().getState();
            Chest randomChest = main.getInventoryManager().getRandomChest();
            for (int nb = 0; nb < randomChest.getBlockInventory().getSize(); nb++) {
                chest.getBlockInventory().setItem(nb, randomChest.getBlockInventory().getItem(nb));
            }
        }
    }

    private void healPlayers() {
        sendToOps("§cDebug : healPlayer()");
        for (PlayerObject player : getPlayersAlive().get()) {
            player.getPlayer().setHealth(player.getPlayer().getMaxHealth());
        }
    }

    private void setInventory() {
        sendToOps("§cDebug : setInventory");
        for (PlayerObject playerObject : getPlayersAlive().get()) {
            Player player = playerObject.getPlayer();
            Inventory inventory = main.getInventoryManager().getStartInventory();
            if (inventory == null) return;
            for (int i = 0; i < inventory.getSize(); i++) {
                switch (i) {
                    case 0:
                        player.getInventory().setHelmet(inventory.getItem(i));
                        break;
                    case 1:
                        player.getInventory().setChestplate(inventory.getItem(i));
                        break;
                    case 2:
                        player.getInventory().setLeggings(inventory.getItem(i));
                        break;
                    case 3:
                        player.getInventory().setBoots(inventory.getItem(i));
                        break;
                    default:
                        if (inventory.getItem(i) != null) {
                            player.getInventory().addItem(inventory.getItem(i));
                        }
                        break;
                }
            }
        }
    }

    private void clearElytras() {
        for (PlayerObject playerObject : getPlayersAlive().get()) {
            playerObject.getPlayer().getInventory().setChestplate(null);
        }
    }

    private void clearAndTpPlayers() {
        for (PlayerObject p : getBRPlayers().get()) {
            p.getPlayer().getInventory().clear();
            p.getPlayer().teleport(Objects.requireNonNull(Bukkit.getWorld("hub")).getSpawnLocation().add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }


    private void updateInvincibility(boolean bool) {
        invincibility = bool;
        sendToOps("§cDebug : updateInvincibility() §7, invincibility = " + bool);
    }

    private void updateZone(double size, int time) {
        worldBorder.setSize(size, time / (long) Main.coef);
        sendToOps("§cDebug : updateZone() §7, size = " + size + " , time = " + time);
    }

    private void givePoints(int points) {
        for (Team team : getTeamsAlive().get()) {
            team.addPoints(points);
        }
    }

    private void splitTeams() {
        this.teams.get().clear();
        for (PlayerObject p : BRPlayers.get()) {
            p.setTeam(createTeam());
        }
    }

    private void balanceTeams() {
        for (PlayerObject p : BRPlayers.get()) {
            if (p.getTeam() == null) {
                for (Team team : teams.get()) {
                    if (team.getMembers().size() == 1) {
                        p.setTeam(team);
                        break;
                    }
                }
                p.setTeam(createTeam());
            }
        }
    }

    private void sendToOps(String str) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(str);
            }
        }
    }

    public ListManager<PlayerObject> getBRPlayers() {
        return BRPlayers;
    }

    public ListManager<String> getPlayersNames() {
        ListManager<String> l = new ListManager<>();
        for (PlayerObject p : main.getOnlinePlayers().get()) {
            l.add(p.getPlayer().getName());
        }
        return l;
    }


    public ListManager<Team> getTeams() {
        return teams;
    }

    public Team createTeam() {
        Team team = new Team(teams.size() + 1);
        teams.add(team);
        return team;
    }

    private ListManager<PlayerObject> getPlayersAlive() {
        ListManager<PlayerObject> list = new ListManager<>();
        for (PlayerObject p : BRPlayers.get()) {
            if (p.isAlive()) list.add(p);
        }
        return list;
    }

    public ListManager<Team> getTeamsAlive() {
        ListManager<Team> list = new ListManager<>();
        for (Team t : teams.get()) {
            if (t.isAlive()) list.add(t);
        }
        return list;
    }

    void refreshPoints() {
        FileConfiguration config = Main.getInstance().getConfig();
        this.KILL_POINTS = config.getInt("points.kill");
        this.STEP_1_POINTS = config.getInt("points.step_1");
        this.STEP_2_POINTS = config.getInt("points.step_2");
        this.STEP_3_POINTS = config.getInt("points.step_3");
        this.STEP_4_POINTS = config.getInt("points.step_4");
        this.STEP_5_POINTS = config.getInt("points.step_5");
        this.STEP_6_POINTS = config.getInt("points.step_6");
        this.STEP_7_POINTS = config.getInt("points.step_7");
        this.STEP_8_POINTS = config.getInt("points.step_8");
        this.STEP_9_POINTS = config.getInt("points.step_9");
        this.STEP_10_POINTS = config.getInt("points.step_10");
        this.VICTORY_POINTS = config.getInt("points.victory");
    }

    public int getKillPoints() {
        return KILL_POINTS;
    }
}
