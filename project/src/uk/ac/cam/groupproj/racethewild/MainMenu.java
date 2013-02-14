package uk.ac.cam.groupproj.racethewild;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenu extends Activity {

	// public final static String ENGINE_MESSAGE =
	// "uk.ac.cam.groupproj.racethewild.ENGINE";
	
	public final static String ANIMAL_ID ="uk.ac.cam.groupproj.racethewild.MESSAGE";

	Engine engine;
	SatNavUpdate snu;
	int movePoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		Engine.initialise(this); // Initialise the engine.
		engine = Engine.get();
		snu = engine.fetchSatNavData(getApplicationContext());
		movePoints = snu.getMovePoints();
		TextView textView = (TextView) findViewById(R.id.infoText);
		textView.setText("Check in now for " + movePoints + " movement points");
	}

	public void moveToCollection(View view) {
		Intent intent = new Intent(this, ScrollAnimalCollection.class);

		// intent.putExtra(ENGINE_MESSAGE, engine); //for when we start sending
		// around Engine.
		startActivity(intent);
	}

	public void moveToNodeMap(View view) {
		Intent intent = new Intent(this, NodeScene.class); 
		startActivity(intent);
	}
	
	public void checkIn(View view){
		Intent intent = new Intent(this, CheckInScene.class);
		startActivity(intent);		
	}
	
	public void reset(View view){
		Engine.reset(this);
	}
	
	public void gpsdebug(View view) {
		Intent intent = new Intent(this, GPSdebug.class);
		startActivity(intent);		
	}
	
	public void toggleGPS(View view) {
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		boolean currentStatus = sharedPref.getBoolean("gps_wanted", false);
		Button toggleButton = (Button)findViewById(R.id.gpsToggleButton);
		
		if (currentStatus) {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("gps_wanted", false);
			editor.commit();
			toggleButton.setText("Turn GPS requests on");
		} else {
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("gps_wanted", true);
			editor.commit();
			toggleButton.setText("Turn GPS requests off");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

}
