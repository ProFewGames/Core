package xyz.ufactions.crates.managers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xyz.ufactions.crates.CrateHook;
import xyz.ufactions.crates.CratesModule;
import xyz.ufactions.crates.objects.Crate;
import xyz.ufactions.crates.objects.CrateType;
import xyz.ufactions.crates.objects.Prize;
import xyz.ufactions.crates.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.ufactions.libs.ItemBuilder;

public class CrateSpinner {

	private Crate crate;
	private Player player;
	private int runnable;
	private int glassStage;
	private List<Prize> prizes;
	private Random random;
	private int spinTime;

	public CrateSpinner(Crate crate, Player player) {
		this.crate = crate;
		this.player = player;
		this.random = new Random();
		this.glassStage = 0;
		this.prizes = new ArrayList<>();
		this.spinTime = crate.getSpinTime();
		randomizePrizes();
	}

	private void end() {
		SoundManager.getInstance().playSound(player, crate.getCloseSound());
		Prize prize = prizes.get(4);
		for (String command : prize.getCommands()) {
			boolean hooked = false;
			;
			for (CrateHook hook : CratesModule.getHooks()) {
				for (String placeholder : hook.getPlaceholders()) {
					if (command.toLowerCase().startsWith(placeholder.toLowerCase())) {
						String[] args = new String[0];
						if (command.contains(" ")) {
							args = command.substring(command.indexOf(32) + 1).split(" ");
						}
						hook.execute(placeholder, args, player);
						hooked = true;
						break;
					}
				}
			}
			if (hooked)
				continue;
			if (command.startsWith("[message]")) {
				player.sendMessage(ChatUtil.cc(command.replace("[message] ", "").replace("%player%", player.getName())
						.replace("%prize%", prize.getDisplayName())));
			} else if (command.startsWith("[broadcast]")) {
				Bukkit.broadcastMessage(ChatUtil.cc(command.replace("[broadcast] ", "")
						.replace("%player%", player.getName()).replace("%prize%", prize.getDisplayName())));
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						command.replace("%player%", player.getName()).replace("%prize%", prize.getDisplayName()));
			}
		}
		String broadcastMessage = crate.getBroadcastMessage();
		if (!broadcastMessage.equals("")) {
			broadcastMessage = broadcastMessage.replace("%prize%", prize.getDisplayName());
			broadcastMessage = broadcastMessage.replace("%prize_raw%", ChatColor.stripColor(prize.getDisplayName()));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastMessage));
		}
		String playerMessage = crate.getPlayerMessage();
		if (!playerMessage.equals("")) {
			playerMessage = playerMessage.replace("%prize%", prize.getDisplayName());
			playerMessage = playerMessage.replace("%prize_raw%", ChatColor.stripColor(prize.getDisplayName()));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', playerMessage));
		}
		Bukkit.getServer().getScheduler().cancelTask(runnable);
	}

	private void prepareInventory(Inventory inv) {
		if (crate.getCrateType() == CrateType.CSGO) {
			for (int i = 0; i < inv.getSize(); i++) {
				if (i == 4 || i == 22) {
					inv.setItem(i, new ItemBuilder(Material.REDSTONE_TORCH_ON).name(" ").build());
					continue;
				}
				if (i == 9) {
					ItemStack item = inv.getItem(i);
					if (item == null) {
						inv.setItem(i, prizes.get(0).getItem());
					} else {
						prizes.remove(0);
						prizes.add(randomPrize());
						inv.setItem(i, prizes.get(0).getItem());
					}
					continue;
				}
				if (i == 10) {
					inv.setItem(i, prizes.get(1).getItem());
					continue;
				}
				if (i == 11) {
					inv.setItem(i, prizes.get(2).getItem());
					continue;
				}
				if (i == 12) {
					inv.setItem(i, prizes.get(3).getItem());
					continue;
				}
				if (i == 13) {
					inv.setItem(i, prizes.get(4).getItem());
					continue;
				}
				if (i == 14) {
					inv.setItem(i, prizes.get(5).getItem());
					continue;
				}
				if (i == 15) {
					inv.setItem(i, prizes.get(6).getItem());
					continue;
				}
				if (i == 16) {
					inv.setItem(i, prizes.get(7).getItem());
					continue;
				}
				if (i == 17) {
					inv.setItem(i, prizes.get(8).getItem());
					continue;
				}
				inv.setItem(i, ItemBuilder.constructPanel(true));
			}
		} else {
			randomizePrizes();
			for (int i = 0; i < inv.getSize(); i++) {
				if (i == 13) {
					inv.setItem(i, prizes.get(4).getItem());
					continue;
				}
				inv.setItem(i, ItemBuilder.constructPanel(true));
			}
		}
	}

	private void randomizePrizes() {
		if (!prizes.isEmpty()) {
			prizes.clear();
		}
		for (int i = 0; i < 45; i++) {
			prizes.add(randomPrize());
		}
	}

	private Prize randomPrize() {
		double max = 0;
		List<Prize> prizes = crate.getPrizes();
		for (Prize prize : prizes)
			if (prize.getChance() > max)
				max = prize.getChance();
		DecimalFormat format = new DecimalFormat("####.##");
		String chanceNumberString = format.format(0 + max * random.nextDouble());
		double chanceNumber = Double.parseDouble(chanceNumberString);
		List<Prize> winnablePrizes = new ArrayList<>();
		for (Prize prize : prizes) {
			double chance = prize.getChance();
			if (chanceNumber <= chance)
				winnablePrizes.add(prize);
		}
		Prize wonPrize = null;
		while (wonPrize == null) {
			if (winnablePrizes.size() > 1) {
				int prizeToPick = random.nextInt(winnablePrizes.size());
				wonPrize = winnablePrizes.get(prizeToPick);
			} else {
				wonPrize = winnablePrizes.get(0);
			}
		}
		return wonPrize;
	}

	private void prepareBetaInv(Inventory inv) {
		if (glassStage == 1) {
			inv.setItem(0, ItemBuilder.constructPanel(7));
			inv.setItem(1, ItemBuilder.constructPanel(7));
			inv.setItem(2, ItemBuilder.constructPanel(7));
			inv.setItem(3, ItemBuilder.constructPanel(7));
			inv.setItem(4, ItemBuilder.constructPanel(3));
			inv.setItem(5, ItemBuilder.constructPanel(7));
			inv.setItem(6, ItemBuilder.constructPanel(7));
			inv.setItem(7, ItemBuilder.constructPanel(7));
			inv.setItem(8, ItemBuilder.constructPanel(7));
			inv.setItem(9, ItemBuilder.constructPanel(7));
			inv.setItem(17, ItemBuilder.constructPanel(7));
			inv.setItem(18, ItemBuilder.constructPanel(3));
			inv.setItem(26, ItemBuilder.constructPanel(3));
			inv.setItem(27, ItemBuilder.constructPanel(7));
			inv.setItem(35, ItemBuilder.constructPanel(7));
			inv.setItem(36, ItemBuilder.constructPanel(7));
			inv.setItem(37, ItemBuilder.constructPanel(7));
			inv.setItem(38, ItemBuilder.constructPanel(7));
			inv.setItem(39, ItemBuilder.constructPanel(7));
			inv.setItem(40, ItemBuilder.constructPanel(3));
			inv.setItem(41, ItemBuilder.constructPanel(7));
			inv.setItem(42, ItemBuilder.constructPanel(7));
			inv.setItem(43, ItemBuilder.constructPanel(7));
			inv.setItem(44, ItemBuilder.constructPanel(7));
		} else if (glassStage == 2) {
			inv.setItem(10, ItemBuilder.constructPanel(7));
			inv.setItem(11, ItemBuilder.constructPanel(7));
			inv.setItem(12, ItemBuilder.constructPanel(7));
			inv.setItem(13, ItemBuilder.constructPanel(3));
			inv.setItem(14, ItemBuilder.constructPanel(7));
			inv.setItem(15, ItemBuilder.constructPanel(7));
			inv.setItem(16, ItemBuilder.constructPanel(7));
			inv.setItem(19, ItemBuilder.constructPanel(3));
			inv.setItem(25, ItemBuilder.constructPanel(3));
			inv.setItem(28, ItemBuilder.constructPanel(7));
			inv.setItem(29, ItemBuilder.constructPanel(7));
			inv.setItem(30, ItemBuilder.constructPanel(7));
			inv.setItem(31, ItemBuilder.constructPanel(3));
			inv.setItem(32, ItemBuilder.constructPanel(7));
			inv.setItem(33, ItemBuilder.constructPanel(7));
			inv.setItem(34, ItemBuilder.constructPanel(7));
		} else if (glassStage == 3) {
			inv.setItem(20, ItemBuilder.constructPanel(3));
			inv.setItem(21, ItemBuilder.constructPanel(3));
			inv.setItem(23, ItemBuilder.constructPanel(3));
			inv.setItem(24, ItemBuilder.constructPanel(3));
		} else {
			if (glassStage != 0) {
				end();
			}
		}
		glassStage++;
		for (int i = 0; i < inv.getSize(); i++) {
			if (i == 22) {
				inv.setItem(i, prizes.get(random.nextInt(i)).getItem());
				continue;
			}
			if (inv.getItem(i) != null && inv.getItem(i).getType() == Material.STAINED_GLASS_PANE) {
				continue;
			}
			inv.setItem(i, prizes.get(i).getItem());
		}
	}

	public void spin() {
		if (crate.getCrateType() == CrateType.BETA) {
			final Inventory inv = Bukkit.createInventory(null, 45, crate.getDisplayName());
			runnable = Bukkit.getServer().getScheduler()
					.scheduleSyncRepeatingTask(CratesModule.getInstance().getPlugin(), new Runnable() {

						public void run() {
							if (!player.getOpenInventory().getTitle().equals(crate.getDisplayName())) {
								randomizePrizes();
								end();
								return;
							}
							prepareBetaInv(inv);
						}
					}, 0, 20);
			SoundManager.getInstance().playSound(player, crate.getOpenSound());
			player.openInventory(inv);
		}
		if (crate.getCrateType() == CrateType.CSGO) {
			spinTime = spinTime * 5;
			final Inventory inv = Bukkit.createInventory(null, 27, crate.getDisplayName());
			runnable = Bukkit.getServer().getScheduler()
					.scheduleSyncRepeatingTask(CratesModule.getInstance().getPlugin(), new Runnable() {

						public void run() {
							if (spinTime <= 21 && spinTime > 0) {
								if (spinTime == 18 || spinTime == 15 || spinTime == 12 || spinTime == 9 || spinTime == 6
										|| spinTime == 3) {
									prepareInventory(inv);
								}
								spinTime--;
								return;
							}
							if (spinTime <= 0) {
								end();
								return;
							}
							if (!player.getOpenInventory().getTitle().equals(crate.getDisplayName())) {
								randomizePrizes();
								end();
								return;
							}
							prepareInventory(inv);
							SoundManager.getInstance().playSound(player, crate.getSpinSound());
							spinTime--;
						}
					}, 0, 1L);
			SoundManager.getInstance().playSound(player, crate.getOpenSound());
			player.openInventory(inv);
		}
		if (crate.getCrateType() == CrateType.ROULETTE) {
			final Inventory inv = Bukkit.createInventory(null, 27, crate.getDisplayName());
			runnable = Bukkit.getServer().getScheduler()
					.scheduleSyncRepeatingTask(CratesModule.getInstance().getPlugin(), new Runnable() {

						public void run() {
							if (spinTime <= 0) {
								end();
								return;
							}
							if (!player.getOpenInventory().getTitle().equals(crate.getDisplayName())) {
								randomizePrizes();
								end();
								return;
							}
							prepareInventory(inv);
							SoundManager.getInstance().playSound(player, crate.getSpinSound());
							spinTime--;
						}
					}, 0, 8L);
			SoundManager.getInstance().playSound(player, crate.getOpenSound());
			player.openInventory(inv);
		}
	}
}
