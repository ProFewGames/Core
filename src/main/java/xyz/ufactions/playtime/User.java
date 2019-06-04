package xyz.ufactions.playtime;

import java.util.UUID;

public class User implements Comparable<User> {

	private UUID uuid;
	private long playTime;

	public User(UUID uuid, long playTime) {
		this.uuid = uuid;
		this.playTime = playTime;
	}

	public long getPlayTime() {
		return playTime;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public int compareTo(User user) {
		return Long.valueOf(this.playTime).compareTo(user.playTime);
	}

	@Override
	public String toString() {
		return "User{uuid=" + uuid + ",playTime=" + playTime + "}";
	}
}