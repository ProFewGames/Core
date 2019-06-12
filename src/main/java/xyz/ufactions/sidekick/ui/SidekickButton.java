package xyz.ufactions.sidekick.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.dsh105.echopet.compat.api.entity.PetType;

import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;

public abstract class SidekickButton extends OrderingButton<SidekickModule> {

	private PetType pet;

	public SidekickButton(SidekickModule plugin, Material material, PetType petType) {
		super(plugin, material, C.cGreen + F.capitalizeFirstLetter(petType.name()), C.cGold + "*Click to use*",
				C.cGray + C.Italics + "/sidekick " + petType.name().toLowerCase().replace("_", ""));

		this.pet = petType;
	}

	public SidekickButton(SidekickModule plugin, int data, PetType petType) {
		super(plugin, Material.MONSTER_EGG, data, C.cGreen + F.capitalizeFirstLetter(petType.name()),
				C.cGold + "*Click to use*",
				C.cGray + C.Italics + "/sidekick " + petType.name().toLowerCase().replace("_", ""));

		this.pet = petType;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.setSidekick(player, pet);
	}
}