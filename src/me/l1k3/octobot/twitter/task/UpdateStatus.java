package me.l1k3.octobot.twitter.task;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class UpdateStatus implements Task {

    public final static String MESSAGE = "message";
    
    protected static Logger logger = Logger.getLogger("OctoTwitter");
    
    @Override
    public void execute(Twitter twitter, JSONObject task) {
        if(task.containsKey(MESSAGE)) {
            updateStatus(twitter, (String)task.get(MESSAGE));
            task.remove(MESSAGE);
        }
    }
    
    protected void updateStatus(Twitter twitter, String message) {
        
        if(twitter!=null) {
            
            try {
                twitter.updateStatus(message);
            } catch (TwitterException te) {
                logger.warn(te.getMessage());
            } catch (IllegalStateException ie) {
                logger.error(ie.getMessage());
            }
        }
    }
}
