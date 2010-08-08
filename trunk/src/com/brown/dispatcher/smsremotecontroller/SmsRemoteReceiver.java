package com.brown.dispatcher.smsremotecontroller;

/**
 * @author lgzvalle@gmail.com
 * Project site: https://code.google.com/p/smsremotecontrol/
 * Code license: Apache 2.0
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsRemoteReceiver extends BroadcastReceiver implements SmsRemoteCommon {
	private static SharedPreferences mSettings = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		mSettings = context.getSharedPreferences(PREFS_NAME, 0);
		Bundle extras = intent.getExtras();
		if (extras == null)
			return;
		
		String smsBody = new String();
		String action = intent.getAction();
		
		// Get real sms or dummy one to try the receiver
		if (action.equals(SMS_RECEIVED)) {
			Object[] pdus = (Object[]) extras.get("pdus");
			
			// Only interested in array first object (last sms received)
			SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
			smsBody = message.getMessageBody();
			
		} else if (action.equals(TRY_IT)) {
			smsBody = extras.getString(DUMMY_SMS);
		}

		//If the message body contains the secret keyword, then launch selected application and forward message body to it.
		if (isKeywordInSms(smsBody)) {
			String packageName = mSettings.getString(PACKAGENAME, null);
			String className = mSettings.getString(CLASSNAME, null);
			
			Intent di = new Intent();
			di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			di.putExtra(SMS_BODY, smsBody);
			di.setClassName(packageName, packageName + "." + className);
			
			try {
				context.startActivity(di);	
				
			} catch (RuntimeException e) {
				CharSequence text = "SmsRemoteController error:\nApplication "+className+" cannot be found.";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
	}
	
	


	/**
	 * Check if user secret keyword is in sms body
	 * 
	 * @param smsBody
	 * @return
	 */
	
	private boolean isKeywordInSms(String smsBody) {
		String keyword  = mSettings.getString(KEYWORD, null);
		return (keyword != null && smsBody.contains(keyword));
	}

}