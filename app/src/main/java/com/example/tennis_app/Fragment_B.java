package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Fragment_B extends Fragment {
    private Button isBluetooth;
    private Button topspin_btn;
    private Button slice_btn;
    private Button random_btn;
    private Button custom1_btn;
    private Button custom2_btn;
    private Button custom3_btn;
    private ImageButton custom_del1;
    private ImageButton custom_del2;
    private ImageButton custom_del3;
    private ImageButton bluetooth_btn;
    private SeekBar top_seekbar;
    private SeekBar btm_seekbar;
    private SeekBar speed_seekbar;
    private SeekBar cycle_seekbar;
    private EditText top_edit;
    private EditText btm_edit;
    private EditText speed_edit;
    private EditText cycle_edit;
    private SwitchCompat switchCompat;
    private View layout;
    private String top_data;
    private String btm_data;
    private String speed_data;
    private String cycle_data;
    private String custom1 = "Null";
    private String custom2 = "Null";
    private String custom3 = "Null";
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치
    private TextView textViewReceive; // 수신 된 데이터를 표시하기 위한 텍스트 뷰
    private EditText editTextSend; // 송신 할 데이터를 작성하기 위한 에딧 텍스트
    private Button buttonSend; // 송신하기 위한 버튼
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ListView listDevice;
    private SimpleAdapter adapterDevice;
    public ProgressDialog asyncDialog;
    public boolean onBT = false;
    public byte[] sendByte = new byte[4];
    List<Map<String, String>> dataDevice;
    List<BluetoothDevice> bluetoothDevices;
    int selectDevice;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid;
    Boolean power = false;
    Random rnd = new Random();
    private double ball_count = 0.5;
    private int MAXSPEED = 20;
    Thread randomThread;
    Boolean[] button_state = {false, false, false, false, false, false};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().startService(new Intent(getActivity(), ForcedTerminationService.class));

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
        isBluetooth = v.findViewById(R.id.isBluetooth);
        topspin_btn = v.findViewById(R.id.topspin_btn);
        slice_btn = v.findViewById(R.id.slice_btn);
        random_btn = v.findViewById(R.id.random_btn);
        custom1_btn = v.findViewById(R.id.custom1_btn);
        custom2_btn = v.findViewById(R.id.custom2_btn);
        custom3_btn = v.findViewById(R.id.custom3_btn);
        bluetooth_btn = v.findViewById(R.id.bluetooth_btn);
        custom_del1 = v.findViewById(R.id.custom_del1);
        custom_del2 = v.findViewById(R.id.custom_del2);
        custom_del3 = v.findViewById(R.id.custom_del3);
        top_seekbar = v.findViewById(R.id.top_motor_seekBar);
        btm_seekbar = v.findViewById(R.id.btm_motor_seekBar);
        speed_seekbar = v.findViewById(R.id.speed_seekBar);
        cycle_seekbar = v.findViewById(R.id.cycle_seekBar);
        top_edit = v.findViewById(R.id.top_motor_edit);
        btm_edit = v.findViewById(R.id.btm_motor_edit);
        speed_edit = v.findViewById(R.id.speed_edit);
        cycle_edit = v.findViewById(R.id.cycle_edit);
        listDevice = v.findViewById(R.id.paring_list);
        layout = v.findViewById(R.id.adjust_layout);
        switchCompat = v.findViewById(R.id.powerswitch);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        top_edit.setText("0");
        btm_edit.setText("0");
        speed_edit.setText("0");
        cycle_edit.setText("7");

        customInit("1");
        customInit("2");
        customInit("3");

        isBluetoothStart();
        setSwitchCompat();
        countFiredBall();
        randomStart();

        editTextListener(top_edit);
        editTextListener(btm_edit);
        editTextListener(speed_edit);
        editTextListener(cycle_edit);

        fixedButtonClick(topspin_btn);
        fixedButtonClick(slice_btn);
        fixedButtonClick(random_btn);

        customButtonClick(custom1_btn, "1");
        customButtonClick(custom2_btn, "2");
        customButtonClick(custom3_btn, "3");

        deleteButtonClick(custom_del1, "1");
        deleteButtonClick(custom_del2, "2");
        deleteButtonClick(custom_del3, "3");

        remoteSeekbar(top_seekbar, top_edit);
        remoteSeekbar(btm_seekbar, btm_edit);
        remoteSeekbar(speed_seekbar, speed_edit);
        remoteSeekbar(cycle_seekbar, cycle_edit);


        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        bluetooth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 블루투스 활성화 하기
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
                dataDevice = new ArrayList<>();
                adapterDevice = new SimpleAdapter(getActivity(), dataDevice, android.R.layout.simple_list_item_2, new String[]{"name", "address"}, new int[]{android.R.id.text1, android.R.id.text2});
                listDevice.setAdapter(adapterDevice);
                //검색된 블루투스 디바이스 데이터
                bluetoothDevices = new ArrayList<>();
                //선택한 디바이스 없음
                selectDevice = -1;

                if (bluetoothAdapter == null) {   // 디바이스가 블루투스 지원하지 않을 시
                } else {   // 디바이스가 블루투스 지원할 시
                    if (bluetoothAdapter.isEnabled()) {   // 블루투스가 활성화되었을 때
                        if (!PairingBluetoothListState()) {   // 연결된 기기가 없을 때
                            selectBluetoothDevice();    // 블루투스 디바이스 선택 함수
                        }

                    } else {   // 블루투스가 비활성화일 때
                        // 블루투스 활성화를 위한 다이얼로그 출력
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        // 선택한 값이 onActivityResult 함수에서 콜백된다.
                        ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                                new ActivityResultContracts.StartActivityForResult(),
                                new ActivityResultCallback<ActivityResult>() {
                                    @Override
                                    public void onActivityResult(ActivityResult result) {
                                        if (result.getResultCode() == Activity.RESULT_OK) { // 사용 선택 시
                                            selectBluetoothDevice();    // 블루투스 디바이스 선택 함수
                                        } else {   // 취소 시

                                        }
                                    }
                                });
                        startActivityResult.launch(intent);


                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Pause 실행");
    }

    @Override
    public void onStop() {
        super.onStop();
        updateCountBall();

        Log.i(TAG, "Stop 실행");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateCountBall();
        sendData("0,0,0,0");
        Log.i(TAG, "Destroy 실행");

    }

    public void updateCountBall(){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date dateObj = calendar.getTime();
        String formattedDate = dtf.format(dateObj);
        String cnt_str = String.valueOf((int)ball_count);
        Log.i(TAG, formattedDate);

        databaseReference.child("users").child(uid).child("ballCount").child(formattedDate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.i(TAG, "update Count Ball");
                if(!task.isSuccessful()){
                    Log.i(TAG, "failed");
                }
                else{
                    String res = String.valueOf(task.getResult().getValue());
                    if(res.equals("null")){
                        databaseReference.child("users").child(uid).child("ballCount").child(formattedDate).setValue(cnt_str);
                        Log.i(TAG, "null");
                    }
                    else{
                        Log.i(TAG, "task");
                        Log.i(TAG, "테스크: " + String.valueOf(task.getResult().getValue()));
                        int res_cnt = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                        res_cnt += (int)ball_count;
                        databaseReference.child("users").child(uid).child("ballCount").child(formattedDate).setValue(String.valueOf(res_cnt));
                    }
                }
                ball_count = 0.5;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    public void countFiredBall(){
        Thread countWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (power) {
                        if(!top_edit.getText().toString().equals("0") && !btm_edit.getText().toString().equals("0") && !speed_edit.getText().toString().equals("0") ){
                            String s = cycle_edit.getText().toString();
                            double d = 0.1 / Double.parseDouble(s);
                            ball_count += d;
                            Log.i(TAG, "볼카운트: " + String.valueOf(ball_count) + " / 주기: " + s + " / 증가율: " + String.valueOf(d));
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                Log.i(TAG, "예외 발생!!!");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        countWorker.start();
    }

    public void setSwitchCompat(){
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Button[] button_list = {topspin_btn, slice_btn, random_btn, custom1_btn, custom2_btn, custom3_btn};
                if(b) {
                    power = true;
                    top_data = top_edit.getText().toString();
                    btm_data = btm_edit.getText().toString();
                    speed_data = speed_edit.getText().toString();
                    cycle_data = cycle_edit.getText().toString();
                    String data = top_data + ", " + btm_data + ", " + speed_data + ", " + cycle_data;
                    sendData(data);
                }
                else{
                    Log.i(TAG, "배열: " + String.valueOf(button_state[2]));
                    for(int i = 0; i < button_state.length; i++){
                        if(button_state[i]){
                            button_state[i] = false;
                            if(i < 3) button_list[i].setBackgroundResource(R.drawable.button_round);
                            else{
                                switch(i){
                                    case 3:
                                        if(custom1 != "Null") button_list[i].setBackgroundResource(R.drawable.button_round);
                                        else button_list[i].setBackgroundResource(R.drawable.button_round2);
                                        break;
                                    case 4:
                                        if(custom2 != "Null") button_list[i].setBackgroundResource(R.drawable.button_round);
                                        else button_list[i].setBackgroundResource(R.drawable.button_round2);
                                        break;
                                    case 5:
                                        if(custom3 != "Null") button_list[i].setBackgroundResource(R.drawable.button_round);
                                        else button_list[i].setBackgroundResource(R.drawable.button_round2);
                                        break;
                                }
                            }
                        }
                    }
                    power = false;
                    top_edit.setText("0");
                    btm_edit.setText("0");
                    speed_edit.setText("0");
                    cycle_edit.setText("7");
                    updateCountBall();
                    sendData("0,0,0,7");
                    Log.i(TAG, "setSwitchCompat ㅎㅎ");
                    Log.i(TAG, "Count: " + String.valueOf(ball_count));
                }
            }
        });
    }

    private void hideKeyboard()
    {
        if (getActivity() != null && getActivity().getCurrentFocus() != null)
        {
            // 프래그먼트기 때문에 getActivity() 사용
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public void editTextListener(EditText edit){
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edit.setSelection(edit.length());
                int v;
                try {
                    v = Integer.parseInt(edit.getText().toString());
                }
                catch(Exception e){
                    v = 0;
                }
                switch(edit.getId()){
                    case R.id.top_motor_edit:
                        top_seekbar.setProgress(v);
                        break;
                    case R.id.btm_motor_edit:
                        btm_seekbar.setProgress(v);
                        break;
                    case R.id.speed_edit:
                        speed_seekbar.setProgress(v);
                        break;
                    case R.id.cycle_edit:
                        cycle_seekbar.setProgress(v);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void customInit(String n){
        databaseReference.child("users").child(uid).child("custom"+n).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String s = task.getResult().getValue().toString();
                String[] str = s.split(", ");
                switch(n){
                    case "1":
                        custom1 = s;
                        if(!s.equals("Null")){
                            custom1_btn.setBackgroundResource(R.drawable.button_round);
                            custom1_btn.setText(str[0]);
                            custom1_btn.setTypeface(Typeface.DEFAULT);
                            custom_del1.setVisibility(View.VISIBLE);
                            custom_del1.setEnabled(true);
                        }
                        else{
                            custom1_btn.setBackgroundResource(R.drawable.button_round2);
                            custom_del1.setVisibility(View.INVISIBLE);
                            custom_del1.setEnabled(false);
                        }
                        break;
                    case "2":
                        custom2 = s;
                        if(!s.equals("Null")){
                            custom2_btn.setBackgroundResource(R.drawable.button_round);
                            custom2_btn.setText(str[0]);
                            custom2_btn.setTypeface(Typeface.DEFAULT);
                            custom_del2.setVisibility(View.VISIBLE);
                            custom_del2.setEnabled(true);
                        }
                        else{
                            custom2_btn.setBackgroundResource(R.drawable.button_round2);
                            custom_del2.setVisibility(View.INVISIBLE);
                            custom_del2.setEnabled(false);
                        }
                        break;
                    case "3":
                        custom3 = s;
                        if(!s.equals("Null")){
                            custom3_btn.setBackgroundResource(R.drawable.button_round);
                            custom3_btn.setText(str[0]);
                            custom3_btn.setTypeface(Typeface.DEFAULT);
                            custom_del3.setVisibility(View.VISIBLE);
                            custom_del3.setEnabled(true);
                        }
                        else{
                            custom3_btn.setBackgroundResource(R.drawable.button_round2);
                            custom_del3.setVisibility(View.INVISIBLE);
                            custom_del3.setEnabled(false);
                        }
                        break;
                }
                Log.i(TAG, "CustomInit 실행 // " + custom1 + ", " + custom2 + ", " + custom3);
            }
        });
    }


    public void remoteSeekbar(SeekBar seekBarComponent, EditText editText) {
        seekBarComponent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editText.setText(String.valueOf(i));
                top_data = top_edit.getText().toString();
                btm_data = btm_edit.getText().toString();
                speed_data = speed_edit.getText().toString();
                cycle_data = cycle_edit.getText().toString();
                String data = top_data + ", " + btm_data + ", " + speed_data + ", " + cycle_data;
                if(power) {
                    sendData(data);
                    Log.i(TAG, "데이터 목록 확인(seekbar)" + data);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void inputSeekbar(String[] strArr){
        top_edit.setText(strArr[1]);
        btm_edit.setText(strArr[2]);
        speed_edit.setText(strArr[3]);
        cycle_edit.setText(strArr[4]);
    }

    public void setButtonActive(Button button){
        Button[] button_list = {topspin_btn, slice_btn, random_btn, custom1_btn, custom2_btn, custom3_btn};
        for(int i = 0; i < button_state.length; i++){
            if(button_state[i]){
                button_state[i] = false;
                switch(button_list[i].getId()){
                    case R.id.custom1_btn:
                        if(custom1 == "Null") button_list[i].setBackgroundResource(R.drawable.button_round2);
                        else button_list[i].setBackgroundResource(R.drawable.button_round);
                        break;
                    case R.id.custom2_btn:
                        if(custom2 == "Null") button_list[i].setBackgroundResource(R.drawable.button_round2);
                        else button_list[i].setBackgroundResource(R.drawable.button_round);
                        break;
                    case R.id.custom3_btn:
                        if(custom3 == "Null") button_list[i].setBackgroundResource(R.drawable.button_round2);
                        else button_list[i].setBackgroundResource(R.drawable.button_round);
                        break;
                    default:
                        button_list[i].setBackgroundResource(R.drawable.button_round);
                }
            }
        }
        int reg = 0;
        for(int i = 0; i < button_list.length; i++){
            if(button_list[i].getId() == button.getId()){
                reg = i;
                break;
            }
        }
        button_state[reg] = true;
        button_list[reg].setBackgroundResource(R.drawable.greenbutton);
    }
    public void enableSeekbar(){
        top_seekbar.setEnabled(false);
        btm_seekbar.setEnabled(false);
        speed_seekbar.setEnabled(false);
        cycle_seekbar.setEnabled(false);
        top_edit.setEnabled(false);
        btm_edit.setEnabled(false);
        speed_edit.setEnabled(false);
        cycle_edit.setEnabled(false);
    }

    public void ableSeekbar(){
        top_seekbar.setEnabled(true);
        btm_seekbar.setEnabled(true);
        speed_seekbar.setEnabled(true);
        cycle_seekbar.setEnabled(true);
        top_edit.setEnabled(true);
        btm_edit.setEnabled(true);
        speed_edit.setEnabled(true);
        cycle_edit.setEnabled(true);
        top_edit.setText("0");
        btm_edit.setText("0");
        speed_edit.setText("0");
        cycle_edit.setText("7");
        sendData("0, 0, 0, 7");
    }

    public void randomStart(){
        randomThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if (button_state[2]){
                        String t = String.valueOf(rnd.nextInt(96) + 5);
                        String b = String.valueOf(rnd.nextInt(96) + 5);
                        String s = String.valueOf(rnd.nextInt(MAXSPEED) + 1);
                        String c = String.valueOf(rnd.nextInt(5) + 2);
                        String[] arr_str = {"Null", t, b, s, c};
                        inputSeekbar(arr_str);
                        Log.i(TAG, "random: "+t+", "+b+", "+s+", "+c);
                        try {
                            sleep(Integer.parseInt(c) * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        randomThread.start();
    }

    public void fixedButtonClick(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(power)
                    switch(button.getId()){
                        case R.id.topspin_btn:
                            if (button_state[0]){
                                button.setBackgroundResource(R.drawable.button_round);
                                ableSeekbar();
                                button_state[0] = false;
                            }
                            else{
                                top_edit.setText("100");
                                btm_edit.setText("30");
                                speed_edit.setText("20");
                                setButtonActive(button);
                                enableSeekbar();
                                cycle_edit.setEnabled(true);
                                cycle_seekbar.setEnabled(true);
                            }
                            break;
                        case R.id.slice_btn:
                            if (button_state[1]){
                                button.setBackgroundResource(R.drawable.button_round);
                                ableSeekbar();
                                button_state[1] = false;
                            }
                            else{
                                top_edit.setText("30");
                                btm_edit.setText("100");
                                speed_edit.setText("20");
                                setButtonActive(button);
                                enableSeekbar();
                                cycle_edit.setEnabled(true);
                                cycle_seekbar.setEnabled(true);
                            }
                            break;
                        case R.id.random_btn:
                            Log.i(TAG, "random 눌림");
                            if (button_state[2]){
                                button.setBackgroundResource(R.drawable.button_round);
                                ableSeekbar();
                                button_state[2] = false;
                            }
                            else{
                                setButtonActive(button);
                                enableSeekbar();
                            }
                            break;
                    }
            }
        });
    }

    public void customButtonClick(Button button, String n){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Button[] button_list = {topspin_btn, slice_btn, random_btn, custom1_btn, custom2_btn, custom3_btn};
                Log.i(TAG, "before btn: " + custom1 + ", " + custom2 + ", " + custom3);
                String custom = "Null";
                switch(n){
                    case "1":
                        custom = custom1;
                        break;
                    case "2":
                        custom = custom2;
                        break;
                    case "3":
                        custom = custom3;
                        break;
                }
                int idx = Integer.parseInt(n)+2;
                if(button_state[idx]){
                    ableSeekbar();
                    button_state[(Integer.parseInt(n)+2)] = false;
                    switch(idx){
                        case 3:
                            if(custom1 != "Null") button_list[idx].setBackgroundResource(R.drawable.button_round);
                            else button_list[idx].setBackgroundResource(R.drawable.button_round2);
                            break;
                        case 4:
                            if(custom2 != "Null") button_list[idx].setBackgroundResource(R.drawable.button_round);
                            else button_list[idx].setBackgroundResource(R.drawable.button_round2);
                            break;
                        case 5:
                            if(custom3 != "Null") button_list[idx].setBackgroundResource(R.drawable.button_round);
                            else button_list[idx].setBackgroundResource(R.drawable.button_round2);
                            break;
                    }
                }
                else{
                    if(!custom.equals("Null")){
                        if(power) {
                            String[] strArr = custom.split(", ");
                            inputSeekbar(strArr);
                            enableSeekbar();
                            setButtonActive(button);
                        }
                    }
                    else{
                        final EditText etEdit = new EditText(getActivity());
                        etEdit.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (keyCode == event.KEYCODE_ENTER)
                                    return true;
                                return false;
                            }
                        });
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("프리셋 이름 설정");
                        dialog.setView(etEdit);
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String custom_name = etEdit.getText().toString();
                                button.setText(custom_name);
                                button.setTypeface(Typeface.DEFAULT);
                                String custom_str = custom_name + ", " + top_edit.getText().toString() + ", " + btm_edit.getText().toString() + ", " + speed_edit.getText().toString() + ", " + cycle_edit.getText().toString();
                                switch (button.getId()){
                                    case R.id.custom1_btn:
                                        custom1 = custom_str;
                                        custom_del1.setVisibility(View.VISIBLE);
                                        custom_del1.setEnabled(true);
                                        break;
                                    case R.id.custom2_btn:
                                        custom2 = custom_str;
                                        custom_del2.setVisibility(View.VISIBLE);
                                        custom_del2.setEnabled(true);
                                        break;
                                    case R.id.custom3_btn:
                                        custom3 = custom_str;
                                        custom_del3.setVisibility(View.VISIBLE);
                                        custom_del3.setEnabled(true);
                                        break;
                                }
                                databaseReference.child("users").child(uid).child("custom"+n).setValue(custom_str);
                                button.setBackgroundResource(R.drawable.button_round);
                            }
                        });
                        dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                }
                Log.i(TAG, "after btn: " + custom1 + ", " + custom2 + ", " + custom3);
            }
        });
    }

    public void deleteButtonClick(ImageButton button, String n){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("users").child(uid).child("custom"+n).setValue("Null");
                switch(n){
                    case "1":
                        custom1 = "Null";
                        custom1_btn.setBackgroundResource(R.drawable.button_round2);
                        custom1_btn.setText("+");
                        break;
                    case "2":
                        custom2 = "Null";
                        custom2_btn.setBackgroundResource(R.drawable.button_round2);
                        custom2_btn.setText("+");
                        break;
                    case "3":
                        custom3 = "Null";
                        custom3_btn.setBackgroundResource(R.drawable.button_round2);
                        custom3_btn.setText("+");
                        break;
                }
                button.setVisibility(View.INVISIBLE);
                button.setEnabled(false);
            }
        });
    }

    public void isBluetoothStart(){
        Thread isPairing = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(PairingBluetoothListState()){
                        isBluetooth.setBackgroundResource(R.drawable.shape_for_circle_button);
                    }
                    else{
                        isBluetooth.setBackgroundResource(R.drawable.shape_for_circle_button2);
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        isPairing.start();
    }

    // 블루투스 관련
    public Boolean PairingBluetoothListState() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT
                }, 1);
            }
            Set<BluetoothDevice> bluetoothDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
                if (isConnected(bluetoothDevice)) {
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }

    public boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void sendData(String text) {
        // 문자열에 개행문자("\n")를 추가해줍니다.
        try{
            // 데이터 송신
            outputStream.write(text.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void checkPermission() {
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT
                    },
                    1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH
                    },
                    1);
        }
    }
//    private String GetDevicesUUID(Context mContext){
//        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
//        final String tmDevice, tmSerial, androidId;
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        String deviceId = deviceUuid.toString();
//        return deviceId;
//    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT
            }, 1);
        }
        devices = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        final int pariedDeviceCount = devices.size();
        // 페어링 된 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), "먼저 Bluetooth 설정에 들어가 페어링을 진행해 주세요.", Toast.LENGTH_SHORT).show();
                    BluetoothDevice device = bluetoothDevices.get(i);

                    try {
                        // 선택한 디바이스 페어링 요청
                        Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {   // 페어링 된 장치가 있는 경우
            // 디바이스 선택을 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice bluetoothDevice : devices) {
                try {
                    list.add(bluetoothDevice.getName());
                } catch (SecurityException e) {
                    Log.i(TAG, "보안 예외 발생!!!");
                }
            }
            list.add("취소");

            // list를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });

            // 뒤로가기 버튼 누를 시 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName) {
        asyncDialog = new ProgressDialog(getContext());
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("블루투스 연결중..");
        asyncDialog.show();
        asyncDialog.setCancelable(false);

        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT
                }, 1);
            }
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        Thread BTConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //HC-06 UUID
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT
                        }, 1);
                    }
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    inputStream = bluetoothSocket.getInputStream();

                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint({"ShowToast", "SetTextI18n"})
                        @Override
                        public void run() {
                            isBluetooth.setBackgroundResource(R.drawable.shape_for_circle_button);
                            Toast.makeText(getActivity(),deviceName + " 연결 완료",Toast.LENGTH_LONG).show();
                            asyncDialog.dismiss();
                        }
                    });
                    onBT = true;
                } catch(Exception e){
                    // 블루투스 연결 중 오류 발생
                    getActivity().runOnUiThread(new Runnable() {
                        @SuppressLint({"ShowToast", "SetTextI18n"})
                        @Override
                        public void run() {
                            asyncDialog.dismiss();
                            Toast.makeText(getActivity(),"블루투스 연결 오류",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        BTConnect.start();
    }
}