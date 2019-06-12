package xyz.ufactions.coins.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.ufactions.coins.CoinModule;
import xyz.ufactions.coins.ui.CoinsUI;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;

public class CoinCommand extends CommandBase<CoinModule> {

	public CoinCommand(CoinModule module) {
		super(module, "coin", "coins");
	}

	@Override
	public void execute(Player caller, String[] args) {
		if (caller.hasPermission("core.coin.admin")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("balance")) {
					Player target = UtilPlayer.searchOnline(caller, args[1], true);
					if (target == null)
						return;
					UtilPlayer.message(caller, F.main(Plugin.getName(), F.elem(target.getName()) + "'s coin balance is "
							+ F.elem(Plugin.getCoins(target) + "") + "."));
					return;
				}
			}
			if (args.length == 3) {
				Player target = UtilPlayer.searchOnline(caller, args[1], true);
				if (target == null)
					return;
				int coins = 0;
				try {
					coins = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					UtilPlayer.message(caller, F.error(Plugin.getName(), "Invalid amount of coins."));
					return;
				}
				if (args[0].equalsIgnoreCase("add")) {
					Plugin.addCoins(caller, target, coins);
					return;
				} else if (args[0].equalsIgnoreCase("remove")) {
					Plugin.addCoins(caller, target, -coins);
					return;
				}
			}
			if (args.length > 0) {
				UtilPlayer.message(caller, F.help("/coin balance <player>", "View another player's coin balance."));
				UtilPlayer.message(caller,
						F.help("/coin add <player> <amount>", "Add a certain amount of coins to the player."));
				UtilPlayer.message(caller,
						F.help("/coin remove <player> <amount>", "Remove a certain amount of coins from a player."));
				return;
			}
		}
		new CoinsUI(Plugin.getPlugin()).openInventory(caller);
		return;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("core.coin.admin")) {
			if (args.length == 1) {
				return getMatches(args[0], Arrays.asList("add", "remove", "balance"));
			}
			if (args.length == 2) {
				return getMatches(args[1], UtilServer.getPlayerNames());
			}
		}
		return super.onTabComplete(sender, commandLabel, args);
	}
}