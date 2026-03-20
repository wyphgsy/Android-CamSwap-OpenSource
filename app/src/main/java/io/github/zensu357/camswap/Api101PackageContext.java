package io.github.zensu357.camswap;

import android.content.pm.ApplicationInfo;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

public final class Api101PackageContext {
    public final XposedModule module;
    public final XposedModuleInterface.PackageReadyParam param;
    public final ClassLoader classLoader;
    public final String packageName;
    public final ApplicationInfo appInfo;
    public final boolean isFirstPackage;

    public Api101PackageContext(XposedModule module, XposedModuleInterface.PackageReadyParam param) {
        this.module = module;
        this.param = param;
        this.classLoader = param.getClassLoader();
        this.packageName = param.getPackageName();
        this.appInfo = param.getApplicationInfo();
        this.isFirstPackage = param.isFirstPackage();
    }
}
