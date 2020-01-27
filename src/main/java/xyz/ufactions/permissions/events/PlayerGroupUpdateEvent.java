package xyz.ufactions.permissions.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGroupUpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;
	private String group;
	private GroupUpdateType type;

	public PlayerGroupUpdateEvent(UUID uuid, String group, GroupUpdateType type) {
		this.uuid = uuid;
		this.group = group;
		this.type = type;
	}

	public GroupUpdateType getType() {
		return type;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getGroup() {
		return group;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static enum GroupUpdateType {
		REMOVE, ADD;
	}
}