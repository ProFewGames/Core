package xyz.ufactions.permissions.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ufactions.permissions.data.PermissionUser;

public class PlayerPermissionsLoginEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;
	private PermissionUser user;

	public PlayerPermissionsLoginEvent(UUID uuid, PermissionUser user) {
		this.uuid = uuid;
		this.user = user;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public PermissionUser getUser() {
		return user;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}