package xyz.ufactions.sidekick.ui.buttons;

import org.bukkit.Material;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class EnderdragonSidekickButton extends SidekickButton {

	public EnderdragonSidekickButton(SidekickModule plugin) {
		super(plugin, Material.DRAGON_EGG, PetType.ENDERDRAGON);
	}
}