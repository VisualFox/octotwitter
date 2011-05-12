package me.l1k3.octobot.twitter.storage;

import java.util.Map;
import java.util.HashMap;

public class MemoryStorage implements Storage {
    
    private static HashMap<String, Map<String, String>> IDs = new HashMap<String, Map<String,String>>();
    
    @Override
    public boolean cached(String key) {
        return IDs.containsKey(key);
    }

    @Override
    public void put(String key, Map<String, String> values) {
        IDs.put(key, values);
    }

    @Override
    public Map<String, String> get(String key) {
        return IDs.get(key);
    }

    @Override
    public void remove(String key) {
        IDs.remove(key);
    }

    @Override
    public void clear() {
        IDs.clear();
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }
}
