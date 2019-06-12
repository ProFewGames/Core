package xyz.ufactions.scoreboard;

import org.bukkit.entity.Player;

public interface IScoreboard {

	public void update(Player player, ScoreboardData data);
}