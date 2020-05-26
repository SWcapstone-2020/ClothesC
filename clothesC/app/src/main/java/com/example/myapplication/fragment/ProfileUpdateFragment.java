package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profileupdate, container, false);
        renewProfile=view.findViewById(R.id.renewProfileImge);

        context=container.getContext();

        view.findViewById(R.id.updateButton).setOnClickListener(onClickListener);
        view.findViewById(R.id.renewProfileImge).setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.updateButton:
                    updateProfile();
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.onFragmentChanged(2);
                    break;
                case R.id.renewProfileImge:
                    loadAlbum();
                    break;
            }
        }
    };

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
