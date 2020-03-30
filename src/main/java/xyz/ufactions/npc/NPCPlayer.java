package xyz.ufactions.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import xyz.ufactions.api.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(npc);
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc);
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
}