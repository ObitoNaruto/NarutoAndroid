
package com.naruto.mobile.h5container.plugin;

import android.text.TextUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.naruto.mobile.h5container.api.H5Intent;
import com.naruto.mobile.h5container.api.H5IntentFilter;
import com.naruto.mobile.h5container.api.H5Plugin;
import com.naruto.mobile.h5container.util.H5Log;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Func {
    String value();
}

public class H5FuncPlugin implements H5Plugin {

    public static final String TAG = "H5FuncPlugin";

    private Map<String, Method> funcs;
    private Object obj;

    public H5FuncPlugin(Object obj) {
        funcs = new HashMap<String, Method>();
        if (obj == null) {
            return;
        }
        register(obj);
        this.obj = obj;
    }

    @Override
    public void onRelease() {
        obj = null;
        funcs.clear();
        funcs = null;
    }

    @Override
    public boolean interceptIntent(H5Intent intent) {
        return false;
    }

    @Override
    public void getFilter(H5IntentFilter filter) {
        Iterator<String> iterator = funcs.keySet().iterator();
        while (iterator.hasNext()) {
            String action = iterator.next();
            filter.addAction(action);
        }
    }

    @Override
    public boolean handleIntent(H5Intent intent) {
        String action = intent.getAction();
        if (funcs.containsKey(action)) {
            Method method = funcs.get(action);

            int len = method.getParameterTypes().length;
            try {
                if (len == 0) {
                    method.invoke(obj);
                } else {
                    method.invoke(obj, intent);
                }
            } catch (Exception e) {
            }
            return true;
        }
        return false;
    }

    private void register(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Func.class)) {
                continue;
            }

            String name = method.getAnnotation(Func.class).value();
            if (TextUtils.isEmpty(name)) {
                continue;
            }

            H5Log.d(TAG, "object func name " + name);
            funcs.put(name, method);
        }
    }
}
