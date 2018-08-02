package com.rsdk.framework;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gh.sdk.listener.FacebookShareListener;
import com.gh.sdk.share.ShareFacebookContent;
import com.hy.sdk.HYSDK;
import com.rsdk.framework.HeyueWrapper;
import com.rsdk.framework.ILoginCallback;
import com.rsdk.framework.InterfaceShare;
import com.rsdk.framework.PluginWrapper;
import com.rsdk.framework.ShareWrapper;
import com.rsdk.framework.UserWrapper;
import com.rsdk.framework.Wrapper;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Hashtable;

public class ShareHeyue implements InterfaceShare {
    private static String TAG = "ShareHeyue";
    private static Activity mActivity = null;
    private static boolean mDebug;
    private static InterfaceShare mShareInterface = null;
    private static String title;
    private static String description;
    private static String link;
    private static String imageurl;

    static {
        mDebug = false;
    }

    public ShareHeyue(Context paramContext) {
        mActivity = (Activity) paramContext;
        mShareInterface = this;
        configDeveloperInfo(Wrapper.getDeveloperInfo());
        HYSDK.handlerFacebookShareListener(facebookShareListener);// facebook
        // 分享回調
    }

    private void configDeveloperInfo(final Hashtable<String, String> initConfig) {

        ILoginCallback mILoginCallback = new ILoginCallback() {
            @Override
            public void onFailed(int paramInt, String msg) {
                com.rsdk.framework.ShareHeyue.actionResult(UserWrapper.ACTION_RET_INIT_FAIL, msg);
            }

            @Override
            public void onSuccessed(int code, String msg) {
                com.rsdk.framework.ShareHeyue.actionResult(UserWrapper.ACTION_RET_INIT_SUCCESS, msg);
            }
        };

        // if (!FacebookWrapper.initSDK(ShareJingqiFacebook.mActivity,
        // initConfig, mILoginCallback))
        // ShareJingqiFacebook.actionResult(UserWrapper.ACTION_RET_INIT_FAIL,
        // "Facebook323Wrapper.initSDK false");
    }

    public static void actionResult(int paramInt, String paramString) {
        System.out.println("actionResult code=" + paramInt + " msg=" + paramString);
        ShareWrapper.onShareResult(mShareInterface, paramInt, paramString);
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
        Method[] arrayOfMethod = com.rsdk.framework.ShareHeyue.class.getMethods();
        for (int i = 0; ; i++) {
            if (i >= arrayOfMethod.length) {
                return false;
            }
            if (arrayOfMethod[i].getName().equals(paramString)) {
                return true;
            }
        }
    }

    @Override
    public void setDebugMode(boolean paramBoolean) {
        mDebug = paramBoolean;
        // FacebookWrapper.setDebugMode(mDebug);
    }

    // public void facebookShare(final String param) {
    // PluginWrapper.runOnMainThread(new Runnable() {
    // public void run() {
    // try {
    // JSONObject jsonArray = new JSONObject(param);
    // title = jsonArray.optString("title");
    // description = jsonArray.optString("description");
    // imageurl = jsonArray.optString("imageurl");
    // link = jsonArray.optString("link");
    // // if(((List<Object>) jsonArray).size() > 0){
    // //// 遍历jsonarray数组，吧每一个对象转成json对象
    // // for(int i= 0;i<jsonArray.size();i++) {
    // // JSONObject jsonObject = jsonArray.getJSONObject(i);
    // // String title = (String) jsonObject.get("title");
    // // String description = (String)
    // // jsonObject.get("description");
    // // String imageURL = (String) jsonObject.get("imageURL");
    // // String linkUrl = (String) jsonObject.get("linkUrl");
    // // }
    // // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // Log.d("share_xy---->", "title" + title);
    // Log.d("share_xy---->", "description" + description);
    // Log.d("share_xy---->", "imageURL" + imageurl);
    // Log.d("share_xy---->", "linkUrl" + link);
    // ShareFacebookContent content = new ShareFacebookContent();
    // content.setTitle(title);
    // content.setDescription(description);
    // content.setImageURL(imageurl);
    // content.setLinkUrl(link);
    // HYSDK.facebookShare(mActivity, content, new FacebookShareListener() {
    //
    // @Override
    // public void onFacebookShare(int code) {
    // // 分享成功
    // if (code == FacebookShareListener.SUCCESS) {
    // ShareWrapper.onShareResult(mShareInterface,
    // ShareWrapper.SHARERESULT_SUCCESS, "");
    // } else {
    // ShareWrapper.onShareResult(mShareInterface,
    // ShareWrapper.SHARERESULT_FAIL, "");
    // }
    //
    // }
    // });
    //
    // }
    // });
    // }

    // private void json(String param) {
    // // TODO
    // try{
    // JSONObject jsonArray = new JSONObject(param);
    // String title = jsonArray.optString("title");
    // String description = jsonArray.optString("description");
    // String imageURL = jsonArray.optString("imageURL");
    // String linkUrl = jsonArray.optString("linkUrl");
    // if(((List<Object>) jsonArray).size() > 0){
    //// 遍历jsonarray数组，吧每一个对象转成json对象
    // for(int i= 0;i<jsonArray.size();i++) {
    // JSONObject jsonObject = jsonArray.getJSONObject(i);
    // String title = (String) jsonObject.get("title");
    // String description = (String) jsonObject.get("description");
    // String imageURL = (String) jsonObject.get("imageURL");
    // String linkUrl = (String) jsonObject.get("linkUrl");
    // }
    // }
    // }catch(Exception e){
    // e.printStackTrace();
    // }
    // }

    // FaceBook分享回调
    FacebookShareListener facebookShareListener = new FacebookShareListener() {

        @Override
        public void onFacebookShare(int code) {
            // 分享成功
            if (code == FacebookShareListener.SUCCESS) {
                Log.d(TAG, "Facebook share success!");
                ShareWrapper.onShareResult(mShareInterface, ShareWrapper.ACTION_TYPE_FB_POST_FEED, "");
            } else {
                Log.d(TAG, "Facebook share fail!");
                ShareWrapper.onShareResult(mShareInterface, ShareWrapper.SHARERESULT_FAIL, "");
            }
        }
    };

    @Override
    public void share(final Hashtable<String, String> paramHashtable) {
        // TODO Auto-generated method stub
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("share-paramHashtable-->", paramHashtable.toString());
                    // {data={"imageurl":"https:\/\/pic3.gamedreamer.com.tw\/panwuxi\/picture\/2017052334301df04b07ecff5660cde26ce867fa.jpg",
                    // "description":"你有足夠的智慧與策略，制霸全球嗎？\n你有高明的外交手段，解除軍團危機嗎？\n快！快！快！戰友急需你的加入！\n",
                    // "title":"開局一艘小艇，一座小島，3天你能建立一個海上帝國嗎？",
                    // "link":"http:\/\/adv.heyyogame.com\/adv.jsp?gamecode=HZOL&partnerName=share&advcode=HZOL_T1Yd_476"}}
                    JSONObject jsonObject = new JSONObject(paramHashtable.toString());
                    JSONObject data = jsonObject.optJSONObject("data");
                    title = data.optString("title");
                    description = data.optString("description");
                    imageurl = data.optString("imageurl");
                    link = data.optString("link");

                    // if(((List<Object>) jsonArray).size() > 0){
                    //// 遍历jsonarray数组，吧每一个对象转成json对象
                    // for(int i= 0;i<jsonArray.size();i++) {
                    // JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // String title = (String) jsonObject.get("title");
                    // String description = (String)
                    // jsonObject.get("description");
                    // String imageURL = (String) jsonObject.get("imageURL");
                    // String linkUrl = (String) jsonObject.get("linkUrl");
                    // }
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("share_xy---->", "title" + title);
                Log.d("share_xy---->", "description" + description);
                Log.d("share_xy---->", "imageURL" + imageurl);
                Log.d("share_xy---->", "linkUrl" + link);
                ShareFacebookContent content = new ShareFacebookContent();
                content.setTitle(title);
                content.setDescription(description);
                content.setImageURL(imageurl);
                content.setLinkUrl(link);
                // HYSDK.facebookShare(mActivity, content, new
                // FacebookShareListener() {
                //
                // @Override
                // public void onFacebookShare(int code) {
                // // 分享成功
                // if (code == FacebookShareListener.SUCCESS) {
                // ShareWrapper.onShareResult(mShareInterface,
                // ShareWrapper.SHARERESULT_SUCCESS, "");
                // } else {
                // ShareWrapper.onShareResult(mShareInterface,
                // ShareWrapper.SHARERESULT_FAIL, "");
                // }
                //
                // }
                // });
                HYSDK.facebookShare(mActivity, content, facebookShareListener);
            }
        });
        // System.out.println("facebook share-->" + paramHashtable);
        // if (!FacebookWrapper.isInited()) {
        // ShareWrapper.onShareResult(this, ShareWrapper.SHARERESULT_FAIL,
        // "inited fialed!");
        // return;
        // }
        // if (!FacebookWrapper.networkReachable(mActivity)) {
        // ShareWrapper.onShareResult(this, ShareWrapper.SHARERESULT_FAIL,
        // "Network not available!");
        // return;
        // }
        //
        // FacebookWrapper.share(mActivity, paramHashtable, new ILoginCallback()
        // {
        // public void onFailed(int paramInt, String paramString) {
        // ShareWrapper.onShareResult(ShareJingqiFacebook.this, paramInt,
        // paramString);
        // }
        //
        // public void onSuccessed(int paramInt, String paramString) {
        // ShareWrapper.onShareResult(ShareJingqiFacebook.this, paramInt,
        // paramString);
        // }
        // });

        return;

    }

    @Override
    public String getPluginId() {
        return HeyueWrapper.getPluginId();
    }
}

/*
 * Location: \\vmware-host\Shared
 * Folders\Project\rframework\rsdk\client\rsdklib\
 * rsdkplugins\android\source\tools\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name: com.anysdk.framework.UserBDYouxi JD-Core Version: 0.6.0
 */