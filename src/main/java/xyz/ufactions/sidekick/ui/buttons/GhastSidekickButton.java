package xyz.ufactions.sidekick.ui.buttons;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class GhastSidekickButton extends SidekickButton {

	public GhastSidekickButton(SidekickModule plugin) {
		super(plugin, 0, PetType.GHAST);
	}
}