package io.github.zensu357.camswap.api101.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;

public final class XposedHelpers {
    private XposedHelpers() {
    }

    public static XposedInterface.HookHandle findAndHookMethod(String className, ClassLoader classLoader,
            String methodName, Object... parameterTypesAndCallback) {
        return findAndHookMethod(findClass(className, classLoader), methodName, parameterTypesAndCallback);
    }

    public static XposedInterface.HookHandle findAndHookMethod(Class<?> clazz, String methodName,
            Object... parameterTypesAndCallback) {
        HookSpec hookSpec = parseHookSpec(parameterTypesAndCallback, clazz.getClassLoader());
        Method method = findMethodRecursive(clazz, methodName, hookSpec.parameterTypes);
        method.setAccessible(true);
        return LegacyHookRegistrar.register(method, hookSpec.callback);
    }

    public static XposedInterface.HookHandle findAndHookConstructor(String className, ClassLoader classLoader,
            Object... parameterTypesAndCallback) {
        return findAndHookConstructor(findClass(className, classLoader), parameterTypesAndCallback);
    }

    public static XposedInterface.HookHandle findAndHookConstructor(Class<?> clazz,
            Object... parameterTypesAndCallback) {
        HookSpec hookSpec = parseHookSpec(parameterTypesAndCallback, clazz.getClassLoader());
        Constructor<?> constructor = findConstructor(clazz, hookSpec.parameterTypes);
        constructor.setAccessible(true);
        return LegacyHookRegistrar.register(constructor, hookSpec.callback);
    }

    private static Class<?> findClass(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class not found: " + className, e);
        }
    }

    private static Method findMethodRecursive(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new IllegalStateException("Method not found: " + clazz.getName() + "#" + methodName);
    }

    private static Constructor<?> findConstructor(Class<?> clazz, Class<?>[] parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Constructor not found: " + clazz.getName(), e);
        }
    }

    private static HookSpec parseHookSpec(Object[] parameterTypesAndCallback, ClassLoader classLoader) {
        if (parameterTypesAndCallback == null || parameterTypesAndCallback.length == 0) {
            throw new IllegalArgumentException("Missing method hook callback");
        }

        Object last = parameterTypesAndCallback[parameterTypesAndCallback.length - 1];
        if (!(last instanceof XC_MethodHook)) {
            throw new IllegalArgumentException("Last argument must be XC_MethodHook");
        }

        Class<?>[] parameterTypes = new Class<?>[parameterTypesAndCallback.length - 1];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = resolveParameterType(parameterTypesAndCallback[i], classLoader);
        }
        return new HookSpec(parameterTypes, (XC_MethodHook) last);
    }

    private static Class<?> resolveParameterType(Object parameterType, ClassLoader classLoader) {
        if (parameterType instanceof Class<?>) {
            return (Class<?>) parameterType;
        }
        if (parameterType instanceof String) {
            return findClass((String) parameterType, classLoader);
        }
        throw new IllegalArgumentException("Unsupported parameter type: " + parameterType);
    }

    private static final class HookSpec {
        final Class<?>[] parameterTypes;
        final XC_MethodHook callback;

        HookSpec(Class<?>[] parameterTypes, XC_MethodHook callback) {
            this.parameterTypes = parameterTypes;
            this.callback = callback;
        }
    }
}
