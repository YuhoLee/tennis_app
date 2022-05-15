package com.example.tennis_app;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class Adjust extends AppCompatActivity {
    private SwitchCompat sw;
    private Button topspin_btn;
    private Button slice_btn;
    private Button random;
    private Button custom1_btn;
    private Button custom2_btn;
    private Button custom3_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adjust);

    }
}
