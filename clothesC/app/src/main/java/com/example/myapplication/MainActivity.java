package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.Clothes.ClothesItemActivity;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.signup.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        //findViewById(R.id.profileButton).setOnClickListener(onClickListener);
       //findViewById(R.id.cameraButton).setOnClickListener(onClickListener);
        //findViewById(R.id.clothesPageButton).setOnClickListener(onClickListener);
       // findViewById(R.id.templogout).setOnClickListener(onClickListener);
        // 게시글 글쓰기 버튼
        //findViewById(R.id.post_write).setOnClickListener(onClickListener);
        findViewById(R.id.action_clothe).setOnClickListener(onClickListener);

    }

    private void init(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(LoginActivity.class);
        } //로그인이 되어있지 않으면 activity_main.xml을 실행하지 않고 startLoginActivity.java 실행하여 로그인 화면으로 넘어감

        //프래그먼트
       HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }


    //기능들 버튼 잠시 주석처리
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_home:
                    break;
                case R.id.action_camera:
                    break;
                case R.id.action_profile:
                    startActivity(ProfileActivity.class);
                    break;
                case R.id.action_clothe:
                    startActivity(ClothesItemActivity.class);
                    break;

//                case R.id.profileButton:
//                    startActivity(ProfileActivity.class);
//                    break;
//                case R.id.clothesPageButton:
//                    startActivity(ClothesItemActivity.class);
//                    break;
//                case R.id.cameraButton:
//                    startActivity(CameraActivity.class);
//                case R.id.templogout:
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(LoginActivity.class);
//                    finish();
//                    break;
//                case R.id.post_write:
//                    startActivity(ShowPostActivity.class);
//                    break;
            }
        }
    };



}


