package xyz.ufactions.market.config;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.FileManager;

public class ShopConfig extends FileManager {

    public ShopConfig(Module module) {
        super(module, "shops.yml");
    }

    @Override
    public void create() {
        set("shops.blocks.identifier", 0);
        set("shops.blocks.items.1.item.material", "GRASS");
        set("shops.blocks.items.1.item.data", 0);
        set("shops.blocks.items.1.buy", 100);
        set("shops.blocks.items.1.sell", 50);
        set("shops.blocks.items.2.item.material", "COBBLESTONE");
        set("shops.blocks.items.2.item.data", 0);
        set("shops.blocks.items.2.buy", 50);
        set("shops.blocks.items.2.sell", 25);
        set("shops.pvp.identifier", 1);
        set("shops.pvp.items.1.item.material", "DIAMOND_SWORD");
        set("shops.pvp.items.1.item.data", 0);
        set("shops.pvp.items.1.buy", 200);
        set("shops.pvp.items.1.sell", 100);
        set("shops.pvp.items.2.item.material", "BOW");
        set("shops.pvp.items.2.item.data", 0);
        set("shops.pvp.items.2.buy", 300);
        set("shops.pvp.items.2.sell", 150);
        super.create();
    }
        /*
        shops:
          blocks:
            identifier: 0
              items:
                1:
                  item:
                    material: GRASS
                    data: 0
                  buy: 100
                  sell: 50
                2:
                  item:
                    material: COBBLESTONE
                    data: 0
                  buy: 50
                  sell: 25
          pvp:
            identifier: 1
              items:
                1:
                  item:
                    material: DIAMOND_SWORD
                    data: 0
                  buy: 200
                  sell: 100
                2:
                  item:
                    material: BOW
                    data: 0
                  buy: 300
                  sell: 150
         */
}