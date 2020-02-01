package xyz.ufactions.chat.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class ClearChatCommand extends CommandBase<ChatModule> {

	public ClearChatCommand(ChatModule module) {
		super(module, "clearchat");
	}

	@Override
	public void execute(Player player, String[] args) {
		if(!player.hasPermission("core.command.clearchat")) {
			UtilPlayer.message(player, F.noPermission());
			return;
		}
		Plugin.clearChat(player);
	}
}