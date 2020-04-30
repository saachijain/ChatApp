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
import com.saachi.whatsapp.R;

public class LoginActivity extends AppCompatActivity {

    EditText email=findViewById(R.id.email);
    EditText password=findViewById(R.id.password);
    Toolbar toolbar = findViewById(R.id.toolbar);
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set whether an activity title/subtitle should be displayed.
    }

    public void login(View v){
        String txt_email=email.getText().toString();
        String txt_password=password.getText().toString();
        if(txt_email.isEmpty()||txt_password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please fill all the details", Toast.LENGTH_LONG).show();
        }else {
            auth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                    }
                }
            });

        }

    }
    public void resetPassword(View v){
        Intent intent=new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }
}
