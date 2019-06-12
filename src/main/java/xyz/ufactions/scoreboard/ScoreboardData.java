
package xyz.ufactions.scoreboard;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import xyz.ufactions.libs.C;

public class ScoreboardData {

	private String title = "UPrison";
	private Objective objective;
	private int shineIndex;
	private Scoreboard scoreboard;
	private boolean shineDirection = true;

	private HashMap<String, String> data = new HashMap<>();

	public ScoreboardData(Player player) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		objective = scoreboard.registerNewObjective("stats", "dummy");
		objective.setDisplayName("Loading...");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
	}

	public String get(String key) {
		if (!data.containsKey(key))
			data.put(key, "");
		return data.get(key);
	}

	public void set(String key, String value) {
		data.put(key, value);
	}

	public Objective getObjective() {
		return objective;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void updateTitle() {
		String out;

		if (shineDirection) {
			out = C.cDAqua + C.Bold;
		} else {
			out = C.cWhite + C.Bold;
		}

		for (int i = 0; i < title.length(); i++) {
			char c = title.charAt(i);

			if (shineDirection) {
				if (i == shineIndex)
					out += C.cAqua + C.Bold;

				if (i == shineIndex + 1)
					out += C.cWhite + C.Bold;
			} else {
				if (i == shineIndex)
					out += C.cAqua + C.Bold;

				if (i == shineIndex + 1)
					out += C.cDAqua + C.Bold;
			}

			out += c;
		}

		objective.setDisplayName(out);

		shineIndex++;

		if (shineIndex == title.length() * 2) {
			shineIndex = 0;
			shineDirection = !shineDirection;
		}
	}
}