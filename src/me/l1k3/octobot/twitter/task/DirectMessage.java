package me.l1k3.octobot.twitter.task;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DirectMessage implements Task {

    public final static String MESSAGE = "message";
    public final static String RECIPIENT = "recipient";
    
    protected static Logger logger = Logger.getLogger("OctoTwitter");
    
    @Override
    public void execute(Twitter twitter, JSONObject task) {
        if(task.containsKey(RECIPIENT) && task.containsKey(MESSAGE)) {
            directMessage(twitter, (String)task.get(RECIPIENT), (String)task.get(MESSAGE));
            task.remove(MESSAGE);
            task.remove(RECIPIENT);
        }
    }

    protected static void directMessage(Twitter twitter, String recipient, String message) {
        
        if(twitter!=null) {
        
            try {
                twitter.sendDirectMessage(recipient, message);
            } catch (TwitterException te) {
                logger.warn(te.getMessage());
            } catch (IllegalStateException ie) {
                logger.error(ie.getMessage());
            }
        }
    }
}
