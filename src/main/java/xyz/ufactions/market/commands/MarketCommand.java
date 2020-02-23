package xyz.ufactions.market.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.commands.ICommand;
import xyz.ufactions.market.MarketModule;
import xyz.ufactions.market.gui.MainShop;

public class MarketCommand extends CommandBase<MarketModule> {

    public MarketCommand(MarketModule plugin) {
        super(plugin, "shop", "market");
    }

    @Override
    public void execute(Player player, String[] args) {
        MainShop shop = new MainShop(Plugin);
        shop.openInventory(player);
    }
}
