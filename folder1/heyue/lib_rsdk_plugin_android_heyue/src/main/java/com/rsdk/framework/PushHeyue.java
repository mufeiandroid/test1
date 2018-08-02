package com.rsdk.framework;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.rsdk.framework.InterfacePush;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PushHeyue implements InterfacePush{

	public static Activity mActivity = null;
	protected InterfacePush obj= this;
	boolean  isStopPush = true;
	private static PushHeyue _instance;
	private static final String TAG = "AnalyticsJPush";
	
	public PushHeyue(Context context) {
		mActivity = (Activity) context;	
		Log.i(TAG, "mActivity"+mActivity);
	}
	
	
	
	public static PushHeyue getInstance() {
		if (_instance == null) {
			_instance = new PushHeyue(mActivity);
		}
		return _instance;
	}
	
	@Override
	public void startPush() {
		// TODO Auto-generated method stub
//		 JPushInterface.init(mActivity);
		 
	}

	@Override
	public void closePush() {
		// TODO Auto-generated method stub
//		JPushInterface.stopPush(mActivity);
		isStopPush = false;
	}

	@Override
	public void setAlias(String paramString) {
		// TODO Auto-generated method stub
//		JPushInterface.setAlias(mActivity, paramString, new TagAliasCallback() {
//			
//			@Override
//			public void gotResult(int arg0, String arg1, Set<String> arg2) {
//				// TODO Auto-generated method stub
//				Log.i("jpush", "设置alias的回调");
//			}
//		});
	}

	@Override
	public void delAlias(String paramString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTags(ArrayList<String> paramArrayList) {
		 Set<String> set=new HashSet<String>();
		for(String x : paramArrayList){
			set.add(x);
		}
		// TODO Auto-generated method stub
//		JPushInterface.setTags(mActivity, set, new TagAliasCallback() {
//			
//			@Override
//			public void gotResult(int arg0, String arg1, Set<String> arg2) {
//				// TODO Auto-generated method stub
//				PushWrapper.onActionResult(obj, PushWrapper.ACTION_RET_RECEIVEMESSAGE, arg1);
//				Log.i("jpush", "设置tag的回调");
//			}
//		});
	}

	@Override
	public void delTags(ArrayList<String> paramArrayList) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void setDebugMode(boolean paramBoolean) {
		// TODO Auto-generated method stub
//		JPushInterface.setDebugMode(paramBoolean);
	}

	@Override
	public String getSDKVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPluginVersion() {
		// TODO Auto-generated method stub
		return null;
	}

//	public String getRegistrationID(){
//		String RegistrationID = JPushInterface.getRegistrationID(mActivity);
//		return RegistrationID;
		
//	}
	public  void setPushTime(Context context,Set<Integer> set,int startHoure,int endHoure){
//		JPushInterface.setPushTime(mActivity, set, startHoure, endHoure);
	}
	public  void setSilenceTime(Context context,int startHour,int startMinute,int endHoure,int endMinute){
//		JPushInterface.setSilenceTime(mActivity,startHour,startMinute,endHoure,endMinute);
	}
	public  boolean isPushStopped(Context context){
		return  isStopPush;
		
	}
	public void resuemPush(){
		if(!isStopPush){
			Log.i("jpush", "恢复推送");
//			JPushInterface.resumePush(mActivity); 
			isStopPush= true;
		}
		
		
	}
}
