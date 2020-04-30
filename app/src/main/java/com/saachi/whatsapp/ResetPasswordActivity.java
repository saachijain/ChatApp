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
import com.google.firebase.auth.FirebaseAuth;
import com.saachi.whatsapp.R;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText email=findViewById(R.id.send_email);
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Toolbar toolbar = findViewById(R.id.toolbar);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set whether an activity title/subtitle should be displayed
    }

    public void changePassword(View v){
        String txt_email=email.getText().toString();
        if(txt_email.isEmpty()){
            Toast.makeText(ResetPasswordActivity.this, "Please enter email", Toast.LENGTH_LONG).show();
        }
        else{
            firebaseAuth.sendPasswordResetEmail(txt_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Please check your email", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(ResetPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    else{
                        String error= task.getException().getMessage();//displaying error
                        Toast.makeText(ResetPasswordActivity.this,error,Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
}
