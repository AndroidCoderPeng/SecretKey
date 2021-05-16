package com.pengxh.secretkey.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

/**
 * @Author: Pengxh
 * @Time: 2021/4/12 13:20
 * @Email: 290677893@qq.com
 **/
public class ToastHelper {

    @SuppressLint({"StaticFieldLeak"})
    private static Context context;
    public static final int DEFAULT = 0;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int WARING = 3;
    public static final int INFO = 4;
    public static final int CONFUSING = 5;

    public static void initToastHelper(Context mContext) {
        ToastHelper.context = mContext.getApplicationContext();
    }

    public static void showToast(String msg, int toastStyle) {
        switch (toastStyle) {
            case DEFAULT:
                TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
                break;
            case SUCCESS:
                TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                break;
            case ERROR:
                TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                break;
            case WARING:
                TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.WARNING);
                break;
            case INFO:
                TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.INFO);
                break;
            case CONFUSING:
                TastyToast.makeText(context, msg, TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
                break;
            default:
                break;
        }
    }
}
