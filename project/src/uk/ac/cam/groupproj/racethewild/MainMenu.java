package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		updateScreen();
	}

	public void moveToCollection(View view) {
		Intent intent = new Intent(this, ScrollAnimalCollection.class);

		startActivity(intent);
	}

	public void moveToNodeMap(View view) {
		Intent intent = new Intent(this, NodeScene.class); 
		startActivity(intent);
	}
	
	public void challengeList(View view) {
		Intent intent = new Intent(this, ChallengeList.class); 
		startActivity(intent);
	}
	
	
	public void reset(View view){
		Engine.reset(this);
		engine = Engine.get(); 
		//whoops! Fixed bug where we reset the engine 
		//but not the reference to it. This is not a good thing to do :P
	}
	
	public void gpsdebug(View view) {
		Intent intent = new Intent(this, GPSdebug.class);
		startActivity(intent);		
	}
	
	public void toggleCheckin(View view) {
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		boolean currentStatus = sharedPref.getBoolean("gps_wanted", true);
		Button toggleButton = (Button)findViewById(R.id.checkin_button);
		
		if (currentStatus) {
			SharedPreferences.Editor editor = sharedPref.edit();  //turn off and check in
			editor.putBoolean("gps_wanted", false);
			editor.commit();
			
			toggleButton.setText(getString(R.string.menu_start_tracking_text)); 
			Intent intent = new Intent(this, CheckInScene.class);
			startActivity(intent);		
		
		
		} else {
			SharedPreferences.Editor editor = sharedPref.edit();  //turn on
			editor.putBoolean("gps_wanted", true);
			editor.commit();
			toggleButton.setText(getString(R.string.menu_check_in_text));
		}
		updateScreen();
	}
	
	public void gpsSettings(View view) {
		Intent intent = new Intent(this, GPSsettings.class);
		startActivity(intent);
	}
	
	public void addMovement(View view) {
		SharedPreferences sharedPref = this.getSharedPreferences(this.getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("movement_points", sharedPref.getInt("movement_points", 0) + 100);
		editor.putInt("distance", sharedPref.getInt("distance", 0) + 50);
		editor.commit();
		
	
		updateScreen();

		
	}
	
	private void updateScreen(){
		
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		boolean currentStatus = sharedPref.getBoolean("gps_wanted", false);
		
		if(!currentStatus)
		{
			TextView textView = (TextView) findViewById(R.id.infoText);
			textView.setText("Press " + getString(R.string.menu_start_tracking_text) + " to start tracking yourself for movement points!");
			

			TextView animalText = (TextView) findViewById(R.id.menu_animal_text);
			animalText.setText("");
			
			ImageView animalImage = (ImageView) findViewById(R.id.menu_animal_image_view);
			Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.otherlogo);
			animalImage.setImageBitmap(logo);
			return;
		}
		snu = engine.fetchSatNavData(getApplicationContext());
		movePoints = snu.getMovePoints();
		
		
		TextView textView = (TextView) findViewById(R.id.infoText);
		textView.setText("Check in now for " + movePoints + " movement points");
		
		TextView animalText = (TextView) findViewById(R.id.menu_animal_text);
		
		List<Animal> animals = engine.getAllAnimals();
		int distance = engine.fetchSatNavData(getApplicationContext()).getDistance();
		int closest = Integer.MAX_VALUE;
		Animal chosen = null;
		for(Animal a:animals){
			if((Math.abs(a.getDistancePerDay()-distance) < Math.abs(closest)) && a.getColour()==Colour.White && 
					(!a.isChallenge())){
				if(closest>0 || (a.getDistancePerDay()-distance) <0)  //make it so if we are giving an animal away we always display that.
				{
				closest = a.getDistancePerDay()-distance;
				chosen = a;
				}
			}
		}
		float ratio = Float.MAX_VALUE;
		
		if(chosen != null){
			try {
			ImageView animalImage = (ImageView) findViewById(R.id.menu_animal_image_view);
			
			

				InputStream animalInStream= getAssets().open(chosen.getGraphicPath());

				Bitmap animbmp = BitmapFactory.decodeStream(animalInStream);
				animalInStream.close();
				if(animbmp != null){
					animalImage.setImageBitmap(animbmp);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			
			if(distance != 0){
				ratio = ((float)chosen.getDistancePerDay())/(float)distance;
			}
			if(ratio<=1){
				animalText.setText("Press Check In to release " + chosen.getName() + " into the wild!");
	
			} else {
				animalText.setText("Move "+(chosen.getDistancePerDay()-distance)+" more meters to release a "+ chosen.getName());
			}

		}
		// Handle the case where all animals have been released already.
		else {
			animalText.setText("You have already released all the animals!");
		} 
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        //case R.id.reset_save:
	        //    reset(getCurrentFocus());
	        //    return true;
	        //case R.id.add_points:
	        //    addMovement(getCurrentFocus());
	        //    return true;
	        case R.id.gpsSettingsButton:
	            gpsSettings(getCurrentFocus());
	            return true;
	        case R.id.gpsDebugButton:
	            gpsdebug(getCurrentFocus());
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
