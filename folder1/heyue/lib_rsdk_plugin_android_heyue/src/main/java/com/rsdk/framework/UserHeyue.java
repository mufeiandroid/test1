package com.rsdk.framework;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hy.sdk.HYSDK;
import com.rsdk.framework.GameUserInfo;
import com.rsdk.framework.HeyueWrapper;
import com.rsdk.framework.ILoginCallback;
import com.rsdk.framework.InterfaceUser;
import com.rsdk.framework.PluginWrapper;
import com.rsdk.framework.UserWrapper;
import com.rsdk.framework.Wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author cwj
 * @ClassName: UserHeyue
 * @Description:用户功能类
 * @date 2015-2-12 上午9:47:07
 */
public class UserHeyue implements InterfaceUser {

    private static final String LOG_TAG = "UserHeyue";

    private Context mContext = null;
    public static InterfaceUser mAdapter = null;
    private static String RoleLevel;
    private static String serverCode;
    private static String personalId;
    private static String gameCode;
    private static String userId;
    private static String checkURL;
    //    private static String postPushInfoUrl = "";
    JSONObject jsonObject;

    public static String getServerCode() {
        return serverCode;
    }

    public static String getPersonalId() {
        return personalId;
    }

    public UserHeyue(Context context) {
        mContext = context;
        mAdapter = this;
        configDeveloperInfo(Wrapper.getDeveloperInfo());// 初始化
    }

    @Override
    public String getPluginId() {
        LogD("getPluginId() invoked!");
        return HeyueWrapper.getPluginId();
    }

    @Override
    public String getPluginVersion() {
        LogD("getPluginVersion() invoked!");
        return HeyueWrapper.getPluginVersion();
    }

    @Override
    public String getSDKVersion() {
        LogD("getSDKVersion() invoked!");
        return HeyueWrapper.getSDKVersion();
    }

    @Override
    public String getUserID() {
        LogD("getUserID() invoked!");
        return HeyueWrapper.getUserID();
    }

    @Override
    public boolean isLogined() {
        LogD("isLogined() invoked!");
        return HeyueWrapper.getInstance().isLogined();
    }

    @Override
    public boolean isSupportFunction(String functionName) {
        LogD("isSupportFunction(" + functionName + ") invoked!");
        Method[] methods = com.rsdk.framework.UserHeyue.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(functionName)) {
                return true;
            }
        }
        return false;
    }

//    public void postPushInfoToGameServer() {
//        postPushInfoUrl = HeyueWrapper.getPostPushInfoUrl();
//
//    }

    /**
     * 登录调用
     *
     * @Description:如果SDK的登录回调在初始化的时候已经定义这里就不用传回调接口了
     */
    @Override
    public void login() {
        LogD("login() invoked!");
        HeyueWrapper.getInstance().userLogin(new ILoginCallback() {
            @Override
            public void onFailed(int arg0, String arg1) {
                actionResult(arg0, arg1);
            }

            @Override
            public void onSuccessed(int arg0, String arg1) {
                actionResult(arg0, arg1);
            }
        });
    }

    /**
     * 获取用户类型
     */
    @Override
    public String getLoginUserType() {
        return HeyueWrapper.getInstance().getLoginUserType();
    }

    /**
     * 获取用户前缀
     */
    @Override
    public String getUserIDPrefix() {
        return HeyueWrapper.getInstance().getUserIDPrefix();
    }

    /**
     * 获取有前缀的用户名
     */
    @Override
    public String getUserIDWithPrefix() {
        return HeyueWrapper.getInstance().getUserIDWithPrefix();
    }

    /**
     * 设置游戏信息
     */
    @Override
    public void setGameUserInfo(GameUserInfo userInfo) {
        LogD("setGameUserInfo test invoke!");
        String scene_Id = userInfo.logType;
        LogD("setGameUserInfo---->" + scene_Id);
        personalId = userInfo.gameUserID;
        String roleName = userInfo.gameUserName;
        serverCode = userInfo.zoneID;
        String roleLevel = userInfo.level;
        HeyueWrapper.callbackPushInfoToGameServer(personalId, roleName, serverCode);
        // 调用检查伺服器接口上传服务器信息
        if (scene_Id.equals("0") || scene_Id.equals("3")) {
            LogD("判断状态");
            // 登录账号成功后调用检测伺服器接口
            // 因使用我们的服务器，在游戏登录成功之后调用checkSever接口传区服ID
            HYSDK.saveRoleName((Activity) mContext, personalId, roleName, roleLevel);
        } else if (scene_Id.equals("1")) {
            LogD("save role infomation!!");
            HYSDK.newRoleName((Activity) mContext, personalId, roleName);
            HYSDK.saveRoleName((Activity) mContext, personalId, roleName, roleLevel);
            LogD("获取用户信息" + personalId + roleName + roleLevel);
        } else if (scene_Id.equals("2")) {
            scene_Id = "levelUp";
        }

    }

    @Override
    public void setDebugMode(boolean bDebug) {
        LogD("setDebugMode(" + bDebug + ") invoked! it is not used.");
        // it is not used.
    }

    /**
     * 调用wrapper初始化
     *
     * @param cpInfo
     */
    private void configDeveloperInfo(Hashtable<String, String> cpInfo) {
        LogD("configDeveloperInfo(" + cpInfo.toString() + ")invoked!");
        final Hashtable<String, String> curCPInfo = cpInfo;
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {

                ILoginCallback listener = new ILoginCallback() {

                    @Override
                    public void onFailed(int arg0, String arg1) {
                        actionResult(UserWrapper.ACTION_RET_INIT_FAIL, arg1);
                    }

                    @Override
                    public void onSuccessed(int arg0, String arg1) {
                        actionResult(UserWrapper.ACTION_RET_INIT_SUCCESS, arg1);
                    }

                };
                if (!HeyueWrapper.getInstance().initSDK(mContext, "UserHeyue", curCPInfo, mAdapter, listener)) {
                    actionResult(UserWrapper.ACTION_RET_INIT_FAIL, "initSDK false");
                }
            }
        });
    }

    public void actionResult(int code, String msg) {
        LogD("actionResult( " + code + ", " + msg + ") invoked!");
        UserWrapper.onActionResult(mAdapter, code, msg);
    }

    protected static void LogE(String msg, Exception e) {
        if (e == null) {
            Log.e(LOG_TAG, msg);
        } else {
            Log.e(LOG_TAG, msg, e);
        }
    }

    protected static void LogD(String msg) {
        try {
            Log.d(LOG_TAG, msg);
        } catch (Exception e) {
            LogE("LogD error", e);
        }
    }

    /**
     * @Title: logout @Description:退出登录 @param @return void @throws
     */
    public void logout() {
        LogD("logout() invoked!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // TODO
            }
        });
    }

    /**
     * @Title: exit @Description:退出游戏，SDK带有退出功能在这里实现 @param @return void @throws
     */
    // public void exit() {
    // LogD("exit() invoked!");
    // PluginWrapper.runOnMainThread(new Runnable() {
    // @Override
    // public void run() {
    // // TODO
    // HYSDK.logout(mContext);
    // }
    // });
    //
    // }

    /**
     * @Title: showToolBar @Description:显示浮窗 @param @param place @return
     * void @throws
     */
    public void showToolBar(int place) {
        LogD("showToolBar(" + place + ") invoked!");
        final int ndPlace = place;
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // TODO
            }
        });
    }

    /**
     * @Title: hideToolBar @Description:隐藏浮窗 @param @return void @throws
     */
    public void hideToolBar() {
        LogD("hideToolBar() invoked!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // TODO
            }
        });
    }

    /**
     * @Title: accountSwitch @Description: 切换账号 @param @return void @throws
     */
    public void accountSwitch() {
        LogD("accountSwitch() invoked!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // TODO
                HYSDK.logout(mContext);
                UserWrapper.onActionResult(com.rsdk.framework.UserHeyue.this, 15, null);
                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        login();
                    }
                }, 800);
            }
        });
    }

    // 获取玩家等级
    public static String getRoleLevel() {
        return RoleLevel;
    }

    public void openUserCenter() {
        LogD("openUserCenter invoke!!");
        HeyueWrapper.openUserCenter();
    }

    public void serviceCenter(String string) {
        LogD("serviceCenter invoke!!");
        HeyueWrapper.showCustomService();
    }

    // 点击支付的时候，调用支付之前调用一次补点接口；补发没有到账的游戏币，serverId无用，为了避免协议的bug，无返回值的无参方法调用不到
    public void checkPurchase(String string) {
        LogD("user checkPurchase invoke!!");
        HeyueWrapper.checkPurchase();
    }

    public void printLog() {
        LogD("test is good");
    }

    // 自定义方法
    // 检测服务器状态
    // 游戏在登录成功的服务器选择界面
    public void checkServerState(String serverId) {
        LogD("checkServerState is invoke！");
        LogD("server id is:" + serverId);
        HeyueWrapper.checkServerStatus(serverId);
    }

    // 查询 FB 账号是否绑定了和悦账号
    public void bindAccountStatus(String string) {
        gameCode = HeyueWrapper.getGameCode();// 固定值，3K客服平台提供 (值：1)
        // gameCode = "GD";
        userId = HeyueWrapper.getUserID();
        // userId = "316027457";
        checkURL = HeyueWrapper.getCheckBindUrl();
        Log.d("UserHeyue", "checkbind--gameCode-->" + gameCode);
        Log.d("UserHeyue", "checkbind--userId-->" + userId);
        Log.d("UserKKKWan", "checkbind--checkURL-->" + checkURL);
        // okhttp3中不允许在主线程中进行耗时的网络访问操作
        new Thread(networkTask).start();
    }

    OkHttpClient client = new OkHttpClient();

    public void post(String url) throws IOException {

        FormBody body = new FormBody.Builder().add("gamecode", gameCode).add("userid", userId).build();

        Request request = new Request.Builder().url(checkURL).post(body).build();
        Log.d("UserKKKWan", "checkbind---->" + "post for check bind result ready");

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call arg0, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        Log.d("UserKKKWan", "checkbind---->" + "post for check bind success");
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("Userheyue::::::", jsonObject.toString());
                        // 已绑定的时候返回("code":"1000","message":"成功")
                        if (jsonObject.toString().contains("1000")) {
                            UserWrapper.onActionResult(com.rsdk.framework.UserHeyue.mAdapter, 1004, "1");
                        } else {
                            UserWrapper.onActionResult(com.rsdk.framework.UserHeyue.mAdapter, 1004, "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.d("UserKKKWan", "checkbind---->" + "post for check bind fail");
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            try {
                post(checkURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

}
