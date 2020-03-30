package xyz.ufactions.timings;

import java.util.HashMap;

public class TimingManager {

    private static HashMap<String, Long> timings = new HashMap<>();

    public static void start(String name) {
        timings.put(name, System.currentTimeMillis());
        System.out.println("Starting timings for : " + name);
    }

    public static void end(String name) {
        long epoch = timings.remove(name);
        System.out.println("");
    }

    public static void main(String[] args) {
        
    }
}