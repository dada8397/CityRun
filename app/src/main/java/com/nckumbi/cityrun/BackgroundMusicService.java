package com.nckumbi.cityrun;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by user on 2016/6/15.
 */
public class BackgroundMusicService {
    private static Context context;
    private static int resourceId;
    private static boolean loop;
    private static boolean stopped;
    private static BackgroundMusicPlayer INSTANCE;

    static {
        stopped = true;
    }

    public static void start() {
        if (INSTANCE != null) {
            start(context, resourceId, loop);
        } else {
            stopped = true;
        }
    }

    public static void start(int resourceId, boolean loop) {
        if (context != null) {
            start(context, resourceId, loop);
        } else {
            stopped = true;
        }
    }

    public static void start(Context context, int resourceId, boolean loop) {
        if (INSTANCE == null) {
            INSTANCE = new BackgroundMusicPlayer(context, resourceId, loop);
            INSTANCE.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (BackgroundMusicService.resourceId != resourceId) {
            clean();

            INSTANCE = new BackgroundMusicPlayer(context, resourceId, loop);
            INSTANCE.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        BackgroundMusicService.context = context;
        BackgroundMusicService.resourceId = resourceId;
        BackgroundMusicService.loop = loop;
        BackgroundMusicService.stopped = false;

        INSTANCE.start();
    }

    public static void pause() {
        if (INSTANCE != null) {
            INSTANCE.pause();
        }

        stopped = true;
    }

    public static void stop() {
        if (INSTANCE != null) {
            INSTANCE.stop();
        }

        stopped = true;
    }

    public static void clean() {
        if (INSTANCE != null) {
            INSTANCE.stop();
            INSTANCE.cancel(false);

            INSTANCE = null;
        }

        stopped = true;
    }

    public static boolean isStopped() {
        return stopped;
    }
}
