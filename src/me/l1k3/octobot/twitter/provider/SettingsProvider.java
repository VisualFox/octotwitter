package me.l1k3.octobot.twitter.provider;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.urbanairship.octobot.Settings;

public class SettingsProvider implements Provider {

    @Override
    public Map<String, String> get(JSONObject task) {
        
        if(task.containsKey(Provider.ID)) {
            
            String id = (String)task.get(Provider.ID);
            
            String consumer_key = Settings.get(id, Provider.CONSUMER_KEY);
            String consumer_secret = Settings.get(id, Provider.CONSUMER_SECRET);
            String access_token = Settings.get(id, Provider.ACCESS_TOKEN);
            String access_token_secret = Settings.get(id, Provider.ACCESS_TOKEN_SECRET);
            
            if(access_token!=null && access_token_secret!=null) {
                
                HashMap<String, String> map = new HashMap<String,String>();
                
                if(consumer_key!=null && !consumer_key.equals(""))
                map.put(Provider.CONSUMER_KEY, consumer_key);
                    
                if(consumer_secret!=null && !consumer_secret.equals(""))
                map.put(Provider.CONSUMER_SECRET, consumer_secret);
                    
                map.put(Provider.ACCESS_TOKEN, access_token);
                map.put(Provider.ACCESS_TOKEN_SECRET, access_token_secret);
            
                return map;
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
}
