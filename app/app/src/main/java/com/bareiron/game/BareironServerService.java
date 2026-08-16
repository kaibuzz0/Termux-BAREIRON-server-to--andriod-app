package com.bareiron.game;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Owns the embedded BAREIRON native server while the user is hosting. */
public class BareironServerService extends Service {
    public static final String ACTION_START = "com.bareiron.game.START_SERVER";
    public static final String ACTION_STOP = "com.bareiron.game.STOP_SERVER";

    private static final String CHANNEL_ID = "bareiron_server";
    private static final int NOTIFICATION_ID = 25565;
    private static volatile boolean startRequested = false;
    private static volatile int lastExitCode = 0;

    private ExecutorService serverExecutor;

    public static boolean isRunning() {
        return startRequested || NativeBareiron.isRunning();
    }

    public static int getPlayerCount() {
        return NativeBareiron.isRunning() ? NativeBareiron.getPlayerCount() : 0;
    }

    public static int getLastExitCode() {
        return lastExitCode;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        serverExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? ACTION_START : intent.getAction();

        if (ACTION_STOP.equals(action)) {
            startRequested = false;
            NativeBareiron.requestStop();
            return START_NOT_STICKY;
        }

        if (isRunning()) {
            return START_STICKY;
        }

        startRequested = true;
        startForeground(NOTIFICATION_ID, buildNotification("Starting local server…"));

        serverExecutor.execute(() -> {
            int result;
            try {
                result = NativeBareiron.run(getFilesDir().getAbsolutePath());
            } catch (Throwable t) {
                result = -100;
            }

            lastExitCode = result;
            startRequested = false;
            stopForeground(true);
            stopSelf();
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        startRequested = false;
        if (NativeBareiron.isRunning()) {
            NativeBareiron.requestStop();
        }
        if (serverExecutor != null) {
            serverExecutor.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "BAREIRON server",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shown while this device is hosting a BAREIRON game server.");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String text) {
        Intent openApp = new Intent(this, MainMenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openApp,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent stopIntent = new Intent(this, BareironServerService.class).setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("BAREIRON server")
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(0, "Stop server", stopPendingIntent)
            .build();
    }
}
