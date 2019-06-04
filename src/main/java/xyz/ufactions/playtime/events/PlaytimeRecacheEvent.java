package xyz.ufactions.playtime.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ufactions.playtime.PlaytimeModule;
import xyz.ufactions.playtime.User;

import java.util.List;

public class PlaytimeRecacheEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final List<User> cache;

	private PlaytimeModule module;

	public PlaytimeRecacheEvent(PlaytimeModule module, List<User> cache) {
		this.cache = cache;
		this.module = module;
	}

	public final List<User> getNewCache() {
		return cache;
	}

	public PlaytimeModule getModule() {
		return module;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}