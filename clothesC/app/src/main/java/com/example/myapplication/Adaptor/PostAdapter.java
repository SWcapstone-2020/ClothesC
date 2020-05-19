package com.example.myapplication.Adaptor;

import android.app.Activity;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.PostInfo;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.GalleryViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        GalleryViewHolder(Activity activity, CardView v, PostInfo postInfo) {
            super(v);
            cardView = v;

            LinearLayout contentsLayout = cardView.findViewById(R.id.contentLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ArrayList<String> contentList = postInfo.getContents();

            if (contentsLayout.getChildCount() == 0) {
                for (int i = 0; i < contentList.size(); i++) {
                    String contents = contentList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches()) { //내용이 url인가? (즉 이미지인가 동영상인가)
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        contentsLayout.addView(imageView);
//                        Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                    } else { //텍스트인가
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
//                        textView.setText(contents);
                        contentsLayout.addView(textView);

                    }
                }
            }
        }
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public PostAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(activity, cardView, mDataset.get(viewType));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        //제목 출력
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        //작성날짜 출력
        TextView createdAtText = cardView.findViewById(R.id.createAtTextView);
        createdAtText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        //내용 출력
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentLayout);
        ArrayList<String> contentList = mDataset.get(position).getContents();

        for (int i = 0; i < contentList.size(); i++) {
            String contents = contentList.get(i);
            if (Patterns.WEB_URL.matcher(contents).matches()) { //내용이 url인가? (즉 이미지인가 동영상인가)
                Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into((ImageView) contentsLayout.getChildAt(i));
            } else { //텍스트인가
                ((TextView) contentsLayout.getChildAt(i)).setText(contents);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}