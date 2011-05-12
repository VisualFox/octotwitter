package me.l1k3.octobot.twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import me.l1k3.octobot.twitter.provider.*;
import me.l1k3.octobot.twitter.storage.*;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.urbanairship.octobot.Settings;

import twitter4j.Twitter;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterFactory {
    
    public final static String STORAGE = "storage";
    public final static String PROVIDERS = "providers";
    public final static String DEFAULT_CONSUMER_KEY = "default_consumer_key";
    public final static String DEFAULT_CONSUMER_SECRET = "default_consumer_secret";
    
    private ArrayList<Provider> providers = new ArrayList<Provider>();
    private Storage storage;
    
    private String defaultConsumerKey;
    private String defaultConsumerSecret;

    private static Logger logger = Logger.getLogger("OctoTwitter");
    
    public Twitter getInstance(JSONObject task) {
        
        Map<String, String> map = null;
        
        for (Provider provider : providers) {
            map = provider.get(task);
            
            if(map!=null && map.containsKey(Provider.ACCESS_TOKEN) && map.containsKey(Provider.ACCESS_TOKEN_SECRET)) {
                String consumerKey = (map.containsKey(Provider.CONSUMER_KEY))? map.get(Provider.CONSUMER_KEY) : defaultConsumerKey;
                String consumerSecret = (map.containsKey(Provider.CONSUMER_SECRET))? map.get(Provider.CONSUMER_SECRET) : defaultConsumerSecret;
                
                return getInstance(task, consumerKey, consumerSecret, map.get(Provider.ACCESS_TOKEN), map.get(Provider.ACCESS_TOKEN_SECRET));
            }
        }
        
        return getInstance();
    }
    
    public Twitter getInstance() {
            
        return new twitter4j.TwitterFactory().getInstance();
    }
    
    public Twitter getInstance(JSONObject task, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        
        if(consumerKey==null || consumerSecret==null || accessToken==null || accessTokenSecret==null) {
            return null;
        }
        
        if(storage!=null) {
            
            if(task.containsKey(Provider.ID)) {
                
                String id = (String)task.get(Provider.ID);
                boolean overwrite = task.containsKey(Storage.OVERWRITE)? ((Boolean)task.get(Storage.OVERWRITE)).booleanValue() : false;
                
                if(!storage.cached(id) || overwrite) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    
                    if(!consumerKey.equals(defaultConsumerKey))
                    map.put(Provider.CONSUMER_KEY, consumerKey);
                    
                    if(!consumerSecret.equals(defaultConsumerSecret))
                    map.put(Provider.CONSUMER_SECRET, consumerSecret);
                    
                    map.put(Provider.ACCESS_TOKEN, accessToken);
                    map.put(Provider.ACCESS_TOKEN_SECRET, accessTokenSecret);
                    
                    storage.put(id, map);
                }
            }
        }
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
            .setOAuthConsumerKey(consumerKey)
            .setOAuthConsumerSecret(consumerSecret)
            .setOAuthAccessToken(accessToken)
            .setOAuthAccessTokenSecret(accessTokenSecret);
        
        return new twitter4j.TwitterFactory(cb.build()).getInstance();
    }
    
    public void start() {

        String key = getTwitterFactoryKey();
        
        defaultConsumerKey = Settings.get(key, DEFAULT_CONSUMER_KEY);
        defaultConsumerSecret = Settings.get(key, DEFAULT_CONSUMER_SECRET);
        
        String storagePolicy = Settings.get(key, STORAGE);
        
        if(storagePolicy!=null && !storagePolicy.equals("")) {
            
            if(storagePolicy.indexOf('.')==-1) {
                storagePolicy = "me.l1k3.octobot.twitter.storage."+storagePolicy;
            }
            
            try {
                Object object = Class.forName(storagePolicy).newInstance();
                
                if(object instanceof Storage) {
                    storage = (Storage)object;
                }
                
            } catch (Exception ex) {
                logger.error("Error loading storage: " + ex.getMessage());
            }
        }

        String providersList = Settings.get(key, PROVIDERS);
        
        if(providersList==null || providersList.equals("")) {
            providersList = "TaskProvider,StorageProvider,PropertyProvider,SettingsProvider";
        }
        
        StringTokenizer tokenizer = new StringTokenizer(providersList, ":");
        
        while(tokenizer.hasMoreTokens()) {
            String p = tokenizer.nextToken();
            
            if(p.indexOf('.')==-1) {
                p = "me.l1k3.octobot.twitter.provider."+p;
            }
            
            try {
                Object object = Class.forName(p).newInstance();
                
                if(object instanceof Provider) {
                    Provider provider = (Provider)object;
                    
                    if(provider instanceof StorageProvider) {
                        if(storage!=null) {
                            ((StorageProvider)provider).setStorage(storage);
                            providers.add(provider);
                        }
                    }
                    else {
                        providers.add(provider);
                    }
                }
                
            } catch (Exception ex) {
                logger.error("Error loading provider: " + ex.getMessage());
            }
        }
        
        if(storage!=null) {
            storage.start();
        }
        
        for (Provider provider : providers) {
            provider.start();
        }
    }
    
    public void stop() {
        for (Provider provider : providers) {
            provider.stop();
        }
        
        if(storage!=null) {
            storage.stop();
        }
    }
    
    protected String getTwitterFactoryKey() {
        return "octotwitter_factory";
    }
}
