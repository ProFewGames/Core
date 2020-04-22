package xyz.ufactions.chat;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.ufactions.libs.UtilTime;

public class BossBar {

    private Player player;
    private String message;
    private EntityWither wither;

    private long initializeTime;
    private long timeout;

    public BossBar(Player player, long timeout, String message) {
        this.player = player;
        this.message = message;
        this.timeout = timeout;
        this.initializeTime = System.currentTimeMillis();
    }

    public void update() {
        Vector d = player.getLocation().getDirection();
        Location location = player.getLocation().add(d.multiply(20));
        removeWither();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        wither = new EntityWither(world);
        wither.setLocation(location.getX(), player.getLocation().getY(), location.getZ(), location.getPitch(), location.getYaw());
        wither.setCustomNameVisible(true);
        wither.setCustomName(message);
        wither.setInvisible(true);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removeWither() {
        if (wither != null) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean elapsed() {
        return UtilTime.elapsed(initializeTime, timeout);
    }
}