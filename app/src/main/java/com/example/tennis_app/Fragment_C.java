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
import android.widget.EditText;
import android.widget.ImageButton;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private Date now = new Date();
    private String now_str = dateFormat.format(now);
    private String curr_year = now_str.substring(0,4);
    private String curr_month = now_str.substring(4,6);
    private String year;
    private String month;
    private BarChart barChart;
    private Button weekButton;
    private Button monthButton;
    private Button year_up;
    private Button year_down;
    private Button month_up;
    private Button month_down;
    private ImageButton search_btn;
    private TextView week_period;
    private TextView month_txt;
    private TextView year_txt;
    private TextView title_txt;
    private EditText month_edit;
    private EditText year_edit;
    private Calendar cal;
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
        year_up = v.findViewById(R.id.year_up);
        year_down = v.findViewById(R.id.year_down);
        month_up = v.findViewById(R.id.month_up);
        month_down = v.findViewById(R.id.month_down);
        week_period = v.findViewById(R.id.week_period);
        month_txt = v.findViewById(R.id.month_txt);
        year_txt = v.findViewById(R.id.year_txt);
        title_txt = v.findViewById(R.id.title_txt);
        month_edit = v.findViewById(R.id.month_edit);
        year_edit = v.findViewById(R.id.year_edit);
        search_btn = v.findViewById(R.id.search_btn);

        buttonClick(weekButton);
        buttonClick(monthButton);
        udButtonClick(year_up);
        udButtonClick(year_down);
        udButtonClick(month_up);
        udButtonClick(month_down);
        searchButtonClick();
        setWeekTrain();  //그래프 기본 세팅
        String first = weekStr[0].substring(0,4) + "-" + weekStr[0].substring(4,6) + "-" + weekStr[0].substring(6);
        String last = weekStr[6].substring(0,4) + "-" + weekStr[6].substring(4,6) + "-" + weekStr[6].substring(6);
        week_period.setText(first + " ~ " + last);
        return v;
    }

    public void enableTrueFalse(Boolean b){
        int vis;
        if(b) vis = View.VISIBLE;
        else vis = View.INVISIBLE;

        year_txt.setVisibility(vis);
        month_txt.setVisibility(vis);
        year_edit.setVisibility(vis);
        month_edit.setVisibility(vis);
        year_up.setVisibility(vis);
        year_down.setVisibility(vis);
        month_up.setVisibility(vis);
        month_down.setVisibility(vis);
        search_btn.setVisibility(vis);
    }

    public void searchButtonClick(){
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = year_edit.getText().toString();
                month = month_edit.getText().toString();
                setMonthTrain(year, month);
            }
        });
    }

    public void udButtonClick(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str;
                int integer_str;
                switch(button.getId()){
                    case R.id.year_up:
                        str = year_edit.getText().toString();
                        integer_str = Integer.parseInt(str);
                        if(integer_str < Integer.parseInt(curr_year)){
                            year_edit.setText(String.valueOf(integer_str+1));
                        }
                        break;
                    case R.id.year_down:
                        str = year_edit.getText().toString();
                        integer_str = Integer.parseInt(str);
                        if(integer_str > Integer.parseInt(curr_year)-5){
                            year_edit.setText(String.valueOf(integer_str-1));
                        }
                        break;
                    case R.id.month_up:
                        str = month_edit.getText().toString();
                        integer_str = Integer.parseInt(str);
                        if(integer_str < 12){
                            month_edit.setText(monthFormat(integer_str+1));
                        }
                        break;
                    case R.id.month_down:
                        str = month_edit.getText().toString();
                        integer_str = Integer.parseInt(str);
                        if(integer_str > 1){
                            month_edit.setText(monthFormat(integer_str-1));
                        }
                        break;
                }
            }
        });
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
                        enableTrueFalse(false);
                        setWeekTrain();
                        String first = weekStr[0].substring(0,4) + "-" + weekStr[0].substring(4,6) + "-" + weekStr[0].substring(6);
                        String last = weekStr[6].substring(0,4) + "-" + weekStr[6].substring(4,6) + "-" + weekStr[6].substring(6);
                        week_period.setText(first + " ~ " + last);
                        week_period.setVisibility(View.VISIBLE);
                        title_txt.setText("주간 훈련 통계");
                        break;
                    case R.id.month_button:
                        enableTrueFalse(true);
                        week_period.setVisibility(View.INVISIBLE);
                        title_txt.setText("월별 훈련 통계");
                        month_edit.setText(curr_month);
                        year_edit.setText(curr_year);
                        setMonthTrain("0","0");
                        break;
                    case R.id.search_btn:

                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }

    public String monthFormat(int i){
        if(i < 10) return "0" + String.valueOf(i);
        else return String.valueOf(i);
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