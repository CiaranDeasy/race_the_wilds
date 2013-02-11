package uk.ac.cam.groupproj.racethewild;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

// Andrew - can you set this so it works correctly as a separate process?
public class SatNavProcess extends Activity {

	private LocationManager locationManager;
	Location currentLocation;
	
	// Andrew - I'm kind of relying on you to refactor this as a process - if this is too much work, let me know and we can split it up more between the three of us
	// Andrew - see my on create method at the bottom - this needs to be executed on process startup - however that works. thanks.
	
	/*
	 *  TODO
	 *  Public interface method - used for IPC
	 */
	public SatNavUpdate getMovementPoints() {
		// TODO
		@SuppressWarnings("unused")
		int latestSatnavData = getLatestData();   // some mock call to getLatestData (removes the warning)
		return new SatNavUpdate(0,0);
	}
	
	/* 
	 *  TODO
	 *  Called by main method whenever movement points are retrieved
	 */
	private void saveData(int movementPoints) {
		// TODO (tmjm2)
	}
	
	/*
	 *  TODO
	 *  Used by the public interface data to get the latest set of
	 *  movement points
	 */
	private int getLatestData() {
		// TODO (tmjm2)
		return 0;
	}
	
	/*
	 *   mti20 TODO
	 *   A simple linear function in this iteration
	 *   THIS HAS NOT BEEN DONE YET - I'll do it on Monday/Tuesday
	 */
	private int calculateMovementPoints(Location newLocation, Location oldLocation) {
		return 10;
	}
	
	/*
	 *  Location listener
	 *  Location updated at least every 30s and if person moves more than 20m
	 *  Interacts with the android GPS methods, calculates movement points
	 *  and saves them using the saveData method
	 */
	
	final int checkIntervalSeconds = 30;                         // interval to check location (in seconds)
	final int minDistanceMoved = 20;                             // minimum distance moved in the above time to trigger an update
	final int thresholdDistance = 10;                             // minimum distance moved to be counted as a reasonable distance (units undefined)
	
	final int checkInterval = checkIntervalSeconds*1000;
	final String provider = LocationManager.GPS_PROVIDER;
	
	private final LocationListener locationListener = new LocationListener() {

	    public void onLocationChanged(Location newLocation) {
   	    	final double distanceMoved = (double)newLocation.distanceTo(currentLocation);
	    	
	    	if (distanceMoved >= thresholdDistance) {
	    		int movementPoints = calculateMovementPoints(newLocation,currentLocation);
	    		saveData(movementPoints);
	    	}

	    	currentLocation = newLocation;
	    }
	    
        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
	};
	

	// Andrew - can you update this so it works properly for a process? I need the locationManger.requestLocationUpdates(...) to run on application startup
	// the rest of this method is probably unnecessary, because it just relates to activities
	@SuppressLint("NewApi") //Allows us to have the upEnabled setting. Could remove later. Android 4.1 Feature.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animal_collection);
		// Show the Up button in the action bar.
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		locationManager.requestLocationUpdates(provider, checkInterval, minDistanceMoved, locationListener);
	}
	
	// Andrew - this is another method that is probably unnecessary - please update for process accordingly
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
