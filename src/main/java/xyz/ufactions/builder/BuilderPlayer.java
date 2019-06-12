package xyz.ufactions.builder;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.ufactions.libs.*;

public class BuilderPlayer {

	private Player player;
	private int currentPage = 0;
	private HashMap<Integer, HashMap<Integer, ItemStack>> pages = new HashMap<>();

	private Map<Integer, ItemStack> inventory = new HashMap<>();

	public BuilderPlayer(Player player) {
		this.player = player;
		Inventory inv = player.getInventory();
		for(int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if(item != null && item.getType() != Material.AIR) {
				inventory.put(i, item);
			}
		}
		flipPage(0, true);
	}

	public void flipPage(int page) {
		flipPage(page, false);
	}

	public void flipPage(int page, boolean initial) {
		if (page < 0) {
			return;
		}
		if(page > 5) {
			UtilPlayer.message(player, F.error("Builder", "Cannot access more than 5 pages..."));
			return;
		}
		if(!initial) {
			savePage(currentPage, player.getInventory());
		}
		currentPage = page;
		HashMap<Integer, ItemStack> items = pages.get(currentPage);
		if (items == null) {
			items = new HashMap<>();
			pages.put(currentPage, items);
		}
		player.getInventory().clear();
		for (Integer i : items.keySet()) {
			player.getInventory().setItem(i, items.get(i));
		}
		reEquipt();
	}

	public void reEquipt() {
		player.getInventory().setItem(0, new ItemBuilder(Material.WOOD_AXE).name(C.cAqua + C.Bold + "WorldEdit Wand").build());
		player.getInventory().setItem(8, new ItemBuilder(Material.PAPER).name(C.cAqua + C.Bold + "Next Page").build());
		if (currentPage > 0) {
			player.getInventory().setItem(0,
					new ItemBuilder(Material.PAPER).name(C.cAqua + C.Bold + "Previous Page").build());
			player.getInventory().setItem(1, new ItemBuilder(Material.WOOD_AXE).name(C.cAqua + C.Bold + "WorldEdit Wand").build());
		}
	}

	private void savePage(int page, Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		HashMap<Integer, ItemStack> items = new HashMap<>();
		for (int i = 0; i < contents.length; i++) {
			items.put(i, contents[i]);
		}
		pages.put(page, items);
	}

	public void reset() {
		currentPage = 0;
		player.getInventory().clear();
		for(Integer integer : inventory.keySet()) {
			player.getInventory().setItem(integer, inventory.get(integer));
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}
}