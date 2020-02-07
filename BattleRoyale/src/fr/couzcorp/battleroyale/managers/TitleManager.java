package fr.couzcorp.battleroyale.managers;

import org.bukkit.entity.Player;

public class TitleManager {

    public static void sendTitle(Player player, String title, String subtitle){
        player.sendTitle(title,subtitle,10, 70, 20);
    }

    /*public static void sendAllActionBar(String message){
        for (PlayerObject p : Main.getInstance().getGameManager().getBRPlayers().get()){
            sendActionBar(p.getPlayer(), message);
        }
    }

    private static void sendActionBar(Player player, String message){
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, ChatMessageType.GAME_INFO);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }*/
}
