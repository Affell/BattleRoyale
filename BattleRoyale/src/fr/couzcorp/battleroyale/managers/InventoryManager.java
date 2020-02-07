package fr.couzcorp.battleroyale.managers;

import fr.couzcorp.battleroyale.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class InventoryManager {

    private FileConfiguration config;

    public InventoryManager() {
        Main main = Main.getInstance();
        config = main.getConfig();
    }

    public Inventory getStartInventory() {
        return getChest("startInv").getBlockInventory();
    }

    public Chest getRandomChest() {
        int percentage = 0;
        int randomPercentage = new Random().nextInt(100);
        for (int i = 1; i <= config.getInt("loot.chests.nb"); i++) {
            percentage += getPercentage(i);
            if (randomPercentage <= percentage) {
                return getChest(String.valueOf(i));
            }
        }
        return null;
    }

    private Chest getChest(String str) {
        Location location = getLocation(str);
        assert location.getBlock().getType().equals(Material.CHEST);
        return (Chest) location.getBlock().getState();
    }

    private Location getLocation(String str) {
        List<String> list = new ArrayList<>(Arrays.asList(Objects.requireNonNull(config.getString("loot.chests." + str + ".l")).split(", ")));
        return new Location(Bukkit.getWorld("creative"), Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)));
    }

    private int getPercentage(int nb) {
        return config.getInt("loot.chests." + nb + ".p");
    }
}