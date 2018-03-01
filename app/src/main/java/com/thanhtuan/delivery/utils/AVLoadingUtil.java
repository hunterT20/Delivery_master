package com.thanhtuan.delivery.utils;

import com.wang.avi.AVLoadingIndicatorView;

public class AVLoadingUtil {
    public static void startAnim(AVLoadingIndicatorView avLoadingIndicatorView){
        avLoadingIndicatorView.smoothToShow();
    }

    public static void stopAnim(AVLoadingIndicatorView avLoadingIndicatorView){
        avLoadingIndicatorView.smoothToHide();
    }
}
