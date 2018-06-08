package com.example.hndingyl.logtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_BOOT)){
            Log.d("dingyl","onBoot");
            Intent intent1 = new Intent(context,MyService.class);
            context.startService(intent1);
        }
    }
}
