package xyz.ufactions.playtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.UtilTime;
import xyz.ufactions.playtime.commands.PlaytimeCommand;
import xyz.ufactions.playtime.events.PlaytimeRecacheEvent;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class PlaytimeModule extends Module {

	private Map<UUID, Long> data = new HashMap<>();

	private File file;
	private FileConfiguration config;

	private List<User> cache = new ArrayList<>();
	private long lastCache = 0;
	private long recacheTime = 60000;

	public PlaytimeModule(JavaPlugin plugin) {
		super("Play Time", plugin);

		File directory = new File(plugin.getDataFolder(), "playtime/");
		if (!directory.exists())
			directory.mkdirs();
		file = new File(directory, "data.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			a(player.getUniqueId());
		}
		recache();
	}

	@Override
	public void addCommands() {
		addCommand(new PlaytimeCommand(this));
	}

	@Override
	public void disable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			b(player.getUniqueId());
		}
	}

	private void a(UUID uuid) {
		data.put(uuid, Long.valueOf(System.currentTimeMillis()));
	}

	private void b(UUID uuid) {
		long sessionTime = System.currentTimeMillis() - data.remove(uuid);
		c(uuid, sessionTime);
	}

	private void c(UUID uuid, long time) {
		long configedTime = config.getLong(uuid.toString());
		configedTime += time;
		config.set(uuid.toString(), configedTime);
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		a(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		b(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getType() != UpdateType.TICK)
			return;

		if (UtilTime.elapsed(lastCache, recacheTime)) {
			recache();
		}
	}

	public String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
		return String.format("%dd %dh %dm", days, hours, minutes);
	}

	public List<User> getUsers() {
		return cache;
	}

	public User getUser(OfflinePlayer player) {
		UUID uuid = player.getUniqueId();
		if (player.isOnline()) {
			b(uuid);
			a(uuid);
		}
		return new User(uuid, config.getLong(uuid.toString(), 0L));
	}

	private List<User> recache() {
		List<User> users = new ArrayList<>();
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			users.add(getUser(player));
		}
		Collections.sort(users);
		Collections.reverse(users);
		cache = users;
		lastCache = System.currentTimeMillis();
		Bukkit.getServer().getPluginManager().callEvent(new PlaytimeRecacheEvent(this, users));
		return users;
	}
}