package com.example.uber_clone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class driver_login_page extends Activity {
    Button login;
    EditText emailtext;
    EditText passwordtext;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_login_page);
        login=(Button)findViewById(R.id.register_btn);
        emailtext=(EditText)findViewById(R.id.driver_emailtext);
        passwordtext=(EditText)findViewById(R.id.driver_passwordtext);
        firebaseAuth=FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailtext.getText().toString().trim();
                String password=passwordtext.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(driver_login_page.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(driver_login_page.this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(driver_login_page.this,driver_MapActivity.class);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(driver_login_page.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });
    }
}
