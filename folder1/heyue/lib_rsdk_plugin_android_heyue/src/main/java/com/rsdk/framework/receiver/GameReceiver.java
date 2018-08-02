package com.rsdk.framework.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hy.sdk.HYSDK;
import com.rsdk.framework.UserHeyue;
import com.rsdk.framework.UserWrapper;

public class GameReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        // 處理sessionid過期(在這裡處理遊戲的內部邏輯，然後重新拉起sdk的登錄頁，刷新用戶信息、伺服器信息
        // 例如：遊戲回到登錄頁，拉起sdk的登錄頁重新登錄然後進入遊戲)
        if (intent.getAction().equalsIgnoreCase(HYSDK.SESSION_ID_ACTION)) {
            Log.d("RSDK", "session is out of date!");
            // 回到游戏登录界面
            UserWrapper.onActionResult(UserHeyue.mAdapter, UserWrapper.ACTION_RET_LOGIN_SUCCESS, "session is out of date");
            // Intent i = new Intent(context, MainActivity.class);
            //// Intent intent = new Intent();
            //// intent.setAction(this.getPackageName());
            //// startActivity(intent);
            // i.putExtra("test", true);
            // context.startActivity(i);
        }
    }

}
