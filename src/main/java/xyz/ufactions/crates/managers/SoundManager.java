package xyz.ufactions.crates.managers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundManager {

	private static SoundManager instance = new SoundManager();

	private SoundManager() {
	}

	public static SoundManager getInstance() {
		return instance;
	}

	public Sound getSound(String sound) {
		try {
			return Sound.valueOf(sound);
		} catch (Exception e) {
			return null;
		}
	}

	public void playSound(Player player, Sound sound) {
		try {
			player.playSound(player.getLocation(), sound, 1f, 1f);
		} catch (Exception e) {
			Bukkit.getLogger().warning(
					"There was an error while playing sound " + sound + " to player " + player.getName() + "!");
		}
	}

	public void playSound(Player player, String sound) {
		playSound(player, getSound(sound));
	}
}
