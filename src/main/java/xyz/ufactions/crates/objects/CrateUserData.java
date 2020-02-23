package xyz.ufactions.crates.objects;

import org.bukkit.entity.Player;

public class CrateUserData {

	private Player player;
	private int runnable;
	private int spinTime;
	private Prize prize;

	public CrateUserData(Player player, int runnable, Prize prize, int spinTime) {
		this.player = player;
		this.runnable = runnable;
		this.spinTime = spinTime;
		this.prize = prize;
	}

	public void setSpinTime(int spinTime) {
		this.spinTime = spinTime;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setPrize(Prize prize) {
		this.prize = prize;
	}

	public void setRunnable(int runnable) {
		this.runnable = runnable;
	}

	public int getSpinTime() {
		return spinTime;
	}

	public Player getPlayer() {
		return player;
	}

	public Prize getPrize() {
		return prize;
	}

	public int getRunnable() {
		return runnable;
	}
}
