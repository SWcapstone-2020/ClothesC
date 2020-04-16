// 로그인 관련 기능을 하는 프로그램
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //▲ activity_login xml 연결

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        //▲ activity_log xml에서 id값이 loginButton을 가지는 애가 클릭 될때 onClickListener 함수 발생
        findViewById(R.id.signButton).setOnClickListener(onClickListener);
        //▲ activity_log xml에서 id값이 signButton을 가지는 애가 클릭 될때 onClickListener 함수 발생
        findViewById(R.id.searchPassword).setOnClickListener(onClickListener);

    }


    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }//로그인 화면에서 뒤로 가기 누르면 앱 자체를 강제적으로 종료시킴
    // 로그아웃 후 다시 로그인할 때 뒤로가기 누르면 다시 로그아웃 화면으로 넘어가는 문제를 해결하기 위함

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.loginButton: //id=loginButton 불러오기
                    login();
                    break;

                case R.id.signButton:
                    startActivity(SignUpActivity.class);
                    break;

                case R.id.searchPassword:
                    startActivity(PasswordResetActivity.class);
                    break;
            }
        }
    }; //버튼이 눌릴 때 수행할 반응을 적어둔 함수

    private void login(){
        String email=((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password=((EditText)findViewById(R.id.passwordEditText)).getText().toString();

        if(email.length()>0 && password.length()>0) { //이메일과 비밀번호가 작성되었는지 체크
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인을 성공했습니다.");
                                finish();
                            } else {
                                if(task.getException().toString() !=null){
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });
            }
        else{
            startToast("이메일 혹은 비밀번호를 입력해주세요.");
        }

    }//회원가입이 이루어지는 함수

    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    } // 알림창 띄우는 함수

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    } //MainActivity.java 로 넘어가게 하는 함수

}
