package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Clothes.SwitchLayoutActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class ClothesFragment extends Fragment  implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ClothesFragment";
    private DrawerLayout mDrawerLayout;

    private Context mContext;
    private FloatingActionButton fab_main;
    private boolean isFabOpen = false;


    public ClothesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clothes_item, container, false);

        mContext = getActivity().getApplicationContext();

        fab_main=view.findViewById(R.id.fab_main);

        fab_main.setOnClickListener(onClickListener);

        //      기본 액션바 대신 툴바 사용선언
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar();

        //툴바에 액션바 기능 사용
        final ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();

       // actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setDisplayShowTitleEnabled(false);

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }


        //  actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // 뒤로가기 버튼을 해당 아이콘으로 커스텀
        //actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 생성


//        actionBar.setTitle("My Closet"); // 상단바 title 지정 //에러

        //Navigation Drawer
        mDrawerLayout = (DrawerLayout)view.findViewById(R.id.drawer_layout);
        // fragment_clothes_item.xml 에서 지정한 id 값으로 네비게이션 드로어를 불러옴
        NavigationView navigationView = (NavigationView)view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        // this라고 주고 위에서 implements NavigationView.OnNavigationItemSelectedListener 이렇게 하면,
        //아래에 NavigationView.OnNavigationItemSelectedListener(인터페이스)의 구현체를 따로 빼서 메소드로 처리할 수 있다.
        // 위 내용은 잘 모르겠음 그냥 onNavigationItemSelected 메소드(메뉴 선택 시 동작을 지정하는 메소드)를 따로 빼서 처리할 수 있다는 듯.

        return view;
    }
    public void onResume() {
        super.onResume();
        getActivity().setTitle("My Closet");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu,menu);


    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
     */


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_main:
                    toggleFab();
                    break;
            }

        }
    };

    private void toggleFab() {
        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_span_1);

            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_span_1);

            isFabOpen = true;
        }
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
        // 네비게이션 드로어 메뉴 선택 시 동작 지정.
        // 네비게이션 드로어 메뉴가 선택되면, 네비게이션 드로어 닫힘
        item.setChecked(true);
        mDrawerLayout.closeDrawers();

        // drawer.xml에서 id 값으로 지정된 네비게이션드로어 메뉴들을 불러옴
        int id = item.getItemId();
        switch (id) {
            case R.id.cardigan:
                Intent switchLayout = new Intent(getActivity(), SwitchLayoutActivity.class);
                startActivity(switchLayout);
                break;
        }
        return true;
    }


/*
    @Override
    public void onBackPressed() {
        // 뒤로가기 시 네비게이션 드로어 닫힘
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
 */

}

