package xyz.ufactions.sidekick.ui.buttons;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class CreeperSidekickButton extends SidekickButton {

	public CreeperSidekickButton(SidekickModule plugin) {
		super(plugin, 50, PetType.CREEPER);
	}
}