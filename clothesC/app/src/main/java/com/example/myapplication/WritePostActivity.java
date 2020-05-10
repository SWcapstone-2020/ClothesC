package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.myapplication.signup.InfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.opencensus.metrics.LongGauge;

public class WritePostActivity extends AppCompatActivity{
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        //확인 버튼 누르면 변화없는게 정상 ->firebase에 posts라고 하는 칸에 추가되어있는지 확인하기.
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.check:
                    writepost();
                    break;
                case R.id.image:
                    //startActivity(GalleyActivity.class,"image");
                    break;
                case R.id.video:
                   // startActivity(GalleyActivity.class,"video");
                    break;
            }
        }
    };


    private void writepost(){
        final String title=((EditText)findViewById(R.id.titleEditText)).getText().toString();
        final String contents=((EditText)findViewById(R.id.contentsEditText)).getText().toString();

        if(title.length()>0 && contents.length()>0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            Writeinfo writeinfo = new Writeinfo(title, contents,user.getUid());
            uploader(writeinfo);
        }else {
            startToast("내용을 입력해주세요.");
        }
    }


    private void uploader(Writeinfo writeinfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(writeinfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumnetSnapshot written with ID"+ documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error add documents",e);
                    }
                });

    }

    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    private void startActivity(Class c, String media){
        Intent intent=new Intent(this,c);
        intent.putExtra("media", media);
        startActivity(intent);
    }
}
