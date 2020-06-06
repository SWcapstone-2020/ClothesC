package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adaptor.OuterAdapter;
import com.example.myapplication.Clothes.ClothesItem;
import com.example.myapplication.Clothes.SubmitActivity;
import com.example.myapplication.Clothes.SwitchLayoutFragment;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.Util.isItemUrl;
import static com.example.myapplication.Util.storageUrlToName;

//import com.example.myapplication.ClothesItem.SwitchLayoutActivity;

public class ClothesFragment extends Fragment  implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ClothesFragment";
    private DrawerLayout mDrawerLayout;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private OuterAdapter clothesAdapter;
    private ArrayList<ClothesItem> itemList;
    private boolean updating;
    private boolean topScrolled;
    private int successCount;


    private Context mContext;
    private FloatingActionButton fab_main;
    private boolean isFabOpen = false;


    public ClothesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 툴바 메뉴 활성화
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clothes_item, container, false);
        mContext = getActivity().getApplicationContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        itemList=new ArrayList<>();
        clothesAdapter=new OuterAdapter(getActivity(),itemList);
        clothesAdapter.setOnPostListener(onPostListener);
        final RecyclerView recyclerView = view.findViewById(R.id.itemRecy);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(clothesAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    postsUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

        postsUpdate(false);


        fab_main=view.findViewById(R.id.fab_main);
        fab_main.setOnClickListener(onClickListener);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);
        actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setTitle("My Closet");


        //Navigation Drawer
        mDrawerLayout = (DrawerLayout)view.findViewById(R.id.drawer_layout);
        // fragment_clothes_item.xml 에서 지정한 id 값으로 네비게이션 드로어를 불러옴
        NavigationView navigationView = (NavigationView)view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
/*         this라고 주고 위에서 implements NavigationView.OnNavigationItemSelectedListener 이렇게 하면,
        아래에 NavigationView.OnNavigationItemSelectedListener(인터페이스)의 구현체를 따로 빼서 메소드로 처리할 수 있다.
         위 내용은 잘 모르겠음 그냥 onNavigationItemSelected 메소드(메뉴 선택 시 동작을 지정하는 메소드)를 따로 빼서 처리할 수 있다는 듯.*/

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu,menu);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_main:
                    myStartActivity(SubmitActivity.class);
                    break;
            }

        }
    };

    private void postsUpdate(final boolean clear) {
        updating = true;
        Date date = itemList.size() == 0 || clear ? new Date() : itemList.get(itemList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection("outer");
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(clear){
                                itemList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                itemList.add(new ClothesItem(
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        document.getData().get("kind").toString(),
                                        document.getId()));
                            }
                            clothesAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }


    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            final String id = itemList.get(position).getId();

            ArrayList<String> contentList = itemList.get(position).getContents();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if(isItemUrl(contents)){ //내용이 url인가? (즉 이미지인가 동영상인가)
                    successCount++;
                    StorageReference desertRef = storageRef.child("outer" + id + "/" + storageUrlToName(contents));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            showToast(ShowPostActivity.this, "삭제 했습니다.");
                            successCount--;
                            storageDeleteUpdate(id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
//                            showToast(ShowPostActivity.this, "삭제하지 못했습니다");
                        }
                    });

                }
            }
            storageDeleteUpdate(id);
        }

        @Override
        public void onModify(int position) {
            goWriteActivity(SubmitActivity.class, itemList.get(position));
        }

    };
    private void storageDeleteUpdate(String id) {
        if (successCount == 0) {
            firebaseFirestore.collection("outer").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            boolean clear=true;
                            postsUpdate(clear);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }


    private void goWriteActivity(Class c, ClothesItem clothesItem) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("item", clothesItem);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toolbar 메뉴 선택 시 동작 지정.
        // AndroidManifest.xml에서 뒤로가기시 이동할 상위 액티비티를 지정.
        //menu.xml에서 id 값으로 지정된 '액션바 메뉴'들을 불러옴
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @see NavigationView.OnNavigationItemSelectedListener
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        int id = item.getItemId();
        SwitchLayoutFragment switchLayoutFrag = new SwitchLayoutFragment();
        Bundle bundle=new Bundle(1);
        String type;
        switch (id) {
            case R.id.nav_item_outer:
               type="outer";
                bundle.putString("type", type);
                switchLayoutFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, switchLayoutFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_item_tops:
                type="shirt";
                bundle.putString("type", type);
                switchLayoutFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, switchLayoutFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_item_bottoms:
                type="pant";
                bundle.putString("type", type);
                switchLayoutFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, switchLayoutFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_item_shoes:
                type="shoes";
                bundle.putString("type", type);
                switchLayoutFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, switchLayoutFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.nav_item_etc:
                type="etc";
                bundle.putString("type", type);
                switchLayoutFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, switchLayoutFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                break;
        }
        return true;
    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }


}

