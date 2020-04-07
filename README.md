
适用于Java、Android。本例子为Android项目。

 * 让你实现子库调用主项目中的类方法
 * 让你方便地调用Android的隐藏方法
 * 让你简单地调用其他类的私有方法

### 使用方法
##### **例子1：调用其他类方法**

假如子module想调用主项目中```Processor类```的```public void Processor.toast(String msg)```方法，以及静态方法```public static String toast(Context context, String msg)```。因为是子项目，一般没有办法获取到该类。我们可以使用替身类的方法实现对Processor类的实例化以及调用。代码如下。

（1）在子类中声明替换类。
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
```
（2）子类中调用的方式，调用ProcessorCopy.toast(String)方法，实际上会调用替换类Processor.toast(String)方法。
```java
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
   
##### **例子2：调用Android隐藏方法。**
该例子将会弹出toast文本`canStartActivityForResult: true`   
Activity的`canStartActivityForResult()'方法是隐藏方法，一般没有办法调用。过去经常使用的方法是重新编一个framework.jar代替Android.jar。使用Acopy可以使你很简单地调用Android的隐藏方法。

（1）声明替换类
```java
// 声明替换类
@ACopy.CopyFrom("android.app.Activity")
public class ActivityCopy extends ACopy {

    // 将实例传进来，注意只能有一个参数，并且使用@Instance注解
    public ActivityCopy(@Instance Activity activity) {
        super(activity);
    }

    //隐藏方法
    public boolean canStartActivityForResult() {
        return (boolean)super.invoke();
    }
}
```
（2）调用的地方
```java
public class MainActivity extends AppCompatActivity {

    public void invokeHide(View view) {
        ActivityCopy activityCopy = new ActivityCopy(this);
        // 隐藏方法
        boolean canStartActivityForResult = activityCopy.canStartActivityForResult();

        Toast.makeText(this, "canStartActivityForResult: " + canStartActivityForResult, Toast.LENGTH_SHORT).show();
    }
}
```

### 项目地址：

码云：[https://gitee.com/jeffwind/ACopy](https://gitee.com/jeffwind/ACopy)

Github：[https://github.com/jeffwind/ACopy](https://github.com/jeffwind/ACopy)

其实内部就一个名字叫Acopy的类，可以拷贝出来用。


----
### 作者

Jeff Zheng  

微信号：jeffwind  

欢迎加我微信。

另外还有一个使用简单的Android网络框架，地址是   
[https://github.com/jeffwind/NetCall](https://github.com/jeffwind/NetCall)  
有兴趣可以看下噢。
  
  
  
  
  
  
