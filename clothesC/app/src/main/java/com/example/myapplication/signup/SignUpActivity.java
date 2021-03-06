package com.example.myapplication.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.myapplication.Util.showToast;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.signUpButton:
                    signUp();
                    break;
            }

        }
    };

    private void signUp(){
        String email=((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password=((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck=((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();

        if(email.length()>0 && password.length()>0 && passwordCheck.length()>0){
            if(password.equals(passwordCheck)){ //비밀번호와 비밀번호 확인이 동일하면 회원가입이 되게 함
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    showToast(SignUpActivity.this,"회원가입을 성공했습니다.");
                                    startActivity(InfoActivity.class);
                                } else {
                                    if(task.getException().toString() !=null){
                                        showToast(SignUpActivity.this,"회원가입에 실패했습니다.");
                                    }
                                }
                            }
                        });
            }
            else{
                showToast(SignUpActivity.this,"비밀번호가 일치하지 않습니다.");
            }
        }
        else{
            showToast(SignUpActivity.this,"이메일 혹은 비밀번호를 입력해주세요.");
        }

    }


    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    } //MainActivity.java 로 넘어가게 하는 함수
}
