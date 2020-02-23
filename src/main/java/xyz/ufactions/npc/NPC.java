package xyz.ufactions.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class NPC {

	public abstract boolean spawn(Location paramLocation);

	public abstract boolean despawn(boolean destroy);

	public abstract boolean isSpawned();

	public abstract Entity getEntity();

}