package me.l1k3.octobot.twitter.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.urbanairship.octobot.Settings;

public class PropertyProvider implements Provider {

    public final static String PATH = "path";
    
    private String path;
    
    public PropertyProvider() {
        
    }
    
    @Override
    public Map<String, String> get(JSONObject task) {
        
        if(task.containsKey(Provider.ID)) {

            String id = (String)task.get(Provider.ID);
            File file;

            if(path==null || path.equals("")) {
                file = new File(id+".properties");
            }
            else {
                file = new File(path+id+".properties");
            }
            
            if (file.exists()) {

                Properties properties = new Properties();
                InputStream is = null;
                
                try {
                    
                    is = new FileInputStream(file);
                    properties.load(is);
                }
                catch (IOException ioe) {
                    
                }
                finally {
                    
                    if (null != is) {
                        try {
                            is.close();
                        } 
                        catch (IOException ignore) {
                            
                        }
                    }
                }
                
                String consumer_key = properties.containsKey(Provider.CONSUMER_KEY)? properties.getProperty(Provider.CONSUMER_KEY) : null;
                String consumer_secret = properties.containsKey(Provider.CONSUMER_SECRET)? properties.getProperty(Provider.CONSUMER_SECRET) : null;
                String access_token = properties.containsKey(Provider.ACCESS_TOKEN)? properties.getProperty(Provider.ACCESS_TOKEN) : null;
                String access_token_secret = properties.containsKey(Provider.ACCESS_TOKEN_SECRET)? properties.getProperty(Provider.ACCESS_TOKEN_SECRET) : null;
                
                if(access_token!=null && access_token_secret!=null) {
                    
                    HashMap<String, String> map = new HashMap<String,String>();
                    
                    if(consumer_key!=null)
                    map.put(Provider.CONSUMER_KEY, consumer_key);
                    
                    if(consumer_secret!=null)
                    map.put(Provider.CONSUMER_SECRET, consumer_secret);
                    
                    map.put(Provider.ACCESS_TOKEN, access_token);
                    map.put(Provider.ACCESS_TOKEN_SECRET, access_token_secret);
                
                    return map;
                }
            }
        }
        
        return null;
    }

    @Override
    public void start() {
        String key = getProviderKey();
        
        path = Settings.get(key, PATH);
        
        if(path!=null && path.length()>0 && !path.endsWith("/")) {
            path += "/";
        }
    }

    @Override
    public void stop() {
        
    }
    
    protected String getProviderKey() {
        return "octotwitter_property_provider";
    }
}
