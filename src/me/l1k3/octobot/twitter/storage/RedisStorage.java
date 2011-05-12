package me.l1k3.octobot.twitter.storage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
//import java.util.concurrent.TimeoutException;

import com.urbanairship.octobot.Settings;

import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;

public class RedisStorage implements Storage {

    public final static String DEFAULT_PREFIX = "OctoTwitter:Redis:Storage:";

    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String PREFIX = "prefix";
    
    protected String prefix;
    protected String host;
    protected int port;
    
    @Override
    public boolean cached(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return (jedis.exists(prefix+key)>0)? true : false;
        } catch (UnknownHostException e1) {
            
        } catch (IOException e1) {
            
        } finally {   
            try {
                releaseJedis(jedis);
            } catch (IOException e) { 
                
            }
        }
        
        return false;
    }

    @Override
    public void put(String key, Map<String, String> values) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            jedis.hmset(prefix+key, values);
        } catch (UnknownHostException e1) {
            
        } catch (IOException e1) {
            
        } finally {   
            try {
                releaseJedis(jedis);
            } catch (IOException e) { 
                
            }
        }
    }

    @Override
    public Map<String, String> get(String key) {
        Jedis jedis = null;
        
        try {
            jedis = getJedis();
            return jedis.hgetAll(prefix+key);
        } catch (UnknownHostException e1) {
            
        } catch (IOException e1) {
            
        } finally {   
            try {
                releaseJedis(jedis);
            } catch (IOException e) { 
                
            }
        }
        
        return null;
    }

    @Override
    public void remove(String key) {
        Jedis jedis = null;
        
        try {
            jedis = getJedis();
            jedis.del(prefix+key);
        } catch (UnknownHostException e1) {
            
        } catch (IOException e1) {
            
        } finally {   
            try {
                releaseJedis(jedis);
            } catch (IOException e) { 
                
            }
        }
    }

    @Override
    public void clear() {
        Jedis jedis = null;
        
        try {
            jedis = getJedis();
            List<String> list = jedis.keys(prefix+"*");
            jedis.del(list.toArray(new String[list.size()]));
        } catch (UnknownHostException e1) {
            
        } catch (IOException e1) {
            
        } finally {   
            try {
                releaseJedis(jedis);
            } catch (IOException e) { 
                
            }
        }
    }

    @Override
    public void start() {
        
        String key = getStorageKey();
        
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
    
    protected String getStorageKey() {
        return "octotwitter_redis_storage";
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
