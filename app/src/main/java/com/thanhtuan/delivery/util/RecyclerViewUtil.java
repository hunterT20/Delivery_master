package com.thanhtuan.delivery.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewUtil {
    public static void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter, Context context){
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
    }
}
