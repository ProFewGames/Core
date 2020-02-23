package xyz.ufactions.crates.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.BlockIterator;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.crates.CratesModule;
import xyz.ufactions.crates.files.CrateFiles;
import xyz.ufactions.crates.managers.LocationManager;
import xyz.ufactions.crates.objects.Crate;
import xyz.ufactions.crates.utils.ChatUtil;
import xyz.ufactions.crates.utils.UtilChat;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;

public class CrateCommand extends CommandBase<CratesModule> {

	public CrateCommand(CratesModule module) {
		super(module, "CustomCrates", "cc", "crates", "crate");
	}

	private final Block getTargetBlock(LivingEntity e, int range) {
		BlockIterator bit = new BlockIterator(e, range);
		while (bit.hasNext()) {
			Block next = bit.next();
			if ((next != null) && (next.getType() != Material.AIR)) {
				return next;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Player player, String[] args) {
		if (!player.hasPermission("core.crate")) {
			return;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("info")) {
				PluginDescriptionFile pdf = Plugin.getPlugin().getDescription();
				UtilPlayer.message(player, F.main(Plugin.getName(), "Module Information"));
				UtilPlayer.message(player, F.main("Author", ChatUtil.listToStr(pdf.getAuthors())));
				UtilPlayer.message(player, F.main("Version", pdf.getVersion()));
				UtilPlayer.message(player, F.main("Description", "A fully customizable crates module (Network Module)"));
				return;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				UtilPlayer.message(player, F.main(Plugin.getName(), "reloading..."));
				Plugin.reload();
				UtilPlayer.message(player, F.main(Plugin.getName(), "Reloaded"));
				return;
			}
			if (args[0].equalsIgnoreCase("list")) {
				UtilPlayer.message(player, F.main(Plugin.getName(), "Available Crates:"));
				for (Crate crate : Crate.getCrates()) {
					UtilPlayer.message(player, " - " + F.elem(crate.getGenericName()));
				}
				return;
			}
			if (args[0].equalsIgnoreCase("remove")) {
				Block block = getTargetBlock(player, 10);
				if (block == null) {
					UtilPlayer.message(player, F.main(Plugin.getName(), "You must be looking at a block."));
					return;
				}
				Location location = block.getLocation();
				LocationManager manager = new LocationManager();
				if (!manager.isCrate(location)) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), "The location you're looking at is not a crate."));
					return;
				}
				manager.removeLocation(location);
				UtilPlayer.message(player,
						F.main(Plugin.getName(), "You have removed the crate at the location you are looking at."));
				return;
			}
		}
		if (args.length == 2) {
			Crate crate = Crate.getCrate(args[1]);
			if (args[0].equalsIgnoreCase("crate")) {
				if (crate == null) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), C.cRed + "You have entered an invalid crate name."));
					return;
				}
				ItemStack item = crate.getCrateItem();
				item.setAmount(1);
				player.getInventory().addItem(item);
				UtilPlayer.message(player,
						F.main(Plugin.getName(), "You have received a " + F.elem(crate.getGenericName()) + " crate."));
				return;
			}
			if (args[0].equalsIgnoreCase("preview")) {
				if (crate == null) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), C.cRed + "You have entered an invalid crate name."));
					return;
				}
				crate.preview(player);
				UtilPlayer.message(player, F.main(Plugin.getName(), "Previewing " + F.elem(crate.getGenericName())));
				return;
			}
			if (args[0].equalsIgnoreCase("open")) {
				if (crate == null) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), C.cRed + "You have entered an invalid crate name."));
					return;
				}
				crate.spin(player);
				UtilPlayer.message(player, F.main(Plugin.getName(), "Opening " + F.elem(crate.getGenericName()) + "..."));
				return;
			}
			if (args[0].equalsIgnoreCase("create")) {
				if (crate != null) {
					UtilPlayer.message(player, F.main(Plugin.getName(), C.cRed + "This crate already exist."));
					return;
				}
				CrateFiles.getInstance().createCrate(true, args[1]);
				UtilPlayer.message(player,
						F.main(Plugin.getName(), "Successfull created the crate " + F.elem(args[1]) + "."));
				return;
			}
			if (args[0].equalsIgnoreCase("set")) {
				Block block = getTargetBlock(player, 10);
				if (block == null) {
					UtilPlayer.message(player, F.main(Plugin.getName(), C.cRed + "You must be looking at a block."));
					return;
				}
				if (crate == null) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), C.cRed + "You have entered an invalid crate name."));
					return;
				}
				if (block.getTypeId() != crate.getBlockId()) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(),
									C.cRed + "You must be looking at a '"
											+ F.elem(F.capitalizeFirstLetter(Material.getMaterial(crate.getBlockId())
													.name().replaceAll("_", " ")))
											+ C.cRed + "' to set it as a crate."));
					return;
				}
				Location location = block.getLocation();
				LocationManager manager = new LocationManager();
				if (manager.isCrate(location)) {
					UtilPlayer.message(player, F.main(Plugin.getName(), C.cRed + "This location is already a crate/"));
					return;
				}
				manager.setLocation(crate, location);
				UtilPlayer.message(player,
						F.main(Plugin.getName(), "The location you are looking at has been set as a crate location."));
				return;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("giveall")) {
				Crate crate = Crate.getCrate(args[1]);
				if (crate == null) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), C.cRed + "You have entered an invalid crate name."));
					return;
				}
				int amount = 0;
				try {
					amount = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					UtilPlayer.message(player, F.help("/" + AliasUsed + " giveall <crate> <amount>",
							"Give all players on the server <amount>x crate keys."));
					return;
				}
				ItemStack item = crate.getKey().getItem();
				item.setAmount(amount);
				int givenKeys = 0;
				for (Player pls : UtilServer.getPlayers()) {
					pls.getInventory().addItem(item);
					UtilPlayer.message(pls, F.main(Plugin.getName(), "You have received " + F.elem(amount + "") + "x "
							+ F.elem(crate.getGenericName()) + " crate keys."));
					givenKeys += amount;
				}
				UtilPlayer.message(player,
						F.main(Plugin.getName(),
								"You have given everyone on the server " + F.elem(amount + "") + "x "
										+ F.elem(crate.getGenericName()) + " crate keys. (Total of " + F.elem(givenKeys + "")
										+ " keys given.)"));
				return;
			}
		}
		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("give")) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					UtilPlayer.message(player, F.main(Plugin.getName(), "Player " + args[1] + " not found."));
					return;
				}
				Crate crate = Crate.getCrate(args[2]);
				if (crate == null) {
					UtilPlayer.message(player,
							F.main(Plugin.getName(), C.cRed + "You have entered an invalid crate name."));
					return;
				}
				int amount = 0;
				try {
					amount = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					UtilPlayer.message(player, F.help("/" + AliasUsed + " give <player> <crate> <amount>",
							"Give <player> <amount>x <crate> keys."));
					return;
				}
				ItemStack item = crate.getKey().getItem();
				item.setAmount(amount);
				target.getInventory().addItem(item);
				UtilPlayer.message(target, F.main(Plugin.getName(),
						"You have received " + F.elem(amount + "") + "x " + F.elem(crate.getGenericName()) + " crate keys."));
				if (target != player) {
					UtilPlayer.message(player, F.main(Plugin.getName(), "You have gave " + target.getDisplayName() + " "
							+ F.elem(amount + "") + "x " + F.elem(crate.getGenericName()) + " crate keys."));
				}
				return;
			}
		}
		UtilPlayer.message(player, F.main(Plugin.getName(), "Commands:"));
		UtilPlayer.message(player, F.help("/" + AliasUsed + " reload", "Reload all crates."));
		UtilPlayer.message(player, F.help("/" + AliasUsed + " info", "Retrieve information about this module."));
		UtilPlayer.message(player, F.help("/" + AliasUsed + " list", "List all available crates"));
		UtilPlayer.message(player, F.help("/" + AliasUsed + " remove", "Removes the crate you are looking at."));
		UtilPlayer.message(player,
				F.help("/" + AliasUsed + " set <crate>", "Sets a crate at the block you are looking at."));
		UtilPlayer.message(player, F.help("/" + AliasUsed + " open <crate>", "Open a crate without a key."));
		UtilPlayer.message(player,
				F.help("/" + AliasUsed + " preview <crate>", "Preview a crate without physically clicking one."));
		UtilPlayer.message(player, F.help("/" + AliasUsed + " giveall <crate> <amount>",
				"Give all players on the server <amount>x crate keys."));
		UtilPlayer.message(player,
				F.help("/" + AliasUsed + " give <player> <crate> <amount>", "Give <player> <amount>x <crate> keys."));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 1) {
			return getMatches(args[0],
					Arrays.asList("info", "list", "remove", "set", "open", "preview", "giveall", "give"));
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("open")
					|| args[0].equalsIgnoreCase("preview") || args[0].equalsIgnoreCase("giveall")) {
				List<String> names = new ArrayList<>();
				for (Crate crate : Crate.getCrates()) {
					names.add(crate.getGenericName().replaceAll(" ", "_"));
				}
				return getMatches(args[1], names);
			}
			if (args[0].equalsIgnoreCase("give")) {
				List<String> names = new ArrayList<>();
				for (Player player : UtilServer.getPlayers()) {
					names.add(player.getName());
				}
				return getMatches(args[1], names);
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				List<String> names = new ArrayList<>();
				for (Crate crate : Crate.getCrates()) {
					names.add(crate.getGenericName());
				}
				return getMatches(args[2], names);
			}
		}
		return super.onTabComplete(sender, commandLabel, args);
	}

	// UtilPlayer.message(player, F.main(Plugin.getName(), ""));
}
