package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedListActivity extends AppCompatActivity {

    ListView medicinesDPListView;
    TextView nameDPTextView;
    TextView addMedTextView;
    ImageView backDPImageView;

    static ArrayList<String> medicinesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_list);

        medicinesList.clear();

        nameDPTextView = findViewById(R.id.nameDPTextView);
        addMedTextView = findViewById(R.id.addMedTextView);
        medicinesDPListView = findViewById(R.id.medicinesDPListView);
        backDPImageView = findViewById(R.id.backDPImageView);

        String usernameOfUser = new SharedPreferenceManager().getUsername(this);
        String nameOfUser = new SharedPreferenceManager().getName(this);

        nameDPTextView.setText("Hello, " + nameOfUser + "!");

        ArrayAdapter arrayAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, medicinesList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                TextView item = (TextView) super.getView(position,convertView,parent);

                item.setTypeface(item.getTypeface(), Typeface.BOLD);
                return item;
            }
        };

        medicinesDPListView.setAdapter(arrayAdapter);

        medicinesDPListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String MedicineName = medicinesList.get(position);

                Intent intent = new Intent(getApplicationContext(), MedDisplayActivity.class);
                intent.putExtra("MedicineName", MedicineName);
                startActivity(intent);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(usernameOfUser).child("Medicines");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    medicinesList.add(dataSnapshot.getKey());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addMedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedInsertActivity.class);
                startActivity(intent);
            }
        });

        backDPImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }
}