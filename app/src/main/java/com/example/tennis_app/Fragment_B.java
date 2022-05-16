package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class Fragment_B extends Fragment{
    private SwitchCompat sw;
    private Button topspin_btn;
    private Button slice_btn;
    private Button random;
    private Button custom1_btn;
    private Button custom2_btn;
    private Button custom3_btn;
    private SeekBar top_seekbar;
    private SeekBar btm_seekbar;
    private SeekBar speed_seekbar;
    private SeekBar cycle_seekbar;
    private EditText top_edit;
    private EditText btm_edit;
    private EditText speed_edit;
    private EditText cycle_edit;
    private String top_data;
    private String btm_data;
    private String speed_data;
    private String cycle_data;

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
        View v = inflater.inflate(R.layout.adjust, container, false);
        topspin_btn = v.findViewById(R.id.topspin_btn);
        slice_btn = v.findViewById(R.id.slice_btn);
        random = v.findViewById(R.id.random_btn);
        custom1_btn = v.findViewById(R.id.custom1_btn);
        custom2_btn = v.findViewById(R.id.custom2_btn);
        custom3_btn = v.findViewById(R.id.custom3_btn);
        top_seekbar = v.findViewById(R.id.top_motor_seekBar);
        btm_seekbar = v.findViewById(R.id.btm_motor_seekBar);
        speed_seekbar = v.findViewById(R.id.speed_seekBar);
        cycle_seekbar = v.findViewById(R.id.cycle_seekBar);
        top_edit = v.findViewById(R.id.top_motor_edit);
        btm_edit = v.findViewById(R.id.btm_motor_edit);
        speed_edit = v.findViewById(R.id.speed_edit);
        cycle_edit = v.findViewById(R.id.cycle_edit);

        top_edit.setText("0");
        btm_edit.setText("0");
        speed_edit.setText("0");
        cycle_edit.setText("0");

        remoteSeekbar(top_seekbar, top_edit);
        remoteSeekbar(btm_seekbar, btm_edit);
        remoteSeekbar(speed_seekbar, speed_edit);
        remoteSeekbar(cycle_seekbar, cycle_edit);


        return v;
    }


    public void remoteSeekbar(SeekBar seekBarComponent, EditText editText){
        seekBarComponent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editText.setText(String.valueOf(i));
                top_data = top_edit.getText().toString();
                btm_data = btm_edit.getText().toString();
                speed_data = speed_edit.getText().toString();
                cycle_data = cycle_edit.getText().toString();
                String data = "<" + top_data + "," + btm_data + "," + speed_data + "," + cycle_data + ">";

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void sendData(){

    }


}