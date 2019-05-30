package xyz.ufactions.tags.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.shop.InverseButton;
import xyz.ufactions.tags.TitleModule;

public class UnderlineButton extends InverseButton<TitleModule> {

	private boolean enabled;

	public UnderlineButton(TitleModule plugin, boolean enabled, InverseButton<TitleModule> reverse) {
		super(plugin, Material.INK_SACK, enabled ? 10 : 8, enabled ? "&a&nUnderline" : "&nUnderline", 24, reverse,
				"&8Click to " + (enabled ? "disable" : "enable"));

		this.enabled = enabled;
	}

	public UnderlineButton(TitleModule plugin, boolean enabled) {
		super(plugin, Material.INK_SACK, enabled ? 10 : 8, enabled ? "&a&nUnderline" : "&nUnderline", 24,
				"&8Click to " + (enabled ? "disable" : "enable"));

		this.enabled = enabled;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		Plugin.getTagManager().underlineTag(player, !enabled);
	}
}