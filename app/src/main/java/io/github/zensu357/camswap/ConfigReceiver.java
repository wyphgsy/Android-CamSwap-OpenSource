package io.github.zensu357.camswap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.github.zensu357.camswap.utils.LogUtil;

public class ConfigReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (IpcContract.ACTION_REQUEST_CONFIG.equals(intent.getAction())) {
            LogUtil.log("【CS-Host】收到配置请求，正在发送当前配置 config request received");
            // Instantiate ConfigManager and set context to reload config
            ConfigManager cm = new ConfigManager();
            cm.setContext(context);
            // Send broadcast response
            cm.sendConfigBroadcast(context);
        }
    }
}
