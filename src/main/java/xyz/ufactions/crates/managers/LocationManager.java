package xyz.ufactions.crates.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.ufactions.crates.CratesModule;
import xyz.ufactions.crates.objects.Crate;

public class LocationManager {

	private File file;
	private FileConfiguration config;

	public LocationManager() {
		file = new File(CratesModule.getInstance().getPlugin().getDataFolder(), "locations.yml");
		config = YamlConfiguration.loadConfiguration(file);
	}

	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeLocation(Location location) {
		int orderID = getOrderIdAt(location);
		Crate crate = getCrateAt(location);
		config.set("locations." + crate.getGenericName() + "." + orderID, null);
		save();
	}

	public void setLocation(Crate crate, Location location) {
		int orderID = config.getInt("index");
		String path = "locations." + crate.getGenericName() + "." + orderID;
		config.set(path + ".world", location.getWorld().getName());
		config.set(path + ".x", location.getBlockX());
		config.set(path + ".y", location.getBlockY());
		config.set(path + ".z", location.getBlockZ());
		config.set("index", Integer.valueOf(orderID + 1));
		save();
	}

	public HashMap<Crate, List<Location>> getLocations() {
		HashMap<Crate, List<Location>> locs = new HashMap<>();
		if (config.isConfigurationSection("locations")) {
			for (String name : config.getConfigurationSection("locations").getKeys(false)) {
				for (String id : config.getConfigurationSection("locations." + name).getKeys(false)) {
					String path = "locations." + name + "." + id;
					World world = Bukkit.getWorld(config.getString(path + ".world"));
					int x = config.getInt(path + ".x");
					int y = config.getInt(path + ".y");
					int z = config.getInt(path + ".z");
					Crate crate = Crate.getCrate(name);
					if (!locs.containsKey(crate))
						locs.put(crate, new ArrayList<>());
					locs.get(crate).add(new Location(world, x, y, z));
				}
			}
		}
		return locs;
	}

	public List<Location> getLocations(Crate crate) {
		List<Location> locations = getLocations().get(crate);
		if (locations == null)
			return new ArrayList<>();
		return locations;
	}

	public boolean isCrate(Location location) {
		if (!config.isConfigurationSection("locations")) {
			return false;
		}
		for (String crateName : config.getConfigurationSection("locations").getKeys(false)) {
			String idLocation = "locations." + crateName;
			for (String orderID : config.getConfigurationSection(idLocation).getKeys(false)) {
				String path = "locations." + crateName + "." + orderID;
				if (config.getString(path + ".world").equalsIgnoreCase(location.getWorld().getName())) {
					if (config.getInt(path + ".x") == location.getBlockX()) {
						if (config.getInt(path + ".y") == location.getBlockY()) {
							if (config.getInt(path + ".z") == location.getBlockZ()) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public int getOrderIdAt(Location location) {
		if (!isCrate(location)) {
			return -1;
		}
		for (String crateName : config.getConfigurationSection("locations").getKeys(false)) {
			String idLocation = "locations." + crateName;
			for (String orderID : config.getConfigurationSection(idLocation).getKeys(false)) {
				String path = "locations." + crateName + "." + orderID;
				if (config.getString(path + ".world").equalsIgnoreCase(location.getWorld().getName())) {
					if (config.getInt(path + ".x") == location.getBlockX()) {
						if (config.getInt(path + ".y") == location.getBlockY()) {
							if (config.getInt(path + ".z") == location.getBlockZ()) {
								return Integer.parseInt(orderID);
							}
						}
					}
				}
			}
		}
		return -1;
	}

	public Crate getCrateAt(Location location) {
		if (!isCrate(location)) {
			return null;
		}
		for (String crateName : config.getConfigurationSection("locations").getKeys(false)) {
			String idLocation = "locations." + crateName;
			for (String orderID : config.getConfigurationSection(idLocation).getKeys(false)) {
				String path = "locations." + crateName + "." + orderID;
				if (config.getString(path + ".world").equalsIgnoreCase(location.getWorld().getName())) {
					if (config.getInt(path + ".x") == location.getBlockX()) {
						if (config.getInt(path + ".y") == location.getBlockY()) {
							if (config.getInt(path + ".z") == location.getBlockZ()) {
								return Crate.getCrate(crateName);
							}
						}
					}
				}
			}
		}
		return null;
	}
}
