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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class Register extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password1;
    private EditText password2;
    private EditText phone_number;
    private EditText year;
    private EditText month;
    private EditText day;
    private TextView pwCheck;
    private Button register;
    private FirebaseAuth mAuth;
    private String name_str;
    private String em;
    private String pw1;
    private String pw2;
    private String phone;
    private String y;
    private String m;
    private String d;
    private ImageView tf;
    private String uid;
    private  FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.signName);
        email = findViewById(R.id.signEmail);
        password1 = findViewById(R.id.signPassword1);
        password2 = findViewById(R.id.signPassword2);
        phone_number = findViewById(R.id.phoneNumber);
        year = findViewById(R.id.signYear);
        month = findViewById(R.id.signMonth);
        day = findViewById(R.id.signDay);
        register = findViewById(R.id.registerButton);
        pwCheck = findViewById(R.id.pwCheck);
        tf = findViewById(R.id.tfBox);
        em = email.getText().toString();
        pw1 = password1.getText().toString();
        pw2 = password2.getText().toString();
        name_str = name.getText().toString();
        phone = phone_number.getText().toString();
        y = year.getText().toString();
        m = month.getText().toString();
        d = day.getText().toString();
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
                    pwCheck.setText("비밀번호가 일치합니다!");
                }
                else{
                    tf.setImageResource(R.drawable.ic_baseline_cancel_24);
                    pwCheck.setText("비밀번호가 일치하지 않습니다.");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 조치
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
                    pwCheck.setText("비밀번호가 일치합니다!");
                }
                else{
                    tf.setImageResource(R.drawable.ic_baseline_cancel_24);
                    pwCheck.setText("비밀번호가 일치하지 않습니다.");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 조치
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "afterTextChanged: " + email.getText().toString());
                Log.i(TAG, "afterTextChanged: " + pw1);
                Log.i(TAG, "afterTextChanged: " + pw2);
                if((TextUtils.isEmpty(email.getText().toString())) || TextUtils.isEmpty(pw1) || TextUtils.isEmpty(pw2)) {
                    Toast.makeText(getApplicationContext(), "빠진 항목이 없는지 확인하세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (pw1.equals(pw2)) {
                        createAccount(email.getText().toString(), password1.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "비밀번호가 일치하는지 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "계정 생성 성공!",Toast.LENGTH_SHORT).show();
                            name_str = name.getText().toString();
                            phone = phone_number.getText().toString();
                            y = year.getText().toString();
                            m = month.getText().toString();
                            d = day.getText().toString();
                            User user = new User();
                            user.setName(name_str);
                            user.setPhone(phone);
                            user.setYear(y);
                            user.setMonth(m);
                            user.setDay(d);

                            final String uid = task.getResult().getUser().getUid();
                            databaseReference.child("users").child(uid).setValue(user);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "계정 생성 실패...",Toast.LENGTH_SHORT).show();
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
                            // 로그인에 성공하면 유저 정보와 함께 UI 업데이트
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            // 로그인에 실패했다면 실패 메세지 출력
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if(user != null){
            Toast.makeText(this,"로그인에 성공하였습니다!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.example.tennis_app.MainActivity.class));
        }
        else{
            Toast.makeText(this, "로그인에 실패하였습니다...", Toast.LENGTH_SHORT).show();
        }
    }
}
