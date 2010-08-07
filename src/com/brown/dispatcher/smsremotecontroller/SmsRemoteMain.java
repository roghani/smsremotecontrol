package com.brown.dispatcher.smsremotecontroller;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
	
	
	/**
	 * Send broadcast message to SmsRemoteReceiver to try keyword recognition and application launching.
	 */
	private void tryit() {
		Intent di = new Intent(this, SmsRemoteReceiver.class);
		di.setAction("TRYIT");
		di.putExtra("fakeSms", "This is a fake sms to try broadcastreceiver. Keyword is "+ mSettings.getString(KEYWORD, null));
		sendBroadcast(di);
	}
		
	
	/** 
	 * Every time we came back the view needs to be filled with new data.	
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
	
	/**
	 * Populate main view form.
	 * This is called everytime view needs to be reloaded.
	 * Data is obtained from SharedPreferences.
	 */
	private void fillData() {
		TextView classname = (TextView)findViewById(R.id.activityName);
		classname.setText(mSettings.getString(CLASSNAME, null));
		
		TextView keywordV = (TextView)findViewById(R.id.keyword);
		keywordV.setText(mSettings.getString(KEYWORD, null));
		
	}
}
