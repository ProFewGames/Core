package xyz.ufactions.chat.commands;

import org.bukkit.entity.Player;

import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class MuteChatCommand extends CommandBase<ChatModule> {

	public MuteChatCommand(ChatModule module) {
		super(module, "mutechat", "togglechat", "silencechat");
	}

	@Override
	public void execute(Player player, String[] args) {
		if(!player.hasPermission("core.command.mutechat")){
			player.sendMessage(F.noPermission());
			return;
		}
		long time = 60000;
		if (args.length > 0) {
			try {
				time = Long.parseLong(args[0]) * 1000;
			} catch (NumberFormatException e) {
				UtilPlayer.message(player, F.help("/" + AliasUsed + " [time]",
						"Mute chat for [time] amount of time in seconds. (Default 60 seconds)"));
				return;
			}
		}
		if (time > 300000) {
			UtilPlayer.message(player,
					F.main(Plugin.getName(), "You cannot mute chat for longer than " + F.elem("5 Minutes") + "."));
			return;
		}
		Plugin.toggleChat(player, time);
	}
}