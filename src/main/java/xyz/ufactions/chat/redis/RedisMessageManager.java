package xyz.ufactions.chat.redis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.redis.JedisManager;
import xyz.ufactions.redis.locate.RedisLocate;
import xyz.ufactions.redis.locate.RedisLocateManager;

import java.util.HashMap;
import java.util.Map;

public class RedisMessageManager {

    private static RedisMessageManager instance;

    public static RedisMessageManager getInstance() {
        if (instance == null) instance = new RedisMessageManager();
        return instance;
    }

    private String server;

    private RedisMessageManager() {
        System.out.println("<Server> RedisMessageManager has connected via redis.");
        server = Bukkit.getServerName();

        RedisMessageHandler handler = new RedisMessageHandler(this);
        JedisManager.getInstance().registerDataType("RedisMessage", RedisMessage.class, handler);
        JedisManager.getInstance().registerDataType("RedisMessageCallback", RedisMessageCallback.class, handler);
    }

    private Map<Player, String> replies = new HashMap<>();

    public String getReply(Player player) {
        return replies.get(player);
    }

    public void addReply(Player player, String target) {
        replies.put(player, target);
    }

    public void message(String sender, String target, String message) {
        RedisMessage command = new RedisMessage(server, sender, target, message);
        command.publish();
    }

    public void handleCallback(RedisMessageCallback callback) {
        RedisMessage command = callback.getMessage();
        Player player = Bukkit.getPlayer(command.getSender());
        if (player != null)
            UtilPlayer.message(player, C.mBody + "[" + C.mHead + command.getServer() + C.mBody + "] " + F.elem("me") + C.cGray + " ➥ " + F.elem(command.getTarget()) + ": " + C.cWhite + command.getMessage());
    }
}