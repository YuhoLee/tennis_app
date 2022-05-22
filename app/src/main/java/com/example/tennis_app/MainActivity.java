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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SendEventListener{
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    static private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listDevice = findViewById(R.id.pare_list);



        // 프래그먼트 객체 선언
        Fragment fragmentA = new Fragment_A();
        Fragment fragmentB = new Fragment_B();
        Fragment fragmentC = new Fragment_C();
        Fragment fragmentD = new Fragment_D();

        Bundle bluetoothBundle = new Bundle();


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
    @Override
    public void sendMessage(String message){
        sendData(message);
    }

    static void sendData(String text) {
        // 문자열에 개행문자("\n")를 추가해줍니다.
        text = "<" + text + ">";
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT
                }, 1);
            }
        }
        devices = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        final int pariedDeviceCount = devices.size();
        // 페어링 된 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(), "먼저 Bluetooth 설정에 들어가 페어링을 진행해 주세요.", Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        asyncDialog = new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("블루투스 연결중..");
        asyncDialog.show();
        asyncDialog.setCancelable(false);

        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT
                    }, 1);
                }
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
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{
                                    Manifest.permission.BLUETOOTH_CONNECT
                            }, 1);
                        }
                    }
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    inputStream = bluetoothSocket.getInputStream();

                    runOnUiThread(new Runnable() {
                        @SuppressLint({"ShowToast", "SetTextI18n"})
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),deviceName + " 연결 완료",Toast.LENGTH_LONG).show();
                            asyncDialog.dismiss();
                        }
                    });
                    onBT = true;
                } catch(Exception e){
                    // 블루투스 연결 중 오류 발생
                    runOnUiThread(new Runnable() {
                        @SuppressLint({"ShowToast", "SetTextI18n"})
                        @Override
                        public void run() {
                            asyncDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"블루투스 연결 오류",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        BTConnect.start();
    }

}

