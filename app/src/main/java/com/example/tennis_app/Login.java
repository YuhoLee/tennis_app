package com.example.tennis_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private CheckBox checkbox_auto_login;
    private CheckBox checkbox_saved_id;

    private String email;
    private String password;
    private Intent intent;

    private SharedPreferences auto_login;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        auto_login = getSharedPreferences("auto_login",0);
        editor = auto_login.edit();
        mAuth = FirebaseAuth.getInstance();
        email_txt = findViewById(R.id.email);
        password_txt = findViewById(R.id.password);
        login_btn = findViewById(R.id.loginButton);
        checkbox_auto_login = findViewById(R.id.checkBox_autologin);
        checkbox_saved_id = findViewById(R.id.checkBox_saveId);
        intent = new Intent(this, com.example.tennis_app.Register.class);




        checkbox_auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });

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

        if(auto_login.getBoolean("Auto_Login_enabled", false)){
            email_txt.setText(auto_login.getString("email", ""));
            password_txt.setText(auto_login.getString("password", ""));
            checkbox_auto_login.setChecked(true);
            login_btn.performClick();
        }


    }


    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if((task.isSuccessful())){
                            // 로그인에 성공하면 유저 정보와 함께 UI 업데이트
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                            if(checkbox_auto_login.isChecked()){
                                String EMAIL = email_txt.getText().toString();
                                String PASSWORD = password_txt.getText().toString();

                                editor.putString("email", EMAIL);
                                editor.putString("password", PASSWORD);
                                editor.putBoolean("Auto_Login_enabled", true);
                                editor.commit();
                            }
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
