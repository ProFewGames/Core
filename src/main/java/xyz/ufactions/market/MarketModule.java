package xyz.ufactions.market;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.market.commands.ConvertConfigCommand;
import xyz.ufactions.market.commands.MarketCommand;
import xyz.ufactions.market.config.MainConfig;
import xyz.ufactions.market.config.ShopConfig;

public class MarketModule extends Module {

    private MainConfig config;
    private ShopConfig shopConfig;

    public MarketModule(JavaPlugin plugin) {
        super("Market", plugin);

        config = new MainConfig(this);
        shopConfig = new ShopConfig(this);
    }

    @Override
    public void addCommands() {
        addCommand(new MarketCommand(this));
        addCommand(new ConvertConfigCommand(this));
    }

    public MainConfig getConfig() {
        return config;
    }

    public ShopConfig getShopConfig() {
        return shopConfig;
    }
}