package xyz.ufactions.chat.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.chat.redis.RedisMessageManager;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.redis.locate.RedisLocateManager;

public class MessageCommand extends CommandBase<ChatModule> {

    public MessageCommand(ChatModule module) {
        super(module, "message", "msg", "whisper", "tell");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length >= 2) {
            String target = args[0];
            if (player.getName().equalsIgnoreCase(target)) {
                UtilPlayer.message(player, F.error(Plugin.getName(), "You cannot message yourself!"));
                return;
            }
            RedisLocateManager.getInstance().locatePlayer(redisPlayerInformation -> {
                if (redisPlayerInformation != null)
                    RedisMessageManager.getInstance().message(player.getName(), target, F.concatenate(1, " ", args));
                else
                    UtilPlayer.message(player, F.error(Plugin.getName(), "That player doesn't seem to be online anywhere on the network!"));
            }, target);
            return;
        }
        UtilPlayer.message(player, F.help("/" + AliasUsed + " <to> <message>", "Send a private message to the specified player"));
    }
}