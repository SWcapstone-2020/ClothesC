package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null){
            startActivity(LoginActivity.class);
        } //로그인이 되어있지 않으면 activity_main.xml을 실행하지 않고 startLoginActivity.java 실행하여 로그인 화면으로 넘어감


        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.cameraButton).setOnClickListener(onClickListener);
        findViewById(R.id.clothesPageButton).setOnClickListener(onClickListener);
        findViewById(R.id.profileChange).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut(); //로그아웃 시켜주는 함수
                    startActivity(LoginActivity.class);
                    break;
                case R.id.cameraButton:
                    changeCamera();
                    break;
                case R.id.clothesPageButton:
                    startClothesItemPageActivity();
                    break;
                case R.id.profileChange:
                    startActivity(InfoActivity.class);
                    break;

            }
        }
    };
    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }

    private void changeCamera(){
        Intent intent=new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void startClothesItemPageActivity(){
        Intent intent=new Intent(this,ClothesItemPageActivity.class);
        startActivity(intent);
    }
}
