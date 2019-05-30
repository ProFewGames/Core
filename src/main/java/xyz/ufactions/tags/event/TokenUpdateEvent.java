package xyz.ufactions.tags.event;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TokenUpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;
	private int amount;
	
	public TokenUpdateEvent(UUID uuid, int amount) {
		this.uuid = uuid;
		this.amount = amount;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public int getAmount() {
		return amount;
	}
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
