package com.thanhtuan.delivery.ui.detail;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.ui.main.MainActivity;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.bottom_nav)      BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.toolbar_title)   TextView txtvTitleToolbar;

    private Toast toast;

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
                .add(R.id.frmMain, new InfoFragment())
                .commit();
        txtvTitleToolbar.setText("Thông tin");
    }

    private void addControls() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            switch (item.getItemId()){
                case R.id.action_info:
                    fragmentTransaction.replace(R.id.frmMain, new InfoFragment());
                    txtvTitleToolbar.setText("Thông tin");
                    break;
                case R.id.action_map:
                    fragmentTransaction.replace(R.id.frmMain, new MapsFragment());
                    txtvTitleToolbar.setText("Bản đồ");
                    break;
                case R.id.action_detail:
                    fragmentTransaction.replace(R.id.frmMain, new DetailFragment());
                    txtvTitleToolbar.setText("Danh sách sản phẩm");
                    break;
            }

            fragmentTransaction.commit();
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.action_info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getStatus();
                if (getStatus() != 0){
                    setToastBack();
                }else {
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
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
        if (getStatus() != 0) {
            setToastBack();
        }
        else {
            Intent intent = new Intent(DetailActivity.this,MainActivity.class);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    public void setIntent(){
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

    private int getStatus(){
        return SharePreferenceUtil.getValueStatus(this);
    }
}
