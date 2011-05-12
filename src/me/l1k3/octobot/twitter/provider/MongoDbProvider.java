package me.l1k3.octobot.twitter.provider;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.urbanairship.octobot.Settings;

public class MongoDbProvider implements Provider {

    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String DB = "db";
    public final static String COLLECTION = "collection";
    public final static String USER = "user";
    public final static String PASSWORD = "password";
    public final static String TWITTER_SCREEN_NAME = "index_twitter_screen_name";
    
    private static Logger logger = Logger.getLogger("OctoTwitter");
    
    private Mongo mongo;
    private String database;
    private String collection;
    private String index;
    private String user;
    private String password;
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> get(JSONObject task) {

        if(task.containsKey(Provider.ID)) {
            
            String id = (String)task.get(Provider.ID);
            
            DBCollection c = getCollection();
    
            if(c==null)
            return null;
    
            BasicDBObject query = new BasicDBObject();
    
            query.put(index, id);
    
            DBCursor cur = c.find(query);
    
            if(cur.hasNext()) {
                DBObject object = cur.next();
                return object.toMap();
            }
        }

        return null;
    }

    @Override
    public void start() {
        String key = getProviderKey();
        
        String host = Settings.get(key, HOST);
        int port = Settings.getAsInt(key, PORT);
        
        user = Settings.get(key, USER);
        password = Settings.get(key, PASSWORD);
        
        index = Settings.get(key, TWITTER_SCREEN_NAME);
        
        database = Settings.get(key, DB);
        collection = Settings.get(key, COLLECTION);
        
        try {
            if(port<=0)
            mongo = new Mongo(host);   
            else
            mongo = new Mongo(host, port);
            
        } catch (UnknownHostException e) {
            logger.error("MongoDb unknown host error "+e.getMessage());
        } catch (MongoException e) {
            logger.error("MongoDb error "+e.getMessage());
        }
    }

    @Override
    public void stop() {
        
    }
    
    protected String getProviderKey() {
        return "octotwitter_mongodb_provider";
    }
    
    protected DB getDB() {
        DB db = mongo.getDB(database);
        
        if(user!=null && !user.equals("") && password!=null && !password.equals("")) {
            if(!db.authenticate(user, password.toCharArray())) {
                logger.error("MongoDb wrong user and password");
                return null;
            }
        }
        
        return db;
    }
    
    protected DBCollection getCollection() {
        DB db = getDB();
        
        if(db!=null) {
            return db.getCollection(collection);
        }
        
        return null;
    }
}
