package com.rsdk.framework.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hy.sdk.HYSDK;

/**
 * firebase推送token接收器
 * 
 * @author Flyjun
 *
 */
public class FirebaseTokenReceiver extends BroadcastReceiver {
    private static String token;
    private static String refreshedToken;
    private static Context mContext = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equalsIgnoreCase(HYSDK.FIREBASE_TOKEN_ACTION)) {
            // 获取token
            mContext = context;
            token = intent.getExtras().getString(HYSDK.FIREBASE_TOKEN_VALUE);
//            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("Firebase receive", "token is =" + token);
//            FirebaseMessaging.getInstance().subscribeToTopic("news");
//            Log.d("subscribeToTopic", "news");
        }
    }
    
    public static String getFCMToken(){
        System.out.print("token is :"+token);
//        System.out.print(refreshedToken);
        Toast.makeText(mContext, "默认Toast样式",
                Toast.LENGTH_SHORT).show();
        Log.d("FirebaseTokenReceiver", "getFCMToken"+"--"+token+"--"
//                +refreshedToken
                );
        return token;
    }

}
