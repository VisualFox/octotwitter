package me.l1k3.octobot.twitter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import me.l1k3.octobot.twitter.task.Task;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.urbanairship.octobot.Settings;

import twitter4j.Twitter;

public class OctoTwitter {

    private final static String KEY = "octotwitter";
    public final static String TASKS = "tasks";

    private static ArrayList<Task> tasks = new ArrayList<Task>();
    private static TwitterFactory factory = new TwitterFactory();
    
    private static Logger logger = Logger.getLogger("OctoTwitter");
    
    public static void start() {
        factory.start();
        
        String tasksList = Settings.get(KEY, TASKS);
        
        if(tasksList==null || tasksList.equals("")) {
            tasksList = "DirectMessageToList:DirectMessage:UpdateStatus";
        }
        
        StringTokenizer tokenizer = new StringTokenizer(tasksList, ":");

        while(tokenizer.hasMoreTokens()) {
            String t = tokenizer.nextToken();
            
            if(t.indexOf('.')==-1) {
                t = "me.l1k3.octobot.twitter.task."+t;
            }
            
            try {
                Object object = Class.forName(t).newInstance();
                
                if(object instanceof Task) {
                    tasks.add((Task)object);
                }
                
            } catch (Exception ex) {
                logger.error("Error loading task: " + ex.getMessage());
            }
        }
    }
    
    public static void stop() {
        factory.stop();
    }
    
    public static void run(JSONObject task) {
        
        Twitter twitter = factory.getInstance(task);
        
        for (Task t : tasks) {
            t.execute(twitter, task);
        }
    }
}
