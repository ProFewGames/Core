package xyz.ufactions.npc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class LookCloseAddon extends Addon {

	private int range = 5;

	public LookCloseAddon(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public void run() {
		for (NPC npc : npcs) {
			Player player = findNewTarget(npc);
			if (player != null) {
				faceLocation(npc.getEntity(), player.getLocation());
			}
		}
	}

	private Player findNewTarget(NPC npc) {
		final List<Entity> nearby = npc.getEntity().getNearbyEntities(this.range, this.range, this.range);
		final Location location = npc.getEntity().getLocation();
		Collections.sort(nearby, new Comparator<Entity>() {
			@Override
			public int compare(Entity o1, Entity o2) {
				if (location.getWorld() != o1.getLocation().getWorld()
						|| location.getWorld() != o2.getLocation().getWorld()) {
					return -1;
				}
				final double d1 = o1.getLocation().distanceSquared(location);
				final double d2 = o2.getLocation().distanceSquared(location);
				return Double.compare(d1, d2);
			}
		});
		for (final Entity entity : nearby) {
			if (entity.getType() == EntityType.PLAYER) {
				if (entity.getLocation().getWorld() != location.getWorld()) {
					continue;
				}
				return (Player) entity;
			}
		}
		return null;
	}

	public void faceEntity(final Entity entity, final Entity at) {
		if (entity.getWorld() != at.getWorld()) {
			return;
		}
		faceLocation(entity, at.getLocation());
	}

	private void faceLocation(final Entity entity, final Location to) {
		if (entity.getWorld() != to.getWorld()) {
			return;
		}
		final Location loc = entity.getLocation();
		final double xDiff = to.getX() - loc.getX();
		final double yDiff = to.getY() - loc.getY();
		final double zDiff = to.getZ() - loc.getZ();
		final double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		final double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);
		double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
		final double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0;
		if (zDiff < 0.0) {
			yaw += Math.abs(180.0 - yaw) * 2.0;
		}
		look(entity, (float) yaw - 90.0f, (float) pitch);
	}

	private void look(final Entity entity, float yaw, final float pitch) {
		final net.minecraft.server.v1_8_R3.Entity handle = ((CraftEntity) entity).getHandle();
		if (handle == null) {
			return;
		}
		yaw = clampYaw(yaw);
		setHeadYaw(handle, handle.yaw = yaw);
		handle.pitch = pitch;
	}

	private float clampYaw(float yaw) {
		while (yaw < -180.0f) {
			yaw += 360.0f;
		}
		while (yaw >= 180.0f) {
			yaw -= 360.0f;
		}
		return yaw;
	}

	private void setHeadYaw(final net.minecraft.server.v1_8_R3.Entity entity, float yaw) {
		final EntityLiving handle = (EntityLiving) entity;
		yaw = clampYaw(yaw);
		handle.aK = yaw;
		if (!(handle instanceof EntityHuman)) {
			handle.aI = yaw;
		}
		handle.aL = yaw;
	}
}