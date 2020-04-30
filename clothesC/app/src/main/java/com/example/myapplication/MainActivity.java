package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.signup.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

//    private BottomNavigationView bottomNavigationView; 바텀 네비게이션 뷰
//    private FragmentManager fm;
//    private FragmentTransaction ft;
//    private FragmentHome fragment_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(LoginActivity.class);
        } //로그인이 되어있지 않으면 activity_main.xml을 실행하지 않고 startLoginActivity.java 실행하여 로그인 화면으로 넘어감

        findViewById(R.id.profileButton).setOnClickListener(onClickListener);
        findViewById(R.id.cameraButton).setOnClickListener(onClickListener);
        findViewById(R.id.clothesPageButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.profileButton:
                    startActivity(ProfileActivity.class);
                    break;
                case R.id.clothesPageButton:
                    startActivity(ClothesItemActivity.class);
                    break;
                case R.id.cameraButton:
                    startActivity(CameraActivity.class);

            }
        }
    };

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }


/*        bottomNavigationView = findViewById(R.id.bottomNavi);
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

        fragment_home = new FragmentHome();
        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택.
    }

    //프래그먼트가 교체가 일어나는 실행문(하단뷰)
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction(); //실질적인 프레그먼트 교체
        switch (n){
            case 0:
                ft.replace(R.id.main_frame, fragment_home);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame,fragment_board);
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
    }*/
}


