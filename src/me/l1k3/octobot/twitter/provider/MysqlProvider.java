package me.l1k3.octobot.twitter.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.urbanairship.octobot.Settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlProvider implements Provider {

    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String DB = "db";
    public final static String USER = "user";
    public final static String PASSWORD = "password";
    public final static String TABLE = "table";
    public final static String FIELD_TWITTER_SCREEN_NAME = "field_twitter_screen_name";
    public final static String FIELD_CONSUMER_KEY = "field_consumer_key";
    public final static String FIELD_CONSUMER_SECRET = "field_consumer_secret";
    public final static String FIELD_ACCESS_TOKEN = "field_access_token";
    public final static String FIELD_ACCESS_TOKEN_SECRET = "field_access_token_secret";
    
    private static Logger logger = Logger.getLogger("OctoTwitter");
    
    protected Connection connection;
    
    protected String table;
    protected String field_twitter_screen_name;
    protected String field_consumer_key;
    protected String field_consumer_secret;
    protected String field_access_token;
    protected String field_access_token_secret;
    
    @Override
    public Map<String, String> get(JSONObject task) {
        
        if(task.containsKey(Provider.ID)) {
            
            String id = (String)task.get(Provider.ID);
            
            Statement stmt = null;
            ResultSet rs = null;
    
            try {
                stmt = getStatement();
                
                if(stmt==null)
                return null;
                
                String fields = field_access_token+", "+field_access_token_secret;
                
                boolean use_field_consumer_key = false;
                boolean use_field_consumer_secret = false;
                
                if(field_consumer_key!=null && !field_consumer_key.equals("")) {
                    fields += ", "+field_consumer_key;
                    use_field_consumer_key = true;
                }
                
                if(field_consumer_secret!=null && !field_consumer_secret.equals("")) {
                    fields += ", "+field_consumer_secret;
                    use_field_consumer_secret = true;
                }
                
                PreparedStatement st = prepareStatement("SELECT "+fields+" FROM "+table+" WHERE "+field_twitter_screen_name+"=?");
                st.setString(1, id);
                rs = st.executeQuery();
                
                if (rs.first()) {
                    
                    String consumer_key = (use_field_consumer_key)? rs.getString(field_consumer_key) : null;
                    String consumer_secret = (use_field_consumer_secret)? rs.getString(field_consumer_secret) : null;
                    
                    HashMap<String, String> map = new HashMap<String,String>();
                    
                    if(consumer_key!=null)
                    map.put(Provider.CONSUMER_KEY, consumer_key);
                    
                    if(consumer_secret!=null)
                    map.put(Provider.CONSUMER_SECRET, consumer_secret);
                    
                    map.put(Provider.ACCESS_TOKEN, rs.getString(field_access_token));
                    map.put(Provider.ACCESS_TOKEN_SECRET, rs.getString(field_access_token_secret));
                
                    return map;
                }
            }
            catch (SQLException ex){
                logError(ex);
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
        
        return null;
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
        
        table = Settings.get(key, TABLE);
        
        field_twitter_screen_name = Settings.get(key, FIELD_TWITTER_SCREEN_NAME);
        field_consumer_key = Settings.get(key, FIELD_CONSUMER_KEY);
        field_consumer_secret = Settings.get(key, FIELD_CONSUMER_SECRET);
        field_access_token = Settings.get(key, FIELD_ACCESS_TOKEN);
        field_access_token_secret = Settings.get(key, FIELD_ACCESS_TOKEN_SECRET);
        
        initConnection(host, port, db, user, password);
    }

    protected void initDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            logger.error("Error loading JDBC driver: " + ex.getMessage());
        }
    }
    
    protected void initConnection(String host, int port, String db, String user, String password) {
        try {
            if(port<=0)
            connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+db, user, password);
            else
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db, user, password);
        } catch (SQLException ex) {
            logError(ex);
        }
    }

    @Override
    public void stop() {
        try {
            if(connection!=null)
            connection.close();
        } catch (SQLException ex) {
            logError(ex);
        }
    }
    
    protected PreparedStatement prepareStatement(String statement) throws SQLException {
        return connection.prepareStatement(statement);
    }
    
    protected Statement getStatement() {
        try {
            if(connection!=null)
            return connection.createStatement();
        } catch (SQLException e) {
            logError(e);
        }
        
        return null;
    }
    
    protected void logError(SQLException ex) {
        logger.error("SQLException: " + ex.getMessage());
        logger.error("SQLState: " + ex.getSQLState());
        logger.error("VendorError: " + ex.getErrorCode());
    }
    
    protected String getProviderKey() {
        return "octotwitter_mysql_provider";
    }
}
