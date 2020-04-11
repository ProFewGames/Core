package xyz.ufactions.chat.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.chat.redis.RedisMessageManager;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.redis.locate.RedisLocateManager;

public class ReplyCommand extends CommandBase<ChatModule> {

    public ReplyCommand(ChatModule module) {
        super(module, "reply", "r");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length >= 1) {
            String target = RedisMessageManager.getInstance().getReply(player);
            if (target == null) {
                UtilPlayer.message(player, F.error(Plugin.getName(), "You do not have anyone who you can reply to."));
                return;
            }
            RedisLocateManager.getInstance().locatePlayer(redisPlayerInformation -> {
                if (redisPlayerInformation != null)
                    RedisMessageManager.getInstance().message(player.getName(), target, F.concatenate(0, " ", args));
                else
                    UtilPlayer.message(player, F.error(Plugin.getName(), F.error(Plugin.getName(), "That player doesn't seem to be online anywhere on the network!")));
            }, target);
            return;
        }
        UtilPlayer.message(player, F.help("/" + AliasUsed + " <message>", "Quick reply to the player who last messaged you"));
    }
}