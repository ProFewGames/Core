package xyz.ufactions.sidekick.ui.buttons;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.SidekickButton;

public class SkeletonSidekickButton extends SidekickButton {

	public SkeletonSidekickButton(SidekickModule plugin) {
		super(plugin, 51, PetType.SKELETON);
	}
}