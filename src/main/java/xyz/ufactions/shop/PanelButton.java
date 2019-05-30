package xyz.ufactions.shop;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.libs.ItemBuilder;

public class PanelButton implements IButton {

	private Integer[] colors = { 3, 4, 0 };
	private ItemStack item;
	private int position;

	public PanelButton(int positon) {
		item = new ItemBuilder(Material.STAINED_GLASS_PANE, colors[new Random().nextInt(colors.length)]).name(" ")
				.build();
		this.position = positon;
	}

	public PanelButton(int positon, Integer... colors) {
		item = new ItemBuilder(Material.STAINED_GLASS_PANE, colors[new Random().nextInt(colors.length)]).name(" ")
				.build();
		this.position = positon;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
	}
}