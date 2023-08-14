package dev.appeazethecheese.pvpmanager.Cooldown;

import dev.appeazethecheese.pvpmanager.Main;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class CooldownTimer {
    private boolean started = false;
    private int secondsElapsed = 0;
    private final int totalSeconds;
    private final TimerTask timerTask;
    private Timer internalTimer;

    public CooldownTimer(TimerTask task, int seconds){
        this.timerTask = task;
        this.totalSeconds = seconds;
    }

    public void start() {
        internalTimer = new Timer();
        schedule(timerTask);
    }

    public void stop() {
        internalTimer.cancel();
        this.started = false;
    }

    private void schedule(TimerTask task) {
        internalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                secondsElapsed++;

                if(secondsElapsed >= totalSeconds){
                    started = false;
                    internalTimer.cancel();
                    if(task != null)
                        task.run();
                }
            }
        }, 0, 1000L);
        started = true;
    }

    public int getSecondsRemaining(){
        return totalSeconds - secondsElapsed;
    }

    public boolean isStarted() {
        return started;
    }
}
