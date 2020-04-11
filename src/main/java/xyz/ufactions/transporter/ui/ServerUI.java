package xyz.ufactions.transporter.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.libs.C;
import xyz.ufactions.shop.IButton;
import xyz.ufactions.shop.Shop;
import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.transporter.TransporterModule;

import java.util.ArrayList;
import java.util.List;

public class ServerUI extends Shop {

    public ServerUI(TransporterModule plugin) {
        super(plugin.getPlugin(), C.mHead + C.Bold + "Servers", 45, Shop.ShopFiller.RAINBOW);

        List<IButton> buttons = new ArrayList<>();
        buttons.add(new ShopItem<TransporterModule>(plugin, Material.TNT, C.cGold + C.Bold + "Factions", 20,
                "",
                "Lorem ipsum dolor sit amet, consectetur",
                "adipiscing elit. Ut eleifend eros necpretium",
                "rhoncus. Morbi placerat tincidunt tempor.",
                "",
                C.cGray + "Players:" + C.cWhite + " n/a",
                "",
                C.cGray + "»" + C.cAqua + C.Italics + " Click to join " + C.cGray + "«") {

            @Override
            public void onClick(Player player, ClickType clickType) {
                player.closeInventory();
                Plugin.transfer(player.getName(), "factions");
            }
        });
        buttons.add(new ShopItem<TransporterModule>(plugin, Material.DIAMOND_SPADE, C.cGold + C.Bold + "Arcade", 24,
                "",
                "A classical arcade experience! You can",
                "party up with your friends and dominate",
                "or join head-first with other players",
                "",
                C.cGray + "Players:" + C.cWhite + " n/a",
                "",
                C.cGray + "»" + C.cAqua + C.Italics + " Click to join " + C.cGray + "«") {

            @Override
            public void onClick(Player player, ClickType clickType) {
                player.closeInventory();
                Plugin.transfer(player.getName(), "arcade");
            }
        });
        setButtons(buttons);

        updatableItems = true;
    }
}