
package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedDisplayActivity extends AppCompatActivity {

    String medNameFromDb;
    int medNumPerDayFromDb;
    String medNumPerTimeFromDb;
    String medStockFromDb;

    String medName;

    TextView medNameMDPTextView;
    TextView daysLeftMDPTextView;
    TextView stockMDPTextView;
    TextView messageMDPTextView;

    Button removeMDPButton;
    Button checkMDPButton;
    Button addStockMDPButton;

    ImageView backMDPImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_display);

        medNameMDPTextView = findViewById(R.id.medNameMDPTextView);
        daysLeftMDPTextView = findViewById(R.id.daysLeftMDPTextView);
        stockMDPTextView = findViewById(R.id.stockMDPTextView);
        messageMDPTextView = findViewById(R.id.messageMDPTextView);
        backMDPImageView = findViewById(R.id.backMDPImageView);
        removeMDPButton = findViewById(R.id.removeMDPButton);
        checkMDPButton = findViewById(R.id.checkMDPButton);
        addStockMDPButton = findViewById(R.id.addStockMDPButton);

        String username = new SharedPreferenceManager().getUsername(getApplicationContext());

        Intent intent = getIntent();
        medName = intent.getStringExtra("MedicineName");

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
                    int medStockFromDbInt = Integer.parseInt(medStockFromDb);

                    int daysLeft = medStockFromDbInt / (medNumPerDayFromDb * medNumPerTimeFromDbInt);

                    String prefixForDays = getColoredSpanned("might last for", "#253A48");
                    String suffixForDays = getColoredSpanned("more days," , "#253A48");
                    String daysTextView = getColoredSpanned(Integer.toString(daysLeft), "#F23B5F");

                    String prefixForStock = getColoredSpanned("since you have", "#253A48");
                    String suffixForStock = getColoredSpanned("medicine left." , "#253A48");
                    String stockTextView = getColoredSpanned(medStockFromDb, "#F23B5F");

                    medNameMDPTextView.setText("The Medicine\n" + medNameFromDb + "\nyou use,");
                    daysLeftMDPTextView.setText(Html.fromHtml(prefixForDays + " " + daysTextView + " " + suffixForDays));
                    stockMDPTextView.setText(Html.fromHtml(prefixForStock + " " + stockTextView + " " + suffixForStock));

                    if(daysLeft > 5){
                        messageMDPTextView.setText("Click the below button to check the nearest Pharmacy.");
                    }
                    else if(daysLeft < 6 && daysLeft >= 0){
                        messageMDPTextView.setText("Click the below button to check the nearest Pharmacy, since you only have stock for " + medStockFromDb + " days.");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        removeMDPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(username);
                reference.child("Medicines").child(medNameFromDb).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MedDisplayActivity.this, "Medicine Deleted", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MedListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MedDisplayActivity.this, "Medicine Not Deleted, Try again later!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                for(int i=1; i<=3; i++) {
                    int intAlarmId = i;
                    reference.child("Alarms").child(String.valueOf(intAlarmId)).equalTo(medName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        addStockMDPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AddStockActivity.class);
                intent.putExtra("MedicineName", medName);
                startActivity(intent);

                // COULD ALSO BE DONE USING AN ALERT DIALOG, BUT DIDN'T LOOK REALLY GREAT
                /*AlertDialog.Builder builder = new AlertDialog.Builder(MedDisplayActivity.this);
                builder.setTitle("Add Stock");

                final EditText input = new EditText(getApplicationContext());

                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addStock = Integer.parseInt(input.getText().toString());
                        Toast.makeText(MedDisplayActivity.this, "" + addStock, Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                */
            }
        });

        backMDPImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MedListActivity.class);
                MedListActivity.medicinesList.clear();
                startActivity(intent);
            }
        });

        checkMDPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}