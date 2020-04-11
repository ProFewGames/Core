package xyz.ufactions.chat.redis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.redis.CommandCallback;
import xyz.ufactions.redis.ServerCommand;

public class RedisMessageHandler implements CommandCallback {

    private RedisMessageManager manager;

    public RedisMessageHandler(RedisMessageManager manager) {
        this.manager = manager;
    }

    @Override
    public void run(ServerCommand serverCommand) {
        if (serverCommand instanceof RedisMessage) {
            RedisMessage command = (RedisMessage) serverCommand;
            Player player = Bukkit.getPlayer(command.getTarget());
            if (player != null) {
                UtilPlayer.message(player, C.mBody + "[" + C.mHead + command.getServer() + C.mBody + "] " + F.elem(command.getSender()) + C.cGray + " ➥ " + F.elem("me") + ": " + C.cWhite + command.getMessage());

                manager.addReply(player, command.getSender());

                RedisMessageCallback callback = new RedisMessageCallback(new RedisMessage(Bukkit.getServerName(), command.getSender(), command.getTarget(), command.getMessage()));
                callback.publish();
            }
        } else if (serverCommand instanceof RedisMessageCallback) {
            RedisMessageManager.getInstance().handleCallback((RedisMessageCallback) serverCommand);
        }
    }
}