package xyz.ufactions.tags.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.shop.InverseButton;
import xyz.ufactions.tags.TitleModule;

public class BoldButton extends InverseButton<TitleModule> {

	private boolean enabled;

	public BoldButton(TitleModule plugin, boolean enabled, InverseButton<TitleModule> reverse) {
		super(plugin, Material.INK_SACK, enabled ? 10 : 8, enabled ? "&a&lBold" : "&lBold", 21, reverse,
				"&8Click to "
						+ (enabled ? "disable"
								: "enable")/*
											 * , enabled ? "" : ChatColor.
											 * translateAlternateColorCodes('&',
											 * plugin.getPermissionsManager().
											 * getGroup("flamingo").getPrefix())
											 */);

		this.enabled = enabled;
	}

	public BoldButton(TitleModule plugin, boolean enabled) {
		super(plugin, Material.INK_SACK, enabled ? 10 : 8, enabled ? "&a&lBold" : "&lBold", 21, "&8Click to " + (enabled
				? "disable" : "enable")/*
										 * , enabled ? "" : ChatColor.
										 * translateAlternateColorCodes('&',
										 * plugin.getPermissionsManager().
										 * getGroup("flamingo").getPrefix())
										 */);

		this.enabled = enabled;
	}

	@Override
	public boolean canInverse(Player player) {
		return Plugin.getTagManager().boldTag(player, !enabled);
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
	}
}