package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.fragment.ClothesFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.example.myapplication.fragment.SearchFragment;
import com.example.myapplication.signup.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

//    private BottomNavigationView bottomNavigationView; 바텀 네비게이션 뷰
//    private FragmentManager fm;
//    private FragmentTransaction ft;
//    private FragmentHome fragment_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(LoginActivity.class);
        } //로그인이 되어있지 않으면 activity_main.xml을 실행하지 않고 startLoginActivity.java 실행하여 로그인 화면으로 넘어감

        //프래그먼트
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, homeFragment)
                                .commit();
                        return true;
                    case R.id.action_search:
                        SearchFragment searchFragment = new SearchFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, searchFragment)
                                .commit();
                        return true;
                    case R.id.action_profile:
                        ProfileFragment profileFragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, profileFragment)
                                .commit();
                    case R.id.action_clothes:
                        ClothesFragment clothesFragment = new ClothesFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, clothesFragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });

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


