package xyz.ufactions.tags.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.shop.InverseButton;
import xyz.ufactions.tags.TitleModule;

public class ItalicButton extends InverseButton<TitleModule> {

	private boolean enabled;

	public ItalicButton(TitleModule plugin, boolean enabled, InverseButton<TitleModule> reverse) {
		super(plugin, Material.INK_SACK, enabled ? 10 : 8, enabled ? "&a&oItalic" : "&oItalic", 23, reverse,
				"&8Click to " + (enabled ? "disable" : "enable"));

		this.enabled = enabled;
	}

	public ItalicButton(TitleModule plugin, boolean enabled) {
		super(plugin, Material.INK_SACK, enabled ? 10 : 8, enabled ? "&a&oItalic" : "&oItalic", 23,
				"&8Click to " + (enabled ? "disable" : "enable"));

		this.enabled = enabled;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		Plugin.getTagManager().italicTag(player, !enabled);
	}
}