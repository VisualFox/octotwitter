package me.l1k3.octobot.twitter.provider;

import java.util.Map;

import org.json.simple.JSONObject;

public class TaskProvider implements Provider {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> get(JSONObject task) {
        
        if (task.containsKey(Provider.ACCESS_TOKEN) && task.containsKey(Provider.ACCESS_TOKEN_SECRET)) {
            
            return task;
        }
        
        return null;
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }    
}
