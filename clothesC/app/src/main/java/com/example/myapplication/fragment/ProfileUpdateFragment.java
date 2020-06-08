package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;

public class ProfileUpdateFragment extends Fragment {
    ImageView renewProfile;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  final int GALLERY_CODE = 10;
    private Context context;

    public ProfileUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 툴바 메뉴 활성화
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profileupdate, container, false);
        renewProfile=view.findViewById(R.id.renewProfileImge);

        context=container.getContext();

        // 툴바 추가
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        // 툴바 홈 버튼
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Drawable img_drawble = getResources().getDrawable(R.drawable.ic_cross);
        img_drawble.mutate();
        img_drawble.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        activity.getSupportActionBar().setHomeAsUpIndicator(img_drawble);
        view.findViewById(R.id.renewProfileImge).setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.renewProfileImge:
                    loadAlbum();
                    break;
            }
        }
    };

    // 툴바 메뉴(버튼) 추가
    // https://hjink247.tistory.com/17
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MenuInflater inflater = getMenuInflater();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_update_menu, menu);;
    }

    // 툴바 버튼 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.updateButton:
                updateProfile();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.onFragmentChanged(2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateProfile(){
        String renewalName=((TextView)getView().findViewById(R.id.updateName)).getText().toString();
        String renewalIntro=((TextView)getView().findViewById(R.id.updateIntro)).getText().toString();
        DocumentReference users = db.collection("users").document(user.getUid());

        users.update("name",renewalName, "intro", renewalIntro)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"프로필 수정 성공.",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"프로필 수정 실패.",Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://clothesc-ver1.appspot.com");
        if (requestCode == GALLERY_CODE) {
            StorageReference desertRef = storageRef.child("profileImage/" + user.getUid()+"/"+"profile.PNG");
            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context,"기존 이미지 삭제 성공",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context,"기존 이미지 삭제 실패.",Toast.LENGTH_LONG).show();
                }
            });
            Uri file = Uri.fromFile(new File(getPath(data.getData())));
            String profile=file.getLastPathSegment();
            profile="profile.PNG";
            StorageReference riversRef = storageRef.child("profileImage/" + user.getUid()+"/"+profile);
            UploadTask uploadTask = riversRef.putFile(file);

            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());

                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();

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
        CursorLoader cursorLoader=new CursorLoader(context,uri,proj,null,null,null);

        Cursor cursor=cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);

    }
}
