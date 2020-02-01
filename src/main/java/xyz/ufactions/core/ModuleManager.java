package xyz.ufactions.core;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.builder.BuilderModule;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.coins.CoinModule;
import xyz.ufactions.help.HelpModule;
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
//        loadModule(new PermissionsModule(plugin)); FIXME Waiting for module to be fixed to be re-enabled in core
        loadModule(new PlaytimeModule(plugin));
        loadModule(new ColorModule(plugin));
        loadModule(new TitleModule(plugin));
        loadModule(new WeatherModule(plugin));
        loadModule(new ScoreboardModule(plugin, "Loading..."));
        loadModule(new CoinModule(plugin));
        loadModule(new SidekickModule(plugin));
        loadModule(new BuilderModule(plugin));
        loadModule(new ChatModule(plugin));
        loadModule(new HelpModule(plugin));
    }

    public void loadModule(Module module) {
        modules.add(module);
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