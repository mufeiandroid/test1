package com.rsdk.framework;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.appsflyer.AFInAppEventType;
import com.hy.sdk.HYSDK;
import com.rsdk.framework.AnalyticsWrapper;
import com.rsdk.framework.HeyueWrapper;
import com.rsdk.framework.InterfaceAnalytics;
import com.rsdk.framework.PluginWrapper;
import com.rsdk.framework.Wrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by xieyu on 18/1/1.
 */

public class AnalyticsHeyue implements InterfaceAnalytics {

    private static final String TAG = "AnalyticsHeyue";
    private static Activity mActivity = null;
    private static InterfaceAnalytics mAnalyticsInterface = null;
    private static String roleLevel;
    private static Context mContext;
    public static String Role_Name = "";
    public static String Server_Name = "";
    public static String Role_Level = "";
    public static String Vip_Lv = "";
    public static String Server_Id = "";
    public static String Role_Id = "";
    public static String deal_Role_Level;

    public static String getServerCode() {
        Log.d(TAG, "getServerCode---"+Server_Id);
        return Server_Id;
    }

    public static String getPersonalId() {
        return Role_Id;
    }

    public AnalyticsHeyue(Context context) {
        this.mContext = (Activity) context;
        mAnalyticsInterface = this;
        configDeveloperInfo(Wrapper.getDeveloperInfo());
    }

    private void configDeveloperInfo(final Hashtable<String, String> initConfig) {
    }

    @Override
    public void startSession() {

    }

    @Override
    public void stopSession() {

    }

    @Override
    public void setSessionContinueMillis(int i) {

    }

    @Override
    public void setCaptureUncaughtException(boolean b) {

    }

    @Override
    public void setDebugMode(boolean b) {

    }

    @Override
    public void logError(String s, String s1) {

    }

    @Override
    public void logEvent(String s) {

    }

    @Override
    public void logEvent(final String eventId, Hashtable<String, String> events) {
        Log.d(TAG, "logEvent(String, Hashtable) invoked!");
        Log.d(TAG, "eventId = " + eventId);
        Log.d(TAG, "events = " + events);
        if (eventId.equals(AnalyticsWrapper.EVENT_NAME_COMPLETED_LOGIN)
                || eventId.equals(AnalyticsWrapper.EVENT_NAME_COMPLETED_REGISTRATION)
                || eventId.equals(AnalyticsWrapper.EVENT_NAME_USERUPDATE)) {
            // 调用用户中心所需参数
            // final CommonSdkExtendData data = new CommonSdkExtendData();
            Role_Name = events.get("game_user_name");
            Server_Name = events.get("server_name");
            Role_Level = events.get("level");
            Vip_Lv = events.get("vip_level");
            Server_Id = events.get("server_id");
            Role_Id = events.get("game_user_id");
            Log.d(TAG, "setGameUserInfo_ready");
            Log.d(TAG, "Role_Name---->" + Role_Name);
            Log.d(TAG, "Server_Name---->" + Server_Name);
            Log.d(TAG, "Role_Level---->" + Role_Level);
            Log.d(TAG, "Vip_Lv---->" + Vip_Lv);
            Log.d(TAG, "Server_Id---->" + Server_Id);
            Log.d(TAG, "Role_Id---->" + Role_Id);
            // 和悦谷歌推送相关信息推送接口
            HeyueWrapper.callbackPushInfoToGameServer(Role_Id, Role_Name, Server_Id);
            // 和悦官斗官阶统计逻辑
            PluginWrapper.runOnMainThread(new Runnable() {
                public void run() {

                    if (HeyueWrapper.getGameName().equals("guandou")
                            && eventId.equals(AnalyticsWrapper.EVENT_NAME_USERUPDATE)) {
                        String[] levelArray = { "2", "4", "6", "8", "10"
                                // ,"1", "3", "5", "7", "9","11", "13", "15",
                                // "17", "19","12", "14", "16", "18", "20"
                        };
                        if (Arrays.asList(levelArray).contains(Role_Level)) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("2", "9");
                            hashMap.put("4", "8");
                            hashMap.put("6", "7");
                            hashMap.put("8", "6");
                            hashMap.put("10", "5");
                            // hashMap.put("1", "9");
                            // hashMap.put("3", "8");
                            // hashMap.put("5", "7");
                            // hashMap.put("7", "6");
                            // hashMap.put("9", "5");
                            deal_Role_Level = hashMap.get(Role_Level);
                            Log.d(TAG, "guandou role_level analytic--->" + deal_Role_Level);
                            Map<String, Object> afMap = new HashMap<String, Object>();
                            afMap.put("af_level", deal_Role_Level);
                            afMap.put("af_roleid", Role_Id);
                            HYSDK.appsFlyerEvent((Activity) mContext,
                                    AFInAppEventType.LEVEL_ACHIEVED + "_" + deal_Role_Level, afMap);
                            Log.d(TAG, "----->角色升级_end");
                        }
                    }
                }
            });
            // 和悦官斗游戏由于官阶统计的特殊性，导致以后其他等级统计需要做特殊配置
            PluginWrapper.runOnMainThread(new Runnable() {
                public void run() {
                    if (eventId.equals(AnalyticsWrapper.EVENT_NAME_COMPLETED_REGISTRATION)) {// 创建角色
                        HYSDK.newRoleName((Activity) mContext, Role_Id, Role_Name);
                        Log.d(TAG, "----->创建角色_end");
                    } else if (eventId.equals(AnalyticsWrapper.EVENT_NAME_COMPLETED_LOGIN)) {// 角色登陆
                        // 调用检查伺服器接口上传服务器信息
                        // JingqiWrapper.checkServer();
                        HYSDK.saveRoleName((Activity) mContext, Role_Id, Role_Name, Role_Level);
                        Log.d(TAG, "----->角色登录或切换_end");
                    }
                }
            });

        }
    }

    private void LogE(String paramString, Exception paramException) {
        Log.e(TAG, paramString, paramException);
        paramException.printStackTrace();
    }

    @Override
    public void logTimedEventBegin(String s) {

    }

    @Override
    public void logTimedEventEnd(String s) {

    }

    @Override
    public String getSDKVersion() {
        return null;
    }

    @Override
    public String getPluginVersion() {
        return null;
    }

    @Override
    public String getPluginId() {
        return null;
    }

}
