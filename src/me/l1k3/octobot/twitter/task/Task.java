package me.l1k3.octobot.twitter.task;

import org.json.simple.JSONObject;

import twitter4j.Twitter;

public interface Task {
    public void execute(Twitter twitter, JSONObject task);
}
