package xyz.ufactions.motd;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.motd.commands.MOTDCommand;
import xyz.ufactions.motd.database.MOTDRepository;

import java.util.HashMap;

public class MOTDModule extends Module {

    private MOTDRepository repository;

    private HashMap<Integer, String> map = new HashMap<>();

    public MOTDModule(JavaPlugin plugin) {
        super("MOTD", plugin);

        repository = new MOTDRepository(plugin);

        refreshMOTDs();

        addCommand(new MOTDCommand(this));
    }

    public void addMOTD(String motd) {
        runAsync(() -> {
            repository.addMOTD(motd);
            refreshMOTDs();
        });
    }

    public void deleteMOTD(int id) {
        runAsync(() -> {
            repository.deleteMOTD(id);
            refreshMOTDs();
        });
    }

    public void refreshMOTDs() {
        runAsync(() -> map = repository.getMOTDs());
    }

    public HashMap<Integer, String> getMOTDs() {
        return map;
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        // FixME : Fetch random motd from list and display
    }
}