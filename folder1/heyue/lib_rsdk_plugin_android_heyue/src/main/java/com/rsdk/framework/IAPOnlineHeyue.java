package com.rsdk.framework;

import android.content.Context;
import android.util.Log;

import com.gh.sdk.listener.PayListener;
import com.gh.sdk.util.GHValues;
import com.hy.sdk.HYSDK;
import com.rsdk.framework.HeyueWrapper;
import com.rsdk.framework.IAPWrapper;
import com.rsdk.framework.ILoginCallback;
import com.rsdk.framework.InterfaceIAP;
import com.rsdk.framework.PluginWrapper;
import com.rsdk.framework.R;
import com.rsdk.framework.Wrapper;
import com.rsdk.framework.java.RSDKIAP;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * @author mdd
 * @ClassName: IAPChannel
 * @Description:支付功能，如果没有特殊要求，只用在payInSDK方法中实现SDK支付
 * @date 2015-2-12 上午9:47:07
 */
public class IAPOnlineHeyue implements InterfaceIAP {

    private static final String LOG_TAG = "IAPOnlineHeyue";
    // private String notifyUrl = "NOTIFY_URL_VALUE";
    private Context mContext;
    private InterfaceIAP mAdapter;
    private String Role_Level;

    private String mOrderId;// 订单号

    public IAPOnlineHeyue(Context context) {
        mContext = context;
        mAdapter = this;
        configDeveloperInfo(Wrapper.getDeveloperInfo());
        HYSDK.handlerPayListener(payListener);// 支付回調
    }

    @Override
    public String getOrderId() {
        return mOrderId;
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

    /**
     * 插件调用支付
     */
    @Override
    public void payForProduct(Hashtable<String, String> productInfo) {
        LogD("payForProduct(" + productInfo.toString() + ")invoked!");
        final Hashtable<String, String> curCPInfo = productInfo;
        PluginWrapper.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (!HeyueWrapper.getInstance().isInited()) {
                    payResult(IAPWrapper.PAYRESULT_FAIL, "init fail");
                    return;
                }
                if (!HeyueWrapper.getInstance().networkReachable(mContext)) {
                    payResult(IAPWrapper.PAYRESULT_NETWORK_ERROR, "Network not available!");
                    return;
                }
                if (HeyueWrapper.getInstance().isLogined()) {
                    // 只有登录成功才能使用支付
                    getPayOrderId(curCPInfo);

                } else {
                    // login first,after the success of the login to pay again
                    HeyueWrapper.getInstance().userLogin(new ILoginCallback() {

                        @Override
                        public void onFailed(int arg0, String arg1) {
                            payResult(IAPWrapper.PAYRESULT_FAIL, "login fail,msg:" + arg1);
                        }

                        @Override
                        public void onSuccessed(int arg0, String arg1) {

                            getPayOrderId(curCPInfo);
                        }

                    });
                }

            }
        });

    }

    @Override
    public void setDebugMode(boolean bDebug) {
        LogD("setDebugMode(" + bDebug + ") invoked! it is not used.");
        // it is not used.
    }

    /**
     * 初始化sdk
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
                        payResult(IAPWrapper.PAYRESULT_INIT_FAIL, arg1);
                    }

                    @Override
                    public void onSuccessed(int arg0, String arg1) {
                        payResult(IAPWrapper.PAYRESULT_INIT_SUCCESS, arg1);
                    }

                };
                if (!HeyueWrapper.getInstance().initSDK(mContext, "IAPOnlineHeyue", curCPInfo, mAdapter, listener)) {
                    payResult(IAPWrapper.PAYRESULT_INIT_FAIL, "initSDK false");
                }
            }
        });
    }

    /**
     * 获取订单号 如果SDK对价格的格式有特殊的要求需要对价格处理好了才去获取订单号
     *
     * @param productInfo
     */
    private void getPayOrderId(final Hashtable<String, String> productInfo) {
        Role_Level = productInfo.get("Role_Level");// 游戏角色等级
        LogD("Role_Level is" + Role_Level);
        // String productId = (String) productInfo.get("Product_Id");//
        // final String productName = (String) productInfo.get("Product_Name");
        // String productPrice = (String) productInfo.get("Product_Price");
        // String productCount = (String) productInfo.get("Product_Count");
        // String gameUserId = (String) productInfo.get("Role_Id");
        // String roleName = (String) productInfo.get("Role_Name");
        // String gameServerId = (String) productInfo.get("Server_Id");
        // String productType = productInfo.get("Product_Type");
        // String goldNum = (String) productInfo.get("Coin_Num");
        // String ext = (String) productInfo.get("EXT");
        // if ((productId == null) || (productName == null) || (productPrice ==
        // null) || (productCount == null)
        // || (gameUserId == null) || (roleName == null) || (gameServerId ==
        // null)) {
        // LogD("something is null");
        // payResult(IAPWrapper.PAYRESULT_PRODUCTIONINFOR_INCOMPLETE, "something
        // is null");
        // return;
        // }
        //
        // if (ext == null) {
        // ext = "";
        // }
        //
        // // 商品数量的判断
        // int count = Integer.parseInt(productCount);
        // count = count < 1 ? 1 : count;
        //
        // // 默认的价格处理，如有特殊要求可修改这部分
        // float price = Float.parseFloat(productPrice);
        // price = price < 0.01f ? 0.01f : price;
        // price = price * count;
        // // 这里给价格做了保留两位小数的处理
        // DecimalFormat df = new DecimalFormat("0.00");
        // productPrice = df.format(price);
        // productCount = String.valueOf(count);
        //
        // // 特殊数据处理完，保存好
        // productInfo.put("Product_Price", productPrice);
        // productInfo.put("Product_Count", productCount);
        // productInfo.put("EXT", ext);
        //
        // // 获取订单号前对数据的封装
        // Hashtable<String, String> orderInfo =
        // IAPWrapper.formatPayRequestData(productPrice, gameUserId,
        // gameServerId,
        // JingqiWrapper.getInstance().getUserID(), productId, productName,
        // goldNum, productType, productCount,
        // ext);
        // orderInfo.put("r_order_url",
        // Wrapper.getSDKParm_r_order_url(JingqiWrapper.getInstance().getSDKServerName()));
        //
        // // 向后端获取订单号
        // IAPWrapper.getPayOrderId(mContext, orderInfo, new SdkHttpListener() {
        // @Override
        // public void onResponse(String response) {
        // LogD("getPayOrderId onResponse:" + response);
        // GetOrderIdCallbackDataInfo info =
        // IAPWrapper.handlerGetOrderIdDataFromServer(response);
        // if (info != null) {// 获取订单号成功
        // mOrderId = info.orderId;
        RSDKIAP.getInstance().resetPayState();
        // 调用支付之前调用一次补点接口；补发没有到账的游戏币
        HeyueWrapper.checkPurchase();
        payInSDK(productInfo);
        // } else {// 获取订单出错
        // payResult(IAPWrapper.PAYRESULT_FAIL, "status error");
        // }
        // }
        //
        // @Override
        // public void onError() {
        // // 其他错误
        // payResult(IAPWrapper.PAYRESULT_FAIL, "getPayOrderId onError");
        // }
        // });

    }

    // 支付回调
    // HYSDK.handlerPayListener(payListener);// 支付回調
    PayListener payListener = new PayListener() {

        @Override
        public void onPayResult(boolean isSuccess, int point) {
            // TODO Auto-generated method stub
            // 支付成功
            if (isSuccess) {
                LogD("pay success:" + point);
                payResult(IAPWrapper.PAYRESULT_SUCCESS, "pay success");
            } else {
                // 支付失败
                LogD("pay failed:" + point);
                payResult(IAPWrapper.PAYRESULT_FAIL, "pay failed");
            }
        }
    };

    /**
     * SDK支付
     *
     * @param productInfo :商品参数
     */
    private void payInSDK(final Hashtable<String, String> productInfo) {
        try {
            // TODO 获取支付回调地址，有些SDK是在前端支付时设置支付回调地址的，有些SDK是在SDK后台设置的，根据情况使用
            String notifyUrl = Wrapper.getSDKParm_r_nofify_url(HeyueWrapper.getInstance().getSDKServerName());

            LogD("pay params:");
            PluginWrapper.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    // TODO SDK支付实现
                    // HashMap<String, String> map = new HashMap<String,
                    // String>();
                    //// map.put(GHValues.ROLE_LEVEL,
                    // UserJingqi.getRoleLevel());
                    // map.put(GHValues.ROLE_LEVEL, "5");
                    // HYSDK.pay(mContext, map, new PayListener() {
                    //
                    // @Override
                    // public void onPayResult(boolean isSuccess, int point) {
                    // // 支付成功
                    // if (isSuccess) {
                    // LogD("pay success:" + point);
                    // payResult(IAPWrapper.PAYRESULT_SUCCESS, "pay success");
                    // } else {
                    // // 支付失败
                    // LogD("pay failed:" + point);
                    // payResult(IAPWrapper.PAYRESULT_FAIL, "pay failed");
                    // }
                    // }
                    // });
                    // String tradeWay = productInfo.get("Ext");
                    // if(!tradeWay.equals("normal"));

                    String channel = mContext.getResources().getString(R.string.hy_platform);
                    LogD("game channel is belong---" + channel);
                    // 判断是不是官斗游戏
                    if (HeyueWrapper.getGameName().equals("guandou")) {
                        if(channel.equals("HY")){
                            // 和悦官网第三方储值接口
                            LogD("This package belong to heyue!");
                            HashMap<String, String> map1 = new HashMap<String, String>();
                            map1.put(GHValues.ROLE_LEVEL, Role_Level);
                            HYSDK.pay(mContext, map1, payListener);
                        }
                        String ProductId = HeyueWrapper.getProductId();
                        LogD("product id is---" + productInfo.get("Product_Id"));
                        // 判断泰国月卡至尊卡商品 ID 特殊标识是否为空,如果为空则非泰国渠道
                        if (ProductId.isEmpty()) {
                            LogD("This guandou package is not to Thailand! Default pay by google！");
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(GHValues.PRO_ITEM_ID, (String) productInfo.get("Product_Id"));
                            HYSDK.singlePay(mContext, map, payListener);
                        } else {
                            // 和悦泰国官斗只有一个包，支付的时候如果是月卡和至尊卡则调用谷歌支付，其余时间都调用第三方支付
                            LogD("This channl is Thailand!!! ProductId is--->" + ProductId);
                            // 如果商品 ID，属于配置好的月卡和至尊卡，那么调用谷歌支付
                            if (ProductId.contains(productInfo.get("Product_Id"))) {
                                LogD("This Product is belong to GooglePlay!");
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put(GHValues.PRO_ITEM_ID, (String) productInfo.get("Product_Id"));
                                LogD("xy------>product_ID is" + (String) productInfo.get("Product_Id"));
                                HYSDK.singlePay(mContext, map, payListener);
                            } else {
                                // 和悦官网第三方储值接口
                                LogD("This package belong to heyue!");
                                HashMap<String, String> map1 = new HashMap<String, String>();
                                map1.put(GHValues.ROLE_LEVEL, Role_Level);
                                HYSDK.pay(mContext, map1, payListener);
                            }
                        }
                    } else {
                        // 其他游戏默认调用谷歌支付接口
                        LogD("Default game google pay！");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(GHValues.PRO_ITEM_ID, (String) productInfo.get("Product_Id"));
                        HYSDK.singlePay(mContext, map, payListener);
                    }
                }
            });

        } catch (Exception e) {
            LogE("payInSDK error", e);
            payResult(IAPWrapper.PAYRESULT_FAIL, "payInSDK error");
        }
    }

    // 第三方网页储值
    public void payForProductByWeb(Hashtable<String, String> productInfo) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(GHValues.PRO_ITEM_ID, (String) productInfo.get("Product_Id"));
        HYSDK.pay(mContext, map, new PayListener() {

            @Override
            public void onPayResult(boolean isSuccess, int point) {
                // TODO Auto-generated method stub
                // 支付成功
                if (isSuccess) {
                    LogD("pay success:" + point);
                    payResult(IAPWrapper.PAYRESULT_SUCCESS, "pay success");
                } else {
                    // 支付失败
                    LogD("pay failed:" + point);
                    payResult(IAPWrapper.PAYRESULT_FAIL, "pay failed");
                }
            }
        });
    }

    protected void LogE(String msg, Exception e) {
        if (e == null) {
            Log.e(LOG_TAG, msg);
        } else {
            Log.e(LOG_TAG, msg, e);
        }
    }

    protected void LogD(String msg) {
        try {
            Log.d(LOG_TAG, msg);
        } catch (Exception e) {
            LogE("LogD error", e);
        }
    }

    public void payResult(int code, String msg) {
        LogD("payResult( " + code + ", " + msg + ") invoked!");
        IAPWrapper.onPayResult(mAdapter, code, msg);
    }

}
