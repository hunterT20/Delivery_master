package com.thanhtuan.delivery.util;


import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogUtil {

    public static void showSweetDialogSuccess(Context context , String alert, SweetAlertDialog.OnSweetClickListener onSweetClickListener){
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Thành công!")
                .setContentText(alert)
                .setConfirmClickListener(onSweetClickListener)
                .show();
    }

    public static void showSweetDialogWarning(Context context, String alert){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Cảnh báo!")
                .setContentText(alert)
                .show();
    }
}
