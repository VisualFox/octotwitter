package me.l1k3.octobot.twitter.task;

import org.json.simple.JSONObject;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class DirectMessageToListUser extends DirectMessage {

    public final static String RECIPIENT_LIST_ID = "recipient_list_id";
    
    @Override
    public void execute(Twitter twitter, JSONObject task) {
        
      //Send a direct message to all member of a given list
        if(task.containsKey(RECIPIENT_LIST_ID)  && task.containsKey(MESSAGE)) {
            
            String message = (String)task.get(MESSAGE);
            int listId = Integer.parseInt((String)task.get(RECIPIENT_LIST_ID));
            
            try {
                long cursor = -1;
                PagableResponseList<User> users;
                do {
                    users = twitter.getUserListMembers(twitter.getScreenName(), listId, cursor);
                    for (User user : users) {
                        directMessage(twitter, user.getScreenName(), message);
                    }
                } 
                while ((cursor = users.getNextCursor()) != 0);
                
            } catch (TwitterException te) {
                logger.warn("Failed to get list members: " + te.getMessage());
            }
            
            task.remove(MESSAGE);
            task.remove(RECIPIENT_LIST_ID);
        }
    }
}
