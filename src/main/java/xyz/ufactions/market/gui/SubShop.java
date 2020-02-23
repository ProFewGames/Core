package xyz.ufactions.market.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.market.MarketModule;
import xyz.ufactions.market.config.ShopConfig;
import xyz.ufactions.shop.IButton;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.shop.Shop;

import java.util.ArrayList;
import java.util.List;

public class SubShop extends Shop {

    public SubShop(MarketModule module, String name, int identifier) {
        super(module.getPlugin(), new MainShop(module), name, ShopFiller.PANE);

        ShopConfig config = module.getShopConfig();
        List<IButton> list = new ArrayList<>();

        // Iterate through available shops
        System.out.println("SubShop Iterating 1");
        for(String shop : config.getFile().getConfigurationSection("shops").getKeys(false)) {
            System.out.println("SubShop A : " + shop);
            // Example : shops.blocks
            int id = config.getInt("shops." + shop + ".identifier");
            System.out.println("SubShop B : " + id);
            // Example : shops.blocks.identifier = 0
            if(id != identifier) continue;
            // Iterate through all items
            System.out.println("SubShop Iterating 2");
            for(String num : config.getFile().getConfigurationSection("shops." + shop + ".items").getKeys(false)) {
                System.out.println("SubShop C : " + num);
                String path = "shops." + shop + ".items." + num;
                System.out.println("SubShop D : " + num);
                // Example : shops.blocks.items.1
                Material material;
                try {
                    System.out.println("Parsing integer");
                    int x = Integer.parseInt(config.getString(path + ".item.material"));
                    System.out.println("INT" + x);
                    material = Material.getMaterial(x);
                } catch (Exception e) {
                    System.out.println("Parsing string");
                    material = Material.getMaterial(config.getString(path + ".item.material"));
                    System.out.println("Parsed");
                }
                System.out.println("SubShop E : " + material.toString());
                int data = config.getInt(path + ".item.data");
                System.out.println("SubShop F : " + data);
                double sell = config.getFile().getDouble(path + ".sell");
                System.out.println("SubShop G : " + sell);
                double buy = config.getFile().getDouble(path + ".buy");
                System.out.println("SubShop H : " + buy);
                Material finalMaterial = material;
                list.add(new OrderingButton<MarketModule>(module, finalMaterial, data, "&r" + material.name()) {
                    @Override
                    public void onClick(Player player, ClickType clickType) {
                        PurchaseGUI gui = new PurchaseGUI(module, finalMaterial, (byte) data, sell, buy);
                        gui.setReturnShop(SubShop.this);
                        gui.openInventory(player);
                    }
                });
            }
            break;
        }
        setButtons(list);
    }
}