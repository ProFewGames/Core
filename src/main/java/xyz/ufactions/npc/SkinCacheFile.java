package xyz.ufactions.npc;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.FileManager;
import xyz.ufactions.libs.UtilTime;

public class SkinCacheFile extends FileManager {

    private final long invalidateCache = 86400000;

    public SkinCacheFile(Module module) {
        super(module, "skin-cache.yml");
    }

    public String[] getCache(String name) {
        if(validateCache(name)) {
            name = name.toLowerCase();
            String string = getString("skins." + name);
            return string == null ? null : string.split(":");
        } else {
            set("skins." + name, null);
            save();
            return null;
        }
    }

    public void saveCache(String name, String[] array) {
        name = name.toLowerCase();
        set("skins." + name, array[0] + ":" + array[1]);
        set("last-cache", System.currentTimeMillis());
        save();
    }

    private boolean validateCache(String name) {
        long lastCache = getLong("last-cache");
        if (UtilTime.elapsed(lastCache, invalidateCache)) {
            return false;
        }
        return true;
    }
}