package xyz.ufactions.market.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.market.MarketModule;
import xyz.ufactions.market.config.MainConfig;
import xyz.ufactions.shop.IButton;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.shop.Shop;

import java.util.ArrayList;
import java.util.List;

public class MainShop extends Shop {

    public MainShop(MarketModule module) {
        super(module.getPlugin(), module.getConfig().getString("main shop.name"), ShopFiller.NONE);

        MainConfig config = module.getConfig();

        List<IButton> list = new ArrayList<>();
        for (String shop : config.getFile().getConfigurationSection("shops").getKeys(false)) {
            Material material;
            try {
                int x = Integer.parseInt(config.getString("shops." + shop + ".item"));
                material = Material.getMaterial(x);
            } catch (Exception e) {
                material = Material.getMaterial(config.getString("shops." + shop + ".item"));
            }
            String name = config.getString("shops." + shop + ".name");
            int identifier = config.getInt("shops." + shop + ".identifier");
            list.add(new OrderingButton<MarketModule>(module, material, name) {

                @Override
                public void onClick(Player player, ClickType clickType) {
                    new SubShop(module, name, identifier).openInventory(player);
                }
            });
        }
        setButtons(list);
    }
}