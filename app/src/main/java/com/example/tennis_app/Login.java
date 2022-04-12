package com.example.tennis_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email_txt;
    private EditText password_txt;
    private Button login_btn;
    private TextView forgot;
    private TextView register;

    private String email;
    private String password;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();
        email_txt = findViewById(R.id.email);
        password_txt = findViewById(R.id.password);
        login_btn = findViewById(R.id.loginButton);
        forgot = findViewById(R.id.registerClick);
        register = findViewById(R.id.registerClick);
        intent = new Intent(this, com.example.tennis_app.Register.class);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = email_txt.getText().toString();
                password = password_txt.getText().toString();
                if ((TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) == true) {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    signIn(email, password);
                }
            }
        });

        password_txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    login_btn.callOnClick();
                    return true;
                }
                return false;
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(intent);
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
                            email_txt.setText("");
                            password_txt.setText("");
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


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

}
