package com.component.learning.toast;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : View 的点击事件封装
 */
public interface OnClickListener<V extends View> {
    void onClick(XToast toast, V view);
}