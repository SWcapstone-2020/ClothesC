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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Clothes.SubmitActivity;
//import com.example.myapplication.Clothes.SwitchLayoutActivity;
import com.example.myapplication.Clothes.SwitchLayoutFragment;

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
        // this라고 주고 위에서 implements NavigationView.OnNavigationItemSelectedListener 이렇게 하면,
        //아래에 NavigationView.OnNavigationItemSelectedListener(인터페이스)의 구현체를 따로 빼서 메소드로 처리할 수 있다.
        // 위 내용은 잘 모르겠음 그냥 onNavigationItemSelected 메소드(메뉴 선택 시 동작을 지정하는 메소드)를 따로 빼서 처리할 수 있다는 듯.

        return view;
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
//                myStartActivity(SwitchLayoutFragment.class);
//                프레그먼트 전환
                SwitchLayoutFragment switchLayoutFrag = new SwitchLayoutFragment();
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

