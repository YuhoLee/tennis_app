package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Fragment_C extends Fragment {
    private ArrayList<Integer> jsonList; // ArrayList 선언
    private ArrayList<String> labelList; // ArrayList 선언
    ArrayAdapter month_adapter;
    ArrayAdapter year_adapter;
    private String[] year_arr = new String[5];
    private String[] month_arr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private Date now = new Date();
    private String now_str = dateFormat.format(now);
    private String curr_year = now_str.substring(0,4);
    private String curr_month = now_str.substring(4,6);
    private BarChart barChart;
    private Button weekButton;
    private Button monthButton;
    private Spinner month_spinner;
    private Spinner year_spinner;
    private TextView week_txt;
    private TextView month_txt;
    private TextView year_txt;
    private Calendar cal;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid;
    private String[] weekStr;
    private int dataCount;
    private long mLastClickTime = 0;
    private boolean isInit = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsonList = new ArrayList<>();
        labelList = new ArrayList<>();
        cal = Calendar.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        curr_year = now_str.substring(0,4);
        curr_month = now_str.substring(4,6);
        calcWeekDay();
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
        week_txt = v.findViewById(R.id.week_txt);
        month_txt = v.findViewById(R.id.month_txt);
        year_txt = v.findViewById(R.id.year_txt);
        month_spinner = v.findViewById(R.id.month_spinner);
        year_spinner = v.findViewById(R.id.year_spinner);

        Log.i(TAG, "curr_year" + curr_year);
        Log.i(TAG, "curr_month" + curr_month);

        month_adapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, month_arr);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month_spinner.setAdapter(month_adapter);
        month_spinner.setSelection(0, false);
        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                month_txt.setText(month_arr[i] + "월 ");
                curr_month = month_arr[i];
                Log.i(TAG, "curr_year" + curr_year);
                Log.i(TAG, "curr_month" + curr_month);
                setMonthTrain(curr_year, curr_month);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int year_itg = Integer.parseInt(now_str.substring(0,4));
        for (int i = 0; i < 5; i++){
            year_arr[i] = String.valueOf(year_itg - i);
            Log.i(TAG,"year: " + year_arr[i]);
        }
        year_adapter = new ArrayAdapter(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, year_arr);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(year_adapter);
        year_spinner.setSelection(0, false);
        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year_txt.setText(year_arr[i] + "년");
                curr_year = year_arr[i];
                Log.i(TAG, "curr_year" + curr_year);
                Log.i(TAG, "curr_month" + curr_month);
                setMonthTrain(curr_year, curr_month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                        month_txt.setVisibility(View.INVISIBLE);
                        month_spinner.setVisibility(View.INVISIBLE);
                        week_txt.setText("주간");
                        week_txt.setVisibility(View.VISIBLE);
                        setWeekTrain();
                        break;
                    case R.id.month_button:
                        week_txt.setVisibility(View.INVISIBLE);
                        setMonthTrain("0","0");
                        month_txt.setText(curr_month + "월 ");
                        month_txt.setVisibility(View.VISIBLE);
                        month_spinner.setVisibility(View.VISIBLE);
                        year_txt.setText(curr_year + "년");
                        year_txt.setVisibility(View.VISIBLE);
                        year_spinner.setVisibility(View.VISIBLE);
                        month_spinner.setSelection(0, true);
                        year_spinner.setSelection(0, true);
                        break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
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
            Log.i(TAG, weekStr[i]);
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
                        if(dataCount >=  7){
                            Log.i(TAG, "카운트: " + String.valueOf(dataCount));
                            for(int m : jsonList){
                                Log.i(TAG, "ㅇㅋ" + String.valueOf(m));
                            }
                            BarChartGraph(labelList, jsonList);
                            barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
                        }
                    }
                }
            });

        }
    }

    public String setMonthTrain(String year, String month){
        dataCount = 0;
        labelList = new ArrayList<>();
        jsonList = new ArrayList<>();
        String[] now_arr = new String[3];
        int md;
        if(year.equals("0") && month.equals("0")){
            Date now = new Date();
            String now_str = dateFormat.format(now);
            now_arr[0] = now_str.substring(0, 4);
            now_arr[1] = now_str.substring(4, 6);
            now_arr[2] = now_str.substring(6);
            cal.set(Integer.parseInt(now_arr[0]),Integer.parseInt(now_arr[1]),1);
        }
       else{
            now_arr[0] = year;
            now_arr[1] = month;
            now_arr[2] = "01";
            cal.set(Integer.parseInt(year),Integer.parseInt(month),1);
        }
        md = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Log.i(TAG, "md = " + String.valueOf(md));
        Log.i(TAG, now_arr[0] + " / " + now_arr[1]);
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
                        if(dataCount == md){
                            Log.i(TAG, "카운트: " + String.valueOf(dataCount));
                            for(int m : jsonList){
                                Log.i(TAG, "ㅇㅋ" + String.valueOf(m));
                            }
                            BarChartGraph(labelList, jsonList);
                            barChart.setTouchEnabled(false); //확대하지못하게 막아버림! 별로 안좋은 기능인 것 같아~
                        }
                    }
                }
            });
        }
        Log.i(TAG, now_arr[1]);
        return now_arr[1];
    }

    /**
     * 그래프함수
     */
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Integer> valList) {
        // BarChart 메소드
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((Integer) valList.get(i), i));
            Log.i(TAG, "entries: " + valList.get(i));
        }

        BarDataSet depenses = new BarDataSet(entries, "일일 훈련시간"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
            Log.i(TAG, "labels: " + labelList.get(i));
        }

        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
    }
}