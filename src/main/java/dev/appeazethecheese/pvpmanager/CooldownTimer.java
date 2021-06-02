package dev.appeazethecheese.pvpmanager;

import java.util.Timer;
import java.util.TimerTask;

public class CooldownTimer extends Timer {
    private boolean started = false;

    @Override
    public void schedule(TimerTask task, long delay) {
        super.schedule(new TimerTask() {
            @Override
            public void run() {
                started = false;
                if(task != null)
                    task.run();
            }
        }, delay);
        started = true;
    }

    @Override
    public void cancel() {
        super.cancel();
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
