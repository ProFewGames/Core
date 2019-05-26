package xyz.ufactions.shop;

import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;

public abstract class UpdatableShop extends Shop {

	public UpdatableShop(JavaPlugin plugin, String name, int length, ShopFiller filler, IButton... items) {
		super(plugin, name, length, filler, items);
	}

	public UpdatableShop(JavaPlugin plugin, String name, int length, ShopFiller filler, List<IButton> items) {
		super(plugin, name, length, filler, items);
	}

	public UpdatableShop(JavaPlugin plugin, Shop returnShop, String name, int length, ShopFiller filler,
			IButton... items) {
		super(plugin, returnShop, name, length, filler, items);
	}

	@Override
	public final void onClose(Player player) {
		if (!allowOverride) {
			cleanUp(player);
		}
	}

	public void cleanUp(Player player) {
	}

	private boolean allowOverride = false;

	@Override
	public void onClick(Player player, IButton button) {
		allowOverride = true;
		update();
		allowOverride = false;
	}

	public final void updateName(Player player, String title) {
		EntityPlayer ep = ((CraftPlayer) player).getHandle();
		PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest",
				new ChatMessage(title), player.getOpenInventory().getTopInventory().getSize());
		ep.playerConnection.sendPacket(packet);
		ep.updateInventory(ep.activeContainer);
	}

	public abstract void update();
}