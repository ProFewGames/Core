package xyz.ufactions.core;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.builder.BuilderModule;
import xyz.ufactions.chat.ChatModule;
import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.coins.CoinModule;
import xyz.ufactions.crates.CratesModule;
import xyz.ufactions.help.HelpModule;
import xyz.ufactions.market.MarketModule;
import xyz.ufactions.monitor.LagMeter;
import xyz.ufactions.npc.NPCModule;
import xyz.ufactions.playtime.PlaytimeModule;
import xyz.ufactions.scoreboard.ScoreboardModule;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.tablist.Tablist;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.weather.WeatherModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModuleManager {

    public static boolean DEBUG = true;

    private JavaPlugin plugin;

    private List<Module> modules = new ArrayList<Module>();

    private Permission permission;

    public ModuleManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadModules(String serverName) {
//        loadModule(new PermissionsModule(plugin)); FIXME Waiting for module to be fixed to be re-enabled in core
        loadModule(new CratesModule(plugin));
        loadModule(new PlaytimeModule(plugin));
        loadModule(new ColorModule(plugin));
        loadModule(new TitleModule(plugin));
        loadModule(new WeatherModule(plugin));
        loadModule(new ScoreboardModule(plugin, serverName));
        loadModule(new CoinModule(plugin));
        if (Bukkit.getPluginManager().isPluginEnabled("EchoPet")) {
            System.out.println("EchoPet dependency found! Enabling Sidekick module...");
            loadModule(new SidekickModule(plugin));
        }
        loadModule(new BuilderModule(plugin));
        loadModule(new ChatModule(plugin));
        loadModule(new HelpModule(plugin));
        if (!setupPermissions()) {
            System.out.println("No vault permission dependency found! Tablist module not enabling");
        } else {
            loadModule(new Tablist(plugin, serverName, permission));
        }
        loadModule(new MarketModule(plugin));
        loadModule(new NPCModule(plugin));
        loadModule(new LagMeter(plugin));
    }

    public void loadModule(Module module) {
        modules.add(module);
    }

    public void unloadModules() {
        Iterator<Module> iterator = new ArrayList<>(modules).iterator();
        while (iterator.hasNext()) {
            Module module = iterator.next();
            module.onDisable();
            modules.remove(module);
        }
    }

    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        System.out.println("!ERROR! API tried fetching \"" + name + "\" but it's not registered as a proper module. Is it loaded or check name?");
        return null;
    }

    public List<Module> getModules() {
        return modules;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
}