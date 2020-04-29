package com.saachi.whatsapp.Fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.saachi.whatsapp.Adapter.UserAdapter;
import com.saachi.whatsapp.Model.User;
import com.saachi.chatapp.R;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    EditText search_users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      View view=inflater.inflate(R.layout.fragment_users, container, false);
      recyclerView=view.findViewById(R.id.recycler_view_users);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      mUsers=new ArrayList<>();
      search_users=view.findViewById(R.id.search_users);
      search_users.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              searchUsers(charSequence.toString().toLowerCase());

          }

          @Override
          public void afterTextChanged(Editable editable) {

          }
      });
        return view;
    }

    private void searchUsers(final String s){
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    assert user !=null;
                    assert fuser !=null;
                    if(!user.getId().equalsIgnoreCase(fuser.getUid())){
                        mUsers.add(user);
                    }
                    UserAdapter userAdapter=new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers(){
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                if(search_users.getText().toString().equals("")){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User user= snapshot.getValue(User.class);
                        assert user !=null;
                        assert fuser !=null;
                        if(!user.getId().equalsIgnoreCase(fuser.getUid())){
                            mUsers.add(user);
                        }
                    }
                    userAdapter =new UserAdapter(getContext(), mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}