package com.nckumbi.cityrun;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/6/6.
 *
 * Usage:
 *   You should override onResume and onPause method in activity.
 *
 *   In activity class:
 *     BackgroundMusicPlayer player;
 *
 *   In onResume:
 *     player = new BackgroundMusicPlayer(CONTEXT, RESOURCE_ID, IS_LOOP);
 *     player.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
 *
 *   In onPause:
 *     player.cancel(true);
 */
public class BackgroundMusicPlayer extends AsyncTask<Void, Void, Void> {
    private boolean loop;
    private int resourceId;

    private Context context;
    private MediaPlayer player;

    private float volume;

    BackgroundMusicPlayer(Context context, int resourceId, boolean loop) {
        this.context = context;
        this.resourceId = resourceId;
        this.loop = loop;

        this.volume = 1.0f;
    }

    @Override
    protected Void doInBackground(Void... params) {
        player = MediaPlayer.create(context, resourceId);

        player.setLooping(loop);
        player.setVolume(volume, volume);
        player.start();

        while(true) {
            if (isCancelled()) {
                final Timer timer = new Timer();

                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        volume -= 0.1f;
                        player.setVolume(volume, volume);

                        if (volume <= 0) {
                            player.stop();

                            timer.cancel();
                            timer.purge();
                        }
                    }
                };

                timer.schedule(timerTask, 100, 100);

                break;
            }
        }

        return null;
    }
}
