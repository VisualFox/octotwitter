package me.l1k3.octobot.twitter.provider;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.simple.JSONObject;

import com.urbanairship.octobot.Settings;

import redis.clients.jedis.Jedis;

public class RedisProvider implements Provider {

    public final static String DEFAULT_PREFIX = "OctoTwitter:Redis:Provider:";
    
    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String PREFIX = "prefix";    
    
    protected String prefix;
    protected String host;
    protected int port;
    
    @Override
    public Map<String, String> get(JSONObject task) {
        
        if(task.containsKey(Provider.ID)) {
            
            String id = (String)task.get(Provider.ID);
            Jedis jedis = null;
            
            try {
                jedis = getJedis();
                return jedis.hgetAll(prefix+id);
            } catch (UnknownHostException e1) {
                
            } catch (IOException e1) {
                
            } finally {   
                try {
                    releaseJedis(jedis);
                } catch (IOException e) { 
                    
                }
            }
        }
        
        return null;
    }

    @Override
    public void start() {
        
        String key = getProviderKey();
        
        prefix = Settings.get(key, PREFIX);
        
        if(prefix==null || prefix.equals("")) {
            prefix = DEFAULT_PREFIX;
        }
        
        host = Settings.get(key, HOST);
        port = Settings.getAsInt(key, PORT);
        
        if(host==null || host.equals("")) {
            host = "localhost";
        }
        
        if(port<=0) {
            port = 6379;
        }
    }

    @Override
    public void stop() {
        
    }
    
    protected String getProviderKey() {
        return "octotwitter_redis_provider";
    }
    
    protected Jedis getJedis() throws UnknownHostException, IOException {
        Jedis jedis = new Jedis(host, port);
        jedis.connect();

        return jedis;
    }
    
    protected void releaseJedis(Jedis jedis) throws IOException {
        if(jedis!=null) {
            jedis.quit();
            jedis.disconnect();
        }
    }    
}
