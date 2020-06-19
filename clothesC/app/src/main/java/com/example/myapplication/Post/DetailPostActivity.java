package com.example.myapplication.Post;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.myapplication.Util.isStorageUrl;

public class DetailPostActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_post);

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.view_post,null);
        CardView cardView=(CardView)view.findViewById(R.id.menu);
        cardView.setVisibility(View.GONE);


        PostInfo postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        //제목 출력
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(postInfo.getTitle());

        //작성날짜 출력
        TextView createdAtText =findViewById(R.id.createAtTextView);
        createdAtText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));

        //내용 출력
        LinearLayout contentsLayout = findViewById(R.id.contentLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentList = postInfo.getContents();

        if(contentsLayout.getTag()==null || !contentsLayout.equals(contentList)){
            contentsLayout.setTag(contentList);
            contentsLayout.removeAllViews();
            final int MORE_NEED=2;
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) { //내용이 url인가? (즉 이미지인가 동영상인가)
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else { //텍스트인가
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextColor(Color.rgb(0,0,0)); //텍스트 내용 색깔 지정
                    contentsLayout.addView(textView);
                }
            }
        }
    }
}
