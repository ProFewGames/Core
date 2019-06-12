package xyz.ufactions.sidekick.ui.buttons;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class GiantSidekickButton extends SidekickButton {

	public GiantSidekickButton(SidekickModule plugin) {
		super(plugin, 54, PetType.GIANT);
	}
}