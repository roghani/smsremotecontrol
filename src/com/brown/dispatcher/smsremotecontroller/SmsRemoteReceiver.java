package com.brown.dispatcher.smsremotecontroller;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsRemoteReceiver extends BroadcastReceiver implements SmsRemoteCommon {
	private static final String TAG = "SmsRemoteReceiver";
	private static SharedPreferences mSettings = null;

	@Override
	public void onReceive(final Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		if (extras == null)
			return;

		mSettings = context.getSharedPreferences(PREFS_NAME, 0);

		Object[] pdus = (Object[]) extras.get("pdus");
		// Only interested in first sms of the array
		SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
		String smsBody = message.getMessageBody();

		if (isKeywordInSms(smsBody)) {
			String packageName = mSettings.getString(PACKAGENAME, null);
			String className = mSettings.getString(CLASSNAME, null);
			
			// Send intent to mActivityName class
			Intent di = new Intent();
			di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			di.setClassName(packageName, packageName + "." + className);
			
			try {
				context.startActivity(di);	
				
			} catch (RuntimeException e) {
				Log.e(TAG, "Clase no encontrada: "+e.getMessage());
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
		        dlgAlert.setMessage("This is an alert with no consequence");
		        dlgAlert.setTitle("App Title");
		        dlgAlert.setPositiveButton("OK", null);
		        dlgAlert.setCancelable(true);
		        dlgAlert.create().show();
				
			}
		}
	}
	
	

	/**
	 * Check user secret keyword in sms body
	 * 
	 * @param smsBody
	 * @return
	 */
	
	private boolean isKeywordInSms(String smsBody) {
		String keyword  = mSettings.getString(KEYWORD, null);
		return smsBody.contains(keyword);
	}

}