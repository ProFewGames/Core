package xyz.ufactions.crates;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.crates.commands.CrateCommand;
import xyz.ufactions.crates.files.CrateFiles;
import xyz.ufactions.crates.listeners.InventoryClick;
import xyz.ufactions.crates.listeners.PlayerInteract;
import xyz.ufactions.crates.managers.LocationManager;
import xyz.ufactions.crates.objects.Crate;
import xyz.ufactions.crates.utils.UtilChat;

import java.util.ArrayList;
import java.util.HashSet;

public class CratesModule extends Module {

    private static HashSet<CrateHook> hooks = new HashSet<>();

    public CratesModule(JavaPlugin plugin) {
        super("Crates", plugin);

        instance = this;
        prefix = "CustomCrates";
        if (CrateFiles.getInstance().getCrateFiles().isEmpty()) {
            CrateFiles.getInstance().createCrate(false, "default");
        }
        for (FileConfiguration config : CrateFiles.getInstance().getCrateFiles()) {
            new Crate(config);
        }
        registerEvents(new InventoryClick());
        registerEvents(new PlayerInteract());
        loadHolograms();

        new CrateHook("[crate_gk]") {

            @Override
            public void execute(String placeholder, String[] args, Player player) {
                String name = convert(args);
                Crate crate = Crate.getCrate(name);
                if (crate == null) {
                    System.err.println("[CRATES] " + name + " does not exist.");
                } else {
                    ItemStack item = crate.getKey().getItem().clone();
                    item.setAmount(1);
                    player.getInventory().addItem(item);
                }
            }
        };
    }

    public void reload() {
        for (Crate crate : new ArrayList<>(Crate.getCrates())) {
            crate.disable();
        }
        for (FileConfiguration config : CrateFiles.getInstance().getCrateFiles()) {
            new Crate(config);
        }
        loadHolograms();
    }

    private void loadHolograms() {
        for (Hologram holo : HologramsAPI.getHolograms(Plugin)) {
            holo.delete();
        }
        for (Crate crate : Crate.getCrates()) {
            for (Location loc : new LocationManager().getLocations(crate)) {
                loc.add(0.5, 1.7, 0.5);
                Hologram holo = HologramsAPI.createHologram(Plugin, loc);
                holo.insertTextLine(0, UtilChat.cc(crate.getDisplayName()));
                holo.insertTextLine(1, UtilChat.cc("&7(&fLeft click &7to see rewards)"));
                holo.insertTextLine(2, UtilChat.cc("&7(&fRight click &7with a key)"));
            }
        }
    }

    @Override
    public void addCommands() {
        addCommand(new CrateCommand(this));
    }

    private static CratesModule instance;
    public String prefix;

    public static CratesModule getInstance() {
        return instance;
    }

    public static void initializeHook(CrateHook hook) {
        hooks.add(hook);
    }

    public static HashSet<CrateHook> getHooks() {
        return hooks;
    }
}
