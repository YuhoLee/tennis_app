package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Fragment_C extends Fragment {
    private ArrayList<Integer> jsonList; // ArrayList 선언
    private ArrayList<String> labelList; // ArrayList 선언
    private BarChart barChart;
    private Button weekButton;
    private Button monthButton;
    private Button yearButton;
    private Calendar cal;
    private SimpleDateFormat dateFormat;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid;
    private String[] weekStr;
    private int dataCount;
    private long mLastClickTime = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsonList = new ArrayList<>();
        labelList = new ArrayList<>();
        cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        Log.i(TAG, "onCreate start");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_c, container, false);

        barChart = (BarChart)v.findViewById(R.id.fragment_bluetooth_chat_barchart);
        weekButton = v.findViewById(R.id.week_button);
        monthButton = v.findViewById(R.id.month_button);
        yearButton = v.findViewById(R.id.year_button);

        buttonClick(weekButton);
        buttonClick(monthButton);

        setWeekTrain();  //그래프 기본 세팅

        return v;
    }

    public void buttonClick(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 500){
                    return;
                }
                switch(button.getId()){
                    case R.id.week_button:
                        setWeekTrain();
                        break;
                    case R.id.month_button:
                        setMonthTrain();
                        break;
                    case R.id.year_button:
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }public void calcWeekDay(){
        weekStr = new String[7];
        for(int i = 0; i < 7; i++){
            cal.set(Calendar.DAY_OF_WEEK, i+1);
            weekStr[i] = dateFormat.format(cal.getTime());
        }
    }


    public void setWeekTrain(){
        dataCount = 0;
        String weekDay[] = {"일", "월", "화", "수", "목", "금", "토"};
        labelList = new ArrayList<>();
        jsonList = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            labelList.add(weekDay[i]);
        }
        calcWeekDay();
        for(int i = 0; i < 7; i++){
            databaseReference.child("users").child(uid).child("ballCount").child(String.valueOf(weekStr[i])).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    dataCount += 1;
                    if(!task.isSuccessful()){
                    }
                    else{
                        String res = String.valueOf(task.getResult().getValue());
                        if(res.equals("null")){
                            jsonList.add(0);
                        }
                        else{
                            jsonList.add(Integer.parseInt(res));
                        }
                        if(dataCount >=  7){
                            Log.i(TAG, "카운트: " + String.valueOf(dataCount));
                            for(int i : jsonList){
                                Log.i(TAG, "ㅇㅋ" + String.valueOf(i));
                            }
                            BarChartGraph(labelList, jsonList);
                            barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
                            barChart.getAxisRight().setAxisMaxValue(500);
                            barChart.getAxisLeft().setAxisMaxValue(500);
                        }
                    }
                }
            });

        }
    }

    public void setMonthTrain(){
        dataCount = 0;
        labelList = new ArrayList<>();
        jsonList = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        String now_str = dateFormat.format(now);
        String[] now_arr = {now_str.substring(0,4), now_str.substring(4,6), now_str.substring(6)};
        cal.set(Integer.parseInt(now_arr[0]),Integer.parseInt(now_arr[1]),Integer.parseInt(now_arr[2]));
        int md = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.i("TAG", "TAG: " + String.valueOf(md) +" " +now_str);
        String[] month_str = new String[md];
        for(int i = 1; i <= md; i++){
            if(i < 10){
                month_str[i-1] = now_arr[0] + now_arr[1] + "0" + String.valueOf(i);
            }
            else{
                month_str[i-1] = now_arr[0] + now_arr[1] + String.valueOf(i);
            }
        }

        for(int i = 1; i <= md; i++){
            labelList.add(String.valueOf(i));
        }
        for(int i = 0; i < md; i++){
            databaseReference.child("users").child(uid).child("ballCount").child(String.valueOf(month_str[i])).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    dataCount += 1;
                    if(!task.isSuccessful()){
                    }
                    else{
                        String res = String.valueOf(task.getResult().getValue());
                        if(res.equals("null")){
                            jsonList.add(0);
                        }
                        else{
                            jsonList.add(Integer.parseInt(res));
                        }
                        if(dataCount >= md){
                            Log.i(TAG, "카운트: " + String.valueOf(dataCount));
                            for(int i : jsonList){
                                Log.i(TAG, "ㅇㅋ" + String.valueOf(i));
                            }
                            BarChartGraph(labelList, jsonList);
                            barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
                            barChart.getAxisRight().setAxisMaxValue(500);
                            barChart.getAxisLeft().setAxisMaxValue(500);
                        }
                    }
                }
            });
        }
    }

    /**
     * 그래프함수
     */
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {
        // BarChart 메소드
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((Integer) valList.get(i), i));
        }

        BarDataSet depenses = new BarDataSet(entries, "일일 훈련시간"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
    }
}