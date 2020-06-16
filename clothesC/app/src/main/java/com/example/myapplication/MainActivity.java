package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.fragment.ClothesFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.fragment.ProfileUpdateFragment;
import com.example.myapplication.fragment.WeatherFragment;
import com.example.myapplication.signup.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

//import com.example.myapplication.fragment.WeatherFragment;



public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(getIntent().getIntExtra("choice", R.id.action_home));


    }

    private void init(int no) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(LoginActivity.class);
        } //로그인이 되어있지 않으면 activity_main.xml을 실행하지 않고 startLoginActivity.java 실행하여 로그인 화면으로 넘어감

        //프래그먼트
        switch (no) {
            case R.id.action_home:
                HomeFragment homeFragment = new HomeFragment();
                setFragment(homeFragment);
                break;
            case R.id.action_weather:
                WeatherFragment weatherFragment = new WeatherFragment();
                setFragment(weatherFragment);
                break;
            case R.id.action_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                setFragment(profileFragment);
                break;
            case R.id.action_clothes:
                ClothesFragment clothesFragment = new ClothesFragment();
                setFragment(clothesFragment);
                break;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        HomeFragment homeFragment = new HomeFragment();
                        setFragment(homeFragment);
                        return true;
                        /*
                    case R.id.action_weather:
                        WeatherApiFragment weatherFragment = new WeatherApiFragment();
                        setFragment(weatherFragment);
                        return true;
                         */
                    case R.id.action_weather:
                        WeatherFragment weatherFragment = new WeatherFragment();
                        setFragment(weatherFragment);
                        return true;
                    case R.id.action_profile:
                        ProfileFragment profileFragment = new ProfileFragment();
                        setFragment(profileFragment);
                        return true;
                    case R.id.action_clothes:
                        ClothesFragment clothesFragment = new ClothesFragment();
                        setFragment(clothesFragment);
                        return true;
                }
                return false;
            }
        });

    }

    public void onFragmentChanged(int index) {

        switch(index) {
            case 1:
                bottomNavigationView.setVisibility(View.GONE);
                ProfileUpdateFragment profileUpdateFragment=new ProfileUpdateFragment();
                setFragment(profileUpdateFragment);
                break;
            case 2:
                bottomNavigationView.setVisibility(View.VISIBLE);
                ProfileFragment profileFragment = new ProfileFragment();
                setFragment(profileFragment);
                break;
        }

    }
    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}



