package io.github.zensu357.camswap.api101.compat;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.libxposed.api.XposedInterface;
import io.github.zensu357.camswap.api101.Api101Runtime;

public final class XposedBridge {
    private static final Map<Method, XposedInterface.Invoker<?, Method>> METHOD_INVOKERS = new ConcurrentHashMap<>();
    private static final Map<Constructor<?>, XposedInterface.CtorInvoker<?>> CONSTRUCTOR_INVOKERS = new ConcurrentHashMap<>();

    private XposedBridge() {
    }

    public static Object invokeOriginalMethod(Executable executable, Object thisObject, Object[] args)
            throws Throwable {
        Object[] actualArgs = args != null ? args : new Object[0];
        if (executable instanceof Method) {
            return getMethodInvoker((Method) executable).invoke(thisObject, actualArgs);
        }
        if (executable instanceof Constructor<?>) {
            return getConstructorInvoker((Constructor<?>) executable).newInstance(actualArgs);
        }
        throw new IllegalArgumentException("Unsupported executable type: " + executable);
    }

    public static void log(String message) {
        if (message == null) {
            message = "null";
        }
        if (Api101Runtime.getModule() != null) {
            Api101Runtime.getModule().log(Log.INFO, "CamSwap", message);
        } else {
            Log.i("LSPosed-Bridge", message);
        }
    }

    private static XposedInterface.Invoker<?, Method> getMethodInvoker(Method method) {
        return METHOD_INVOKERS.computeIfAbsent(method,
                key -> Api101Runtime.requireModule().getInvoker(key).setType(XposedInterface.Invoker.Type.ORIGIN));
    }

    @SuppressWarnings("unchecked")
    private static XposedInterface.CtorInvoker<Object> getConstructorInvoker(Constructor<?> constructor) {
        return (XposedInterface.CtorInvoker<Object>) CONSTRUCTOR_INVOKERS.computeIfAbsent(constructor,
                key -> Api101Runtime.requireModule()
                        .getInvoker((Constructor<Object>) key)
                        .setType(XposedInterface.Invoker.Type.ORIGIN));
    }
}
