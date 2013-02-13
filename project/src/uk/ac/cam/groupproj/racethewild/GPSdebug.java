package uk.ac.cam.groupproj.racethewild;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class GPSdebug extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// load values!
		setContentView(R.layout.activity_gpsdebug);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gpsdebug, menu);
		return true;
	}
	
	public void updateMovementCoefficients(View view){
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_coefficients_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		EditText A = (EditText) findViewById(R.id.gps_coeff_A);
		editor.putFloat("gps_A", Float.parseFloat(A.getText().toString()));
		EditText b = (EditText) findViewById(R.id.gps_coeff_b);
		editor.putFloat("gps_b", Float.parseFloat(b.getText().toString()));
		EditText c = (EditText) findViewById(R.id.gps_coeff_c);
		editor.putFloat("gps_c", Float.parseFloat(c.getText().toString()));
		EditText d = (EditText) findViewById(R.id.gps_coeff_d);
		editor.putFloat("gps_d", Float.parseFloat(d.getText().toString()));
		EditText e = (EditText) findViewById(R.id.gps_coeff_e);
		editor.putFloat("gps_e", Float.parseFloat(e.getText().toString()));
		editor.commit();
	}
	
	public void startGPSprocess(View view){
		// create a text box for how often gps is polled
		System.out.println("GPS start button pressed");
		Context context = getApplicationContext();
		Intent intent = new Intent(context,GPSservice.class);
		PendingIntent pintent = PendingIntent.getService(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
		((AlarmManager)getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60*1000, pintent);
	}
	
	public void stopGPSprocess(View view){
		
	}
	
}