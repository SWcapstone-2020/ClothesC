package com.example.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.example.myapplication.signup.LoginActivity;
import com.example.myapplication.signup.MemberInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    ImageView imageView;
    FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView=(ImageView)findViewById(R.id.profileView);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://clothesc-ver1.appspot.com");
        StorageReference pathReference = storageReference.child("profileImage/"+user.getUid()+"/"+"profile.png");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(ProfileActivity.this).load(uri).into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
            }
        });

        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String name = documentSnapshot.get("name").toString();
                            String birth = documentSnapshot.get("birth").toString();
                            TextView textName=(TextView)findViewById(R.id.nameEditText);
                            textName.setText(name);
                            TextView textBirth=(TextView)findViewById(R.id.BirthdayEditText);
                            textBirth.setText(birth);
//                            Log.d(TAG, "onComplete: " +documentSnapshot.get("name").toString());
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) { //성공시 출력
//                            Log.d(TAG, "onComplete");
                        }
                    });
        }
        else{
            startActivity(LoginActivity.class);
        }

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.profileUpdate).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutButton:
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.class);
                finish();
                break;
            case R.id.profileUpdate:
                startActivity(ProfileUpdateActivity.class);
                finish();
            }
        }
    };

    private void logout(){
        auth.signOut();
//        LoginManager.getInstance().logOut();
        finish();
        startActivity(LoginActivity.class);
    }


    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    private void startActivity(Class c){
        Intent intent=new Intent(this,c);
        startActivity(intent);
    }


}
