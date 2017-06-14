package com.thanhtuan.delivery.view.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.view.adapter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)       Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView txtvTitleToolbar;
    @BindView(R.id.tabs)          TabLayout tabLayout;
    @BindView(R.id.viewpager)     ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        addControls();
        addViews();
    }

    private void addViews() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void addControls() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        txtvTitleToolbar.setText("Danh sách đơn hàng");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    public void Intent_Detail(){
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        startActivity(intent);
        finish();
    }
}
