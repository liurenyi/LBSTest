package com.android.lbstest.lbstest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public TextView positionText;
    public LocationClient mLocationClient;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_main);
        positionText = (TextView) findViewById(R.id.position_text);
        dialog = new ProgressDialog(MainActivity.this);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            Log.e("liu", "11111");
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            Log.e("liu", "2222");
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("liu", "3333");
        }
        if (!permissionList.isEmpty()) {
            Log.e("liu", "......");
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    public void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能正常使用", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {

                }
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {


        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("经度： ").append(bdLocation.getLatitude()).append("\n");
                    sb.append("维度： ").append(bdLocation.getLongitude()).append("\n");
                    sb.append("国家： ").append(bdLocation.getCountry()).append("\n");
                    sb.append("省 ：").append(bdLocation.getProvince()).append("\n");
                    sb.append("市： ").append(bdLocation.getCity());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                showLocation(sb.toString());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });


        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    public void showLocation(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                positionText.setText(result);
            }
        });


    }
}
