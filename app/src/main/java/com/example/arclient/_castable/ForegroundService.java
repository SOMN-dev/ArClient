package com.example.arclient._castable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.arclient.Castable;
import com.example.arclient.MainActivity;



public class ForegroundService extends Service implements Castable {

    private static final String TAG = "Castable:ForegroundService";
    private static final String CHANNEL_ID = "Projection";
    private static final int FOREGROUND_SERVICE_ID = 1000;

    @Override
    public void cast(Object... obj) { }

    @Override
    public void launch(Object... obj) {

        Context _context = (Context) obj[0];
        Intent _intent = new Intent(_context, ForegroundService.class);
        _context.startService( _intent );

    }

    public void stop(Context _context) {

        Intent _intent = new Intent(_context, ForegroundService.class);
        _context.stopService( _intent );

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        _foreground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void _foreground() {

        NotificationChannel serviceChannel;
        Notification notification;
        PendingIntent pendingIntent;
        Intent notificationIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            serviceChannel = new NotificationChannel(

                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(serviceChannel);

        }
        notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(

                this,
                0,
                notificationIntent,
                0
        );
        notification = ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this))
                .setContentTitle("Foreground Service")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(FOREGROUND_SERVICE_ID, notification);

    }

}