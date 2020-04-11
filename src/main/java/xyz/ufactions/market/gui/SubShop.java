package xyz.ufactions.market.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.libs.F;
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
        for (String shop : config.getFile().getConfigurationSection("shops").getKeys(false)) {
            // Example : shops.blocks
            int id = config.getInt("shops." + shop + ".identifier");
            // Example : shops.blocks.identifier = 0
            if (id != identifier) continue;
            // Iterate through all items
            for (String num : config.getFile().getConfigurationSection("shops." + shop + ".items").getKeys(false)) {
                String path = "shops." + shop + ".items." + num;
                // Example : shops.blocks.items.1
                Material material;
                try {
                    int x = Integer.parseInt(config.getString(path + ".item.material"));
                    material = Material.getMaterial(x);
                } catch (Exception e) {
                    material = Material.getMaterial(config.getString(path + ".item.material"));
                }
                int data = config.getInt(path + ".item.data");
                double sell = config.getFile().getDouble(path + ".sell");
                double buy = config.getFile().getDouble(path + ".buy");
                Material finalMaterial = material;

                String itemName = material.name().replaceAll("_", " ");
                if (itemName.contains(" ")) {
                    String[] array = itemName.split(" ");
                    itemName = "";
                    for (int i = 0; i < array.length; i++) {
                        if (itemName.isEmpty()) {
                            itemName = F.capitalizeFirstLetter(array[i]);
                        } else {
                            itemName += " " + F.capitalizeFirstLetter(array[i]);
                        }
                    }
                }

                list.add(new OrderingButton<MarketModule>(module, finalMaterial, data, "&r" + itemName) {
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