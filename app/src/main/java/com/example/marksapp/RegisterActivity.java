package com.example.marksapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.model.PlaceType;
import com.google.maps.model.Unit;

public class RegisterActivity extends AppCompatActivity {

    public TextView loginoption;
    public EditText emailEditText, passwordEditText, passwordcheckEditText;
    public Button btnRegister;
    public ProgressBar progressBar;

    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mAuth  = FirebaseAuth.getInstance(); //need firebase authentication instance


        loginoption = (TextView) findViewById(R.id.loginOption);
        emailEditText = (EditText) findViewById(R.id.usernameEditTxt);
        passwordEditText = (EditText) findViewById(R.id.passwordEditTxt);
        passwordcheckEditText = (EditText) findViewById(R.id.passwordEditTxt2);
        btnRegister = (Button) findViewById(R.id.regBtn);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registerUser();
            }
        });

        loginoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();

        String password = passwordEditText.getText().toString().trim();

        String password2 = passwordcheckEditText.getText().toString().trim();


        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length()<6)
        {
            Toast.makeText(getApplicationContext(), "Enter a longer password", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }


        // create new user or register new user (Firebase, 2022) https://firebase.google.com/docs/auth/android/start
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    //public Users(String userMeasurement,  double userTotalDistance, PlaceType pref){
                    Users users = new Users(Unit.METRIC, 0, PlaceType.RESTAURANT);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    Log.d("123456", "onComplete: " + mAuth.getUid());
                    // How to write to firebase (Firebase, 2022) https://firebase.google.com/docs/database/android/read-and-write
                    reference.child(firebaseUser.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                //if the user created intent to login activity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);startActivity(intent);
                            }

                        }
                    });

                }
                else {
                    // Registration failed
                    //FirebaseAuthException e = (FirebaseAuthException )task.getException();
                    Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
