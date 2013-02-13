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
import android.widget.Toast;

public class GPSservice extends Service {   	// to stop call stopSelf()
	
	private LocationListener locationListener;
	private LocationManager locationManager;
	private int checkIntervalSeconds;
	private int minDistanceMoved;
	private int checkInterval;
	private int thresholdDistance;                // threshold distance to count as an actual move (assuming moves >1mph over interval)
	private int cumulativeMovementPoints;
	Location currentLocation;
	
	/*
	 *  Returns true if the user wants to use GPS and false otherwise
	 */
	private boolean gpsWanted() {
		// TODO
		return true;
	}
	
	/* 
	 *  A debugging method used to save location data
	 *  for later offline analysis
	 */
	@SuppressWarnings("unused")
	private void log(Location location, int movementPoints) {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("GPS service created");
		
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE); 
		checkIntervalSeconds = sharedPref.getInt("gps_poll_update", 60);
		minDistanceMoved = 20;    
		checkInterval = checkIntervalSeconds*1000;
		thresholdDistance = (int)Math.round(0.4 * checkIntervalSeconds);    
	}
	
	@Override
	public void onDestroy() {
		// save data to key-value store	
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 *  Stops the service once an update has been completed
	 */
	Thread gpsUpdateThread = new Thread(new Runnable() {
		public void run() {
			System.out.println("Killing the service");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// do nothing 
				e.printStackTrace();
			}
			stopSelf();
		}
		
	}, "gpsWorkerThread");
	
	/*
	 *  This is quite a hacky way of getting a single location update.
	 *  We request multiple updates, then stop asking for updates when we get one.
	 *  If elegance is required, this can be updated in the third iteration. 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		System.out.println("GPS service started");
		
		if (gpsWanted()) {
			System.out.println("Looking for a GPS update");
			try {

				locationListener = new LocationListener() {
					
					public void onLocationChanged(Location newLocation) {
						System.out.println("Received location update.");
						locationManager.removeUpdates(this);
						
						currentLocation = newLocation;
						
						System.out.println("Starting thread");
						gpsUpdateThread.start();    	
					}
					
					public void onProviderDisabled(String provider) {
					}
					
					public void onProviderEnabled(String provider) {
					}
					
					public void onStatusChanged(String provider, int status, Bundle extras) {
					}
				};
				
				locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, checkInterval, minDistanceMoved, locationListener);  
				
			} catch (NullPointerException e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			} catch (SecurityException e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			} catch (IllegalArgumentException e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			}
			
		} else {
			System.out.println("GPS is not wanted. Ignoring request for update.");
		}

		return Service.START_STICKY;   // indicates service is explicitly started and stopped as needed
	}
	
	/* 
	 *   Description needs doing (mti20)
	 */
	private int calculateMovementPoints(Location newLocation, Location oldLocation) {
		double A,b,c,d,e;
		
		double v = 0; // TODO speed
		double x = 0; // TODO distance
		
		Context context = getApplicationContext();
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
	

}
















