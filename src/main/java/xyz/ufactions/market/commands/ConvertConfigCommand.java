package xyz.ufactions.market.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.ufactions.api.Module;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.FileManager;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.market.MarketModule;

import java.io.File;

public class ConvertConfigCommand extends CommandBase<MarketModule> {

    public ConvertConfigCommand(MarketModule plugin) {
        super(plugin, "convertconfig");
    }

    @Override
    public void execute(Player player, String[] args) {
        UtilPlayer.message(player, F.main(Plugin.getName(), "Converting..."));

        try {
            File file = new File(Plugin.getDataFolder(), "convert.yml");
            System.out.println("File path : " + file.getAbsolutePath());
            System.out.println("File exists? : " + file.exists());
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            System.out.println("Config path : " + config.getCurrentPath());
            System.out.println("Config : " + config);

            ConvertedFile conf = new ConvertedFile(Plugin);

            int i = 0;

            for (String shop : config.getConfigurationSection("shops").getKeys(false)) {
                System.out.println("Converting " + shop + "...");
                conf.set("shops." + shop + ".identifier", i);
                // Example : shops.building
                for (String key : config.getConfigurationSection("shops." + shop + ".items").getKeys(false)) {
                    // Example : shops.buildings.items.1
                    String path = "shops." + shop + ".items." + key;

                    String material = config.getString(path + ".item.material");
                    int data = 0;
                    if (config.contains(path + ".item.damage")) {
                        data = config.getInt(path + ".item.damage");
                    }
                    double buy = config.getDouble(path + ".buyPrice");
                    double sell = 0;
                    if (config.contains(path + ".sellPrice")) {
                        sell = config.getDouble(path + ".sellPrice");
                    }

                    try {
                        int x = Integer.parseInt(material);
                        conf.set("shops." + shop + ".items." + key + ".item.material", x);
                    } catch (Exception e) {
                        conf.set("shops." + shop + ".items." + key + ".item.material", material);
                    }
                    conf.set("shops." + shop + ".items." + key + ".item.data", data);
                    conf.set("shops." + shop + ".items." + key + ".buy", buy);
                    conf.set("shops." + shop + ".items." + key + ".sell", sell);
                    System.out.println(shop + " converted!");
                }
                i++;
            }
            conf.save();
        }
        catch(Exception e) {
            UtilPlayer.message(player, F.error(Plugin.getName(), "Error converting market. " + F.elem(e.getLocalizedMessage())));
        }

        UtilPlayer.message(player, F.main(Plugin.getName(), "Converted!"));
    }

    public class ConvertedFile extends FileManager {

        public ConvertedFile(Module module) {
            super(module, "converted.yml");
        }
    }
}