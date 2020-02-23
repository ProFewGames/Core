package xyz.ufactions.market.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.ufactions.market.MarketModule;
import xyz.ufactions.shop.CurrencyType;
import xyz.ufactions.shop.PurchasableItem;
import xyz.ufactions.shop.Shop;

public class PurchaseGUI extends Shop {

    public PurchaseGUI(MarketModule module, Material material, byte data, double sell, double buy) {
        super(module.getPlugin(), "Transaction", 27, ShopFiller.PANE);

        // 1x
        addButton(new PurchasableItem<MarketModule>(module, CurrencyType.MONEY, new PurchasableItem.PurchasableData(1, 1, buy, sell, material, data), Material.PAPER, "&b[One]", 11) {

            @Override
            public void purchaseSuccessful(Player player, PurchasableData data) {
            }
        });

        // 32x
        addButton(new PurchasableItem<MarketModule>(module, CurrencyType.MONEY, new PurchasableItem.PurchasableData(32, 32, buy, sell, material, data), Material.PAPER, "&b[Half a stack]", 13) {

            @Override
            public void purchaseSuccessful(Player player, PurchasableData data) {
            }
        });

        //64x
        addButton(new PurchasableItem<MarketModule>(module, CurrencyType.MONEY, new PurchasableItem.PurchasableData(64, 64, buy, sell, material, data), Material.PAPER, "&b[Stack]", 15) {

            @Override
            public void purchaseSuccessful(Player player, PurchasableData data) {
            }
        });
    }
}