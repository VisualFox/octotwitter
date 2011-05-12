package me.l1k3.octobot.twitter.storage;

import java.util.Map;

public interface Storage {
    
    public final static String RESET = "reset";
    public final static String OVERWRITE = "overwrite";
    
    public boolean cached(String key);
    public void put(String key, Map<String, String> values);
    public Map<String, String> get(String key);
    public void remove(String key);
    public void clear();
    
    public void start();
    public void stop();
}
