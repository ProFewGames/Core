package xyz.ufactions.shop;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.*;

import java.util.ArrayList;
import java.util.List;

public abstract class PurchasableItem<PluginType extends Module> extends ShopItem<PluginType> {

    private CurrencyType type;

    private PurchasableData data;

    protected boolean closeInventory = false;

    private static Economy economy;

    public static void setEconomy(Economy economy) {
        PurchasableItem.economy = economy;
    }

    public PurchasableItem(PluginType plugin, CurrencyType type, PurchasableData data, Material material, String name, int position, String... additionalLore) {
        super(plugin, material, name, position, parseLore(type, data, additionalLore));

        this.type = type;
        this.data = data;
    }

    private static List<String> parseLore(CurrencyType type, PurchasableData data, String... extra) {
        List<String> lore = new ArrayList<>();
        lore.add(C.mBody + "Right click to sell " + F.elem(String.valueOf(data.getSellAmount())) + "x " + F.capitalizeFirstLetter(data.getMaterial().name().replaceAll("_", " ")) + " for " + type.getSymbol() + F.elem(UtilMath.fixMoney(data.getSellCost() * data.getBuyAmount())));
        lore.add(C.mBody + "Left click to buy " + F.elem(String.valueOf(data.getBuyAmount())) + "x " + F.capitalizeFirstLetter(data.getMaterial().name().replaceAll("_", " ")) + " for " + type.getSymbol() + F.elem(UtilMath.fixMoney(data.getBuyCost() * data.getBuyAmount())));
        return lore;
    }

    public boolean additionalPurchaseChecks(Player player, PurchaseType type) {
        return true;
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        System.out.println(" ");
        System.out.println("Click");
        if (type == CurrencyType.MONEY) {
            System.out.println("MONEY");
            if (economy == null) {
                System.out.println("Economy is not setup with the plugin.");
                UtilPlayer.message(player, F.main("Shop", C.cRed
                        + "Economy is not setup with the plugin. If you believe this is an error please contact a server administrator."));
                return;
            }
            System.out.println("CHECKING");
            if (clickType == ClickType.RIGHT) {
                System.out.println("RIGHT");
                EconomyResponse response = economy.withdrawPlayer(player, (data.getBuyCost() * data.getBuyAmount()));
                if (response.transactionSuccess()) {
                    if (additionalPurchaseChecks(player, PurchaseType.BUY)) {
                        player.getInventory().addItem(new ItemBuilder(data.getMaterial(), data.getData()).amount(data.getBuyAmount()).build());
                        UtilPlayer.message(player, F.main("Shop", "You've brought " + F.elem(String.valueOf(data.getBuyAmount())) + "x " + F.capitalizeFirstLetter(data.getMaterial().name().toLowerCase().replaceAll("_", " ")) + " for $" + F.elem(UtilMath.fixMoney(data.getBuyAmount() * data.getBuyCost()))));
                        purchaseSuccessful(player, data);
                        if (closeInventory)
                            player.closeInventory();
                    } else {
                        return;
                    }
                } else {
                    UtilPlayer.message(player, C.cRed + "You don't have enough money to purchase this item.");
                    purchaseDeclined(player, data);
                    return;
                }
            } else if (clickType == ClickType.LEFT) {
                System.out.println("LEFT");
                if (UtilInv.remove(player, data.getMaterial(), data.getData(), data.getSellAmount())) {
                    if (additionalPurchaseChecks(player, PurchaseType.SELL)) {
                        economy.depositPlayer(player, (data.getSellCost() * data.getSellAmount()));
                        UtilPlayer.message(player, F.main("Shop", "You sold " + F.elem(String.valueOf(data.getSellAmount())) + "x " + F.capitalizeFirstLetter(data.getMaterial().name().toLowerCase().replaceAll("_", " ")) + " for $" + F.elem(UtilMath.fixMoney(data.getSellAmount() * data.getSellCost()))));
                        purchaseSuccessful(player, data);
                        if (closeInventory) player.closeInventory();
                    }
                } else {
                    UtilPlayer.message(player, C.cRed + "You don't have enough of this item to sell.");
                    purchaseDeclined(player, data);
                }
                // TODO Purchase
            }
        }
        // TODO Add more cost types
    }

    //Player player, Material item, byte data, int toRemove

    public CurrencyType getType() {
        return type;
    }

    public void purchaseDeclined(Player player, PurchasableData data) {
    }

    public abstract void purchaseSuccessful(Player player, PurchasableData data);

    protected enum PurchaseType {
        SELL, BUY;
    }

    public static class PurchasableData {

        private int buyAmount;
        private int sellAmount;
        private double buyCost;
        private double sellCost;
        private Material material;
        private byte data;

        public PurchasableData(int buyAmount, int sellAmount, double buyCost, double sellCost, Material material, byte data) {
            this.buyAmount = buyAmount;
            this.sellAmount = sellAmount;
            this.buyCost = buyCost;
            this.sellCost = sellCost;
            this.material = material;
            this.data = data;
        }

        public Material getMaterial() {
            return material;
        }

        public byte getData() {
            return data;
        }

        public int getBuyAmount() {
            return buyAmount;
        }

        public int getSellAmount() {
            return sellAmount;
        }

        public double getBuyCost() {
            return buyCost;
        }

        public double getSellCost() {
            return sellCost;
        }
    }
}