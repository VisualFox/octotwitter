package me.l1k3.octobot.twitter.provider;

import java.util.Map;

import org.json.simple.JSONObject;

import me.l1k3.octobot.twitter.storage.Storage;

public class StorageProvider implements Provider {

    private Storage storage;
    
    @Override
    public Map<String, String> get(JSONObject task) {
        
        //reset cache
        if(task.containsKey(Storage.RESET)) {
            
            Boolean reset = (Boolean)task.get(Storage.RESET);
            
            if(reset.booleanValue()) {
                if(task.containsKey(ID)) {
                    storage.remove((String)task.get(ID));
                }
                else {
                    storage.clear();
                }
            }
            
            return null;
        }
        
        if(task.containsKey(Provider.ID)) {
            String id = (String)task.get(Provider.ID);
            
            if(storage.cached(id)) {
                return storage.get(id);
            }
        }
        
        return null;
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }    
}
