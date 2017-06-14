package com.thanhtuan.delivery.util;

import android.graphics.Color;
import android.view.View;

import com.victor.loading.newton.NewtonCradleLoading;

public class NewtonLoadingUtil {
    private NewtonCradleLoading newtonCradleLoading;

    public NewtonLoadingUtil(NewtonCradleLoading newtonCradleLoading) {
        this.newtonCradleLoading = newtonCradleLoading;
    }

    public void show(){
        newtonCradleLoading.setVisibility(View.VISIBLE);
        newtonCradleLoading.start();
        newtonCradleLoading.setLoadingColor(Color.parseColor("#FFEB903C"));
    }

    public void dismiss(){
        newtonCradleLoading.stop();
        newtonCradleLoading.setVisibility(View.GONE);
    }
}
