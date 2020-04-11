package xyz.ufactions.timings;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.libs.NautHashMap;

import java.util.Map;

public class TimingManager implements Listener {

    private static TimingManager instance;

    private JavaPlugin plugin;
    private static NautHashMap<String, Long> timingList = new NautHashMap<>();
    private static NautHashMap<String, TimeData> totalList = new NautHashMap<>();

    private static Object timingLock = new Object();
    private static Object totalLock = new Object();

    public static boolean debug = true;

    protected TimingManager(JavaPlugin plugin) {
        instance = this;

        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static TimingManager initialize(JavaPlugin plugin) {
        if (instance == null) instance = new TimingManager(plugin);
        return instance;
    }

    public static TimingManager instance() {
        return instance;
    }

    public static void startTotal(String title) {
        if (!debug) return;

        synchronized (totalLock) {
            if (totalList.containsKey(title)) {
                TimeData data = totalList.get(title);
                data.lastMarker = System.currentTimeMillis();
            } else {
                TimeData data = new TimeData(title, System.currentTimeMillis());
                totalList.put(title, data);
            }
        }
    }

    public static void stopTotal(String title) {
        if (!debug) return;

        synchronized (totalLock) {
            if (totalList.containsKey(title)) {
                totalList.get(title).addTime();
            }
        }
    }

    public static void printTotal(String title) {
        if (!debug) return;

        synchronized (totalLock) {
            totalList.get(title).printInfo();
        }
    }

    public static void endTotal(String title, boolean print) {
        if (!debug) return;

        synchronized (totalLock) {
            TimeData data = totalList.remove(title);
            if (data != null && print) data.printInfo();
        }
    }

    public static void printTotals() {
        if (!debug) return;

        synchronized (totalLock) {
            for (Map.Entry<String, TimeData> entry : totalList.entrySet()) {
                entry.getValue().printInfo();
            }
        }
    }

    public static void start(String title) {
        if (!debug) return;

        synchronized (timingLock) {
            timingList.put(title, System.currentTimeMillis());
        }
    }

    public static void stop(String title) {
        if (!debug) return;
        synchronized (timingLock) {
            System.out.println("==[TIMING]== " + title + " took " + (System.currentTimeMillis() - timingList.get(title) + "ms"));
            timingList.remove(title);
        }
    }
}