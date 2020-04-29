package com.saachi.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saachi.chatapp.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        //checks whether a user is currently using the application or not. If not, allows the functioning to continue
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent= new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void toLogin(View v){
        startActivity((new Intent(StartActivity.this, LoginActivity.class)));
    }

    public void toRegister(View v){
        startActivity((new Intent(StartActivity.this, RegisterActivity.class)));
    }
}
