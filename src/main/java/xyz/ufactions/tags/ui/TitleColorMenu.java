package xyz.ufactions.tags.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.ufactions.libs.C;
import xyz.ufactions.shop.PanelButton;
import xyz.ufactions.shop.UpdatableShop;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.tags.ui.buttons.*;
import xyz.ufactions.tags.ui.buttons.colors.*;

public class TitleColorMenu extends UpdatableShop {

	private TitleModule plugin;
	private Player player;

	public TitleColorMenu(Player player, TitleModule plugin) {
		super(plugin.getPlugin(), C.Line + "*", 27, ShopFiller.NONE, new PanelButton(5));

		if (plugin.getTagManager().getTag(player).equals("")) {
			player.sendMessage(C.cRed + "You need to have a title equipped!");
			return;
		}

		this.plugin = plugin;
		this.player = player;

		// Buttons
		addButton(new BlackButton(plugin));
		addButton(new DarkBlueButton(plugin));
		addButton(new DarkGreenButton(plugin));
		addButton(new DarkAquaButton(plugin));
		addButton(new DarkPurpleButton(plugin));
		addButton(new GoldButton(plugin));
		addButton(new GrayButton(plugin));
		addButton(new DarkGrayButton(plugin));
		addButton(new GreenButton(plugin));
		addButton(new AquaButton(plugin));
		addButton(new RedButton(plugin));
		addButton(new LightPurpleButton(plugin));
		addButton(new YellowButton(plugin));
		addButton(new WhiteButton(plugin));

		// Special Buttons
		UppercaseButton uEnabled = new UppercaseButton(plugin, true);
		UppercaseButton uDisabled = new UppercaseButton(plugin, false, uEnabled);
		uEnabled.setReverse(uDisabled);
		addButton(uDisabled);

		BoldButton bEnabled = new BoldButton(plugin, true);
		BoldButton bDisabled = new BoldButton(plugin, false, bEnabled);
		bEnabled.setReverse(bDisabled);
		addButton(bDisabled);

		StrikeButton sEnabled = new StrikeButton(plugin, true);
		StrikeButton sDisabled = new StrikeButton(plugin, false, sEnabled);
		sEnabled.setReverse(sDisabled);
		addButton(sDisabled);

		ItalicButton iEnabled = new ItalicButton(plugin, true);
		ItalicButton iDisabled = new ItalicButton(plugin, false, iEnabled);
		iEnabled.setReverse(iDisabled);
		addButton(iDisabled);

		UnderlineButton ulEnabled = new UnderlineButton(plugin, true);
		UnderlineButton ulDisabled = new UnderlineButton(plugin, false, ulEnabled);
		ulEnabled.setReverse(ulDisabled);
		addButton(ulDisabled);

		Inventory inv = getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				addButton(new PanelButton(i));
			}
		}

		openInventory(player);
		plugin.getTagManager().prepareForTag(player);
	}

	@Override
	public void cleanUp(Player player) {
		plugin.getTagManager().exitTag(player);
	}

	@Override
	public void update() {
		if (plugin.getTagManager().getColoredTag(player).equals(""))
			return;
		updateName(player, plugin.getTagManager().getColoredTag(player) + C.cDGray + C.Line + "*");
	}
}