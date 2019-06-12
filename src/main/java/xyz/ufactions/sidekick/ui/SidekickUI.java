package xyz.ufactions.sidekick.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import xyz.ufactions.shop.IButton;
import xyz.ufactions.shop.Shop;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.sidekick.ui.buttons.BlazeSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.CaveSpiderSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.CreeperSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.EnderdragonSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.EndermanSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.GhastSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.GiantSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.IronGolemSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.MagmaCubeSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.NoSidekicksButton;
import xyz.ufactions.sidekick.ui.buttons.RemoveSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.SilverfishSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.SkeletonSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.SnowGolemSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.SpiderSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.WitchSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.WitherSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.WitherSkeletonSidekickButton;
import xyz.ufactions.sidekick.ui.buttons.ZombiePigmanSidekickButton;
import xyz.ufactions.libs.UtilMath;

public class SidekickUI {

	private Shop shop;

	public SidekickUI(Player player, SidekickModule plugin) {
		List<IButton> buttons = new ArrayList<>();
		if (player.hasPermission("echopet.pet.type.enderdragon")) {
			buttons.add(new EnderdragonSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.wither")) {
			buttons.add(new WitherSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.giant")) {
			buttons.add(new GiantSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.irongolem")) {
			buttons.add(new IronGolemSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.snowman")) {
			buttons.add(new SnowGolemSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.magmacube")) {
			buttons.add(new MagmaCubeSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.ghast")) {
			buttons.add(new GhastSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.blaze")) {
			buttons.add(new BlazeSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.witherskeleton")) {
			buttons.add(new WitherSkeletonSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.pigzombie")) {
			buttons.add(new ZombiePigmanSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.witch")) {
			buttons.add(new WitchSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.enderman")) {
			buttons.add(new EndermanSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.skeleton")) {
			buttons.add(new SkeletonSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.creeper")) {
			buttons.add(new CreeperSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.spider")) {
			buttons.add(new SpiderSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.cavespider")) {
			buttons.add(new CaveSpiderSidekickButton(plugin));
		}
		if (player.hasPermission("echopet.pet.type.silverfish")) {
			buttons.add(new SilverfishSidekickButton(plugin));
		}

		if (buttons.isEmpty()) {
			for (int i = 0; i < 9; i++) {
				buttons.add(new NoSidekicksButton(plugin));
			}
		} else {
			buttons.add(new RemoveSidekickButton(plugin));
		}
		this.shop = new Shop(plugin.getPlugin(), "Sidekicks", UtilMath.round(buttons.size()), Shop.ShopFiller.NONE, buttons);
		shop.openInventory(player);
	}
}