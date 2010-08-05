package com.brown.dispatcher.smsremotecontroller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SmsRemoteKeywordEdit extends Activity implements OnClickListener, SmsRemoteCommon{
	private static SharedPreferences mSettings;
	
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keywordedit);
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		
		//Set current keyword value
		EditText keyword = (EditText)findViewById(R.id.keyword_edit);
		keyword.setText(mSettings.getString(KEYWORD, null));
		
		//Button listener
		Button keywordButton = (Button)findViewById(R.id.keyword_button);
		keywordButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.keyword_button) {
			//Store new keyword in app preferences and return
			EditText keyword = (EditText)findViewById(R.id.keyword_edit);
			
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putString(KEYWORD, keyword.getText().toString());
			editor.commit();
			finish();
		}
		
	}
}
