package com.example.myapplication.Adaptor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Post.DetailPostActivity;
import com.example.myapplication.Post.PostInfo;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.myapplication.Util.isStorageUrl;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener=onPostListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DetailPostActivity.class);
                intent.putExtra("postInfo", mDataset.get(postViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v,postViewHolder.getAdapterPosition());
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        TextView createdAtText = cardView.findViewById(R.id.createAtTextView);
        createdAtText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentList = mDataset.get(position).getContents();

        if(contentsLayout.getTag()==null || !contentsLayout.equals(contentList)){
            contentsLayout.setTag(contentList);
            contentsLayout.removeAllViews();
            final int MORE_NEED=2;
            for (int i = 0; i < contentList.size(); i++) {
                if(i==MORE_NEED){
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("더보기...");
                    contentsLayout.addView(textView);
                    break;
                }
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else { //텍스트인가
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextColor(Color.rgb(0,0,0)); //텍스트 내용 색깔 지정
                    contentsLayout.addView(textView);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void showPopup(View v, final int position) { //수정, 삭제 메뉴 뜨게함
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        onPostListener.onModify(position);
                        return true;
                    case R.id.delete:
                        onPostListener.onDelete(position);
                        return true;
                    default:
                        return false;
                }

            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }



}