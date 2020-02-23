package xyz.ufactions.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.ufactions.api.Module;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SkinCaching {

    public static SkinCaching instance;

    public static SkinCaching getInstance() {
        return instance;
    }

    public static void initialize(Module module) {
        if (instance == null) instance = new SkinCaching(module);
    }

    private SkinCacheFile config;

    private SkinCaching(Module module) {
        config = new SkinCacheFile(module);
    }

    public String[] textures(String name) {
        if (config.getCache(name) != null) {
            return config.getCache(name);
        }
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();
            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();
            String[] array = new String[]{texture, signature};
            config.saveCache(name, array);
            return array;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}