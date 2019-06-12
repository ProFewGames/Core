package xyz.ufactions.tags;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.tags.command.GiveTitleCreateCommand;
import xyz.ufactions.tags.command.TitleColorCommand;
import xyz.ufactions.tags.command.TitleCommand;
import xyz.ufactions.tags.command.TitleCreateCommand;
import xyz.ufactions.tags.data.UpdateMessage;
import xyz.ufactions.tags.event.TokenUpdateEvent;
import xyz.ufactions.tags.manager.TitleManager;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class TitleModule extends Module {

	private TitleManager tagManager;
	private HashMap<UUID, UpdateMessage> Response = new HashMap<>();

	public TitleModule(JavaPlugin plugin) {
		super("Tags", plugin);

		tagManager = new TitleManager(this);
		for (Player player : Bukkit.getOnlinePlayers()) {
			tagManager.login(player);
		}
	}

	public void waitForResponse(Player sender, final UUID uuid, String message) {
		UpdateMessage uMessage = new UpdateMessage();
		uMessage.sender = sender;
		uMessage.message = message;
		Response.put(uuid, uMessage);
		Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), new Runnable() {

			@Override
			public void run() {
				Response.remove(uuid);
			}
		}, 20 * 8);
	}

	@EventHandler
	public void onTokenUpdate(TokenUpdateEvent e) {
		if (Response.containsKey(e.getUniqueId())) {
			Player sender = Response.get(e.getUniqueId()).sender;
			OfflinePlayer player = Bukkit.getOfflinePlayer(e.getUniqueId());
			String name = (!player.isOnline() ? player.getName() : player.getPlayer().getDisplayName());
			String message = Response.get(e.getUniqueId()).message;
			message = message.replace("%name%", name);
			message = message.replace("%amount%", e.getAmount() + "");
			UtilPlayer.message(sender, F.main(getName(), message));
			Response.remove(e.getUniqueId());
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getType() != UpdateType.SEC)
			return;
		Set<Player> tempList = getTagManager().getQueuedTags().keySet();
		for (Player player : tempList) {
			if (!player.isOnline()) {
				getTagManager().exitTag(player);
			} else {
				if (player.getOpenInventory() == null) {
					getTagManager().exitTag(player);
				} else {
					if (player.getOpenInventory().getItem(0).getType() != Material.STAINED_GLASS_PANE) {
						getTagManager().exitTag(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		e.setFormat(e.getFormat().replace("{TAG}", tagManager.getTagFormatted(e.getPlayer())));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		tagManager.login(e.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		tagManager.quit(e.getPlayer());
	}

	public TitleManager getTagManager() {
		return tagManager;
	}

	@Override
	public void addCommands() {
		addCommand(new TitleColorCommand(this));
		addCommand(new GiveTitleCreateCommand(this));
		addCommand(new TitleCommand(this));
		addCommand(new TitleCreateCommand(this));
	}
}