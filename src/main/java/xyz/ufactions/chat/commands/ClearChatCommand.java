package xyz.ufactions.chat.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;

public class ClearChatCommand extends CommandBase<ChatModule> {

	public ClearChatCommand(ChatModule module) {
		super(module, "clearchat");
	}

	@Override
	public void execute(Player player, String[] args) {
		String aZ = "core.clearchat";
		if(!player.hasPermission(aZ)){ 
			player.sendMessage(F.noPermission());
			return;
		}
		Plugin.clearChat(player);
	}
}