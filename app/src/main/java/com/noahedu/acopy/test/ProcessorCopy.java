package com.noahedu.acopy.test;

import android.content.Context;

import com.acopy.ACopy;

// 声明替换类，类中的方法将由替换类来执行
@ACopy.CopyFrom("com.noahedu.acopy.test.Processor")
public class ProcessorCopy extends ACopy {

    // 与替换类相同的构造函数
    public ProcessorCopy(Context context) {
        super(context);
    }

    // 调用替换类的toast方法
    public void toast(String msg) {
        super.invoke(msg);
    }

    // 调用替换类的静态toast方法
    public static String toast(Context context, String msg) {
        return (String)invokeStatic(context, msg);
    }
}
