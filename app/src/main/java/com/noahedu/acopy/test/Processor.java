package com.noahedu.acopy.test;

import android.content.Context;
import android.widget.Toast;

public class Processor {

    private Context context;

    public Processor(Context context) {
        this.context = context;
    }

    // 非静态方法
    public void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    // 静态方法
    public static String toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        return msg;
    }
}
