package com.example.myapplication.signup;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.GalleryActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.ProfileUpdateActivity;
import com.example.myapplication.R;
import com.example.myapplication.signup.MemberInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InfoActivity extends AppCompatActivity {
    private  final String TAG="InfoActivity";
    private  final int GALLERY_CODE = 10;
    ImageView profileImageView;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        findViewById(R.id.profileSignButton).setOnClickListener(onClickListener);
        findViewById(R.id.startProfileImage).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
        profileImageView=(ImageView)findViewById(R.id.startProfileImage);

        storage=FirebaseStorage.getInstance();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.profileSignButton:
                    profileSign();
                    break;
                    /* 잠깐 주석처리
                case R.id.startProfileImage:
                    loadAlbum();
                    break;
                    */

                //case R.id.profileView:
                case R.id.picture:
                    startActivity(CameraActivity.class);
                    break;
                case R.id.gallery:
                    if(ContextCompat.checkSelfPermission(InfoActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(InfoActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        if (ActivityCompat.shouldShowRequestPermissionRationale(InfoActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        } else {
                            startToast("권한을 허용해주세요.");
                        }
                    }else {
                        startActivity(GalleryActivity.class);
                    }
                    break;
            }

        }
    };


/*
    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }

 */

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {

            Uri file = Uri.fromFile(new File(getPath(data.getData())));
            StorageReference storageRef = storage.getReferenceFromUrl("gs://clothesc-ver1.appspot.com"); //storage 서버로 이동
            String profile=file.getLastPathSegment();
            profile="profile.png";
            StorageReference riversRef = storageRef.child("profileImage/" + user.getUid()+"/"+profile);
            UploadTask uploadTask = riversRef.putFile(file);

            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getContentResolver().openInputStream(data.getData());

                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                // 이미지 표시
                profileImageView.setImageBitmap(img);
                //영상코드
                //profilPath = data.getStringExtra("profilePath");
                //Bitmap bmp = BitmapFactory.decorderFile(profilePath);
                // profileImageView.setImageBitmap(bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });


        }
    }
    public String getPath(Uri uri){
        String [] proj={MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader=new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor=cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);

    }


    private void profileSign(){
        final String name=((EditText)findViewById(R.id.nameEditText)).getText().toString(); // 닉네임 받아옴
        final String birth=((EditText)findViewById(R.id.BirthdayEditText)).getText().toString(); //생년월일 받아옴

        if(name.length()>0 && birth.length()>5) {
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
        //startActivity(intent);
        startActivityForResult(intent, 0);
    }



}
