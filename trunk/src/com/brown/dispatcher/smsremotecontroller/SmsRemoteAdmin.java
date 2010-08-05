package com.brown.dispatcher.smsremotecontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SmsRemoteAdmin extends ListActivity implements SmsRemoteCommon {
	private static final String TAG = "SmsRemoteAdmin";

	public static final String SMS_FROM_ADDRESS_EXTRA = "com.example.android.apis.os.SMS_FROM_ADDRESS";
	public static final String SMS_FROM_DISPLAY_NAME_EXTRA = "com.example.android.apis.os.SMS_FROM_DISPLAY_NAME";
	public static final String SMS_MESSAGE_EXTRA = "com.example.android.apis.os.SMS_MESSAGE";

	private ArrayList<HashMap<String, String>> appNameList = null;
	private static SharedPreferences mSettings = null;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
		// mDbHelper = new SmsRemoteDbAdapter(this);
		// mDbHelper.open();

		
		// Restore preferences
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		String className = mSettings.getString(CLASSNAME, null);
		String packageName = mSettings.getString(PACKAGENAME, null);
		
		
		Log.e(TAG, "Restoring settings "+packageName+" - "+className);
		
		selectActivity(className, packageName);

		fillActivities();
		registerForContextMenu(getListView());

	}

	private void fillActivities() {

		// Get ACTION_MAIN ables applications
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> pkgAppsList = getPackageManager()
				.queryIntentActivities(mainIntent, 0);

		// Extract app name and populate arraylist with it.
		appNameList = new ArrayList<HashMap<String, String>>();
		Iterator<ResolveInfo> it = pkgAppsList.iterator();
		while (it.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();

			ActivityInfo activityInfo = ((ResolveInfo) it.next()).activityInfo;
			String activityPackgeName = activityInfo.name.substring(0,
					activityInfo.name.lastIndexOf("."));
			String activityClassName = activityInfo.name.substring(
					activityInfo.name.lastIndexOf(".") + 1, activityInfo.name
							.length());
			
			
			map.put(PACKAGENAME, activityPackgeName);
			map.put(CLASSNAME, activityClassName);
			appNameList.add(map);
			it.next();
		}

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { PACKAGENAME, CLASSNAME, CLASSNAME };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.packagename, R.id.classname, R.id.testest };

		// Build list
		SimpleAdapter clasess = new SimpleAdapter(this, appNameList, R.layout.activitylist, from, to);
		setListAdapter(clasess);
		getListView().setTextFilterEnabled(true);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long idx) {
		super.onListItemClick(l, v, position, idx);

		int id = (int) idx;
		
		String className = mSettings.getString(CLASSNAME, null);
		String packageName = mSettings.getString(PACKAGENAME, null);
		
		
		className = (String) ((HashMap) appNameList.get(id)).get(CLASSNAME);
		packageName = (String) ((HashMap) appNameList.get(id)).get(PACKAGENAME);
		selectActivity(className, packageName);
		
		className = mSettings.getString(CLASSNAME, null);
		packageName = mSettings.getString(PACKAGENAME, null);
		
		
		setResult(RESULT_OK);
		finish();
		// Intent i = new Intent(this, NoteEdit.class);
		// i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		// startActivityForResult(i, ACTIVITY_EDIT);
	}

	private void selectActivity(String className, String packageName) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString(CLASSNAME, className);
		editor.putString(PACKAGENAME, packageName);
		editor.commit();
	}

}
