package com.brown.dispatcher.smsremotecontroller;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SmsRemoteMain extends Activity implements OnClickListener, SmsRemoteCommon {
	private static SharedPreferences mSettings;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Load preferences
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		
		
		//Register view listeners
		ArrayList<Integer> views = new ArrayList<Integer>();
		views.add(R.id.keyword_label);
		views.add(R.id.keyword);
		views.add(R.id.activity_label);
		views.add(R.id.activityName);
		views.add(R.id.exit);
		views.add(R.id.tryit);
		registerListeners(views);

		
		//Set classname & keyword info
		fillData();

	}

	private void registerListeners(ArrayList<Integer> views) {
		Iterator<Integer> it = views.iterator();
		while (it.hasNext()) {
			View view = findViewById((Integer)it.next());
			view.setOnClickListener(this);
		}
		
	}

	private void fillData() {
		//String packageName = mSettings.getString(PACKAGENAME, null);
		
		TextView classname = (TextView)findViewById(R.id.activityName);
		classname.setText(mSettings.getString(CLASSNAME, null));
		
		//EditText keyword = (EditText)findViewById(R.id.keyword_edit);
		//keyword.setText(mSettings.getString(KEYWORD, null));
		
		TextView keywordV = (TextView)findViewById(R.id.keyword);
		keywordV.setText(mSettings.getString(KEYWORD, null));
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_label: case R.id.activityName: 
			startActivityForResult(new Intent (this, SmsRemoteAdmin.class), 0);
			break;
		case R.id.keyword_label: case R.id.keyword:
			startActivityForResult(new Intent (this, SmsRemoteKeywordEdit.class), 0);
			break;
		case R.id.tryit:
			tryit();
			break;	
		case R.id.exit:
			finish();
			break;
		default:
			break;
		}
	}
	

	private void tryit() {
		String packageName = mSettings.getString(PACKAGENAME, null);
		String className = mSettings.getString(CLASSNAME, null);
		
		// Send intent to mActivityName class
		Intent di = new Intent();
		di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		di.setClassName(packageName, packageName + "." + className);
		try {
			this.startActivity(di);	
			
		} catch (RuntimeException e) {
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
	        dlgAlert.setMessage("Ooops! Application "+className+" cannot be launched. Maybe uninstalled?");
	        dlgAlert.setTitle("Error");
	        dlgAlert.setPositiveButton("OK", null);
	        dlgAlert.setCancelable(true);
	        dlgAlert.create().show();
			
		}
	}
		
	
		
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
