package com.example.meditrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    static String MTUsername;
    static String MTName;
    static String MTEmail;
    static String MTPassword;
    static String MTPhoneNo;
    static String MTAge;

    private EditText nameRPEditText;
    private EditText usernameRPEditText;
    private EditText emailRPEditText;
    private EditText passwordRPEditText;
    private EditText phoneNoRPEditText;
    private EditText ageRPEditText;

    private Button registerRPButton;
    private TextView loginRPTextView;
    private ImageView backRPImageView;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameRPEditText = findViewById(R.id.nameRPEditText);
        usernameRPEditText = findViewById(R.id.usernameRPEditText);
        emailRPEditText = findViewById(R.id.emailRPEditText);
        passwordRPEditText = findViewById(R.id.passwordRPEditText);
        phoneNoRPEditText = findViewById(R.id.phoneNoRPEditText);
        ageRPEditText = findViewById(R.id.ageRPTextView);
        registerRPButton = findViewById(R.id.registerRPButton);
        loginRPTextView = findViewById(R.id.loginRPTextView);
        backRPImageView = findViewById(R.id.backRPImageView);

        MTName = nameRPEditText.getText().toString();
        MTUsername = usernameRPEditText.getText().toString();
        MTEmail = emailRPEditText.getText().toString();
        MTPassword = passwordRPEditText.getText().toString();
        MTPhoneNo = phoneNoRPEditText.getText().toString();
        MTAge = ageRPEditText.getText().toString();

        registerRPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameRPEditText.getText().toString();
                String username = usernameRPEditText.getText().toString();
                String email = emailRPEditText.getText().toString();
                String password = passwordRPEditText.getText().toString();
                String phone = phoneNoRPEditText.getText().toString();
                String age = ageRPEditText.getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(age)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_LONG).show();
                }
                else if(!validateUsername(username)){
                    Toast.makeText(RegisterActivity.this, "Invalid Username", Toast.LENGTH_LONG).show();
                }
                //else if(!userExists(username)){
                //    Toast.makeText(RegisterActivity.this, "Username Already Taken", Toast.LENGTH_LONG).show();
                //}
                else if(!isValidEmail(email)){
                    Toast.makeText(RegisterActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                }
                else if(password.length() < 8 && !isValidPassword(password)){
                    Toast.makeText(RegisterActivity.this, "Invalid Password, Password must be atleast 8 characters long and must contain atleast 1 Alphabet, 1 Number and 1 Special Character ", Toast.LENGTH_LONG).show();
                }
                else if(phone.length() != 10){
                    Toast.makeText(RegisterActivity.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                }
                else if(age.length() > 3 && Integer.parseInt(age) < 18){
                    Toast.makeText(RegisterActivity.this, "Invalid Age", Toast.LENGTH_LONG).show();
                }
                else{
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    Query checkUser = reference.orderByChild("username").equalTo(username);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(RegisterActivity.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                registerUser(name, username, email, password, phone, age);

                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

        loginRPTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        backRPImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser(String name, String username, String email, String password, String phone, String age) {
        UserHelperClass userHelperClass = new UserHelperClass(name, username, email, password, phone, age);
        AppConfig.rootRef.child(username).child("UserData").setValue(userHelperClass);

    }

    private boolean validateUsername(final String username){

        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if(username.length()>=15){
            return false;
        }
        else if(!username.matches(noWhiteSpace)){
            return false;
        }
        else{
            return true;
        }

    }

    public static boolean isValidEmail(final String email){

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(final String password){

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.* [@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}