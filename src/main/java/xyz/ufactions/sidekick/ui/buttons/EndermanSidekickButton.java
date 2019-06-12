package xyz.ufactions.sidekick.ui.buttons;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class EndermanSidekickButton extends SidekickButton {

	public EndermanSidekickButton(SidekickModule plugin) {
		super(plugin, 58, PetType.ENDERMAN);
	}
}