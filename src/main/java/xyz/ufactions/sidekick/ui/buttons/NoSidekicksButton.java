package xyz.ufactions.sidekick.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.libs.C;

public class NoSidekicksButton extends OrderingButton<SidekickModule> {

	public NoSidekicksButton(SidekickModule plugin) {
		super(plugin, Material.BARRIER, C.cRed + C.Bold + "No Sidekicks!", C.cGreen + "You don't have any sidekicks!",
				C.cGray + C.Italics + "That's no fun! " + C.cWhite + "Buy packs on the store!",
				"  " + C.cGold + C.Italics + "buy.ufactions.xyz");
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
	}
}