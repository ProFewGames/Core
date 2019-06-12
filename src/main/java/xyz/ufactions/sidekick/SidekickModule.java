package xyz.ufactions.sidekick;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dsh105.echopet.api.EchoPetAPI;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetType;
import xyz.ufactions.api.Module;

public class SidekickModule extends Module {

	public SidekickModule(JavaPlugin plugin) {
		super("Sidekick", plugin);
	}

	@Override
	public void addCommands() {
		addCommand(new SidekickCommand(this));
	}

	public boolean hasSidekick(Player player) {
		return getSidekick(player) != null;
	}

	public IPet getSidekick(Player player) {
		return getEchoPetAPI().getPet(player);
	}

	public EchoPetAPI getEchoPetAPI() {
		return EchoPetAPI.getAPI();
	}

	public void setSidekick(Player player, PetType petType) {
		getEchoPetAPI().givePet(player, petType, true);
	}

	public void removeSidekick(Player player) {
		getEchoPetAPI().removePet(player, true, true);
	}
}