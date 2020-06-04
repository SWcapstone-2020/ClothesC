//package com.example.myapplication.Clothes;
//
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplication.R;
//
//import java.util.List;
//
//import static com.example.myapplication.Clothes.ItemViewer.SPAN_COUNT_ONE;
//import static com.example.myapplication.Clothes.ItemViewer.SPAN_COUNT_THREE;
//
//public class SwitchLayoutActivity extends AppCompatActivity {
//    private RecyclerView recyclerView;
//    private ItemViewer itemAdapter;
//    private GridLayoutManager gridLayoutManager;
//    private List<Item> items;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_switch_layout);
//
//        // SVG 사용
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//
//        // items에 item들 할당
//        initItemsData();
//
//        // 객체 생성
//        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_ONE);
//        itemAdapter = new ItemViewer(items, gridLayoutManager);
//        // recyclerview 가져옴
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setAdapter(itemAdapter);               // ItemViewer를 어댑터로 연결
//        recyclerView.setLayoutManager(gridLayoutManager);   // GridLayoutManger 사용 선언
//    }
//
////  item 요소(해당 이미지, 제목, 설명 등) 설정한 뒤 items(배열)에 추가하는 메소드.
//    private void initItemsData() {
////        items = new ArrayList<>(4);
////        items.add(new Item(R.drawable.img1, "Image 1", 20, 33));
////        items.add(new Item(R.drawable.img2, "Image 2", 10, 54));
////        items.add(new Item(R.drawable.img3, "Image 3", 27, 20));
////        items.add(new Item(R.drawable.img4, "Image 4", 45, 67));
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
////  레이아웃 전환 아이콘 선택 시 동작을 처리하는 메소드.
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.menu_switch_layout) {
//            switchLayout();     // 레이아웃 전환
//            switchIcon(item);   // 아이콘 바꾸기
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//// 한 줄에 표시되는 아이템 수에 따라 레이아웃을 전환하는 메소드.
//    private void switchLayout() {
//        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
//            gridLayoutManager.setSpanCount(SPAN_COUNT_THREE);
//        } else {
//            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);
//        }
//
//        itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
//    }
//
////  레이아웃 전환 시 아이콘을 바꾸는 메소드.
//    private void switchIcon(MenuItem item) {
//        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_THREE) {
//            item.setIcon(getResources().getDrawable(R.drawable.ic_span_3));
//        } else {
//            item.setIcon(getResources().getDrawable(R.drawable.ic_span_1));
//        }
//    }
//}
