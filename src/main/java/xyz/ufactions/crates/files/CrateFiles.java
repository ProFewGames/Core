package xyz.ufactions.crates.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.ufactions.crates.CratesModule;
import xyz.ufactions.crates.objects.Crate;

public class CrateFiles {
	private static CrateFiles instance = new CrateFiles();

	private CrateFiles() {
	}

	public static CrateFiles getInstance() {
		return instance;
	}

	public List<FileConfiguration> getCrateFiles() {
		List<FileConfiguration> files = new ArrayList<>();
		for (String rawName : getRawFiles()) {
			files.add(YamlConfiguration
					.loadConfiguration(new File(CratesModule.getInstance().getPlugin().getDataFolder(), "/crates/" + rawName)));
		}
		return files;
	}

	public List<String> getRawFiles() {
		File file = new File(CratesModule.getInstance().getPlugin().getDataFolder(), "/crates");
		file.mkdirs();
		String a = "/crates/";
		File b = new File(CratesModule.getInstance().getPlugin().getDataFolder(), a);
		String[] c = b.list();
		List<String> d = new ArrayList<>();
		for (int e = 0; e < c.length; e++) {
			d.add(c[e]);
		}
		return d;
	}

	public boolean createCrate(boolean createInternally, String name) {
		File file = new File(CratesModule.getInstance().getPlugin().getDataFolder(), "/crates/" + name + ".yml");
		try {
			if (!file.createNewFile()) {
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String cratePath = "Crate";
		config.set(cratePath + ".name", name);
		config.set(cratePath + ".block id", 54);
		config.set(cratePath + ".block data", 0);
		config.set(cratePath + ".type", "ROULETTE");
		config.set(cratePath + ".spin time", 5);
		config.set(cratePath + ".close time", 3);
		config.set(cratePath + ".open sound", "CHEST_OPEN");
		config.set(cratePath + ".spin sound", "NOTE_PLING");
		config.set(cratePath + ".close sound", "CHEST_CLOSE");
		String keyPath = "Key";
		config.set(keyPath + ".display name", name + " key");
		config.set(keyPath + ".id", 131);
		config.set(keyPath + ".data", 0);
		config.set(keyPath + ".glow", true);
		config.set(keyPath + ".lore", Arrays.asList("&7Use this at the nearest", "&7Crate to open!"));
		String prizePath = "Prizes";
		String diamondPath = prizePath + ".diamond";
		config.set(diamondPath + ".display name", "&b&lDIAMOND!!!");
		config.set(diamondPath + ".glow", true);
		config.set(diamondPath + ".chance", 50.0);
		config.set(diamondPath + ".item id", 264);
		config.set(diamondPath + ".item data", 0);
		config.set(diamondPath + ".item amount", 1);
		config.set(diamondPath + ".commands", Arrays.asList("give %player% 264 1"));
		config.set(diamondPath + ".lore", Arrays.asList("&aChance: &e%chance%"));
		String goldPath = prizePath + ".gold";
		config.set(goldPath + ".display name", "&6&lGold");
		config.set(goldPath + ".glow", false);
		config.set(goldPath + ".chance", 50.0);
		config.set(goldPath + ".item id", 266);
		config.set(goldPath + ".item data", 0);
		config.set(goldPath + ".item amount", 1);
		config.set(goldPath + ".commands", Arrays.asList("give %player% 266 1"));
		config.set(goldPath + ".lore", Arrays.asList("&aChance: &e%chance%"));
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (createInternally) {
			new Crate(config);
		}
		return true;
	}
}
