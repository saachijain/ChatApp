package com.saachi.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saachi.chatapp.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText name = findViewById(R.id.name);
    EditText email = findViewById(R.id.register_email);
    EditText password = findViewById(R.id.password);
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    Toolbar toolbar = findViewById(R.id.toolbar);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set whether an activity title/subtitle should be displayed.


    }

    public void onClick(View v) {
        String txt_name, txt_email, txt_password;
            txt_name = name.getText().toString();
            txt_email = email.getText().toString();
            txt_password = password.getText().toString();

            if(txt_name.isEmpty()||txt_email.isEmpty()||txt_password.isEmpty())
            {
               Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
            }
            else if(txt_password.length()<6)
            {
                Toast.makeText(RegisterActivity.this, "Password must be 6 or more characters long", Toast.LENGTH_LONG).show();
            }
            else {
               register(txt_name,txt_email,txt_password);
            }
    }
//add users to database linked by Firebase
    public void register(final String name, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                           @Override
                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                               if(task.isSuccessful()) {
                                                   FirebaseUser firebaseUser=auth.getCurrentUser();
                                                   String user_id= firebaseUser.getUid();
                                                   //makes entry in database wrt user id of current user
                                                   reference= FirebaseDatabase.getInstance().getReference("Users").child(user_id);
                                                   //adding info in tabular form with key to identify each data point
                                                   HashMap<String, String> hashMap= new HashMap<>();
                                                   hashMap.put("id", user_id);
                                                   hashMap.put("name", name);
                                                   hashMap.put("status", "offline");
                                                   hashMap.put("search", name.toLowerCase());
                                                   reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful()){
                                                             Intent intent =new Intent(RegisterActivity.this, MainActivity.class);
                                                             //removes this activity entirely for this time's usage of app
                                                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                                             startActivity(intent);
                                                             finish();
                                                       }
                                                   }

                                               });
                                               } else {
                                                   Toast.makeText(RegisterActivity.this, "This mail/password cannot be used", Toast.LENGTH_LONG).show();
                                               }

                                           }
                                       }
                );

    }
}