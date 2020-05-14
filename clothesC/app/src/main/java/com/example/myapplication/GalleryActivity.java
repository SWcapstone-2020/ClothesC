package com.example.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

import com.example.myapplication.Adaptor.GalleryAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);

        final int numberofcolumns = 3; //세줄씩 이미지가 나오게끔
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberofcolumns));

        mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity){
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;

        //동영상
        Intent intent = getIntent();
        //final int media = intent.getIntExtra(INTENT_MEDIA, GALLERY_IMAGE);
        if(intent.getStringExtra("media").equals("video")){
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else{
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }


        //이미지 uri 가져와서 처리
        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);


        while(cursor.moveToNext()){
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

}
