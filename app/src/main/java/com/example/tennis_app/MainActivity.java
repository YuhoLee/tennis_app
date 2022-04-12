package com.example.tennis_app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 프래그먼트 객체 선언
        Fragment fragmentA = new Fragment_A();
        Fragment fragmentB = new Fragment_B();
        Fragment fragmentC = new Fragment_C();
        Fragment fragmentD = new Fragment_D();

        //제일 처음 띄워줄 뷰를 세팅해줍니다. commit();까지 해줘야 합니다.
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentA).commitAllowingStateLoss();

        // 바텀 네비게이션 객체 선언
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 바텀 네비게이션 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.fragment_a:
                        // replace(프레그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentA).commitAllowingStateLoss();
                        return true;
                    case R.id.fragment_b:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentB).commitAllowingStateLoss();
                        return true;
                    case R.id.fragment_c:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentC).commitAllowingStateLoss();
                        return true;
                    case R.id.fragment_d:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentD).commitAllowingStateLoss();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}

