package xyz.ufactions.crates.files;

import org.bukkit.configuration.file.FileConfiguration;

import xyz.ufactions.crates.CratesModule;

public interface FileInterface {

	CratesModule plugin = CratesModule.getInstance();

	public FileConfiguration getFile();

	public void save();

	public void reload();

	public void set(String path, Object value);

	public Object get(String path);

	public boolean exists();

	public void create();

	public void setHeader();
}
