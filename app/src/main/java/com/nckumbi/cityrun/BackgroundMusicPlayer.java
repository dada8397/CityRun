package com.nckumbi.cityrun;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

/**
 * Created by user on 2016/6/6.
 *
 * Usage:
 *   You should override onResume and onPause method in activity.
 *
 * In activity class:
 *   BackgroundMusicPlayer player;
 *
 * In onResume:
 *   player = new BackgroundMusicPlayer(CONTEXT, RESOURCE_ID, IS_LOOP);
 *   player.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
 *
 * In onPause:
 *   player.cancel(true);
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

        while (true) {
            if (isCancelled()) {
                if (player.isPlaying()) {
                    player.stop();
                    player.release();
                }
                break;
            }
        }

        return null;
    }

    public void start() {
        if (player != null && !player.isPlaying()) {
            player.start();
        }
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public void stop() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }
}
