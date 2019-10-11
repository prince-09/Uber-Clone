package com.example.uber_clone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class customer_login_page extends Activity {
    Button login;
    EditText emailtext;
    EditText passwordtext;
    FirebaseAuth firebaseAuth;
    DatabaseReference CustomerDatabaseref;
    String onlineCustomerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login_page);
        login=(Button)findViewById(R.id.login);
        emailtext=(EditText)findViewById(R.id.emailtext);
        passwordtext=(EditText)findViewById(R.id.passworrdtext);
        firebaseAuth=FirebaseAuth.getInstance();
        onlineCustomerID=firebaseAuth.getCurrentUser().getUid();
        CustomerDatabaseref= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerID);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailtext.getText().toString().trim();
                String password=passwordtext.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(customer_login_page.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    CustomerDatabaseref.setValue(true);
                                    Intent driverintent=new Intent(getApplicationContext(),CustomerMapActivity.class);
                                    startActivity(driverintent);
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(customer_login_page.this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(customer_login_page.this,CustomerMapActivity.class);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(customer_login_page.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });

    }
}
