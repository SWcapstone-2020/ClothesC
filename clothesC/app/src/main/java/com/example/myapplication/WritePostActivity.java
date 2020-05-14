package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.opencensus.metrics.LongGauge;

public class WritePostActivity extends AppCompatActivity{
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;

    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentslayout);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
            if (resultCode == Activity.RESULT_OK) {
                String profilepath = data.getStringExtra("profilepath");
                pathList.add(profilepath);


                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                ImageView imageView = new ImageView(WritePostActivity.this);
                imageView.setLayoutParams(layoutParams);
                Glide.with(this).load(profilepath).override(1000).into(imageView);
                parent.addView(imageView);

                EditText editText =new EditText(WritePostActivity.this);
                editText.setLayoutParams(layoutParams);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                parent.addView(editText);
            }
            break;
            }
         }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        //확인 버튼 누르면 변화없는게 정상 ->firebase에 posts라고 하는 칸에 추가되어있는지 확인하기.
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    startActivity(GalleryActivity.class,"image");
                    break;
                case R.id.video:
                    startActivity(GalleryActivity.class,"video");
                    break;
            }
        }
    };


    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();

        if (title.length() > 0) {
            final ArrayList<String> contentsList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (view instanceof EditText) {
                    String text = ((EditText) view).getText().toString();
                    if (text.length() > 0) {
                        contentsList.add(text);
                    } else {
                        contentsList.add(pathList.get(pathCount));
                        final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/" + pathCount + "/" + "jpg");
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                //완료
                                                Writeinfo writeinfo = new Writeinfo(title, contentsList, user.getUid(), new Date());
                                                storeUpload(writeinfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }

        } else {
            startToast("제목을 입력해주세요.");
        }
    }


    private void storeUpload(Writeinfo writeinfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(writeinfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumenttSnapshot written with ID"+ documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
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
