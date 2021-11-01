package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotiQuestionActivity extends AppCompatActivity {

    String medNumPerTime;
    String medStock;
    ArrayList<MedHelperClass> medList;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_question);

        String username = new SharedPreferenceManager().getUsername(this);
        System.out.println("Username - "+username);
        medList = new ArrayList<>();
        reference  = FirebaseDatabase.getInstance().getReference("Medicine").child(username);
        fetchMedListFromDb(username);

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Did you take your Medicine?")
                .setMessage("Only Click 'Yes' if you took the Medicine")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void fetchMedListFromDb(String username) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot meds : snapshot.getChildren()){
                        medList.add(meds.getValue(MedHelperClass.class));
                    }

                }

                ArrayList<MedHelperClass> updatedList = updateMedDetails(medList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FetchMed:",""+error.getMessage());
            }
        });
    }

    /**
     * This method updates the med take count when user taps yes in alert dialog
     * @param medList Holds the list of meds
     * @return returns the updated list
     */
    private ArrayList<MedHelperClass> updateMedDetails(ArrayList<MedHelperClass> medList) {

        // before the update

//        for(int i =0; i<medList.size(); i++){
//            System.out.println("Med name:"+medList.get(i)+" Med count:"+medList.get(i).getMedNumPerDay());
//        }

//        // update med stock
//        for(int i = 0; i<medList.size(); i++){
//            int medStock = Integer.parseInt(medList.get(i).getStock());
//            if(medTakeCount != 0){
//                medTakeCount = medTakeCount - Integer.parseInt(medList.get(i).getMedNumPerTime());
//                medList.get(i).setMedNumPerDay(String.valueOf(medTakeCount));
//            }
//        }
        // after updating the med count
//        for(int i = 0 ; i<medList.size(); i++){
//            System.out.println("Med name:"+medList.get(i).getMedName()+" Med count:"+medList.get(i).getMedNumPerDay());
//        }

        // Send updated data to firebase
//        for(int i = 0; i<medList.size(); i++){
//            reference = reference.child(medList.get(i).getMedName());
//            reference = reference.child("medNumPerDay");
//            reference.setValue(medList.get(i).getMedNumPerDay());
//        }

        return medList;
    }
}