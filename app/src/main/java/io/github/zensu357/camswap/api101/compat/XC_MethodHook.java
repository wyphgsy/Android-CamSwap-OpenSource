package io.github.zensu357.camswap.api101.compat;

import java.lang.reflect.Executable;

public abstract class XC_MethodHook {
    public static class MethodHookParam {
        public Executable method;
        public Object thisObject;
        public Object[] args;
        Object result;
        Throwable throwable;
        boolean returnEarly;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
            this.throwable = null;
            this.returnEarly = true;
        }

        void setResultNoEarly(Object result) {
            this.result = result;
            this.throwable = null;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public boolean hasThrowable() {
            return throwable != null;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
            if (throwable != null) {
                this.result = null;
                this.returnEarly = true;
            } else {
                this.returnEarly = false;
            }
        }

        void setThrowableNoEarly(Throwable throwable) {
            this.throwable = throwable;
            if (throwable != null) {
                this.result = null;
            }
        }

        boolean isReturnEarly() {
            return returnEarly;
        }
    }

    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    }

    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    }
}
