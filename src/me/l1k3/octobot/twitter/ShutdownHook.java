package me.l1k3.octobot.twitter;

public class ShutdownHook implements Runnable {

    @Override
    public void run() {
        OctoTwitter.stop();
    }
}
