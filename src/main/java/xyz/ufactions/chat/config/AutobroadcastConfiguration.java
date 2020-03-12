package xyz.ufactions.chat.config;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.FileManager;

import java.util.Arrays;

public class AutobroadcastConfiguration extends FileManager {

    public AutobroadcastConfiguration(Module module) {
        super(module, "broadcast.yml");
    }

    @Override
    public void create() {
        set("Update Type", "MIN_01");
        set("broadcasts", Arrays.asList("Test broadcast #1", "Test broadcast #2", "Test broadcast #3"));
        super.create();
    }
}