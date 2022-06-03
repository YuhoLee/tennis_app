package com.example.tennis_app;
import static android.content.ContentValues.TAG;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Fragment_A extends Fragment {
    private TextView name;
    private String uid;
    private int dataCount = 0;
    private ArrayList<Integer> jsonList; // ArrayList 선언
    private ArrayList<String> labelList; // ArrayList 선언
    private BarChart barChart;
    private SimpleDateFormat dateFormat;
    private Calendar cal;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String[] weekStr;


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
        View v = inflater.inflate(R.layout.fragment_a, container, false);
        name = v.findViewById(R.id.userNametext);
        barChart = (BarChart)v.findViewById(R.id.recently_barchart);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        jsonList = new ArrayList<>();
        labelList = new ArrayList<>();
        cal = Calendar.getInstance();
        uid = user.getUid();
        dateFormat = new SimpleDateFormat("yyyyMMdd");

        databaseReference.child("users").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                name.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        calcWeekDay();
        setWeekTrain();
        setWeekTrain();
        barChart.invalidate();

        return v;
    }

    public void calcWeekDay(){
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
                        if(dataCount == 7){
                            Log.i(TAG, "카운트: " + String.valueOf(dataCount));
                            for(int i : jsonList){
                                Log.i(TAG, "ㅇㅋ" + String.valueOf(i));
                            }
                            BarChartGraph(labelList, jsonList);
                            barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~

                        }
                    }
                }
            });
            dataCount = 0;
        }
    }

    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {
        ArrayList<String> xLabel = new ArrayList<>();
        for(int i = 0; i < weekStr.length; i++){
            xLabel.add(weekStr[i].substring(4,6) + "/" + weekStr[i].substring(6));
        }

        // BarChart 메소드
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((Integer) valList.get(i), i));
            Log.i(TAG, "val: " + valList.get(i));
        }

        BarDataSet depenses = new BarDataSet(entries, "일일 훈련시간"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
            Log.i(TAG, "label: " + (String) labelList.get(i));
        }


        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
    }

}