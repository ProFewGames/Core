package xyz.ufactions.weather;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;
import xyz.ufactions.weather.commands.WeatherCommand;

public class WeatherModule extends Module {

    private HashSet<WeatherOptions> weather = new HashSet<>();

    public WeatherModule(JavaPlugin plugin) {
        super("Weather", plugin);

        if(getDataFolder().exists()) {
            File file = new File(getDataFolder(), "data.yml");
            if (file.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (String string : config.getStringList("options")) {
                    weather.add(WeatherOptions.decode(string));
                }
            }
        }
    }

    public void save() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> options = new ArrayList<>();
        for (WeatherOptions wo : weather) {
            options.add(wo.encode());
        }
        config.set("options", options);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WeatherOptions getWeather(World world) {
        for (WeatherOptions weather : weather) {
            if (weather.getWorld() == world) {
                return weather;
            }
        }
        WeatherOptions weather = new WeatherOptions(world);
        this.weather.add(weather);
        return weather;
    }

    @Override
    public void addCommands() {
        addCommand(new WeatherCommand(this));
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SEC)
            return;

        for (WeatherOptions weather : weather) {
            World world = weather.getWorld();
            if (weather.isTimeLock()) {
                world.setTime(weather.getLockedTime());
            }
            if (world.hasStorm() && weather.isDisableRain()) {
                world.setStorm(false);
            }
        }
    }
}