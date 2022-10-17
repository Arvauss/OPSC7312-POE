package com.example.marksapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Declaration of variables (The IIE, 2022)
    EditText Password, Email;
    Button LoginButton;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Auth = FirebaseAuth.getInstance();

        setupUI();
        setupListener();

}
    private void setupUI() {
        Email = findViewById(R.id.usernameEditTxt);
        Password = findViewById(R.id.passwordEditTxt);
        LoginButton = findViewById(R.id.loginBtn);
        //"@+id/regOption"
    }
    private void setupListener(){
    LoginButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (CheckUsername()) {
                signIn(Email.getText().toString(), Password.getText().toString());
            } else {
                //prompt if user makes a wong input on password or username (The IIE, 2022)
                Toast t = Toast.makeText(getApplicationContext(), "Wrong Email or Password", Toast.LENGTH_SHORT);
                t.show();
            }

        }
    });
        }

    boolean isEmpty (EditText Username){
        CharSequence str = Username.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean CheckUsername() {

        //Method uses a boolean to check if the enter data is eiter true or false (The IIE, 2022)
        boolean isInputValid = true;

        //If statement used to show an error message if the username text is empty
        if (isEmail(Email)==false) {
            Email.setError("Enter a valid email!");
            isInputValid = false;
        }
        //If statement used to show an error message if the password text is empty
        if (isEmpty(Password)) {
            Password.setError("Enter password to login");
            isInputValid = false;
        } else {
            //Else if statement is used to check if the user password is longer than 4 characers
            if (Password.getText().toString().length() < 4) {
                Password.setError("Password must be longer than 4 characters");
                isInputValid = false;
            }
        }
        return isInputValid;
    }

    public void signIn (String email, String password) {
        Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            // Sign in success, update UI with the signed-in user's information

                            //FirebaseUser user = Auth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed. Credentials incorrect", Toast.LENGTH_SHORT).show();

                           // updateUI(null);
                        }
                    }
                });
    }
        private void updateUI (FirebaseUser user){

        }

    public void GoToRegister (View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    }



