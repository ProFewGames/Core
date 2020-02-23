package xyz.ufactions.npc;

import java.util.*;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.UtilLoc;
import xyz.ufactions.libs.UtilMath;
import xyz.ufactions.libs.UtilPlayer;

public class NPCPlayer extends NPC implements Listener {

    private Module module;

    private EntityPlayer npc;
    private String name;

    private static List<NPCPlayer> npcs = new ArrayList<>();

    public NPCPlayer(Module module, String name) {
        this.name = name;
        this.module = module;
    }

    @Override
    public boolean spawn(Location at) {
        if (isSpawned())
            return false;
        String[] prop;
        prop = SkinCaching.instance.textures(name);
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile GP = new GameProfile(UUID.randomUUID(), name);
        GP.getProperties().put("textures", new Property("textures", prop[0], prop[1]));
        WorldServer ws = ((CraftWorld) at.getWorld()).getHandle();
        npc = new EntityPlayer(server, ws, GP, new PlayerInteractManager(ws));
        npc.spawnIn(ws);
        npc.setLocation(at.getX(), at.getY(), at.getZ(), at.getYaw(), at.getPitch());
        npc.getDataWatcher().watch(10, (byte) 0xFF);

        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player);
        }
        module.registerEvents(this);
        npcs.add(this);
        return true;
    }

    public void show(Player player) {
        if (player.getWorld().getName() != npc.getWorld().getWorld().getName()) return;
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc);
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(npc);
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(add);
        connection.sendPacket(spawn);
        Bukkit.getScheduler().scheduleSyncDelayedTask(module.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(remove);
                }
            }
        }, 5);
    }

    @Override
    public boolean despawn(boolean destroy) {
        if (!isSpawned())
            return false;
        HandlerList.unregisterAll(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        }
        npc = null;
        if (destroy) {
            name = null;
            module = null;
            npcs.remove(this);
        }
        return true;
    }

    @Override
    public boolean isSpawned() {
        if (npc == null)
            return false;
        if (!npc.isAlive())
            return false;
        return true;
    }

    @Override
    public Entity getEntity() {
        return CraftEntity.getEntity((CraftServer) Bukkit.getServer(), npc);
    }

    public static List<NPCPlayer> getNpcs() {
        return npcs;
    }

//    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Bukkit.broadcastMessage("A");
        double bestDist = 0;
        Player best = null;
        Bukkit.broadcastMessage(UtilLoc.encodeLoc(getEntity().getLocation()));
        for (Player cur : getEntity().getLocation().getWorld().getPlayers()) {
            Bukkit.broadcastMessage("Iterating");
            if (UtilPlayer.isSpectator(cur)) continue;
            if (cur.isDead()) continue;
            double dist = UtilMath.offset(cur.getLocation(), getEntity().getLocation());
            Bukkit.broadcastMessage("Dist : " + dist);
            if (dist < 10.0) {
                Bukkit.broadcastMessage("B");
                if (best == null || dist < bestDist) {
                    Bukkit.broadcastMessage("E");
                    best = cur;
                    bestDist = dist;
                }
            }
        }
        Bukkit.broadcastMessage("C");
        if (best != null) {
            Bukkit.broadcastMessage("D");
            npc.setLocation(npc.locX, npc.locY + 1, npc.locZ, npc.pitch, npc.yaw + 25);
            PacketPlayOutUpdateEntityNBT packet = new PacketPlayOutUpdateEntityNBT();
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(packet);
            }
        }
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