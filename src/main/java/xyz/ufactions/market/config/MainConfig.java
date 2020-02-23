package xyz.ufactions.market.config;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.FileManager;

public class MainConfig extends FileManager {

    public MainConfig(Module module) {
        super(module, "config.yml");
    }

    @Override
    public void create() {
        set("main shop.name", "&c&lMain Shop");
        set("shops.blocks.identifier", 0);
        set("shops.blocks.name", "&b&lBlocks");
        set("shops.blocks.item", "GRASS");
        set("shops.pvp.identifier", 1);
        set("shops.pvp.name", "&b&lPvP");
        set("shops.pvp.item", "DIAMOND_SWORD");
        super.create();
    }
}