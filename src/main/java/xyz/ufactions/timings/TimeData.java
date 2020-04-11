package xyz.ufactions.timings;

public class TimeData {

    public String title;
    public long started;
    public long lastMarker;
    public long total;
    public int count = 0;

    public TimeData(String title, long time) {
        this.title = title;
        this.started = time;
        this.lastMarker = time;
        total = 0L;
    }

    public void addTime() {
        total += System.currentTimeMillis() - lastMarker;
        lastMarker = System.currentTimeMillis();
        count++;
    }

    public void printInfo() {
        System.out.println("==[TIME DATA]== " + count + " " + title + " took " + total + "ms in the last " + (System.currentTimeMillis() - started) + "ms");
    }
}