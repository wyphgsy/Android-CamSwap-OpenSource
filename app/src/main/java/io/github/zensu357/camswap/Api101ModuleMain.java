package io.github.zensu357.camswap;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;
import io.github.zensu357.camswap.api101.Api101Runtime;
import io.github.zensu357.camswap.utils.LogUtil;

public class Api101ModuleMain extends XposedModule {
    private static final String TAG = "CamSwap-API101";

    private final Set<String> initializedPackages = Collections
            .newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public void onModuleLoaded(XposedModuleInterface.ModuleLoadedParam param) {
        Api101Runtime.setModule(this);
        LogUtil.log("【CS】LibXposed 已加载 framework=" + getFrameworkName()
                + " version=" + getFrameworkVersion()
                + " api=" + getApiVersion()
                + " process=" + param.getProcessName()
                + " props=" + getFrameworkProperties());
    }

    @Override
    public void onPackageReady(XposedModuleInterface.PackageReadyParam param) {
        String packageKey = param.getPackageName() + "@" + System.identityHashCode(param.getClassLoader());
        if (!initializedPackages.add(packageKey)) {
            return;
        }

        try {
            if ("io.github.zensu357.camswap".equals(param.getPackageName())) {
                hookModuleSelfCheck(param.getClassLoader());
                return;
            }
            new HookMain().handleLoadPackage(new Api101PackageContext(this, param));
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "Hook package failed: " + param.getPackageName(), t);
        }
    }

    private void hookModuleSelfCheck(ClassLoader classLoader) throws Exception {
        Class<?> activityClass = Class.forName("io.github.zensu357.camswap.MainActivity", false, classLoader);
        Method method = activityClass.getDeclaredMethod("isModuleActive");
        method.setAccessible(true);
        hook(method).intercept(chain -> Boolean.TRUE);
        LogUtil.log("【CS】已通过 API 101 原生链路 Hook MainActivity.isModuleActive");
    }
}
