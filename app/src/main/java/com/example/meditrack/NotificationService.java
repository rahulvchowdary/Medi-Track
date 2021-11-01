package com.example.meditrack;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationService extends Service {
    private static final String URI_BASE = NotificationService.class.getName() + ".";
    public static final String UPDATE_ACTION = URI_BASE + "UPDATE_ACTION";
    private static String alarmId = "";
    private String username = "";
    ArrayList<String> medNames;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String id = intent.getStringExtra(Constants.EXTRA_TIMING_INTENT_DATA);
        String action = intent.getAction();
        System.out.println("action:"+action+" "+id);
        if(UPDATE_ACTION.equals(action)){
            System.out.println(medNames);
            Toast.makeText(getApplicationContext(), "Alarm:"+alarmId, Toast.LENGTH_SHORT).show();

            for(int i=0; i<medNames.size(); i++){
                AppConfig.rootRef.child(username).child("Medicines").child(medNames.get(i))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                MedHelperClass med = snapshot.getValue(MedHelperClass.class);
                                int perTime = Integer.parseInt(med.getMedTimes());
                                int stock = Integer.parseInt(med.getMedStock()) - perTime;
                                med.setMedStock(String.valueOf(stock));
                                AppConfig.rootRef.child(username).child("Medicines").child(med.getMedName()).setValue(med);
                                stopServiceNow();

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

        }else{
            System.out.println("Alarm id:"+id);
            alarmId = id;
            medNames = new ArrayList<>();
            updateStockInFirebase();
        }

        return START_NOT_STICKY;
    }

    private void updateStockInFirebase(){
        username = new SharedPreferenceManager().getUsername(getApplicationContext());
        AppConfig.rootRef.child(username).child("Alarms").child(alarmId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snaps : snapshot.getChildren()){
                            medNames.add(snaps.getValue(String.class));
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void stopServiceNow(){
        Intent i = new Intent(this, NotificationService.class);
        stopService(i);

        // Canceling the current notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
