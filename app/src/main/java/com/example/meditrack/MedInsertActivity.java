package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedInsertActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private EditText medNameMIPEditText;
    private CheckBox checkBoxMorning, checkBoxNoon, checkBoxNight;
    private EditText stockMIPEditText;
    private EditText numPerTimeEditText;
    private Button confirmMIPButton;

    ImageView backMIPImageView;

    ArrayList<String> alarmList;

    DatabaseReference refUsers, refMeds, refAlarms;

    private String medName, medStock, medsPerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_insert);
        initViews();
        initEventListeners();

        backMIPImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initEventListeners() {
        checkBoxMorning.setOnCheckedChangeListener(this);
        checkBoxNoon.setOnCheckedChangeListener(this);
        checkBoxNight.setOnCheckedChangeListener(this);

        confirmMIPButton.setOnClickListener(this);

    }

    private void saveDataToFirebase() {
       medName = medNameMIPEditText.getText().toString();
       medStock = stockMIPEditText.getText().toString();
       medsPerTime = numPerTimeEditText.getText().toString();

       MedHelperClass medHelper = new MedHelperClass();
       medHelper.setMedName(medName);
       medHelper.setMedStock(medStock);
       medHelper.setMedTimes(medsPerTime);
       medHelper.setMedAlarms(alarmList);

       String username = new SharedPreferenceManager().getUsername(getApplicationContext());

       AppConfig.rootRef.child(username).child("Medicines").child(medName).setValue(medHelper);

       for(int i=0; i<3; i++){

           if(alarmList.get(i).equals("0")) {
               continue;
           }
           AppConfig.rootRef.child(username).child("Alarms").
                   child(alarmList.get(i)).child(medName).setValue(medName);
       }
    }

    private void initViews() {
        medNameMIPEditText = findViewById(R.id.medNameMIPEditText);
        numPerTimeEditText = findViewById(R.id.numPerTimeEditText);
        stockMIPEditText = findViewById(R.id.stockMIPEditText);
        backMIPImageView= findViewById(R.id.backMIPImageView);

        checkBoxMorning = findViewById(R.id.checkBoxMorning);
        checkBoxNoon = findViewById(R.id.checkBoxNoon);
        checkBoxNight = findViewById(R.id.checkBoxNight);

        confirmMIPButton = findViewById(R.id.confirmMIPButton);

        alarmList = new ArrayList<>();
        for(int i = 0; i<3; i++){
            alarmList.add("0");
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.confirmMIPButton:
                saveDataToFirebase();
                Intent intent = new Intent(getApplicationContext(), MedListActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkBoxMorning:
                if(isChecked){
                    alarmList.set(0,"1");
                }else{
                    alarmList.set(0,"0");
                }
                break;


            case R.id.checkBoxNoon:
                if(isChecked){
                    alarmList.set(1,"2");
                }else{
                    alarmList.set(1,"0");
                }
                break;

            case R.id.checkBoxNight:
                if(isChecked){
                    alarmList.set(2,"3");
                }else{
                    alarmList.set(2,"0");
                }
                break;
        }
    }
}