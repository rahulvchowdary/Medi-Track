package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    public int max = 0;

    Button loginButton;
    TextView welcomeTextView;
    TextView registerWPTextView;
    TextView questionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        questionTextView = findViewById(R.id.questionTextView);


        loginButton = findViewById(R.id.loginWPButton);
        registerWPTextView = findViewById(R.id.registerWPTextView);

        String username = new SharedPreferenceManager().getUsername(getApplicationContext());
        String name = new SharedPreferenceManager().getName(getApplicationContext());

        Boolean isUserLoggedIn;
        if(username.equals("")){
            isUserLoggedIn = false;
        }
        else{
            isUserLoggedIn = true;
        }


        if (!isUserLoggedIn) {
            loginButton.setText("Login");
            questionTextView.setText("You are not a member?");
            registerWPTextView.setText("Register");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            registerWPTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            loginButton.setText("Continue");
            questionTextView.setText("Want to Logout?");
            registerWPTextView.setText("Logout");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Medicine").child(username);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Integer.parseInt(dataSnapshot.child("medNumPerDay").getValue(String.class)) > max){
                            max = Integer.parseInt(dataSnapshot.child("medNumPerDay").getValue(String.class));
                        }
                    }
                    setAlarm();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MedListActivity.class);
                    MedListActivity.medicinesList.clear();
                    intent.putExtra("Username", username);
                    intent.putExtra("Name", name);
                    startActivity(intent);
                }
            });

            registerWPTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SharedPreferenceManager().logoutUser(getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setAlarm(){
        if (max == 1) {

            //For the Only Notification at 8AM
            alarmMgr(8, 0);
        }
        else if (max == 2) {

            //For the First Notification at 8AM
            alarmMgr(8, 1);


            // For the Second Notification at 8PM
            alarmMgr(20, 2);
        }
        else if (max == 3) {

            //For the First Notification at 8AM
            alarmMgr(8, 3);

            // For the Second Notification at 2PM
            alarmMgr(14, 4);

            //For the Third Notification at 8PM
            alarmMgr(20, 5);
        }
    }

    private void alarmMgr(int hourOfDay, int reqCode){

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reqCode, intent , 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, 00);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}