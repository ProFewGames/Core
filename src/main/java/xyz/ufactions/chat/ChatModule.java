package xyz.ufactions.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.chat.commands.ClearChatCommand;
import xyz.ufactions.chat.commands.MuteChatCommand;
import xyz.ufactions.chat.config.AutobroadcastConfiguration;
import xyz.ufactions.libs.*;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.util.List;
import java.util.Random;

public class ChatModule extends Module {

    private List<String> broadcasts;
    private Random random = new Random();
    private int bc = 0;

    private boolean autobroadcast = true;

    private boolean canChat = true;
    private long time = 0;

    private UpdateType updateType;

    public ChatModule(JavaPlugin plugin) {
        super("Chat", plugin);

        AutobroadcastConfiguration config = new AutobroadcastConfiguration(this);
        broadcasts = config.getStringList("broadcasts");
        updateType = UpdateType.valueOf(config.getString("Update Type"));

        if (broadcasts.isEmpty()) {
            autobroadcast = false;
        }
    }

    @EventHandler
    public void broadcastUpdate(UpdateEvent e) {
        if (e.getType() != updateType) return;

        if (autobroadcast) {
            String msg = "";
            if (broadcasts.size() == 1) {
                msg = broadcasts.get(0);
            } else {
                int r = random.nextInt(broadcasts.size());
                while (r == bc) {
                    r = random.nextInt(broadcasts.size());
                }
                msg = broadcasts.get(r);
                bc = r;
            }
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            msg = msg.replaceAll("%e", C.mElem).replaceAll("%b", C.mBody).replaceAll("%h", C.mHead);
            for (Player player : Bukkit.getOnlinePlayers()) {
                UtilPlayer.message(player, msg);
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SEC)
            return;

        if (time - System.currentTimeMillis() <= 0 && !canChat) {
            toggleChat(null, 0);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!canChat) {
            if (!e.getPlayer().isOp()) {
                e.setCancelled(true);
                UtilPlayer.message(e.getPlayer(), F.main(getName(), "Chat is silenced."));
            }
        }
    }

    public boolean canChat() {
        return canChat;
    }

    public boolean toggleChat(Player sender, long time) {
        if (time <= 0)
            canChat = true;
        else
            canChat = !canChat;
        if (!canChat)
            clearChat(null);
        for (Player pls : UtilServer.getPlayers()) {
            UtilPlayer.message(pls,
                    C.cGray + "Chat has been " + (canChat ? "un" : "") + "silenced "
                            + (canChat ? "" : "for " + F.elem(UtilTime.MakeStr(time) + " ")) + "by "
                            + (sender == null ? "CONSOLE" : sender.getDisplayName()) + C.cGray + ".");
        }
        if (sender != null)
            UtilPlayer.message(sender, F.main(getName(), "You " + F.ed(canChat) + " chat"
                    + (canChat ? "" : " for " + F.elem(UtilTime.MakeStr(time))) + "."));
        this.time = System.currentTimeMillis() + time;
        return canChat;
    }

    public void clearChat(Player sender) {
        for (Player pls : UtilServer.getPlayers()) {
            for (int i = 0; i < 100; i++) {
                UtilPlayer.message(pls, " ");
            }
            if (sender != null)
                UtilPlayer.message(pls,
                        C.cGray + "Chat has been cleared by " + sender.getDisplayName() + C.cGray + ".");
        }
    }

    @Override
    public void addCommands() {
        addCommand(new MuteChatCommand(this));
        addCommand(new ClearChatCommand(this));
    }
}