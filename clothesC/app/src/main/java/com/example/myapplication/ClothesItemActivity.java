package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;

public class ClothesItemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_item);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//      Use Toolbar instead ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//      Using ActionBar methods
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // Custom Menu icon implement on 'onOptionsItemSelected' method.
        actionBar.setDisplayHomeAsUpEnabled(true); // BACK Button
        actionBar.setTitle("My Closet"); // ActionBar Title

//      Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        switch (id) {
            case R.id.navigation_item_outer:
                Intent switchLayout = new Intent(this, SwitchLayoutActivity.class);
                startActivity(switchLayout);
                Toast.makeText(ClothesItemActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_item_tops:
                Toast.makeText(ClothesItemActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_item_bottoms:
                Toast.makeText(ClothesItemActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_item_shoes:
                Toast.makeText(ClothesItemActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_sub_menu_item01:
                Toast.makeText(ClothesItemActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_sub_menu_item02:
                Toast.makeText(ClothesItemActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


}
