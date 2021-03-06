// 로그인 관련 기능을 하는 프로그램
package com.example.myapplication.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.myapplication.Util.showToast;

public class PasswordResetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        //▲ activity_login xml 연결

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sendButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendButton:
                    send();
                    break;
            }
        }
    };

    private void send(){
        String email=((EditText)findViewById(R.id.emailEditText)).getText().toString();

        if(email.length()>0) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast(PasswordResetActivity.this,"이메일을 보냈습니다.");
                            }
                        }
                    });
            }
        else{
            showToast(PasswordResetActivity.this,"이메일을 입력해주세요.");
        }

    }//회원가입이 이루어지는 함수


}
