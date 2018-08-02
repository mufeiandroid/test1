package com.rsdk.framework.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.hy.sdk.HYSDK;

/**
 * firebase推送信息接收器
 * @author Flyjun
 *
 */
public class FirebaseMessageReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equalsIgnoreCase(HYSDK.FIREBASE_MESSAGE_ACTION)){
			RemoteMessage remoteMessage=intent.getExtras().getParcelable(HYSDK.FIREBASE_MESSAGE_VALUE);
//			String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//			Log.d("Firebase", "remoteMessage is-->"+refreshedToken);
		}
	}

	
}
