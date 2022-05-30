package com.example.tennis_app;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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
    private TextView modify_info;
    private TextView one_on_one;
    private TextView logout;
    private Switch alarm_switch;
    private ImageView user_image;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String uid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_d, container, false);
        name = v.findViewById(R.id.mypage_name);
        email = v.findViewById(R.id.mypage_email);
        modify_info = v.findViewById(R.id.modify_userInformation);
        one_on_one = v.findViewById(R.id.oneonone);
        logout = v.findViewById(R.id.button_logout);
        alarm_switch = v.findViewById(R.id.alarm_switch);
        user_image = v.findViewById(R.id.mypage_image);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = user.getUid();

        modify_info.setOnClickListener(this);
        one_on_one.setOnClickListener(this);
        logout.setOnClickListener(this);

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

        mDatabase.child("users").child(uid).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                email.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_userInformation:
                Intent intent1 = new Intent(getActivity(),EditInfo.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent1);
                break;

            case R.id.oneonone:
                Intent intent3 = new Intent(getActivity(),OneOnOne.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
                break;
            case R.id.button_logout:
                getActivity().finish();
                break;

        }

    }
}