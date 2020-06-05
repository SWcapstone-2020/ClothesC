package com.example.myapplication.Clothes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ItemViewer extends RecyclerView.Adapter<ItemViewer.ItemViewHolder> {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 3;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;

    private List<Item> mItems;
    private GridLayoutManager mLayoutManager;

    public ItemViewer(List<Item> items, GridLayoutManager layoutManager) {
        mItems = items; //아이템의 이미지, 제목이 담겨 있는 배열. (SwitchLayoutActivity.java에서 items 할당)
        mLayoutManager = layoutManager;
    }

//  현재 레이아웃 뷰 유형(Reclcycler/Grid) 리턴
    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount(); // 한 줄에 보여지는 아이템 개수에 따라
        if (spanCount == SPAN_COUNT_ONE) { // 1일 때,
            return VIEW_TYPE_BIG;          // 리사이클러 뷰 인자 리턴.
        } else {                           // 3일 때,
            return VIEW_TYPE_SMALL;        // 그리드 뷰 인자 리턴.
        }
    }

//  뷰홀더를 생성하는 메소드. 인자로 넘어오는 parent는 뷰를 보여줄 부모 뷰를 의미한다.
//  그리고 viewType은 아이템에 따라 서로 다른 뷰홀더를 생성할 때 사용할 수 있는 인자이다.
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  LayoutInflacter 클래스는 레이아웃 XML 파일을 자바에서 사용할 수 있는 객체로 변환하는 역할을 한다.
        //  from() 메소드에 컨텍스트를 지정하고 생성자로 받은 아이템 레이아웃을 inflate() 메소드에 지정하면 된다.
        //  이렇게 생성한 View 객체를 가지고 ViewHolder 객체를 생성해서 반환한다.
        View view;
        if (viewType == VIEW_TYPE_BIG) { // recycler view 레이아웃 가져옴
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_view, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }

//  뷰홀더에 아이템을 설정하는 메소드. 인자로는 뷰홀더와 아이템을 보여줄 위치 값이 있다.
//  어댑터 생성자로 넘어온 items에서 position에 맞는 아이템을 꺼내서 뷰홀더에 설정하면 해당 아이템이 보이게 된다.
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
//        Item item = mItems.get(position % 4);   // items 에서 데이터를 꺼내 아이템 저장
//        holder.title.setText(item.getTitle());  // 현재 아이템에 해당되는 제목 설정
//        holder.iv.setImageResource(item.getImgResId()); // 현재 아이템에 해당되는 이미지 설정

        if (getItemViewType(position) == VIEW_TYPE_BIG) { // 리사이클러 뷰일 때,
//            holder.info.setText(item.getLikes() + " likes  ·  " + item.getComments() + " comments");
        }
    }

    @Override
    public int getItemCount() {
        return 30;
    } // 가져올 아이템의 개수

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView title;
        TextView info;

        ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            // 해당하는 레이아웃의 아이템 요소 가져오기
            if (viewType == VIEW_TYPE_BIG) {  // recycler view 일 때,
                //onBindViewHolder에서 bind한 이미지, 제목, 설명을 view에 보여주기
                iv = (ImageView) itemView.findViewById(R.id.image_big);
                title = (TextView) itemView.findViewById(R.id.title_big);
                info = (TextView) itemView.findViewById(R.id.tv_info);
            } else { //grid view 일 때,
                iv = (ImageView) itemView.findViewById(R.id.image_small);
                title = (TextView) itemView.findViewById(R.id.title_small);
            }
        }
    }
}
