package uk.ac.cam.groupproj.racethewild;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class GPSsettings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpssettings);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gpssettings, menu);
		return true;
	}
	
	public void plus(View view){
		EditText poll_update = (EditText) findViewById(R.id.gps_pt);
		Integer currentTime = Integer.parseInt(poll_update.getText().toString());
		currentTime++;
		poll_update.setText(currentTime.toString());
	}
	
	public void minus(View view){
		EditText poll_update = (EditText) findViewById(R.id.gps_pt);
		Integer currentTime = Integer.parseInt(poll_update.getText().toString());
		if (currentTime > 0) {
			currentTime--;
			poll_update.setText(currentTime.toString());
		}
	}
	

}
