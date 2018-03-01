package com.thanhtuan.delivery.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thanhtuan.delivery.ui.main.ChuaGiaoFragment;
import com.thanhtuan.delivery.ui.main.DaGiaoFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitles = new String[]{"Chưa giao", "Đã giao",};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChuaGiaoFragment();
            case 1:
                return new DaGiaoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
