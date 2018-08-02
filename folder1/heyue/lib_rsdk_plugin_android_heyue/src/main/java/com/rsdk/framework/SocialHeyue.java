package com.rsdk.framework;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gh.sdk.dto.DataBean;
import com.gh.sdk.dto.GHFBFriend;
import com.gh.sdk.invite.FaceBookGetAppRequestsResult;
import com.gh.sdk.invite.FaceBookInviteFrisResult;
import com.gh.sdk.invite.FacebookInviteFrisContent;
import com.gh.sdk.listener.FacebookGetAppRequestsFromBeanListener;
import com.gh.sdk.listener.FacebookGetInvitableFriListener;
import com.gh.sdk.listener.FacebookInviteFriendsListener;
import com.hy.sdk.HYSDK;
import com.rsdk.framework.HeyueWrapper;
import com.rsdk.framework.ILoginCallback;
import com.rsdk.framework.InterfaceSocial;
import com.rsdk.framework.PluginWrapper;
import com.rsdk.framework.SocialWrapper;
import com.rsdk.framework.UserWrapper;
import com.rsdk.framework.Wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class SocialHeyue implements InterfaceSocial {
    private static Activity mActivity = null;
    private static boolean mDebug;
    private static InterfaceSocial mSocialInterface = null;
    private static String title;
    private static String message;
    private static String fbParms_getmyinfo;
    private static String fbParms_getfriend;
    private static String fbParms_defaultpic;

    public final static int FB_ACTION_TYPE_LOGIN = 1;//
    public final static int FB_ACTION_TYPE_BIND_USER = 2;//
    public final static int FB_ACTION_TYPE_LOGIN_AND_POST_FEED = 3;
    public final static int FB_ACTION_TYPE_LOGIN_AND_GET_FRIEND = 4;
    public final static int FB_ACTION_TYPE_LOGIN_AND_GET_MYINFO = 5;
    public final static int FB_ACTION_TYPE_LOGIN_AND_SHOW_SOCIAL_VIEW = 6;
    public static Map<String, String> friendListHashtable;

    public static int actiontype = 0;

    static {
        mDebug = false;
    }

    public SocialHeyue(Context paramContext) {
        mActivity = (Activity) paramContext;
        mSocialInterface = this;
        configDeveloperInfo(Wrapper.getDeveloperInfo());
        HYSDK.handlerFacebookInviteFriendsListener(facebookInviteFriendsListener);
        HYSDK.handlerFacebookGetInvitableFriListener(facebookGetInvitableFriListener);// facebook
                                                                                      // 獲取可受邀請好友名單
        HYSDK.handlerFacebookGetAppRequestsFromBeanListener(facebookGetAppRequestsFromBeanListener);// 獲取
                                                                                                    // fb
                                                                                                    // 邀請人信息回調
    }

    private void configDeveloperInfo(final Hashtable<String, String> initConfig) {
        ILoginCallback mILoginCallback = new ILoginCallback() {
            public void onFailed(int paramInt, String msg) {
                com.rsdk.framework.SocialHeyue.actionResult(UserWrapper.ACTION_RET_INIT_FAIL, msg);
            }

            public void onSuccessed(int code, String msg) {
                com.rsdk.framework.SocialHeyue.actionResult(UserWrapper.ACTION_RET_INIT_SUCCESS, msg);
            }
        };

        // if (!FacebookWrapper.initSDK(SocialJingqiFacebook.mActivity,
        // initConfig, mILoginCallback))
        // SocialJingqiFacebook.actionResult(UserWrapper.ACTION_RET_INIT_FAIL,
        // "initSDK false");
    }

    public static void actionResult(int paramInt, String paramString) {
        System.out.println("actionResult code=" + paramInt + " msg=" + paramString);
        SocialWrapper.onSocialResult(mSocialInterface, paramInt, paramString);
    }

    @Override
    public String getPluginVersion() {
        return HeyueWrapper.getPluginVersion();
    }

    @Override
    public String getSDKVersion() {
        return HeyueWrapper.getSDKVersion();
    }

    public boolean isSupportFunction(String paramString) {
        Method[] arrayOfMethod = com.rsdk.framework.SocialHeyue.class.getMethods();
        for (int i = 0;; i++) {
            if (i >= arrayOfMethod.length)
                return false;
            if (arrayOfMethod[i].getName().equals(paramString))
                return true;
        }
    }

    public void setDebugMode(boolean paramBoolean) {
        mDebug = paramBoolean;
        // FacebookWrapper.setDebugMode(mDebug);
    }

    public void signIn() {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("deprecation")
    public void facebookInviteWithFriends(final String param) {

    }

    public void signOut() {
        // TODO Auto-generated method stub

    }

    public void submitScore(String paramString, long paramLong) {
        // TODO Auto-generated method stub

    }

    public void showLeaderboard(String paramString) {
        // TODO Auto-generated method stub

    }

    public void unlockAchievement(final Hashtable<String, String> paramHashtable) {
        // TODO Auto-generated method stub

    }

    public void showAchievements() {
        // TODO Auto-generated method stub

    }

    @Override
    public Vector<Map<String, String>> getFriends(Map<String, String> paramHashtable) {
        // TODO Auto-generated method stub
        return null;
    }

    // FaceBook邀请好友回调
    FacebookInviteFriendsListener facebookInviteFriendsListener = new FacebookInviteFriendsListener() {

        @Override
        public void onFaceBookInviteFriendResult(FaceBookInviteFrisResult faceBookInviteFrisResult) {
            // TODO Auto-generated method stub
            Log.d("SocialHeyue_xy--getCode-->", faceBookInviteFrisResult.getCode() + "");
            if (faceBookInviteFrisResult.getCode() < 203) {
                // String myfbId = "FB11W54Z450n725";
                // String myfbId =
                // AccessToken.getCurrentAccessToken().getUserId();
                String myfbId = faceBookInviteFrisResult.getMyFBId();
                // List<String> friendList1 = new
                // ArrayList<>();
                // friendList1 =
                // faceBookInviteFrisResult.getInvitedFriendsIdList();
                Log.d("SocialHeyue_xy--myfbId-->", myfbId);
                List<String> list = new ArrayList<>();
                // 结果是一维数组[13743264908772945，1794326490878125,
                // 286298158484736]
                list = faceBookInviteFrisResult.getAllInvitedFriendsIdList();
                Log.d("SocialHeyue_xy---->", list.toString());
                // 将玩家自己的fbID加到数组中第一位
                list.add(0, myfbId);
                Log.d("SocialHeyue_xy---->", list.toString());

                SocialWrapper.onSocialResult(mSocialInterface, SocialWrapper.ACTION_TYPE_INVITE_FRIEND,
                        list.toString());

            }
        }
    };
    // 获取FB可邀请好友名单
    FacebookGetInvitableFriListener facebookGetInvitableFriListener = new FacebookGetInvitableFriListener() {

        @Override
        public void onResult(List<GHFBFriend> friendList) {
            // TODO Auto-generated method stub
            if (friendList.size() > 0) {
                try {
                    Log.d("social_xy--friendListHashtable-->", friendListHashtable.toString());
                    JSONObject jsonObject = new JSONObject(friendListHashtable.toString());
                    JSONObject data = jsonObject.optJSONObject("data");
                    title = data.optString("title");
                    message = data.optString("message");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("social_xy---->", "title" + title);
                Log.d("social_xy---->", "message" + message);

                FacebookInviteFrisContent content = new FacebookInviteFrisContent();
                // 只设置title和message，不需要设置invitedFriList
                content.setTitle(title);
                content.setMessage(message);
                // List<GHFBFriend> friendList1 = new ArrayList<>();
                // friendList.add(friendList.get(0));
                // content.setInvitedFriList(friendList1);
                // 调用“发送邀请信息”接口
                HYSDK.facebookInviteWithFriends(mActivity, content, facebookInviteFriendsListener);
                // HYSDK.facebookInviteWithFriends(mActivity, content, new
                // FacebookInviteFriendsListener() {
                //
                // @Override
                // public void onFaceBookInviteFriendResult(
                // FaceBookInviteFrisResult faceBookInviteFrisResult) {
                // // TODO Auto-generated method stub
                // Log.d("SocialJingqi_xy--getCode-->",
                // faceBookInviteFrisResult.getCode() + "");
                // if (faceBookInviteFrisResult.getCode() < 203) {
                // // String myfbId = "FB11W54Z450n725";
                // // String myfbId =
                // // AccessToken.getCurrentAccessToken().getUserId();
                // String myfbId = faceBookInviteFrisResult.getMyFBId();
                // // List<String> friendList1 = new
                // // ArrayList<>();
                // // friendList1 =
                // // faceBookInviteFrisResult.getInvitedFriendsIdList();
                // Log.d("SocialJingqi_xy--myfbId-->", myfbId);
                // List<String> list = new ArrayList<>();
                // // 结果是一维数组[13743264908772945，1794326490878125,
                // // 286298158484736]
                // list = faceBookInviteFrisResult.getAllInvitedFriendsIdList();
                // Log.d("SocialJingqi_xy---->", list.toString());
                // // 将玩家自己的fbID加到数组中第一位
                // list.add(0, myfbId);
                // Log.d("SocialJingqi_xy---->", list.toString());
                //
                // SocialWrapper.onSocialResult(mSocialInterface,
                // SocialWrapper.ACTION_TYPE_INVITE_FRIEND, list.toString());
                //
                // }
                // }
                // });
            }
        }
    };

    @Override
    public String inviteFriend(final Map<String, String> paramHashtable) {
        PluginWrapper.runOnMainThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                friendListHashtable = paramHashtable;
                HYSDK.facebookInvitableFriendList(mActivity, facebookGetInvitableFriListener);
                // HYSDK.facebookInvitableFriendList(mActivity, new
                // FacebookGetInvitableFriListener() {
                //
                // @Override
                // public void onResult(List<GHFBFriend> friendList) {
                // // TODO Auto-generated method stub
                // if (friendList.size() > 0) {
                // try {
                // Log.d("social_xy--paramHashtable-->",
                // paramHashtable.toString());
                // JSONObject jsonObject = new
                // JSONObject(paramHashtable.toString());
                // JSONObject data = jsonObject.optJSONObject("data");
                // title = data.optString("title");
                // message = data.optString("message");
                //
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // Log.d("social_xy---->", "title" + title);
                // Log.d("social_xy---->", "message" + message);
                //
                // FacebookInviteFrisContent content = new
                // FacebookInviteFrisContent();
                // // 只设置title和message，不需要设置invitedFriList
                // content.setTitle(title);
                // content.setMessage(message);
                // // List<GHFBFriend> friendList1 = new ArrayList<>();
                // // friendList.add(friendList.get(0));
                // // content.setInvitedFriList(friendList1);
                // // 调用“发送邀请信息”接口
                // HYSDK.facebookInviteWithFriends(mActivity, content,
                // facebookInviteFriendsListener);
                //// HYSDK.facebookInviteWithFriends(mActivity, content, new
                // FacebookInviteFriendsListener() {
                ////
                //// @Override
                //// public void onFaceBookInviteFriendResult(
                //// FaceBookInviteFrisResult faceBookInviteFrisResult) {
                //// // TODO Auto-generated method stub
                //// Log.d("SocialJingqi_xy--getCode-->",
                // faceBookInviteFrisResult.getCode() + "");
                //// if (faceBookInviteFrisResult.getCode() < 203) {
                //// // String myfbId = "FB11W54Z450n725";
                //// // String myfbId =
                //// // AccessToken.getCurrentAccessToken().getUserId();
                //// String myfbId = faceBookInviteFrisResult.getMyFBId();
                //// // List<String> friendList1 = new
                //// // ArrayList<>();
                //// // friendList1 =
                //// // faceBookInviteFrisResult.getInvitedFriendsIdList();
                //// Log.d("SocialJingqi_xy--myfbId-->", myfbId);
                //// List<String> list = new ArrayList<>();
                //// // 结果是一维数组[13743264908772945，1794326490878125,
                //// // 286298158484736]
                //// list =
                // faceBookInviteFrisResult.getAllInvitedFriendsIdList();
                //// Log.d("SocialJingqi_xy---->", list.toString());
                //// // 将玩家自己的fbID加到数组中第一位
                //// list.add(0, myfbId);
                //// Log.d("SocialJingqi_xy---->", list.toString());
                ////
                //// SocialWrapper.onSocialResult(mSocialInterface,
                //// SocialWrapper.ACTION_TYPE_INVITE_FRIEND, list.toString());
                ////
                //// }
                //// }
                //// });
                // }
                // }
                // });
            }
        });
        return null;
    }

    public String getPluginId() {
        return HeyueWrapper.getPluginId();
    }

    public void fbGetFriendsInfo(String parms) {
        // FacebookWrapper.getFriendsInfo(parms);
    }

    FacebookGetAppRequestsFromBeanListener facebookGetAppRequestsFromBeanListener = new FacebookGetAppRequestsFromBeanListener() {

        @Override
        public void onResult(FaceBookGetAppRequestsResult result) {
            // TODO Auto-generated method stub
            int code = result.getCode();
            if (code == 200) {
                String fbUserId = result.getFbUserId();
                Set<DataBean.FromBean> fromBeanSet = result.getFromBeanSet();
                JSONObject json = new JSONObject();
                try {
                    json.putOpt("name", "");
                    json.putOpt("id", fbUserId);
                    json.putOpt("picture", "");
                    com.rsdk.framework.SocialHeyue.actionResult(SocialWrapper.ACTION_TYPE_FB_GET_MYINFO, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (code == 303) {
                Toast.makeText(mActivity, "请登录facebook之后再尝试接受邀请", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void fbGetMyInfo(String parms) {
        PluginWrapper.runOnMainThread(new Runnable() {
            public void run() {
                HYSDK.faceBookGetAppRequests(mActivity, facebookGetAppRequestsFromBeanListener);
                // HYSDK.faceBookGetAppRequests(mActivity, new
                // FacebookGetAppRequestsFromBeanListener() {
                //
                // @Override
                // public void onResult(FaceBookGetAppRequestsResult result) {
                // // TODO Auto-generated method stub
                // int code = result.getCode();
                // if (code == 200) {
                // String fbUserId = result.getFbUserId();
                // Set<DataBean.FromBean> fromBeanSet = result.getFromBeanSet();
                // JSONObject json = new JSONObject();
                // try {
                // json.putOpt("name", "");
                // json.putOpt("id", fbUserId);
                // json.putOpt("picture", "");
                // SocialJingqi.actionResult(SocialWrapper.ACTION_TYPE_FB_GET_MYINFO,
                // json.toString());
                // } catch (JSONException e) {
                // e.printStackTrace();
                // }
                // } else if (code == 303) {
                // Toast.makeText(mActivity, "请登录facebook之后再尝试接受邀请",
                // Toast.LENGTH_SHORT).show();
                // }
                // }
                // });
            }
        });
    }
}

/*
 * Location: \\vmware-host\Shared
 * Folders\Project\rframework\rsdk\client\rsdklib\
 * rsdkplugins\android\source\tools\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name: com.anysdk.framework.UserBDYouxi JD-Core Version: 0.6.0
 */