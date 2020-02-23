package xyz.ufactions.npc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Addon implements Runnable {

	protected List<NPC> npcs = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public Addon(JavaPlugin plugin) {
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 20L, 20L);
	}

	public void add(NPC npc) {
		npcs.add(npc);
	}

	public void remove(NPC npc) {
		npcs.remove(npc);
	}

	public boolean contains(NPC npc) {
		return npcs.contains(npc);
	}
}