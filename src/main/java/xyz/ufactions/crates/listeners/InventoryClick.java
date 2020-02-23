package xyz.ufactions.crates.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import xyz.ufactions.crates.objects.Crate;
import xyz.ufactions.crates.utils.ChatUtil;

public class InventoryClick implements Listener {

	@EventHandler
	public void onCrateInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.getCurrentItem() == null)
			return;
		for (Crate crate : Crate.getCrates()) {
			if (e.getClickedInventory().getTitle().equalsIgnoreCase(ChatUtil.cc(crate.getDisplayName()))) {
				e.setCancelled(true);
			}
		}
	}
}