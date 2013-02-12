package uk.ac.cam.groupproj.racethewild;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSservice extends Service {   	// to stop call stopSelf()
	
	private LocationManager locationManager;
	Location currentLocation;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		locationManager.requestLocationUpdates(provider, checkInterval, minDistanceMoved, locationListener);
		return Service.START_STICKY;   // indicates service is explicitly started and stopped as needed
	}
	
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
	 *   Description needs doing (mti20)
	 */
	private int calculateMovementPoints(Location newLocation, Location oldLocation) {
		double A,b,c,d,e;
		
		double v = 0; // TODO speed
		double x = 0; // TODO distance
		
		Context context = this;
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_coefficients_file_key), Context.MODE_PRIVATE); 
		A = (double)sharedPref.getFloat("gps_A",(float)0.005);
		b = (double)sharedPref.getFloat("gps_b",(float)0.3);
		c = (double)sharedPref.getFloat("gps_c",(float)0);
		d = (double)sharedPref.getFloat("gps_d",(float)0.001);
		e = (double)sharedPref.getFloat("gps_e",(float)15);
		
		double movementPoints = e * (A*Math.exp((-1)*b*v)+c*v+d) * x;
		//return (int)Math.round(movementPoints);
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
	final int thresholdDistance = 20;                            // minimum distance moved to be counted as a reasonable distance (units undefined)
	
	final int checkInterval = checkIntervalSeconds*1000;
	final String provider = LocationManager.GPS_PROVIDER;
	
	private final LocationListener locationListener = new LocationListener() {

	    @Override
	    public void onLocationChanged(Location newLocation) {
   	    	final double distanceMoved = (double)newLocation.distanceTo(currentLocation);
	    	
	    	if (distanceMoved >= thresholdDistance) {
	    		int movementPoints = calculateMovementPoints(newLocation,currentLocation);
	    		saveData(movementPoints);
	    	}

	    	currentLocation = newLocation;
	    }
	    
        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
	};


}
