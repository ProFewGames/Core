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
        System.out.println("Fetching sub shops");
        for (String shop : config.getFile().getConfigurationSection("shops").getKeys(false)) {
            System.out.println("Sub shop fetched : " + shop);
            Material material;
            try {
                System.out.println("Parsing integer");
                int x = Integer.parseInt(config.getString("shops." + shop + ".item"));
                System.out.println("INT" + x);
                material = Material.getMaterial(x);
            } catch (Exception e) {
                System.out.println("Parsing string");
                material = Material.getMaterial(config.getString("shops." + shop + ".item"));
                System.out.println("Parsed");
            }
            System.out.println("Material " + material.name());
            String name = config.getString("shops." + shop + ".name");
            System.out.println("Name : " + name);
            int identifier = config.getInt("shops." + shop + ".identifier");
            System.out.println("Identifier : " + identifier);
            list.add(new OrderingButton<MarketModule>(module, material, name) {

                @Override
                public void onClick(Player player, ClickType clickType) {
                    System.out.println("Click of : " + getItem().getItemMeta().getDisplayName());
                    new SubShop(module, name, identifier).openInventory(player);
                }
            });
        }
        setButtons(list);
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(getButtons().size());
    }

    @Override
    public void onInventoryOpen(Player player) {
        System.out.println(" ");
        System.out.println(getInventory().getSize());
        System.out.println(" ");
    }
}