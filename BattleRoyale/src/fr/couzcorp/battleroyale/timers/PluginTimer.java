package fr.couzcorp.battleroyale.timers;

import fr.couzcorp.battleroyale.Main;
import fr.couzcorp.battleroyale.GameManager;

public class PluginTimer implements Runnable {

    private Main main = Main.getInstance();
    public static int timer = 0;

    @Override
    public void run() {
        if (GameManager.isStarted) {
            GameManager gameManager = main.getGameManager();
            gameManager.update(timer);
            timer++;
        }
    }
}