package xyz.ufactions.game;

import org.bukkit.Material;

/**
 * This class is reserved in the core for when we're pinging the arcade server
 * and it pings back a display with which game is currently on
 * we can easily fetch it from within the core without having to do some witchcraft
 * to obtain the display
 */
public enum GameDisplay {

    Slimewars("Slimewars", Material.SLIME_BLOCK, (byte) 0, 1),
    Spleef("Spleef", Material.DIAMOND_SPADE, (byte) 0, 2),
    Testing("Testing", Material.BEDROCK, (byte) 0, 99);

    String name;
    Material mat;
    byte data;

    private int gameId; // Unique identifying id for this gamemode (used for statistics)

    public int getGameId() {
        return gameId;
    }

    GameDisplay(String name, Material mat, byte data, int gameId) {
        this.name = name;
        this.mat = mat;
        this.data = data;
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return mat;
    }

    public byte getMaterialData() {
        return data;
    }

    public static GameDisplay matchName(String name) {
        for (GameDisplay display : values()) {
            if (display.getName().equalsIgnoreCase(name)) return display;
        }
        return null;
    }
}