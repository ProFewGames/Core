package xyz.ufactions.coins;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.coins.commands.CoinCommand;
import xyz.ufactions.coins.repository.CoinRepository;
import xyz.ufactions.libs.Callback;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class CoinModule extends Module {

	private CoinRepository repository;

	private HashMap<Player, Integer> coins = new HashMap<>();

	public CoinModule(JavaPlugin plugin) {
		super("Coins", plugin);

		repository = new CoinRepository(plugin);
	}

	@Override
	public void addCommands() {
		addCommand(new CoinCommand(this));
	}

	public int getCoins(Player player) {
		if (!coins.containsKey(player)) {
			login(player);
		}
		return coins.get(player);
	}

	public void canRemoveCoins(final Callback<Boolean> callback, final Player player, final int cost) {
		runAsync(new Runnable() {

			@Override
			public void run() {
				final boolean canRemove = repository.getCoins(player.getUniqueId()) - cost < 0;
				runSync(new Runnable() {

					@Override
					public void run() {
						callback.run(canRemove);
					}
				});
			}
		});
	}

	public void addCoins(final Player caller, final Player target, final int coins) {
		runAsync(new Runnable() {

			@Override
			public void run() {
				repository.addCoins(target.getUniqueId(), coins);
				final int newCoins = repository.getCoins(target.getUniqueId());
				CoinModule.this.coins.put(target, newCoins);
				if (caller != null) {
					UtilPlayer.message(caller, F.main(getName(),
							F.elem(target.getName()) + "'s new coin balance: " + F.elem(newCoins + "") + "."));
					runSync(new Runnable() {

						@Override
						public void run() {
							UtilPlayer.message(caller,
									F.main(getName(),
											F.elem(Math.abs(coins) + "") + " coins "
													+ (coins < 0 ? "removed from" : "added to") + " "
													+ F.elem(target.getName())));
						}
					});
				}
			}
		});
	}

	private void login(final Player player) {
		coins.put(player, 0);
		runAsync(new Runnable() {

			@Override
			public void run() {
				coins.put(player, repository.getCoins(player.getUniqueId()));
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		login(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		coins.remove(e.getPlayer());
	}
}