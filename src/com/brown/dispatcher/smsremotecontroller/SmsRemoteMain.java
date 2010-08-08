package com.brown.dispatcher.smsremotecontroller;

/**
 * @author lgzvalle@gmail.com
 * Project site: https://code.google.com/p/smsremotecontrol/
 * Code license: Apache 2.0
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class SmsRemoteMain extends Activity implements OnClickListener, SmsRemoteCommon {
	private static SharedPreferences mSettings;
	private ArrayList<String> appPackageArray;
	private ArrayList<String> appNameArray;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Load preferences
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		
		//Fill spinner
		Spinner s = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = fillActivities();
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    s.setAdapter(adapter);
	    s.setOnItemSelectedListener(new ActivitiesSpinnerListener());
		
		//Register view listeners
		TextView keyword = (TextView)findViewById(R.id.keyword);
		keyword.setText(mSettings.getString(KEYWORD, null));
		keyword.addTextChangedListener(new KeywordWatcherListener());
		
	}
	
	private class KeywordWatcherListener implements TextWatcher {

		/**
		 * After every type on keyword EditView we store the value. 
		 */
		public void afterTextChanged(Editable s) {
			String keyword = s.toString();
			SharedPreferences.Editor editor = mSettings.edit();
			
			//If empty save KEYWORD as 'null' for validation purposes
			editor.putString(KEYWORD, keyword.equals("")?null:keyword);
			editor.commit();
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
				//Do nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
				//Do nothing
		}
		
	}
	
	private class ActivitiesSpinnerListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	// Store package and class names in SharedPreferences
	    	String className = (String) appNameArray.get(pos);
			String packageName = (String) appPackageArray.get(pos);
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putString(CLASSNAME, className);
			editor.putString(PACKAGENAME, packageName);
			editor.commit();
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.exit:
	    	if (validateForm()) {
	    		finish();
	    	}
	        return true;
	        
	    case R.id.tryit:
	    	if (validateForm()) {
	    		tryit();
	    	}
	        return true;
	        
	    case R.id.about:
	    	Intent in = new Intent(this, SmsRemoteHowto.class);
	    	startActivity(in);
	    	return true;
	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private boolean validateForm() {
		if (mSettings.getString(CLASSNAME, null) == null ||
				mSettings.getString(KEYWORD, null) == null) {
			Toast.makeText(this, this.getString(R.string.validationFail), Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/** 
	 * Fills activities adapter to populate the spinner
	 * @return
	 */
	private ArrayAdapter<String> fillActivities() {

		// Get ACTION_MAIN applications
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);

		// We need an array with package and class name of each activity
		appPackageArray = new ArrayList<String>();
		appNameArray = new ArrayList<String>();
		
		// Iterate over application list from PackageManager and get class and package names
		Iterator<ResolveInfo> it = pkgAppsList.iterator();
		while (it.hasNext()) {
			ActivityInfo activityInfo = ((ResolveInfo) it.next()).activityInfo;

			String activityPackgeName = activityInfo.name.substring(0, activityInfo.name.lastIndexOf("."));
			String activityClassName = activityInfo.name.substring(activityInfo.name.lastIndexOf(".") + 1, activityInfo.name.length());
			
			appPackageArray.add(activityPackgeName);
			appNameArray.add(activityClassName);
			it.next();
		}

		// Build list
		ArrayAdapter<String> clasess = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNameArray);
		return clasess;

	}

	/**
	 * Send broadcast message to SmsRemoteReceiver to try keyword recognition and application launching.
	 */
	private void tryit() {
		final String smsBody = this.getString(R.string.dummy_sms) + mSettings.getString(KEYWORD, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("- Activity to launch: "+mSettings.getString(CLASSNAME, null) + "\n\n- SMS message to forward: \""+smsBody+"\"")
		       .setCancelable(false)
		       .setTitle("Sending dummy sms to BroadcastReceiver")
		       .setPositiveButton("Send", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        		sendBroadcastSMS(smsBody);
		           }
		       
		       });
	       
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void sendBroadcastSMS(String body) {
		Intent di = new Intent(this, SmsRemoteReceiver.class);
		di.setAction(TRY_IT);
		di.putExtra(DUMMY_SMS, body);
		sendBroadcast(di);
	}
	
	@Override
	public void onClick(View v) {
		//Do nothing
	}
}
