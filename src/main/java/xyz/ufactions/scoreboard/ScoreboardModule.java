package xyz.ufactions.scoreboard;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.scoreboard.commands.ScoreboardCommand;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class ScoreboardModule extends Module {

	private int scoreboardTick = 0;
	private HashMap<Player, ScoreboardData> map = new HashMap<>();
	private IScoreboard iScoreboard;

	private HashSet<Player> toggledScoreboards = new HashSet<>();
	public boolean TogglableScoreboard = true;

	private String displayName;

	public ScoreboardModule(JavaPlugin plugin, String displayName) {
		super("Scoreboard Module", plugin);
		this.displayName = displayName;
		iScoreboard = (player, data) -> {
			Objective objective = data.getObjective();
			objective.getScore(" ").setScore(2);
			objective.getScore("Default Scoreboard").setScore(1);
			objective.getScore("  ").setScore(0);
		};
		for (Player player : UtilServer.getPlayers()) {
			map.put(player, new ScoreboardData(player, displayName));
		}
	}

	public String getBlankLine(int lines) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < lines; i++) {
			builder.append(" ");
		}
		return builder.toString();
	}

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        for(Player player : Bukkit.getOnlinePlayers()) {
        	map.get(player).setDisplayName(displayName);
		}
    }

    @Override
	public void addCommands() {
		addCommand(new ScoreboardCommand(this));
	}

	public boolean toggleScoreboard(Player player) {
		if (!TogglableScoreboard)
			return false;
		if (toggledScoreboards.contains(player)) {
			toggledScoreboards.remove(player);
			return false;
		} else {
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			toggledScoreboards.add(player);
			return true;
		}
	}

	public void setScoreboardInterface(IScoreboard iScoreboard) {
		this.iScoreboard = iScoreboard;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		map.put(player, new ScoreboardData(player, displayName));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		map.remove(player);
		toggledScoreboards.remove(player);
	}

	private int sec = 0;

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getType() != UpdateType.TICK)
			return;

		scoreboardTick = (scoreboardTick + 1) % 3;

		if (scoreboardTick != 0)
			return;

		if(sec < 20) {
			sec++;
		}

		for (Player player : UtilServer.getPlayers()) {
			if (toggledScoreboards.contains(player))
				return;
			ScoreboardData data = map.get(player);
			if (!player.getScoreboard().equals(data.getScoreboard()))
				player.setScoreboard(data.getScoreboard());

			data.updateTitle();

			if(sec >= 20) {
				iScoreboard.update(player, data);
			}
		}
		if(sec >= 20) {
			sec = 0;
		}
	}
}