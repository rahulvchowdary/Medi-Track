package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameLPEditText;
    private EditText passwordLPEditText;
    private Button loginLPButton;

    private TextView registerTextView;
    private ImageView backlpImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameLPEditText = findViewById(R.id.usernameLPEditText);
        passwordLPEditText = findViewById(R.id.passwordLPEditText);
        loginLPButton = findViewById(R.id.loginLPButton);

        registerTextView = findViewById(R.id.registerLPTextView);
        backlpImageView = findViewById(R.id.backLPImageView);

        loginLPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameLPEditText.getText().toString().trim();
                String password = passwordLPEditText.getText().toString().trim();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Empty Credentials", Toast.LENGTH_LONG).show();
                }
                else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(username).child("UserData");

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                String nameFromDB = snapshot.child("name").getValue(String.class);
                                String usernameFromDB = snapshot.child("username").getValue(String.class);
                                String passwordFromDB = snapshot.child("password").getValue(String.class);

                                if (password.equals(passwordFromDB)) {

                                    new SharedPreferenceManager().saveLoginDetails(getApplicationContext(), usernameFromDB , nameFromDB ,password);

                                    MedListActivity.medicinesList.clear();
                                    Intent intent = new Intent(getApplicationContext(), MedListActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
                                }

                                Log.d("UserSnap:",""+snapshot.getKey());
                            } else {
                                Toast.makeText(LoginActivity.this, "User doesn't exist", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        backlpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }
}