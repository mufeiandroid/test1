package com.rsdk.framework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gh.sdk.dto.SDKConfig;
import com.gh.sdk.dto.Server;
import com.gh.sdk.dto.User;
import com.gh.sdk.listener.CheckServerControlByGameListener;
import com.gh.sdk.listener.LoginListener;
import com.gh.sdk.listener.MemberListener;
import com.gh.sdk.listener.StartListener;
import com.gh.sdk.util.GHToast;
import com.gh.sdk.util.GHValues;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hy.sdk.HYSDK;
import com.rsdk.Util.SdkHttpListener;
import com.rsdk.framework.AnalyticsHeyue;
import com.rsdk.framework.IActivityCallback;
import com.rsdk.framework.ILoginCallback;
import com.rsdk.framework.InterfaceIAP;
import com.rsdk.framework.InterfaceUser;
import com.rsdk.framework.LoginCallbackDataInfo;
import com.rsdk.framework.PluginWrapper;
import com.rsdk.framework.UserHeyue;
import com.rsdk.framework.UserWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


/**
 * @ClassName: HeyueWrapper
 * @Description:此类是单例模式，SDK公共类，SDK初始化和公用的参数以及方法都写在这里；
 * @author cwj
 * @date 2015-2-12 上午9:47:07
 * 
 */
public class HeyueWrapper {

    private static final String LOG_TAG = "HeyueWrapper";
    private final static String SDK_VERSION = "1.0.0";
    private final static String PLUGIN_VERSION = "2.0.0" + "_" + SDK_VERSION;
    private final static String PLUGIN_ID = "700024";

    private static com.rsdk.framework.HeyueWrapper mInstance = null;

    public static com.rsdk.framework.HeyueWrapper getInstance() {
        if (mInstance == null) {
            mInstance = new com.rsdk.framework.HeyueWrapper();
        }
        return mInstance;
    }

    private boolean mDebug = false;
    private static Context mContext = null;
    private String mClassName;
    private String mPluginId;
    private String mPluginName;

    private static boolean isLogined = false;
    private boolean isInited = false;
    private static String sUid = "";

    private String userIDPrefix = "";
    private String userType = "";

    // 可以直接使用user、pay的回调
    private InterfaceUser mUserAdapter = null;
    private InterfaceIAP mIAPAdapter = null;

    private String r_sdkserver_name;
    private String r_token = "";// SDK登录成功后返回的token，后端验签使用
    private String r_refresh_token = "";
    private static String r_pid = "";// SDK登录成功后返回的userId
    private String r_nickname = "";
    private String r_userType = UserWrapper.LOGIN_USER_TYPE_SDK;
    private String r_customData = "";
    private String r_login_time = "";
    private String r_sign = "";
    private String r_platform_sdk_data = "";
    private static String r_ext1 = "";// 自定义字段，登录验签时的其他数据
    private String r_ext2 = "";
    private String r_ext3 = "";
    public static String gameName = "";
    public static String ProductId = "";
    public static String whichChannel = "";
    private static String gameCode = "";
    private static String checkBindURL = "";
    static JSONObject jsonObject;
    private static String refreshedToken = "";
    private static String platform = "";
    private static int screenOrientation;
    // private static String isXproject = "";
    private static ILoginCallback listeners;

    private static boolean serverState = false;// 服务器状态

    /**
     * SDK初始化，每个功能类都需要调用这个初始化方法来初始化SDK
     * 
     * @Description:
     * @param context
     * @param cpInfo
     *            :devlepInfo中的参数
     * @param adapter
     *            :功能类的实例，方便在wrapper中使用功能类的回调，如登录的回调是写在初始化的方法时，
     *            就能在登录回调中用user的回调通知了
     * @param listener
     *            :回调
     * @return
     */
    public boolean initSDK(Context context, String className, Hashtable<String, String> cpInfo, Object adapter,
            final ILoginCallback listener) {

        if (adapter instanceof InterfaceUser) {
            mUserAdapter = (InterfaceUser) adapter;
        } else if (adapter instanceof InterfaceIAP) {
            mIAPAdapter = (InterfaceIAP) adapter;
        }

        if (isInited) {
            return isInited;
        }

        isInited = true;
        mContext = context;
        mClassName = className;
        mPluginId = PluginWrapper.getSuportPluginId(mClassName);
        mPluginName = PluginWrapper.getSuportPluginname(mClassName);
        r_sdkserver_name = mPluginName;
        if(cpInfo.get("ProductId") != null){
            ProductId = cpInfo.get("ProductId");// 获取调用谷歌支付的商品ID（泰国官斗的月卡和至尊卡用）
        }
        gameName = cpInfo.get("gameName");// 获取游戏名，为官斗统计特殊逻辑做处理
        whichChannel = cpInfo.get("whichChannel");// 是否是 Google 包
        gameCode = cpInfo.get("gameCode");// 游戏码，查询用户绑定状态用
        checkBindURL = cpInfo.get("checkBindURL");// 查询绑定状态地址
//        postPushInfoUrl = cpInfo.get("postPushInfoUrl");// 推送信息发送到游戏服务器的地址
        platform = cpInfo.get("platform");// 谷歌包还是和悦官网包
        screenOrientation = Integer.parseInt(cpInfo.get("screenOrientation"));// 屏幕朝向

        setActivityCallback();// 设置生命周期
        // PluginWrapper.runOnMainThread(new Runnable() {
        // @Override
        // public void run() {
        // TODO SDK初始化实现
        // android6.0+之后需要将权限传入初始化方法中，如果游戏不需要再6.0+上打包或者游戏内部已对全线部分进行处理，直接传null
        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        // permissionList.add(Manifest.permission.READ_PHONE_STATE);
        // permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        // permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // isXproject = cpInfo.get("isXproject");
        LogD(gameCode + "--" + gameName + "--" + platform + "--" + screenOrientation);
        SDKConfig sdkConfig = new SDKConfig(gameCode, gameName);
        sdkConfig.setPlatform(platform);
        sdkConfig.setOrientation(screenOrientation);
        HYSDK.start((Activity) mContext, sdkConfig, new StartListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                // 初始化成功回调之后调用登录接口
                // FirebaseMessaging.getInstance().subscribeToTopic("heyyogame");
                // Log.d("subscribeToTopic", "heyyogame");
                LogD("init successed");
                listener.onSuccessed(UserWrapper.ACTION_RET_INIT_SUCCESS, "init succeed");
            }

            @Override
            public void onExit() {
                // TODO Auto-generated method stub
                // 初始化失败时调用退出游戏接口
                // System.exit(0);
                LogD("init failed1");
                listener.onFailed(UserWrapper.ACTION_RET_INIT_FAIL, "init failed");
            }
        });
        return isInited;

    }

    // 登录监听
    LoginListener loginListener = new LoginListener() {

        @Override
        public void onLogin(User paramUser, Server paramServer) {
            // TODO Auto-generated method stub
            // 注:游戏中需要保存User对象和Server对象数据
            User loginUser = paramUser;
            Server loginServer = paramServer;
            LogD("loginServer" + loginServer.toString());

            r_token = loginUser.getToken();
            r_pid = loginUser.getUserId();
            r_refresh_token = loginUser.getSessionId();
            r_ext2 = String.valueOf(System.currentTimeMillis());
            // HYSDK.saveRoleName((Activity) mContext, "111",
            // "yuyuyu", "1");
            getAccessToken(listeners);
            // 检测伺服器状态正常在改回调中进入游戏界面
            // checkServer(listener);
        }
    };

    /**
     * 登录
     * 
     * @param  //listener根据SDK情况传入回调
     *            ，有些SDK是在初始化的时候添加回调的，这里就不用添加回调
     */
    public void userLogin(final ILoginCallback listener) {

        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // TODO
                // 测试bugly
                // CrashReport.testJavaCrash();
                // SDK登录实现，SDK登录成功后，如有验证的token需要赋值给r_token，以及将userId赋值给r_pid
                // HYSDK.login(mContext, new LoginListener() {
                //
                // @SuppressLint("ShowToast")
                // @Override
                // public void onLogin(User paramUser, Server paramServer) {
                // // TODO Auto-generated method stub
                // // 注:游戏中需要保存User对象和Server对象数据
                // User loginUser = paramUser;
                // Server loginServer = paramServer;
                // LogD("loginServer" + loginServer.toString());
                //
                // r_token = loginUser.getToken();
                // r_pid = loginUser.getUserId();
                // r_refresh_token = loginUser.getSessionId();
                // r_ext2 = String.valueOf(System.currentTimeMillis());
                // // HYSDK.saveRoleName((Activity) mContext, "111",
                // // "yuyuyu", "1");
                // getAccessToken(listener);
                // // 检测伺服器状态正常在改回调中进入游戏界面
                // // checkServer(listener);
                // }
                // });
                listeners = listener;
                LogD("HY login begin");
                HYSDK.login(mContext, loginListener);
                // getAccessToken(listener);
            }
        });

    }

    /**
     * 登录token验签，这里验签通过了才算是真正的登录成功
     * 
     * @param listener
     */
    public void getAccessToken(final ILoginCallback listener) {
        Hashtable<String, String> codeInfo = UserWrapper.getAccessTokenInfo(r_sdkserver_name, r_token, r_refresh_token,
                r_pid, r_nickname, r_userType, r_customData, r_login_time, r_sign, r_platform_sdk_data, r_ext1, r_ext2,
                r_ext3);
        LogD("getAccessTokenParams:" + codeInfo.toString());
        // No special requirements,no changing the following code
        UserWrapper.getAccessToken(mContext, codeInfo, new SdkHttpListener() {
            @Override
            public void onResponse(String response) {
                LogD("getAccessToken response:" + response);
                LoginCallbackDataInfo info = UserWrapper.handlerLoginDataFromServer(response);
                if (info != null) {// 验签成功，并返回结果
                    isLogined = true;
                    userIDPrefix = info.pid_prefix;
                    userType = info.user_type;
                    sUid = info.pid;
                    UserWrapper.onActionResult(UserHeyue.mAdapter, UserWrapper.ACTION_RET_LOGIN_SUCCESS,
                            info.toString());
                    // listener.onSuccessed(UserWrapper.ACTION_RET_LOGIN_SUCCESS,
                    // info.toString());
                } else {// 验签错误
                    isLogined = false;
                    UserWrapper.onActionResult(UserHeyue.mAdapter, UserWrapper.ACTION_RET_LOGIN_FAIL,
                            "getAccessToken error");
                    // listener.onFailed(UserWrapper.ACTION_RET_LOGIN_FAIL,
                    // "getAccessToken error");
                }
            }

            @Override
            public void onError() {
                // 其他错误
                isLogined = false;
                listener.onFailed(UserWrapper.ACTION_RET_LOGIN_FAIL, "getAccessToken onError");
            }
        });
    }

    // 伺服器狀態檢測回調
    static CheckServerControlByGameListener checkServerControlByGameListener = new CheckServerControlByGameListener() {

        @Override
        public void onCheckServerSuccess(Server server) {
            // 游戏中要保持该server对象信息，使用该server对象覆盖登录时或者玩家切换账号是保存的server对象信息
            // r_ext1 = server.toString();
            LogD("xy-->r_ext1：" + r_ext1);
            // 服务器状态良好
            // 协议暂时不支持异步回调方法，通过登录回调将代码传给游戏，1003是游戏服务器状态相关，1代表服务器状态良好
            UserWrapper.onActionResult(UserHeyue.mAdapter, 1003, "1");
        }

        @Override
        public void onCheckServerFail(String failMsg) {
            // 检验伺服器状态不正常，可在这里处理游戏相关逻辑，不可进入游戏页面。failMsg为错误信息
            LogD("Heyue ServerState is wrong!!和悦服务器关闭，请联系和悦");
            Toast.makeText(mContext, failMsg, Toast.LENGTH_LONG).show();
            // 协议暂时不支持异步回调方法，通过登录回调将代码传给游戏，1003是游戏服务器状态相关，0代表服务器关闭
            UserWrapper.onActionResult(UserHeyue.mAdapter, 1003, "0");
        }
    };

    // 检查伺服器接口
    public static void checkServerStatus(final String serverId) {
        // map里面需要传入用户ID和需要检查状态的伺服器的serverCode
        // 由于使用我们自己的服务器，整理传入我们自己的pid和serverCode
        LogD("checkServer is invoke!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<String, String>();
                // 老游戏（旧协议）是从setGameUserInfo里面取的服务器ID
                // if (isXproject == null && !isXproject.equals("true")) {
                LogD("is not old RSDK protocol!");
                map.put(GHValues.USER_ID, r_pid);
                map.put(GHValues.SERVER_CODE, serverId);
                // } else {

                // map.put(GHValues.USER_ID, r_pid);
                // map.put(GHValues.SERVER_CODE, UserJingqi.getServerCode());
                // }
                // LogD("getServerCode--" + AnalyticsJingqi.getServerCode());

                // 检测伺服器，在玩家登录成功之后，点击进入游戏时，调用该接口检测选择的伺服器状态是否正常
                // HYSDK.checkServer(mContext, map, new
                // CheckServerControlByGameListener() {
                //
                // @Override
                // public void onCheckServerSuccess(Server server) {
                // // 游戏中要保持该server对象信息，使用该server对象覆盖登录时或者玩家切换账号是保存的server对象信息
                // // r_ext1 = server.toString();
                // LogD("xy-->r_ext1：" + r_ext1);
                // // 服务器状态良好
                // // 协议暂时不支持异步回调方法，通过登录回调将代码传给游戏，1003是游戏服务器状态相关，1代表服务器状态良好
                // UserWrapper.onActionResult(UserJingqi.mAdapter, 1003, "1");
                // }
                //
                // @Override
                // public void onCheckServerFail(String failMsg) {
                // // 检验伺服器状态不正常，可在这里处理游戏相关逻辑，不可进入游戏页面。failMsg为错误信息
                // LogD("Jingqi ServerState is wrong!!晶琦服务器关闭，请联系晶琦");
                // // 协议暂时不支持异步回调方法，通过登录回调将代码传给游戏，1003是游戏服务器状态相关，0代表服务器关闭
                // UserWrapper.onActionResult(UserJingqi.mAdapter, 1003, "0");
                // }
                // });
                HYSDK.checkServer(mContext, map, checkServerControlByGameListener);
            }
        });

    }

    // 会员中心退出方法监听
    static MemberListener memberListener = new MemberListener() {

        @Override
        public void onLogout() {
            // 游戏进行登出操作
            HYSDK.logout(mContext);
            listeners.onSuccessed(UserWrapper.ACTION_RET_ACCOUNTSWITCH_SUCCESS, "");
            // System.exit(0);
        }
    };

    // 用户中心,暴露接口给游戏让游戏调用
    public static void openUserCenter() {
        LogD("openUserCenter begin!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map2 = new HashMap<String, String>();
                map2.put(GHValues.USER_ID, r_pid);
                map2.put(GHValues.SERVER_CODE, UserHeyue.getServerCode());
                // HYSDK.memberCenter(mContext, new MemberListener() {
                //
                // @Override
                // public void onLogout() {
                // // 游戏进行登出操作
                // HYSDK.logout(mContext);
                // System.exit(0);
                // }
                // });
                HYSDK.memberCenter(mContext, memberListener);
            }
        });

    }

    // 调用客服中心接口，如果玩家还未登录的话不调用setParams()方法，直接调用show()方法
    public static void showCustomService() {
        LogD("CustomCenter invoke!!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(GHValues.SERVER_CODE, AnalyticsHeyue.getServerCode());
                if (isLogined) {
                    HYSDK.cs(mContext).setParmas(map).show();
                } else {
                    HYSDK.cs(mContext).show();
                }

            }
        });

    }

    // 手动补单
    public static void checkPurchase() {
        LogD("CheckPurchase invoke!!");
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                HYSDK.checkPurchase(mContext);
            }
        });
    }

    public static String getPluginVersion() {
        return PLUGIN_VERSION;
    }

    public static String getSDKVersion() {
        return SDK_VERSION;
    }

    public static String getPluginId() {
        return PLUGIN_ID;
    }

    public static String getUserID() {
        return sUid;
    }

    public String getSDKServerName() {
        return r_sdkserver_name;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public boolean isInited() {
        return isInited;
    }

    public String getUserIDWithPrefix() {
        LogD("getUserIDWithPrefix() invoked! return: " + getUserIDPrefix() + getUserID());
        return getUserIDPrefix() + getUserID();
    }

    public String getUserIDPrefix() {
        LogD("getUserIDPrefix() invoked! return: " + userIDPrefix);
        return userIDPrefix;
    }

    public String getLoginUserType() {
        LogD("getLoginUserType() invoked! return: " + userType);
        return userType;
    }

    /**
     * start方法回调
     */
    private static StartListener startListener = new StartListener() {

        @Override
        public void onSuccess() {
            mContext.startActivity(new Intent(mContext, com.rsdk.framework.HeyueWrapper.class));
            ((Activity) mContext).finish();
        }

        @Override
        public void onExit() {
            GHToast.showToast(mContext, "初始化失败");
            // 初始化失败，此时需要退出游戏
            ((Activity) mContext).finish();
        }
    };

    /**
     * 生命周期，如SDK要求在Activity生命周期中调用某些方法就写在对应的方法中
     */
    protected void setActivityCallback() {
        PluginWrapper.setActivityCallback(new IActivityCallback() {

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                // TODO Auto-generated method stub
                if (requestCode == HYSDK.REQUEST_CODE && resultCode == HYSDK.RESULT_CODE_START) {
                    HYSDK.handlerStart(requestCode, resultCode, data, startListener);
                }
            }

            @Override
            public void onDestroy() {
                // TODO Auto-generated method stub
                HYSDK.logout(mContext);
                // HYSDK.onDestroy((Activity) mContext);
            }

            @Override
            public void onNewIntent(Intent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPause() {
                // TODO Auto-generated method stub
                // HYSDK.onPause((Activity) mContext);
            }

            @Override
            public void onRestart() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResume() {
                // TODO Auto-generated method stub
                // HYSDK.onResume((Activity) mContext);
            }

            @Override
            public void onStop() {
                // TODO Auto-generated method stub
                // HYSDK.onStop((Activity) mContext);
            }

            @Override
            public void onWindowFocusChanged(boolean arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCreate(Bundle arg0) {
                // TODO Auto-generated method stub
                // 處理在低端機上調用 sdk 方法后,由於內存緊張導致 APP 被系統回收,無法成功回調,請 sdk 對接的時候還需按
                // sdk 的要求處理
                HYSDK.handlerLoginListener(loginListener);// 登錄回調
                HYSDK.handlerCheckServerControlByGameListener(checkServerControlByGameListener);// 伺服器狀態檢測回調
                HYSDK.handlerMemberListener(memberListener);// 會員中心退出回調
                // HYSDK.handlerPayListener(payListener);// 支付回調 在支付类中实现
                // HYSDK.handlerFacebookShareListener(facebookShareListener);//
                // facebook 分享回調 sharejingqi类的构造方法中实现
                // HYSDK.handlerFacebookGetInvitableFriListener(facebookGetInvitableFriListener);//
                // facebook 獲取可受邀請好友名單 在socialjignqi 类的构造方法中实现
                // HYSDK.handlerFacebookInviteFriendsListener(facebookInviteFriendsListener);//
                // fb 發送 fb 邀請信息 在socialjignqi 类的构造方法中实现
                // HYSDK.handlerFacebookGetAppRequestsFromBeanListener(facebookGetAppRequestsFromBeanListener);//
                // 獲取 fb 邀請人信息回調 在socialjignqi 类的构造方法中实现
                // HYSDK.handlerServerListListener(serverListListener);//
                // 伺服器頁面回調,暂时未接入这个功能
                // HYSDK.handlerFacebookAppInviteListener(facebookAppInviteListener);//
                // facebook 應用邀請回調 未实现
                // HYSDK.handlerFacebookGetPromotionDetailsCodeListener(FacebookGetPromotionDetailsCodeListener);//
                // 獲取 FB 應用邀請的邀請碼回調 未实现
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                // HYSDK.onStart((Activity) mContext);
            }

            @Override
            public void onBackPressed() {
                // TODO Auto-generated method stub
                // if (HYSDK.onBackPressed()) {
                // Log.d(LOG_TAG, "HYSDK.onBackPressed() is true");
                // return;
                // } else {
                // // onBackPressed();
                // }
            }

            @Override
            public void onConfigurationChanged(Configuration arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSaveInstanceState(Bundle arg0) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * 网络判断
     * 
     * @param paramContext
     * @return
     */
    public boolean networkReachable(Context paramContext) {
        try {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext.getSystemService("connectivity"))
                    .getActiveNetworkInfo();
            if (localNetworkInfo == null) {
                return false;
            }
            boolean bool = localNetworkInfo.isAvailable();
            return bool;
        } catch (Exception localException) {
            Log.e("getActiveNetworkInfo", localException.getMessage());
            localException.printStackTrace();
        }
        return false;
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
            LogE(msg, e);
        }
    }

    public static String getGameName() {
        LogD("gameName is" + gameName);
        return gameName;
    }

    public static String getChannel() {
        LogD("channel belong to" + whichChannel);
        return whichChannel;
    }

    public static String getGameCode() {
        LogD("gameCode" + gameCode);
        return gameCode;
    }

    public static String getCheckBindUrl() {
        LogD("checkBindURL" + checkBindURL);
        return checkBindURL;
    }

//    public static String getPostPushInfoUrl() {
//        LogD("getPostPushInfoUrl");
//        return postPushInfoUrl;
//    }

    public static String getProductId() {
        LogD("ProductId:" + ProductId);
        return ProductId;
    }

    public static void callbackPushInfoToGameServer(String personalId, String roleName, String serverCode) {
        Log.d("HeyueWrapper", "callbackPushInfoToGameServer");
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("refreshedToken", "refreshedToken is:"+refreshedToken);
        jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("pid", r_pid);
            jsonObject.putOpt("personalId", personalId);
            jsonObject.putOpt("roleName", roleName);
            jsonObject.putOpt("serverCode", serverCode);
            jsonObject.putOpt("googlePushToken", refreshedToken);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("HeyueWrapper", "callbackPushInfoToGameServer----:"+jsonObject.toString());
        // 协议暂时不支持异步回调方法，通过登录回调将代码传给游戏，1006是推送信息相关，json中携带玩家信息和google Token
        UserWrapper.onActionResult(UserHeyue.mAdapter, 1006, jsonObject.toString());
    }
}
