package com.thanhtuan.delivery.view.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.view.fragment.DetailFragment;
import com.thanhtuan.delivery.view.fragment.InfoFragment;
import com.thanhtuan.delivery.view.fragment.MapFragment;
import com.thanhtuan.delivery.sharePreference.MyShare;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.bottom_nav)      BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.toolbar_title)   TextView txtvTitleToolbar;

    private Toast toast;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        addViews();
        addControls();
    }

    private void addViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frmMain, new InfoFragment())
                .commit();
        txtvTitleToolbar.setText("Thông tin");
    }

    private void addControls() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                switch (item.getItemId()){
                    case R.id.action_info:
                        fragmentTransaction.replace(R.id.frmMain, new InfoFragment());
                        txtvTitleToolbar.setText("Thông tin");
                        break;
                    case R.id.action_map:
                        fragmentTransaction.replace(R.id.frmMain, new MapFragment());
                        txtvTitleToolbar.setText("Bản đồ");
                        break;
                    case R.id.action_detail:
                        fragmentTransaction.replace(R.id.frmMain, new DetailFragment());
                        txtvTitleToolbar.setText("Danh sách sản phẩm");
                        break;
                }

                fragmentTransaction.commit();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getStatus();
                if (status != 0){
                    setToastBack();
                }else {
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        getStatus();
        if (status != 0) {
            setToastBack();
        }
        else {
            Intent intent = new Intent(DetailActivity.this,MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    public void setEventHuy(){
        Intent intent = new Intent(DetailActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setToastBack(){
        if (toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this, "Bạn chưa hoàn thành việc giao hàng, hãy cố gắng để hoàn thành!", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void getStatus(){
        SharedPreferences pre = getSharedPreferences(MyShare.NAME, MODE_PRIVATE);
        status = pre.getInt("status",0);
    }
}
