package xyz.ufactions.crates.listeners;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.ufactions.crates.managers.LocationManager;
import xyz.ufactions.crates.objects.Crate;
import xyz.ufactions.crates.utils.ChatUtil;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Location location = block.getLocation();
		LocationManager manager = new LocationManager();
		Player player = e.getPlayer();
		if (manager.isCrate(location)) {
			if (player.hasPermission("customcrates.break.crate")) {
				if (player.isSneaking()) {
					manager.removeLocation(location);
					ChatUtil.sendMessage(player, "You have broken the crate at this location.");
					location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 0);
					return;
				}
			}
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		ItemStack item = e.getItemInHand();
		if (player.hasPermission("customcrates.place.crate")) {
			if (item.hasItemMeta()) {
				for (Crate crate : Crate.getCrates()) {
					if (item.getTypeId() == crate.getBlockId()
							&& item.getItemMeta().getDisplayName().equals(crate.getGenericName())) {
						Block block = e.getBlock();
						Location location = block.getLocation();
						LocationManager manager = new LocationManager();
						if (manager.isCrate(location)) {
							ChatUtil.error(player, "This location is already a crate location.");
							return;
						}
						manager.setLocation(crate, location);
						ChatUtil.success(player, "This blocks location has been marked as a crate location.");
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player player = e.getPlayer();
			if (player.isSneaking()) {
				return;
			}
			LocationManager manager = new LocationManager();
			Location loc = e.getClickedBlock().getLocation();
			if (manager.isCrate(loc)) {
				Crate crate = manager.getCrateAt(loc);
				if (crate == null) {
					return;
				}
				e.setCancelled(true);
				if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
					crate.preview(player);
					return;
				}
				Inventory inv = player.getInventory();
				ItemStack item = crate.getKey().getItem();
				item.setAmount(1);
				if (inv.containsAtLeast(item, 1)) {
					inv.removeItem(item);
				} else {
					ChatUtil.error(player, "You don't have a &7" + crate.getGenericName() + " &ckey.");
					return;
				}
				crate.spin(player);
				e.setCancelled(true);
			}
		}
	}
}