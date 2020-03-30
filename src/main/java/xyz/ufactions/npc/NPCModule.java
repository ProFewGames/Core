package xyz.ufactions.npc;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;

public class NPCModule extends Module {

	private HashSet<NPC> npcs = new HashSet<>();

	private HashMap<Module, HashSet<NPC>> moduleNPCs = new HashMap<>();

	public NPCModule(JavaPlugin plugin) {
		super("npc", plugin);

		SkinCaching.initialize(this);
	}

	@Override
	public void disable() {
		for (Module module : moduleNPCs.keySet()) {
			for (NPC npc : moduleNPCs.get(module)) {
				unregisterNPC(npc);
			}
		}
		moduleNPCs.clear();
		for (NPC npc : npcs) {
			unregisterNPC(npc);
		}
		npcs.clear();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		for(NPCPlayer player : NPCPlayer.getNpcs()) {
			if(player.isSpawned()) {
				player.show(e.getPlayer());
			}
		}
	}

	public void registerNPC(Module module, NPC npc) {
		if (!moduleNPCs.containsKey(module))
			moduleNPCs.put(module, new HashSet<>());
		moduleNPCs.get(module).add(npc);
	}

	public HashSet<NPC> getRegisteredNPCs(Module module) {
		return moduleNPCs.get(module);
	}

	public void unregisterNPCs(Module module) {
		HashSet<NPC> npcs = moduleNPCs.remove(module);
		if (npcs != null) {
			for (NPC npc : npcs) {
				unregisterNPC(npc);
			}
		}
	}

	public void unregisterNPC(NPC npc) {
		npc.despawn(true);
	}
}