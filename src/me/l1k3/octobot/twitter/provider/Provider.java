package me.l1k3.octobot.twitter.provider;

import java.util.Map;

import org.json.simple.JSONObject;

public interface Provider {
    
    public final static String CONSUMER_KEY = "consumer_key";
    public final static String CONSUMER_SECRET = "consumer_secret";
    public final static String ACCESS_TOKEN = "access_token";
    public final static String ACCESS_TOKEN_SECRET = "access_token_secret";
    public final static String ID = "id";
    
    public Map<String, String> get(JSONObject task);
    public void start();
    public void stop();
}