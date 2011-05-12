package me.l1k3.octobot.twitter.provider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Map;

import org.json.simple.JSONObject;
import org.lorecraft.phparser.SerializedPhpParser;

import com.urbanairship.octobot.Settings;

public class DrupalTwitterMysqlProvider extends MysqlProvider {
    
    protected String defaultConsumerKey;
    protected String defaultConsumerSecret;
    
    @Override
    public Map<String, String> get(JSONObject task) {
        Map<String, String> map = super.get(task);
        
        if(!map.containsKey(Provider.CONSUMER_KEY) && defaultConsumerKey!=null && !defaultConsumerKey.equals(""))
        map.put(Provider.CONSUMER_KEY, defaultConsumerKey);
        
        if(!map.containsKey(Provider.CONSUMER_SECRET) && defaultConsumerSecret!=null && !defaultConsumerSecret.equals(""))
        map.put(Provider.CONSUMER_SECRET, defaultConsumerSecret);
        
        return map;
    }
    
    @Override
    public void start() {
        
        initDriver();
        
        String key = getProviderKey();
        
        String host = Settings.get(key, HOST);
        int port = Settings.getAsInt(key, PORT);
        String db = Settings.get(key, DB);
        String user = Settings.get(key, USER);
        String password = Settings.get(key, PASSWORD);
        
        table = "twitter_account";
        
        field_twitter_screen_name = "screen_name";
        field_consumer_key = null;
        field_consumer_secret = null;
        field_access_token = "oauth_token";
        field_access_token_secret = "oauth_token_secret";
        
        initConnection(host, port, db, user, password);
        
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = getStatement();

            if(stmt==null)
            return;

            try {
                PreparedStatement st = prepareStatement("SELECT value FROM variable WHERE name=?");
                st.setString(1, "twitter_consumer_key");
                rs = st.executeQuery();

                if (rs.first()) {
                    
                    //unserialize php serialize string
                    SerializedPhpParser serializedPhpParser = new SerializedPhpParser(rs.getString("value"));
                    Object result = serializedPhpParser.parse();
                    
                    if(result instanceof String)
                    defaultConsumerKey = (String)result;
                }
            } catch (SQLException e) {
                logError(e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } 
                    catch (SQLException sqlEx) { 
                        
                    }

                    rs = null;
                }
            }
            
            try {
                PreparedStatement st = prepareStatement("SELECT value FROM variable WHERE name=?");
                st.setString(1, "twitter_consumer_secret");
                rs = st.executeQuery();
            
                if (rs.first()) {
                    
                    //unserialize php serialize string
                    SerializedPhpParser serializedPhpParser = new SerializedPhpParser(rs.getString("value"));
                    Object result = serializedPhpParser.parse();
                    
                    if(result instanceof String)
                    defaultConsumerSecret = (String)result;
                }
            } catch (SQLException e) {
                logError(e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } 
                    catch (SQLException sqlEx) { 
                        
                    }

                    rs = null;
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } 
                catch (SQLException sqlEx) { 
                    
                }

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } 
                catch (SQLException sqlEx) { 
                    
                }

                stmt = null;
            }
        }
    }
    
    protected String getProviderKey() {
        return "octotwitter_drupal_twitter_mysql_provider";
    }
}
