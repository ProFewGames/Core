package xyz.ufactions.core;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.Module;
import xyz.ufactions.chatcolor.ColorModule;

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
        modules.add(new ColorModule(plugin));
    }

    public void unloadModules() {
        Iterator<Module> iterator = modules.iterator();
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