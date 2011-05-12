package me.l1k3.octobot.twitter;

public class StartupHook implements Runnable {

    @Override
    public void run() {
        OctoTwitter.start();
    }
}
