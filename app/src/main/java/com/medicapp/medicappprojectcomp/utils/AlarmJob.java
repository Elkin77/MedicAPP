package com.medicapp.medicappprojectcomp.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.PrincipalPatientActivity;


public class AlarmJob extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Medicapp";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarma activada", Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon_menu)
                .setContentTitle("Recordatorio de medicina")
                .setContentText("Debes aplicar tu insulina")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Crear un intent para abrir la actividad principal al hacer clic en la notificación
        Intent mainIntent = new Intent(context, PrincipalPatientActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canal de recordatorios");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
