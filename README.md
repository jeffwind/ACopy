# ACopy 让你轻松调用本来无法调用的类方法

适用于Java、Android。本库中的例子为Android项目。

 * 他能让你子库调用主项目的类方法
 * 他能让你调用Android的隐藏方法
 * 他能让你如调用其他类的私有方法

### 使用方法
例子1：调用其他类方法
```java
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

public class Main {
    public void invoke(View view) {
        // 最终会调用Processor类的toast方法
        new ProcessorCopy(this).toast("你好");
        processor.toast("你好");

        // 最终会调用Processor类的toast静态方法
        ProcessorCopy.toast(this, "你好吗？？");
    }
}
```
  
例子2：调用Android隐藏方法。
该例子将会弹出toast文本`canStartActivityForResult: true`
```java
// 声明替换类
@ACopy.CopyFrom("android.app.Activity")
public class ActivityCopy extends ACopy {

    // 将实例传进来
    public ActivityCopy(@Instance Activity activity) {
        super(activity);
    }

    //隐藏方法
    public boolean canStartActivityForResult() {
        return (boolean)super.invoke();
    }
}

public class MainActivity extends AppCompatActivity {

    public void invokeHide(View view) {
        ActivityCopy activityCopy = new ActivityCopy(this);
        // 隐藏方法
        boolean canStartActivityForResult = activityCopy.canStartActivityForResult();

        Toast.makeText(this, "canStartActivityForResult: " + canStartActivityForResult, Toast.LENGTH_SHORT).show();
    }
}
```
----
### 作者

Jeff Zheng  

微信号：jeffwind  

觉得不错的话加个好友吧，欢迎骚扰：)
  
  
  
  
  
