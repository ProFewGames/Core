package xyz.ufactions.weather;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WeatherOptions {

	private final World world;
	private boolean disableRain = false;
	private boolean timeLock = false;
	private long lockedTime = 0L;

	public WeatherOptions(World world, boolean disableRain, boolean timeLock, long lockedTime) {
		Validate.notNull(world);

		this.world = world;
		this.disableRain = disableRain;
		this.timeLock = timeLock;
		this.lockedTime = lockedTime;
	}

	public WeatherOptions(World world) {
		Validate.notNull(world);

		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public void setLockedTime(long lockedTime) {
		this.lockedTime = lockedTime;
	}

	public long getLockedTime() {
		return lockedTime;
	}

	public void setDisableRain(boolean disableRain) {
		this.disableRain = disableRain;
	}

	public boolean isDisableRain() {
		return disableRain;
	}

	public void setTimeLock(boolean timeLock) {
		this.timeLock = timeLock;
	}

	public boolean isTimeLock() {
		return timeLock;
	}

	public String encode() {
		return world.getName() + ";" + disableRain + ";" + timeLock + ";" + lockedTime;
	}

	public static WeatherOptions decode(String string) {
		String[] array = string.split(";");
		World world = Bukkit.getWorld(array[0]);
		boolean disableRain = Boolean.valueOf(array[1]);
		boolean timeLock = Boolean.valueOf(array[2]);
		long lockedTime = Long.valueOf(array[3]);
		return new WeatherOptions(world, disableRain, timeLock, lockedTime);
	}
}