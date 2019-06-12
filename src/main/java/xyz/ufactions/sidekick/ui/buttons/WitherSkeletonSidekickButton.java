package xyz.ufactions.sidekick.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.libs.C;

public class WitherSkeletonSidekickButton extends OrderingButton<SidekickModule> {

	public WitherSkeletonSidekickButton(SidekickModule plugin) {
		super(plugin, Material.MONSTER_EGG, 0, C.cGreen + "Wither Skeleton");
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.getEchoPetAPI().givePet(player, PetType.SKELETON, true);
		Plugin.getEchoPetAPI().addData(Plugin.getEchoPetAPI().getPet(player), PetData.WITHER);
	}
}