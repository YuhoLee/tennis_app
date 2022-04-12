package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Fragment_D extends Fragment implements View.OnClickListener {

    private TextView name;
    private TextView email;
    private TextView mem_info;
    private TextView user_management;
    private TextView one_on_one;
    private TextView logout;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String uid;
    private String uname;
    private String uemail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_d, container, false);
        name = v.findViewById(R.id.menu_name);
        email = v.findViewById(R.id.menu_email);
        mem_info = v.findViewById(R.id.menu_mem_info);
        user_management = v.findViewById(R.id.menu_user_management);
        one_on_one = v.findViewById(R.id.menu_OneOnOne);
        logout = v.findViewById(R.id.menu_logout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = user.getUid();

        mDatabase.child("users").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                name.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mem_info.setOnClickListener(this);
        user_management.setOnClickListener(this);
        one_on_one.setOnClickListener(this);
        logout.setOnClickListener(this);






        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_mem_info:
                Intent intent1 = new Intent(getActivity(),EditInfo.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent1);
                break;
            case R.id.menu_user_management:
                Intent intent2 = new Intent(getActivity(),UserManagement.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
                break;
            case R.id.menu_OneOnOne:
                Intent intent3 = new Intent(getActivity(),OneOnOne.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
                break;
            case R.id.menu_logout:
                getActivity().finish();
                break;

        }

    }
}