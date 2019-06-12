package xyz.ufactions.sidekick.ui.buttons;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class SnowGolemSidekickButton extends SidekickButton {

	public SnowGolemSidekickButton(SidekickModule plugin) {
		super(plugin, 0, PetType.SNOWMAN);
	}
}