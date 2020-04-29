package com.saachi.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saachi.whatsapp.Adapter.MessageAdapter;
import com.saachi.whatsapp.Model.Chat;
import com.saachi.whatsapp.Model.User;
import com.saachi.chatapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    ImageButton btn_send;
    EditText text_send;
    String user_id;
    TextView username;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    Intent intent;

    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        });

        recyclerView=findViewById(R.id.recycler_view);
        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_sent);

        intent=getIntent();
        user_id=intent.getStringExtra("userid");
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg= text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), user_id, msg);
                }else{
                    Toast.makeText(getApplicationContext(), "You can't send empty message", Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                readMessages(firebaseUser.getUid(), user_id, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(user_id);

    }

    private void seenMessage(final String user_id){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener= reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(user_id)){
                        HashMap<String, Object> hashMap= new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference chatRef= FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(user_id);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(user_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void readMessages(final String myid, final String user_id, final String imageURL){
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(user_id)) || (chat.getSender().equals(myid) && chat.getReceiver().equals(user_id))){
                        mChat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this, mChat, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void status(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume(){
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause(){
        super.onPause();
        status("offline");
    }


}
