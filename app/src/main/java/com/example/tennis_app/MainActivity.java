package com.example.tennis_app;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private Fragment fragmentA, fragmentB, fragmentC, fragmentD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        fragmentA = new Fragment_A();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragmentA).commit();

        // 바텀 네비게이션 객체 선언
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // 바텀 네비게이션 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.fragment_a:
                        if(fragmentA == null){
                            fragmentA = new Fragment_A();
                            fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentA).commit();
                        }
                        if(fragmentA != null) fragmentManager.beginTransaction().show(fragmentA).commit();
                        if(fragmentB != null) fragmentManager.beginTransaction().hide(fragmentB).commit();
                        if(fragmentC != null) fragmentManager.beginTransaction().hide(fragmentC).commit();
                        if(fragmentD != null) fragmentManager.beginTransaction().hide(fragmentD).commit();
                        return true;

                    case R.id.fragment_b:
                        if(fragmentB == null){
                            fragmentB = new Fragment_B();
                            fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentB).commit();
                        }
                        if(fragmentA != null) fragmentManager.beginTransaction().hide(fragmentA).commit();
                        if(fragmentB != null) fragmentManager.beginTransaction().show(fragmentB).commit();
                        if(fragmentC != null) fragmentManager.beginTransaction().hide(fragmentC).commit();
                        if(fragmentD != null) fragmentManager.beginTransaction().hide(fragmentD).commit();
                        return true;

                    case R.id.fragment_c:
                        if(fragmentC == null){
                            fragmentC = new Fragment_C();
                            fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentC).commit();
                        }
                        if(fragmentA != null) fragmentManager.beginTransaction().hide(fragmentA).commit();
                        if(fragmentB != null) fragmentManager.beginTransaction().hide(fragmentB).commit();
                        if(fragmentC != null) fragmentManager.beginTransaction().show(fragmentC).commit();
                        if(fragmentD != null) fragmentManager.beginTransaction().hide(fragmentD).commit();
                        return true;

                    case R.id.fragment_d:
                        if(fragmentD == null){
                            fragmentD = new Fragment_D();
                            fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentD).commit();
                        }
                        if(fragmentA != null) fragmentManager.beginTransaction().hide(fragmentA).commit();
                        if(fragmentB != null) fragmentManager.beginTransaction().hide(fragmentB).commit();
                        if(fragmentC != null) fragmentManager.beginTransaction().hide(fragmentC).commit();
                        if(fragmentD != null) fragmentManager.beginTransaction().show(fragmentD).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


}

