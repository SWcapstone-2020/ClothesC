package com.example.myapplication.signup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.signup.MemberInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoActivity extends AppCompatActivity {
    private static final String TAG="InfoActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        findViewById(R.id.profileSignButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.profileSignButton:
                    profileSign();
                    break;
            }

        }
    };

    private void profileSign(){
        String name=((EditText)findViewById(R.id.nameEditText)).getText().toString(); // 닉네임 받아옴
        String birth=((EditText)findViewById(R.id.BirthdayEditText)).getText().toString(); //생년월일 받아옴

        if(name.length()>0 && birth.length()>5) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            MemberInfo memberInfo = new MemberInfo(name, birth);

            if(user !=null){
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("회원정보 등록을 성공했습니다.");
                                startActivity(MainActivity.class);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("회원정보 등록을 실패했습니다.");
                            }
                        });
                }
            }

            else{
                startToast("회원정보를 입력해주세요.");
            }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }



}
