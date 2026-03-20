package io.github.zensu357.camswap.api101.compat;

import java.lang.reflect.Executable;
import java.util.List;

import io.github.libxposed.api.XposedInterface;
import io.github.zensu357.camswap.api101.Api101Runtime;

final class LegacyHookRegistrar {
    private LegacyHookRegistrar() {
    }

    static XposedInterface.HookHandle register(Executable executable, XC_MethodHook callback) {
        return Api101Runtime.requireModule()
                .hook(executable)
                .intercept(chain -> intercept(chain, callback));
    }

    private static Object intercept(XposedInterface.Chain chain, XC_MethodHook callback) throws Throwable {
        XC_MethodHook.MethodHookParam param = new XC_MethodHook.MethodHookParam();
        param.method = chain.getExecutable();
        param.thisObject = chain.getThisObject();
        param.args = toArgs(chain.getArgs());

        try {
            callback.beforeHookedMethod(param);
        } catch (Throwable t) {
            param.setThrowable(t);
        }

        if (!param.isReturnEarly() && !param.hasThrowable()) {
            try {
                param.setResultNoEarly(chain.proceed(param.args));
            } catch (Throwable t) {
                param.setThrowableNoEarly(t);
            }
        }

        try {
            callback.afterHookedMethod(param);
        } catch (Throwable t) {
            param.setThrowable(t);
        }

        if (param.hasThrowable()) {
            throw param.getThrowable();
        }
        return param.getResult();
    }

    private static Object[] toArgs(List<Object> args) {
        return args.toArray(new Object[0]);
    }
}
