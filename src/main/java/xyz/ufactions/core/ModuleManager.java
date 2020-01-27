package xyz.ufactions.core;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.builder.BuilderModule;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.coins.CoinModule;
import xyz.ufactions.permissions.PermissionsModule;
import xyz.ufactions.playtime.PlaytimeModule;
import xyz.ufactions.scoreboard.ScoreboardModule;
import xyz.ufactions.sidekick.SidekickModule;
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
        modules.add(new PermissionsModule(plugin));
        modules.add(new PlaytimeModule(plugin));
        modules.add(new ColorModule(plugin));
        modules.add(new TitleModule(plugin));
        modules.add(new WeatherModule(plugin));
        modules.add(new ScoreboardModule(plugin, "Loading..."));
        modules.add(new CoinModule(plugin));
        modules.add(new SidekickModule(plugin));
        modules.add(new BuilderModule(plugin));
        modules.add(new ChatModule(plugin));
    }

    public void unloadModules() {
        Iterator<Module> iterator = new ArrayList<>(modules).iterator();
        while(iterator.hasNext()) {
            Module module = iterator.next();
            module.onDisable();
            modules.remove(module);
        }
    }

    public Module getModule(String name) {
        for(Module module : modules) {
            if(module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        System.out.println("!ERROR! API tried fetching \"" + name + "\" but it's not registered as a proper module. Is it loaded or check name?");
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }
}