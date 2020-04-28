package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 activity_main;
    private Frag2 activity_camera;
    private Frag3 activity_clothesbt;
    private Frag4 activity_person;
    private Frag5 activity_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null){
            startActivity(LoginActivity.class);
        } //로그인이 되어있지 않으면 activity_main.xml을 실행하지 않고 startLoginActivity.java 실행하여 로그인 화면으로 넘어감

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
//        findViewById(R.id.cameraButton).setOnClickListener(onClickListener);
        findViewById(R.id.clothesPageButton).setOnClickListener(onClickListener);

        //하단뷰 함수
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_camera:
                        setFrag(1);
                        break;
                    case R.id.action_create:
                        setFrag(2);
                        break;

                    case R.id.action_clothe:
                        setFrag(3);
                        break;

                    case R.id.action_profile:
                        setFrag(4);
                        break;
                }
                return true;
            }
        });

        activity_main = new Frag1();
        activity_camera = new Frag2();
        activity_clothesbt = new Frag3();
        activity_person = new Frag4();
        activity_write = new Frag5();

        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택.
    }


    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    startActivity(LoginActivity.class);
                    break;
                case R.id.clothesPageButton:
                    startActivity(ClothesItemActivity.class);
                    break;

            }
        }
    };

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }

    //프래그먼트가 교체가 일어나는 실행문(하단뷰)
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction(); //실질적인 프레그먼트 교체
        switch (n){
            case 0:
                ft.replace(R.id.main_frame, activity_main);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame,activity_camera);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame,activity_clothesbt);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame,activity_person);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame,activity_write);
                ft.commit();
                break;

        }
    }
}
