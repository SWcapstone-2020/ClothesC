package com.example.myapplication.Adaptor;

import android.app.Activity;
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
import com.example.myapplication.Clothes.ClothesItem;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;

import java.util.ArrayList;

import static com.example.myapplication.Util.isShoesUrl;

public class ShoesAdapter extends RecyclerView.Adapter<ShoesAdapter.ItemViewHolder> {
    private ArrayList<ClothesItem> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        ItemViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ShoesAdapter(Activity activity, ArrayList<ClothesItem> myDataset) {
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
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothes, parent, false);
        final ItemViewHolder clothesViewHolder = new ItemViewHolder(cardView);
        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v,clothesViewHolder.getAdapterPosition());
            }
        });

        return clothesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.kindsText);
        String lower=mDataset.get(position).getLowerkind();
        titleTextView.setText(lower);


        LinearLayout contentsLayout = cardView.findViewById(R.id.contentLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ArrayList<String> contentList = mDataset.get(position).getContents();
        if(contentsLayout.getTag()==null || !contentsLayout.equals(contentList)){
            contentsLayout.setTag(contentList);
            contentsLayout.removeAllViews();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isShoesUrl(contents)) { //내용이 url인가? (즉 이미지인가 동영상인가)
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setPadding(10,10,50,20);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else { //텍스트인가
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setPadding(20,10,30,20);
                    textView.setTextSize(20);
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