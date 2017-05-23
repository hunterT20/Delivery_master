package com.thanhtuan.delivery.activity;

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

import com.skyfishjy.library.RippleBackground;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.fragment.DetailFragment;
import com.thanhtuan.delivery.fragment.InfoFragment;
import com.thanhtuan.delivery.fragment.MapFragment;
import com.thanhtuan.delivery.fragment.NghiemThuFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.bottom_nav) BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)    Toolbar toolbar;
    @BindView(R.id.toolbarChildren_title)   TextView txtvTitleToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        addViews();
        addControls();
    }

    /*Khởi tạo Views*/
    private void addViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        InfoFragment infoFragment = new InfoFragment();
        fragmentTransaction.replace(R.id.frmMain, infoFragment);
        fragmentTransaction.commit();
        txtvTitleToolbar.setText("Thông tin");
    }

    /*Khởi tạo controls*/
    private void addControls() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_info:
                        /*Sự kiện khi nhấn vào Nav Bottom Info*/
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        InfoFragment infoFragment = new InfoFragment();
                        fragmentTransaction.replace(R.id.frmMain, infoFragment);
                        fragmentTransaction.commit();
                        txtvTitleToolbar.setText("Thông tin");
                        break;
                    case R.id.action_map:
                        FragmentManager fm2 = getFragmentManager();
                        FragmentTransaction fragmentTransaction2 = fm2.beginTransaction();
                        MapFragment mapFragment = new MapFragment();
                        fragmentTransaction2.replace(R.id.frmMain, mapFragment);
                        fragmentTransaction2.commit();
                        txtvTitleToolbar.setText("Bản đồ");
                        break;
                    case R.id.action_detail:
                        FragmentManager fm3 = getFragmentManager();
                        FragmentTransaction fragmentTransaction3 = fm3.beginTransaction();
                        DetailFragment detailFragment = new DetailFragment();
                        fragmentTransaction3.replace(R.id.frmMain, detailFragment);
                        fragmentTransaction3.commit();
                        txtvTitleToolbar.setText("Danh sách sản phẩm");
                }
                return true;
            }
        });
    }

    /*Khởi sự kiện button back*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences pre = getSharedPreferences("MyPre", MODE_PRIVATE);
                if (pre.getInt("status",0) != 0){
                    Toast.makeText(this, "Bạn chưa hoàn thành việc giao hàng, hãy cố gắng để hoàn thành!", Toast.LENGTH_SHORT).show();
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

    public void setLayoutNghiemThu(){
        FragmentManager fm3 = getFragmentManager();
        FragmentTransaction fragmentTransaction3 = fm3.beginTransaction();
        NghiemThuFragment nghiemThuFragment = new NghiemThuFragment();
        fragmentTransaction3.replace(R.id.frmMain, nghiemThuFragment);
        fragmentTransaction3.commit();
    }
}
