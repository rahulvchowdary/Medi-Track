package com.example.meditrack;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationReceiver extends BroadcastReceiver {
    NotificationManagerCompat notificationManager;
    String alarmId;
    String stockAlarm;
    String _medicines = "";
    String userName = "";
    Intent stockIntent;
    String lowStockMeds= "";
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmId = intent.getStringExtra(Constants.EXTRA_TIMING_INTENT_DATA);
        stockAlarm = intent.getStringExtra(Constants.EXTRA_STOCK_CHECK_DATA);
        userName = new SharedPreferenceManager().getUsername(context);
        getFirebaseData(context, intent);
        getFirebaseStockData(context, intent);



    }

    private void getFirebaseStockData(Context context, Intent intent) {
        AppConfig.rootRef.child(userName).child("Medicines").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snaps : snapshot.getChildren()){
                            MedHelperClass meds = snaps.getValue(MedHelperClass.class);
                            int stock = Integer.parseInt(meds.getMedStock());
                            if(stock < 10){
                                lowStockMeds = lowStockMeds + meds.getMedName()+" ";
                            }
                        }

                        System.out.println("LowMeds:"+lowStockMeds);
                        if(!lowStockMeds.isEmpty()){
                            createNotificationForStock(context, intent);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createNotificationForStock(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentTitle("This meds are running out of stock")
                .setContentText(lowStockMeds)
                .setChannelId(AppConfig.CHANNEL_ID);
        Intent mapIntent = new Intent(context, MapsActivity.class);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(context, 123, mapIntent, 0  );
        notificationBuilder.setContentIntent(mapPendingIntent);
        notificationManager.notify(1, notificationBuilder.build());


    }

    private void createNotification(Context context, Intent intent, String _medicines) {
        notificationManager = NotificationManagerCompat.from(context);
        String alarmType = "";
        if(alarmId.equals(Constants.ALARM_DAY)){
            alarmType = "morning";
        }else if(alarmId.equals(Constants.ALARM_AFTERNOON)){
            alarmType = "afternoon";
        }else if(alarmId.equals(Constants.ALARM_NIGHT)){
            alarmType = "night";
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentTitle("Did you take these "+alarmType+" meds?")
                .setContentText(_medicines)
                .setChannelId(AppConfig.CHANNEL_ID);

        // update action intent
        Intent updateIntent = new Intent(context, NotificationService.class);
        updateIntent.setAction(NotificationService.UPDATE_ACTION);
        int i = Integer.parseInt(alarmId);
        PendingIntent updatePendingIntent = PendingIntent.getService(
                context,
                i,
                updateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        System.out.println(updateIntent.getAction());
        notificationBuilder.addAction(R.mipmap.ic_launcher,
                "YES", updatePendingIntent);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void getFirebaseData(Context context, Intent intent){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(userName).child("Alarms").child(alarmId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot meds : snapshot.getChildren()){
                    _medicines = _medicines + meds.getKey()+" ";
                }
                createNotification(context, intent, _medicines);
                stockIntent = new Intent(context, NotificationService.class);
                stockIntent.putExtra(Constants.EXTRA_TIMING_INTENT_DATA, alarmId);
                context.startService(stockIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

