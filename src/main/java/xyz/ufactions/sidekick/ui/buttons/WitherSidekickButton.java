package xyz.ufactions.sidekick.ui.buttons;

import org.bukkit.Material;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class WitherSidekickButton extends SidekickButton {

	public WitherSidekickButton(SidekickModule plugin) {
		super(plugin, Material.SKULL_ITEM, PetType.WITHER);
	}
}