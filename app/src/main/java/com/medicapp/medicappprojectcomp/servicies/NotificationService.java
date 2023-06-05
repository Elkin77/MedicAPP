package com.medicapp.medicappprojectcomp.servicies;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.activities.PrincipalPatientActivity;
import com.medicapp.medicappprojectcomp.models.Message;
import com.medicapp.medicappprojectcomp.utils.DatabaseRoutes;

public class NotificationService extends JobIntentService {
    private static final int JOB_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "1";

    private int notificationId = 0;
    private static NotificationChannel channel;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DatabaseRoutes.MESSAGES_PATH);
    Long serviceStartedAt = System.currentTimeMillis();

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channel == null) {
            channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notificacion de mensajes", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }



        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if(message.getStamp() > serviceStartedAt && !message.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_email)
                            .setContentTitle(String.format("%s dice:", message.getFrom()))
                            .setContentText(message.getMessage())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    Intent intent = new Intent(getApplicationContext(), PrincipalPatientActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    mBuilder.setContentIntent(pendingIntent);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    notificationManager.notify(notificationId++, mBuilder.build());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
    }
}


