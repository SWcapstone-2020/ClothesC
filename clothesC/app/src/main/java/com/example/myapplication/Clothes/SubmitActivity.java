package com.example.myapplication.Clothes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.GalleryActivity;
import com.example.myapplication.R;
import com.example.myapplication.Util;
import com.example.myapplication.view.ClothesImgView;
import com.example.myapplication.view.ContentsItemView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

import static com.example.myapplication.Util.GALLERY_IMAGE;
import static com.example.myapplication.Util.INTENT_MEDIA;
import static com.example.myapplication.Util.INTENT_PATH;
import static com.example.myapplication.Util.isImageFile;
import static com.example.myapplication.Util.isStorageUrl;
import static com.example.myapplication.Util.showToast;
import static com.example.myapplication.Util.storageUrlToName;

public class SubmitActivity extends AppCompatActivity {
    private static final String TAG = "Submit";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageVIew;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private Item item;
    private int pathCount, successCount;
    private Util util;


    private String clotheskind;

    private ArrayAdapter<CharSequence> kindspinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_clothe);

        util = new Util(this);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLyaout);
        contentsEditText = findViewById(R.id.contentsEditText);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);


        FirebaseStorage storage = FirebaseStorage.getInstance(); //파이어스토어 초기화
        storageRef = storage.getReference();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("옷 종류를 선택하세요");
        kindspinner = ArrayAdapter.createFromResource(this, R.array.kindsClothes, android.R.layout.simple_spinner_item);
        kindspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(kindspinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clotheskind = kindspinner.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showToast(SubmitActivity.this, "종류를 선택해주세요.");
            }
        });

        item = (Item) getIntent().getSerializableExtra("item");
        postInit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //갤러리 불러와서 선택한 이미지 저장하는 코드
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);
                    ClothesImgView clothesImgView = new ClothesImgView(this);

                    if (selectedEditText == null) {
                        parent.addView(clothesImgView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(clothesImgView, i + 1);
                                break;
                            }
                        }
                    }

                    clothesImgView.setImage(path);
                    clothesImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    clothesImgView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageVIew.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageVIew);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    final View selectedView = (View) selectedImageVIew.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                    if(isStorageUrl(path)){
                        StorageReference desertRef = storageRef.child(clotheskind+"/" + item.getId() + "/" + storageUrlToName(path));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(SubmitActivity.this, "파일을 삭제하였습니다.");
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(SubmitActivity.this, "파일을 삭제하는데 실패하였습니다.");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };

    private void storageUpload() {
        loaderLayout.setVisibility(View.VISIBLE); // 게시글 올릴 때 로딩화면을 나오게 함
        final ArrayList<String> contentsList = new ArrayList<>(); // 입력한 내용을 배열로 저장
        final ArrayList<String> formatList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser(); //파이어베이스에서 유저를 불러옴

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        //205~207까지 파이어베이스 사용을 위해 쓰는 코드

        Item item = (Item) getIntent().getSerializableExtra("item");

        final DocumentReference documentReference = item == null ? firebaseFirestore.collection(clotheskind).document() : firebaseFirestore.collection(clotheskind).document(item.getId());
//            firebaseFirestore.collection == (홈페이지) 파이어베이스 데이터베이스에서 괄호 안에 적힌 것과 같은 폴더에 접근함.


        final Date date = item == null ? new Date() : item.getCreatedAt();  //수정시 수정된 날짜로 변경되는걸 방지
        for (int i = 0; i < parent.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
            for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                View view = linearLayout.getChildAt(ii);
                if (view instanceof EditText) {
                    String text = ((EditText) view).getText().toString();
                    if (text.length() > 0) {
                        contentsList.add(text);
                        formatList.add("text");
                    }
                } else if (!isStorageUrl(pathList.get(pathCount))) {
                    String path = pathList.get(pathCount);
                    successCount++;
                    contentsList.add(path);
                    if (isImageFile(path)) {
                        formatList.add("image");
                    } else {
                        formatList.add("text");
                    }
                    String[] pathArray = path.split("\\.");
                    final StorageReference mountainImagesRef = storageRef.child(clotheskind+"/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                    //238~239 파이어베이스 스토어에 이미지 저장
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
                                            Item item = new Item(contentsList, formatList, user.getUid(), date,clotheskind);
                                            storeUpload(documentReference, item);
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

//            String title, ArrayList contents, ArrayList formats, String publisher, Date createdAt, String season, String kind
        if (successCount == 0) {
            storeUpload(documentReference, new Item(contentsList, formatList, user.getUid(), date,clotheskind));
        }
    }

    private void storeUpload(DocumentReference documentReference, final Item item) {
        documentReference.set(item.getItemInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE); //업로드 성공시 로딩 화면 끄게 함
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("item", item);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void postInit() {
        if (item != null) {
            ArrayList<String> contentsList = item.getContents(); //contentsList에 작성한 내용을 넣음
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) { // isStorageUrl = 파이어베이스 데이터베이스 주소 불러옴
                    pathList.add(contents);
                    ContentsItemView contentsItemView = new ContentsItemView(this);
                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if (i < contentsList.size() - 1) {
                        String nextContents = contentsList.get(i + 1);
                        if (!isStorageUrl(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }
                } else if (i == 0) {
                    contentsEditText.setText(contents);
                }
            }
        }
    }

    private void myStartActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);
        startActivityForResult(intent, requestCode);
    }
}