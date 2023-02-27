package com.cafe24.as8794.schoolbuscliker;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

public class MainActivity extends AppCompatActivity
{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1981;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2981;
    private static final String[] PERMISSIONS =
            {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    BottomNavigationView bottomNavigationView;

    ReservationFragment reservationFragment;
    SearchBusStopFragment searchBusStopFragment;
    ReservationInformation reservationInformation;
    MyInformation myInformation;

    // 네이버 맵 관련
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private boolean isSearchBusStop;

    String str_busNumber;

    String userID, userPass, userName, email, tel, address;

    private void DefaultSetting()
    {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userPass = intent.getStringExtra("userPass");
        userName = intent.getStringExtra("userName");
        email = intent.getStringExtra("email");
        tel = intent.getStringExtra("tel");
        address = intent.getStringExtra("address");

        reservationFragment.setUserID(userID);
        reservationFragment.setUserPass(userPass);
        reservationFragment.setUserName(userName);
        reservationFragment.setEmail(email);
        reservationFragment.setTel(tel);
        reservationFragment.setAddress(address);

        reservationInformation.setUserID(userID);
        reservationInformation.setUserPass(userPass);
        reservationInformation.setUserName(userName);

        myInformation.setUserID(userID);
        myInformation.setUserPass(userPass);
        myInformation.setUserName(userName);
        myInformation.setEmail(email);
        myInformation.setTel(tel);
        myInformation.setAddress(address);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, PERMISSIONS, 1000);
        isSearchBusStop = false;

        // 프래그먼트 설정
        reservationFragment = new ReservationFragment();
        searchBusStopFragment = new SearchBusStopFragment();
        reservationInformation = new ReservationInformation();
        myInformation = new MyInformation();

        DefaultSetting();

        // 네이버 맵 관련
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, reservationFragment).commit();

        // 상단바 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("예약하기");

        // 바텀네비게이션 설정
        bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.item1:
                        isSearchBusStop = false;
                        getSupportActionBar().setTitle("예약하기");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, reservationFragment).commit();
                        return true;
                    case R.id.item2:
                        isSearchBusStop = true;
                        getSupportActionBar().setTitle("정류장찾기");
                        Location();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, searchBusStopFragment).commit();
                        return true;
                    case R.id.item3:
                        isSearchBusStop = false;
                        getSupportActionBar().setTitle("예약정보");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, reservationInformation).commit();
                        return true;
                    case R.id.item4:
                        isSearchBusStop = false;
                        getSupportActionBar().setTitle("내정보");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, myInformation).commit();
                        return true;
                }
                return false;
            }
        });
    }

    // 위도 경도
    void Location()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && isSearchBusStop == true)
        {
            try
            {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                final LocationListener gpsLocationListener = new LocationListener()
                {
                    public void onLocationChanged(Location location) {

                        String provider = location.getProvider();
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        double altitude = location.getAltitude();

                        String str = "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도  : " + altitude;

//                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                        searchBusStopFragment.setLocation(longitude, latitude);
                        try
                        {
                            searchBusStopFragment.LocationPoint();
                        }
                        catch (Exception e)
                        {
                            // Toast.makeText(MainActivity.this, e + "", Toast.LENGTH_SHORT).show();
                        }


                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, gpsLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, gpsLocationListener);
            }
            catch (Exception e)
            {

            }

        }
        else
        {
            double latitude = 36.32588035150573;
            double longitude = 127.33871827934472;
            searchBusStopFragment.setLocation(longitude, latitude);
        }
    }

    // 뒤로가기
    private long tempTime = System.currentTimeMillis();
    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();
        if (System.currentTimeMillis() > tempTime + 2000)
        {
            tempTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (System.currentTimeMillis() <= tempTime + 2000)
        {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 권한 테스트
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1000:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
//                    Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
                }
                else
                {
//                    Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

}