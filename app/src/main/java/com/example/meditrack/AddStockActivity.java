package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddStockActivity extends AppCompatActivity {

    String medNameFromDb;
    int medNumPerDayFromDb;
    String medNumPerTimeFromDb;
    String medStockFromDb;

    TextView medNameASPTextView;
    TextView daysLeftASPTextView;
    TextView stockASPTextView;
    EditText addStockASPEditText;

    ImageView backASPImageView;

    Button addStockASPButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        medNameASPTextView = findViewById(R.id.medNameASPTextView);
        daysLeftASPTextView = findViewById(R.id.daysLeftASPTextView);
        stockASPTextView = findViewById(R.id.stockASPTextView);
        addStockASPEditText = findViewById(R.id.addStockASPEditText);
        addStockASPButton = findViewById(R.id.addStockASPButton);
        backASPImageView = findViewById(R.id.backASPImageView);

        String username = new SharedPreferenceManager().getUsername(getApplicationContext());

        Intent intent = getIntent();
        String medName = intent.getStringExtra("MedicineName");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(username).child("Medicines").child(medName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    medNumPerDayFromDb = 0;
                    for(int i=0; i<3; i++){
                        if(snapshot.child("MedAlarms").getValue() != "0"){
                            medNumPerDayFromDb++;
                        }
                    }

                    medNameFromDb = snapshot.child("medName").getValue(String.class);
                    medNumPerTimeFromDb = snapshot.child("medTimes").getValue(String.class);
                    medStockFromDb = snapshot.child("medStock").getValue(String.class);

                    int medNumPerTimeFromDbInt = Integer.parseInt(medNumPerTimeFromDb);
                    int stockFromDbInt = Integer.parseInt(medStockFromDb);

                    int daysLeft = stockFromDbInt / (medNumPerDayFromDb * medNumPerTimeFromDbInt);

                    String prefixForDays = getColoredSpanned("might last for", "#253A48");
                    String suffixForDays = getColoredSpanned("more days," , "#253A48");
                    String daysTextView = getColoredSpanned(Integer.toString(daysLeft), "#F23B5F");

                    String prefixForStock = getColoredSpanned("since you have", "#253A48");
                    String suffixForStock = getColoredSpanned("medicine left." , "#253A48");
                    String stockTextView = getColoredSpanned(medStockFromDb, "#F23B5F");

                    medNameASPTextView.setText("The Medicine\n" + medNameFromDb);
                    daysLeftASPTextView.setText(Html.fromHtml(prefixForDays + " " + daysTextView + " " + suffixForDays));
                    stockASPTextView.setText(Html.fromHtml(prefixForStock + " " + stockTextView + " " + suffixForStock));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStockASPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(username).child("Medicines").child(medName);

                String addStock = addStockASPEditText.getText().toString();

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            int finalStock = Integer.parseInt(medStockFromDb) + Integer.parseInt(addStock);
                            reference.child("medStock").setValue(Integer.toString(finalStock));

                            Intent intent = new Intent(getApplicationContext(), MedDisplayActivity.class);
                            intent.putExtra("MedicineName", medName);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        backASPImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedDisplayActivity.class);
                intent.putExtra("MedicineName", medName);
                startActivity(intent);
            }
        });
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}