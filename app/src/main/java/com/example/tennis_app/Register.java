package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    private EditText up_name;
    private EditText up_email;
    private EditText password1;
    private EditText password2;
    private EditText phone_number;
    private EditText ymd_et;
    private TextView pwCheck;
    private Button register;
    private Button email_check;
    private FirebaseAuth mAuth;
    private String name_str;
    private String em;
    private String pw1;
    private String pw2;
    private String phone;
    private String ymd;
    private String y;
    private String m;
    private String d;
    private ImageView tf;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();
        up_name = findViewById(R.id.signName);
        up_email = findViewById(R.id.signEmail);
        password1 = findViewById(R.id.signPassword1);
        password2 = findViewById(R.id.signPassword2);
        phone_number = findViewById(R.id.phoneNumber);
        ymd_et = findViewById(R.id.signYMD);
        register = findViewById(R.id.registerButton);
        email_check = findViewById(R.id.email_check);
        pwCheck = findViewById(R.id.pwCheck);
        tf = findViewById(R.id.tfBox);
        em = up_email.getText().toString();
        pw1 = password1.getText().toString();
        pw2 = password2.getText().toString();
        name_str = up_name.getText().toString();
        phone = phone_number.getText().toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();




        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pw1 = password1.getText().toString();
                pw2 = password2.getText().toString();
                Log.i(TAG, "afterTextChanged: " + pw1);
                Log.i(TAG, "afterTextChanged: " + pw2);
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                if(pw1.equals(pw2)){
                    tf.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    pwCheck.setText("??????????????? ???????????????!");
                }
                else{
                    tf.setImageResource(R.drawable.ic_baseline_cancel_24);
                    pwCheck.setText("??????????????? ???????????? ????????????.");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ???????????? ?????? ??????
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pw1 = password1.getText().toString();
                pw2 = password2.getText().toString();
                Log.i(TAG, "afterTextChanged: " + pw1);
                Log.i(TAG, "afterTextChanged: " + pw2);

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                if(pw1.equals(pw2)){
                    tf.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    pwCheck.setText("??????????????? ???????????????!");
                }
                else{
                    tf.setImageResource(R.drawable.ic_baseline_cancel_24);
                    pwCheck.setText("??????????????? ???????????? ????????????.");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ???????????? ?????? ??????
            }
        });

        email_check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                checkIdDuplicate();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "afterTextChanged: " + up_email.getText().toString());
                Log.i(TAG, "afterTextChanged: " + pw1);
                Log.i(TAG, "afterTextChanged: " + pw2);
                if((TextUtils.isEmpty(up_email.getText().toString())) || TextUtils.isEmpty(pw1) || TextUtils.isEmpty(pw2)) {
                    Toast.makeText(getApplicationContext(), "?????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (pw1.equals(pw2)) {
                        createAccount(up_email.getText().toString(), password1.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "??????????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkIdDuplicate(){
        userReference = databaseReference.child("users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue().toString();    //???????????? ??????
                    int vidx = value.indexOf("email") + 6;
                    String firebaseEmail = value.substring(vidx,value.length()-1);
                    em = up_email.getText().toString();
                    if(em.equals(firebaseEmail)){
                        Toast.makeText(getApplicationContext(), "???????????? ???????????????! ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "???????????? ????????? ??? ????????????! ", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "?????? ?????? ??????!",Toast.LENGTH_SHORT).show();
                            name_str = up_name.getText().toString();
                            phone = phone_number.getText().toString();
                            ymd = ymd_et.getText().toString();
                            y = ymd.substring(0,2);
                            m = ymd.substring(2,4);
                            d = ymd.substring(4);
                            em = up_email.getText().toString();
                            User user = new User();
                            user.setEmail(em);
                            user.setName(name_str);
                            user.setPhone(phone);
                            user.setYear(y);
                            user.setMonth(m);
                            user.setDay(d);
                            user.setCustom1("Null");
                            user.setCustom2("Null");
                            user.setCustom3("Null");

                            final String uid = task.getResult().getUser().getUid();
                            databaseReference.child("users").child(uid).setValue(user);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "?????? ?????? ??????...",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if((task.isSuccessful())){
                            // ???????????? ???????????? ?????? ????????? ?????? UI ????????????
                            Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            // ???????????? ??????????????? ?????? ????????? ??????
                            Toast.makeText(getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if(user != null){
            Toast.makeText(this,"???????????? ?????????????????????!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.example.tennis_app.MainActivity.class));
        }
        else{
            Toast.makeText(this, "???????????? ?????????????????????...", Toast.LENGTH_SHORT).show();
        }
    }
}
