package xyz.ufactions.tags.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.ufactions.libs.C;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.tags.data.CTag;
import xyz.ufactions.tags.data.TPlayer;
import xyz.ufactions.tags.repository.TitleRepository;

import java.util.*;

public class TitleManager {

	private List<TPlayer> players = new ArrayList<>();
	private TitleModule plugin;
	private TitleRepository repository;
	private HashSet<Player> loggingIn = new HashSet<>();
	private HashMap<Player, CTag> tags = new HashMap<>();

	public TitleManager(TitleModule plugin) {
		this.plugin = plugin;
		repository = new TitleRepository(plugin.getPlugin());
	}

	public void quit(Player player) {
		List<TPlayer> tempList = players;
		for (TPlayer pl : tempList) {
			if (pl.uuid.equals(player.getUniqueId())) {
				players.remove(pl);
				break;
			}
		}
	}

	public void login(final Player player) {
		if (loggingIn.contains(player)) {
			return;
		}
		loggingIn.add(player);
		final TPlayer tPlayer = new TPlayer();
		tPlayer.uuid = player.getUniqueId();
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				tPlayer.tag = repository.getTag(tPlayer.uuid);
				tPlayer.tags = repository.getTags(tPlayer.uuid);
				tPlayer.tokens = repository.getTokens(tPlayer.uuid);
				players.add(tPlayer);
				Bukkit.getServer().getScheduler().runTask(plugin.getPlugin(), new Runnable() {

					@Override
					public void run() {
						loggingIn.remove(player);
					}
				});
			}
		});
	}

	public List<String> getTags(Player player) {
		return getPlayer(player).tags;
	}

	public boolean hasTag(Player player, String tag) {
		for (String string : getTags(player)) {
			if (string.equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}

	public void createCustomTag(final Player player, final String tag) {
		getPlayer(player).tags.add(tag);
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				repository.addTag(player.getUniqueId(), tag);
			}
		});
	}

	public void updateTokens(final UUID uuid, final int amount) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			getPlayer(player).tokens = getPlayer(player).tokens + amount;
		}
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				repository.updateTokens(uuid, amount);
			}
		});
	}

	public int getTokens(Player player) {
		return getPlayer(player).tokens;
	}

	public void enableTag(final Player player, final String tag) {
		if (tag == null || tag.equals("")) {
			player.sendMessage(C.cYellow + "Unequipped your title");
		} else {
			player.sendMessage(C.cYellow + "Equipped your title: " + C.cWhite + tag);
		}
		getPlayer(player).tag = tag;
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				repository.saveTag(player.getUniqueId(), tag);
			}
		});
	}

	public String getTag(Player player) {
		return getPlayer(player).tag;
	}

	public String getTagFormatted(Player player) {
		String tag = getTag(player);
		tag = tag.equals("") ? "" : C.cDGray + "[" + C.cGray + tag + C.cDGray + "] " + ChatColor.RESET;
		return tag;
	}

	public void exitTag(Player player) {
		tags.remove(player);
	}

	public void uppercaseTag(Player player, boolean uppercase) {
		if (!tags.containsKey(player))
			return;
		tags.get(player).uppercase = uppercase;
	}

	public boolean boldTag(Player player, boolean bold) {
		if (!tags.containsKey(player))
			return false;
		if (!player.hasPermission("arkham.title.bold")) {
			player.sendMessage(C.cRed + "You need to have the " + C.cPurple + "Flamingo " + C.cRed
					+ "global rank to use bold in your titles! " + C.cYellow + C.Line + "buy.ufactions.xyz");
			return false;
		}
		tags.get(player).bold = bold;
		return true;
	}

	public void strikeTag(Player player, boolean strike) {
		if (!tags.containsKey(player))
			return;
		tags.get(player).strike = strike;
	}

	public void italicTag(Player player, boolean italic) {
		if (!tags.containsKey(player))
			return;
		tags.get(player).italic = italic;
	}

	public void underlineTag(Player player, boolean underline) {
		if (!tags.containsKey(player))
			return;
		tags.get(player).underline = underline;
	}

	public void prepareForTag(Player player) {
		CTag tag = new CTag();
		tag.rawTag = ChatColor.stripColor(getTag(player));
		tags.put(player, tag);
	}

	public boolean colorTag(Player player, ChatColor color) {
		if (!tags.containsKey(player)) {
			CTag tag = new CTag();
			tag.rawTag = ChatColor.stripColor(getTag(player));
			tags.put(player, tag);
		}
		char c = tags.get(player).rawTag.charAt(tags.get(player).index);
		if (tags.get(player).uppercase) {
			c = Character.toUpperCase(c);
		}
		tags.get(player).coloredTag = tags.get(player).coloredTag + color + (tags.get(player).bold ? C.Bold : "")
				+ (tags.get(player).strike ? C.Strike : "") + (tags.get(player).italic ? C.Italics : "")
				+ (tags.get(player).underline ? C.Line : "") + c;
		tags.get(player).index++;
		if (tags.get(player).rawTag.length() <= tags.get(player).index) {
			enableTag(player, tags.get(player).coloredTag);
			exitTag(player);
			return true;
		}
		return false;
	}

	public TPlayer getPlayer(Player player) {
		for (TPlayer pl : players) {
			if (pl.uuid.equals(player.getUniqueId())) {
				return pl;
			}
		}
		login(player);
		TPlayer tPlayer = new TPlayer();
		tPlayer.uuid = player.getUniqueId();
		return tPlayer;
	}

	public TitleRepository getRepository() {
		return repository;
	}

	public String getColoredTag(Player player) {
		if (!tags.containsKey(player)) {
			return "";
		}
		return tags.get(player).coloredTag;
	}

	public HashMap<Player, CTag> getQueuedTags() {
		return tags;
	}
}