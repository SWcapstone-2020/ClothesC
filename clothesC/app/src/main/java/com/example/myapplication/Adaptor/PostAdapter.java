package com.example.myapplication.Adaptor;

import android.app.Activity;
import android.util.Patterns;
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
import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    private OnPostListener onPostListener;

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
/*            LinearLayout contentsLayout = cardView.findViewById(R.id.contentLayout);
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
        }*/
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
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

        //제목 출력
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        //작성날짜 출력
        TextView createdAtText = cardView.findViewById(R.id.createAtTextView);
        createdAtText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        //내용 출력
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentList = mDataset.get(position).getContents();

        contentsLayout.removeAllViews();
        if(contentList.size()>0) {
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (Patterns.WEB_URL.matcher(contents).matches()) { //내용이 url인가? (즉 이미지인가 동영상인가)
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
                String id=mDataset.get(position).getId();
                switch (item.getItemId()) {
                    case R.id.modify:
                        onPostListener.onModify(id);
                        return true;
                    case R.id.delete:
                        onPostListener.onDelete(id);
                       /* firebaseFirestore.collection("posts").document(mDataset.get(position).getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(activity, "게시글을 삭제하였습니다",Toast.LENGTH_SHORT).show();
//                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(activity, "게시글을 삭제하지 못했습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });*/
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