package com.acopy;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用替身的方式调用项目中无法获取到的类方法
 *
 * 如子库调用主项目的类方法
 * 如调用Android.jar的隐藏方法
 * 如调用其他类的私有方法
 *
 * @Author 郑俊锋
 */
public class ACopy {

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface CopyFrom {
        /** 声明替换类 */
        String value();
    }

    /** 构造函数中可以用这个注解传入实体类进行初始化，必须只有一个参数 */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Instance {
    }

    private static final String TAG = ACopy.class.getSimpleName();
    private static Map<String, Class> PRIMATIVE_MATCH_MAP = new HashMap<>();

    static {

        PRIMATIVE_MATCH_MAP.put("boolean", Boolean.class);
        PRIMATIVE_MATCH_MAP.put("char", Character.class);
        PRIMATIVE_MATCH_MAP.put("short", Number.class);
        PRIMATIVE_MATCH_MAP.put("int", Number.class);
        PRIMATIVE_MATCH_MAP.put("long", Number.class);
        PRIMATIVE_MATCH_MAP.put("float", Number.class);
        PRIMATIVE_MATCH_MAP.put("double", Number.class);
    }

    private Object instance;

    public ACopy(Object... args) {

        initInstance(args);
    }

    public void setInstance(Object instance) {
        String className = getReplaceClassName();
        if (!instance.getClass().getName().equals(className)) {
            throw new IllegalStateException("Instance " + instance.getClass().getName() + " should be " + className);
        }
        this.instance = instance;
    }

    /**
     * 调用静态方法
     */
    protected static Object invokeStatic(Object... args) {

        args = replaceArgs(args);
        StackTraceElement stackTraceElement = getInvokeStackTraceElement();
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        Class clazz;
        try {
            clazz = Class.forName(className);
            CopyFrom copyFrom = (CopyFrom)clazz.getAnnotation(CopyFrom.class);
            className = copyFrom.value();
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        return invoke(clazz, null, methodName, args);
    }

    /**
     * 调用普通方法
     */
    protected Object invoke(Object... args) {

        args = replaceArgs(args);
        StackTraceElement stackTraceElement = getInvokeStackTraceElement();
        String methodName = stackTraceElement.getMethodName();
        return invoke(instance.getClass(), instance, methodName, args);
    }

    private void initInstance(Object... args) {
        String className = getReplaceClassName();

        if (args.length == 1) {
            Constructor<?>[] constructors = getClass().getConstructors();
            for (Constructor constructor : constructors) {
                Class[] paramTypes = constructor.getParameterTypes();
                if (!isParamsSame(args, paramTypes)) {
                    continue;
                }
                Annotation[][] annotations = constructor.getParameterAnnotations();
                for (Annotation anno : annotations[0]) {
                    if (anno instanceof Instance) {
                        instance = args[0];
                        return;
                    }
                }
                break;
            }
        }

        // 同样的构造方法初始化instance
        args = replaceArgs(args);
        try {
            Class clazz = Class.forName(className);
            Constructor c = null;
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                Class[] paramTypes = constructor.getParameterTypes();
                if (isParamsSame(args, paramTypes)) {
                    c = constructor;
                    break;
                }
            }
            if (c == null) {
                throw new IllegalStateException("Constructor not found");
            }

            instance = c.newInstance(args);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No Class [" + className + "] Found in ["
                    + getClass().getName() + "] annotation!", e);
        }  catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    /**
     * 调用替换类的某个方法
     * @param replaceClassInstance 替换类的实例，如果为static则为null
     * @param replaceClass 替换类
     * @param methodName 调用的方法名
     * @param args 调用别的方法参数
     * @return 返回参数
     */
    private static Object invoke(Class replaceClass, Object replaceClassInstance, String methodName, Object[] args) {

        Method[] methods = replaceClass.getMethods();
        for (Method method : methods) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            Class[] paramTypes = method.getParameterTypes();
            if (!isParamsSame(args, paramTypes)) {
                continue;
            }

            method.setAccessible(true);
            try {
                return method.invoke(replaceClassInstance, args);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException("No Method \"" + methodName + "(...)\" found in " +
                replaceClass.getName());
    }

    private static StackTraceElement getInvokeStackTraceElement() {

        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (stackTraces.length < 5) {
            Log.e(TAG, "Not enough method traces");
            return null;
        }
        StackTraceElement stackTraceElement = stackTraces[4];
        return stackTraceElement;
    }

    private static boolean isParamsSame(Object[] args, Class[] paramTypes) {

        if (paramTypes.length != args.length) {
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            Class paramClass = paramTypes[i];
            if (!isInstance(paramClass, args[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * obj 是否属于clazz类
     */
    private static boolean isInstance(Class clazz, Object obj) {

        Class checkClazz = obj.getClass();
        if (clazz.isPrimitive() || checkClazz.isPrimitive()) {
            if (clazz.isPrimitive() && checkClazz.isPrimitive()) {
                return clazz.getName().equals(checkClazz.getName());
            }

            Class priClass;
            Class norClass;
            if (clazz.isPrimitive()) {
                priClass = clazz;
                norClass = checkClazz;
            } else {
                priClass = checkClazz;
                norClass = clazz;
            }

            Class norClassMatch = PRIMATIVE_MATCH_MAP.get(priClass.getName());

            if (norClassMatch.isAssignableFrom(norClass)) {
                return true;
            }
            return false;
        }

        // checkClazz是否属于clazz
        if (clazz.isAssignableFrom(checkClazz)) {
            return true;
        }
        return false;
    }

    private static Object[] replaceArgs(Object[] args) {
        // TODO 将分身类的参数转化成替换类参数
        return args;
    }

    private String getReplaceClassName() {

        CopyFrom copyFrom = getClass().getAnnotation(CopyFrom.class);
        String className = copyFrom.value();
        return className;
    }
}
