package com.example.tennis_app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Array;

public class OneOnOne extends AppCompatActivity {
    private EditText title;
    private EditText content;
    private Button inquire;
    private Button back;
    private Spinner category;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String[] items = {"동작 오류", "기능 문제", "하드웨어 결함", "어플리케이션 오류", "기타"};
    private ArrayAdapter<String> adapter;
    private String type = "Null";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_on_one);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        inquire = findViewById(R.id.inquire);
        back = findViewById(R.id.back);
        category = findViewById(R.id.category);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        type = items[0];
        adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,items);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });

        inquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = user.getUid();
                Content c = new Content();
                c.setType(type);
                c.setEmail(user.getEmail());
                c.setTitle(title.getText().toString());
                c.setContent(content.getText().toString());
                databaseReference.child("inquiries").child(uid).setValue(c);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = items[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
