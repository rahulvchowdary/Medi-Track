package com.example.meditrack;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AppConfig extends Application {
    public static final String CHANNEL_ID = "MyChannel";
    public static DatabaseReference rootRef;

    @Override
    public void onCreate() {
        super.onCreate();
        rootRef = FirebaseDatabase.getInstance().getReference("Users");
        initiateAlarms();
        createTaskNotificationChannel();
    }

    private void initiateAlarms() {
        Calendar day = setCalendar(8, 00);
        Calendar afternoon = setCalendar(14, 00);
        Calendar night = setCalendar(20, 00);
        Calendar checkStock = setCalendar(00, 00);
        // for day
        AlarmManager dayAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent dayIntent = new Intent(this, NotificationReceiver.class);
        dayIntent.putExtra(Constants.EXTRA_TIMING_INTENT_DATA, "1");
        PendingIntent dayPendingIntent = getPendingIntent(this, 1, dayIntent, 0);
        dayAlarm.setExact(AlarmManager.RTC_WAKEUP, day.getTimeInMillis(), dayPendingIntent);
        //for afternoon
        AlarmManager noonAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent noonIntent = new Intent(this, NotificationReceiver.class);
        noonIntent.putExtra(Constants.EXTRA_TIMING_INTENT_DATA, "2");
        PendingIntent noonPendingIntent = getPendingIntent(this, 2, noonIntent, 0);
        noonAlarm.setExact(AlarmManager.RTC_WAKEUP, afternoon.getTimeInMillis(), noonPendingIntent);
        // for night
        AlarmManager nightAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent nightIntent = new Intent(this, NotificationReceiver.class);
        nightIntent.putExtra(Constants.EXTRA_TIMING_INTENT_DATA, "3");
        PendingIntent nightPendingIntent = getPendingIntent(this, 3, nightIntent, 0);
        nightAlarm.setExact(AlarmManager.RTC_WAKEUP, night.getTimeInMillis(), nightPendingIntent);
        // for checking stock
        // for night
        AlarmManager stockUpdateAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent stockIntent = new Intent(this, NotificationReceiver.class);
        stockIntent.putExtra(Constants.EXTRA_STOCK_CHECK_DATA, "4");
        PendingIntent stockPendingIntent = getPendingIntent(this, 4, nightIntent, 0);
        stockUpdateAlarm.setExact(AlarmManager.RTC_WAKEUP, checkStock.getTimeInMillis(), stockPendingIntent);
    }

    private PendingIntent getPendingIntent(Context context, int _rcode, Intent intent, int flag){
        return PendingIntent.getBroadcast(
                context,
                _rcode,
                intent,
                flag
        );
    }

    private Calendar setCalendar( int hour, int min){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private void createTaskNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setDescription("Take your Medicine");
            notificationChannel.setName("Medi-Track");
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
