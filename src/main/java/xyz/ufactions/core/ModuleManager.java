package xyz.ufactions.core;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.playtime.PlaytimeModule;
import xyz.ufactions.scoreboard.ScoreboardModule;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.weather.WeatherModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModuleManager {

    private JavaPlugin plugin;

    private List<Module> modules = new ArrayList<Module>();

    public ModuleManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadModules() {
        modules.add(new PlaytimeModule(plugin));
        modules.add(new ColorModule(plugin));
        modules.add(new TitleModule(plugin));
        modules.add(new WeatherModule(plugin));
        modules.add(new ScoreboardModule(plugin));
    }

    public void unloadModules() {
        Iterator<Module> iterator = new ArrayList<>(modules).iterator();
        while(iterator.hasNext()) {
            Module module = iterator.next();
            module.onDisable();
            modules.remove(module);
        }
    }

    public List<Module> getModules() {
        return modules;
    }
}