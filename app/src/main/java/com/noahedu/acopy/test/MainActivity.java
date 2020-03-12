package com.noahedu.acopy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void invoke(View view) {
        ProcessorCopy processor = new ProcessorCopy(this);
        processor.toast("你好");
    }

    public void invokeStatic(View view) {
        String text = ProcessorCopy.toast(this, "你好吗？？");
        ((TextView)view).setText(text);
    }

    public void invokeHide(View view) {
        ActivityCopy activityCopy = new ActivityCopy(this);
        boolean canStartActivityForResult = activityCopy.canStartActivityForResult();
        // 隐藏方法
//        boolean canStartActivityForResult = this.canStartActivityForResult();

        Toast.makeText(this, "canStartActivityForResult: " + canStartActivityForResult, Toast.LENGTH_SHORT).show();
    }
}
