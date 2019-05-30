package xyz.ufactions.tags.ui;

import org.bukkit.entity.Player;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.UtilMath;
import xyz.ufactions.shop.IButton;
import xyz.ufactions.shop.Shop;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.tags.ui.buttons.RemoveTitleButton;
import xyz.ufactions.tags.ui.buttons.TitleButton;

import java.util.ArrayList;
import java.util.List;

public class TitleMenu {

	private Shop shop;
	private TitleModule module;

	public TitleMenu(Player player, TitleModule module) {
		this.module = module;
		buildPage(player);
	}

	private void buildPage(Player player) {
		List<IButton> buttons = new ArrayList<>();
		for (String title : module.getTagManager().getTags(player)) {
			buttons.add(new TitleButton(module, title));
		}
		if (buttons.isEmpty()) {
			player.sendMessage(C.cRed + "You don't own any tags.");
			return;
		} else {
			buttons.add(new RemoveTitleButton(module));
			this.shop = new Shop(module.getPlugin(), "Titles", UtilMath.round(buttons.size()), Shop.ShopFiller.NONE,
					buttons);
			shop.openInventory(player);
		}
	}
}