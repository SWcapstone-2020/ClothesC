package Adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private String[] mDataset;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public GalleryViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public GalleryAdapter(String[] myDataset){
        mDataset = myDataset;
    }

    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent,false);
        GalleryViewHolder vh = new GalleryViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.GalleryViewHolder holder, int position) {
        TextView textView = holder.cardView.findViewById(R.id.textView);
        textView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
    
}
