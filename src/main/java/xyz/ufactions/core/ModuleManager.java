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
import xyz.ufactions.motd.MOTDModule;
import xyz.ufactions.npc.NPCModule;
import xyz.ufactions.playtime.PlaytimeModule;
import xyz.ufactions.redis.Utility;
import xyz.ufactions.scoreboard.ScoreboardModule;
import xyz.ufactions.selections.SelectionManager;
import xyz.ufactions.selections.data.Selection;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.tablist.Tablist;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.timings.TimingManager;
import xyz.ufactions.transporter.TransporterModule;
import xyz.ufactions.weather.WeatherModule;

import java.util.*;

public class ModuleManager {

    public static boolean DEBUG = true;

    private JavaPlugin plugin;

    private List<Module> modules = new ArrayList<Module>();

    private Permission permission;

    public ModuleManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadModules(String serverName) {
        loadModules(serverName, Collections.emptyList());
    }

    public void loadModules(String serverName, List<Class<? extends Module>> disabledClasses) {
        TimingManager.initialize(plugin);
        if (!disabledClasses.isEmpty()) System.out.println("Modules ordered not to load: " + disabledClasses);
        if (!disabledClasses.contains(CratesModule.class))
            loadModule(new CratesModule(plugin));
//        if (!disabledClasses.contains(PlaytimeModule.class))
        loadModule(new PlaytimeModule(plugin));
        if (!disabledClasses.contains(ColorModule.class))
            loadModule(new ColorModule(plugin));
        if (!disabledClasses.contains(TitleModule.class))
            loadModule(new TitleModule(plugin));
        if (!disabledClasses.contains(WeatherModule.class))
            loadModule(new WeatherModule(plugin));
        if (!disabledClasses.contains(ScoreboardModule.class))
            loadModule(new ScoreboardModule(plugin, serverName));
        if (!disabledClasses.contains(MOTDModule.class))
            loadModule(new MOTDModule(plugin));
        if (!disabledClasses.contains(CoinModule.class))
            loadModule(new CoinModule(plugin));
        if (!disabledClasses.contains(SidekickModule.class)) {
            if (Bukkit.getPluginManager().isPluginEnabled("EchoPet")) {
                loadModule(new SidekickModule(plugin));
            } else {
                System.out.println("Optional plugin 'EchoPet' will enable the sidekick module");
            }
        }
        if (!disabledClasses.contains(BuilderModule.class))
            loadModule(new BuilderModule(plugin));
        if (!disabledClasses.contains(ChatModule.class))
            loadModule(new ChatModule(plugin));
        if (!disabledClasses.contains(HelpModule.class))
            loadModule(new HelpModule(plugin));
        if (!disabledClasses.contains(Tablist.class)) {
            if (!setupPermissions()) {
                System.out.println("No vault permission dependency found! Tablist module not enabling");
            } else {
                loadModule(new Tablist(plugin, serverName, permission));
            }
        }
        if (!disabledClasses.contains(MarketModule.class))
            loadModule(new MarketModule(plugin));
        if (!disabledClasses.contains(NPCModule.class))
            loadModule(new NPCModule(plugin));
        SelectionManager selectionManager = null;
        if (!disabledClasses.contains(SelectionManager.class))
            loadModule((selectionManager = new SelectionManager(plugin)));
        if (Utility.allowRedis() && selectionManager != null)
            loadModule(new TransporterModule(plugin, selectionManager));
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