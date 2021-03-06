package uk.ac.cam.groupproj.racethewild;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class GPSservice extends Service { 
	
	private LocationListener locationListener;	private LocationManager locationManager;
	private int checkIntervalSeconds;           Location currentLocation;
	private int thresholdDistance;              // threshold distance to count as an actual move (assuming moves >1mph over interval)
	int currentLocationUpdates;
	
	/*
	 *  Returns true if the user wants to use GPS and false otherwise
	 */
	private boolean gpsWanted() {
		SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		return sharedPref.getBoolean("gps_wanted",true);
	}
	
	/* 
	 *  A debugging method used to save location data
	 *  for later offline analysis
	 */
	private void log(Location location, double distance, int movementPoints, boolean wasSignificant) {
		File log;                 String state = Environment.getExternalStorageState();
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			log = new File(path, "rtw_gps_log.csv");
			System.out.println("Opening file");
		} else {
			System.err.println("Could not write to external storage medium. No log will be created");
			return;
		}
		StringBuilder lineBuilder = new StringBuilder();
		lineBuilder.append(location.getTime());
		lineBuilder.append(",");
		lineBuilder.append(location.getLatitude());
		lineBuilder.append(",");
		lineBuilder.append(location.getLongitude());
		lineBuilder.append(",");
		lineBuilder.append(distance);
		lineBuilder.append(",");
		lineBuilder.append(movementPoints);
		lineBuilder.append(",");
		lineBuilder.append(wasSignificant);
		String line = lineBuilder.toString();
		if (!log.exists()) {
			try {
				log.createNewFile();
				System.out.println("Creating new log file.");
			} catch (IOException e) {
				System.err.println("Could not create new log file");
				e.printStackTrace();
				return;
			}
		}
		try {
			path.mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
			bw.append(line);
			System.out.println("Appending to log file.");
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			System.err.println("Failed to write to log file");
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("GPS service created");
		
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE); 
		checkIntervalSeconds = 300;                                            // gps poll frequency - currently set to 5 minutes
		// moving > 1mph (with safety margin in case of fast refresh rate)
		int minThresholdDistance = (int)Math.round(0.4 * checkIntervalSeconds);
		thresholdDistance = (minThresholdDistance > 135) ? minThresholdDistance : 135;
	}
	
	
	/*
	 *  Used to save out the user's location
	 */
	private void saveOutLocation(Location location) {
		// retrieve the necessary values
		double thisLatitude  = location.getLatitude();
		double thisLongitude = location.getLongitude();
		long thisTime = currentLocation.getTime();
		
		// no way of storing a double and loss-of-precision would be catastrophic, so store as long
		long latitudeToStore  = Double.doubleToLongBits(thisLatitude);
		long longitudeToStore = Double.doubleToLongBits(thisLongitude);
		
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putLong("old_latitude",  latitudeToStore);
		editor.putLong("old_longitude", longitudeToStore);
		editor.putLong("old_time",      thisTime);
		editor.commit();
	}
	
	/*
	 *  Used to save out the user's movement points
	 */
	private void saveOutMovementPoints(int movementPoints) {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("movement_points", movementPoints);
		editor.commit();
	}
	
	/*
	 *  Used to save out the user's distance
	 */
	private void saveOutDistance(int distance) {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("distance", distance);
		editor.commit();
	}
	
	/*
	 *  Read the latitude of the previous location update
	 */
	private double readOldLatitude() {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		long codedLatitude = sharedPref.getLong("old_latitude",0);
		double latitude    = Double.longBitsToDouble(codedLatitude);
		return latitude;
	}
	
	/*
	 *  Read the longitude of the previous location update
	 */
	private double readOldLongitude() {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		long codedLongitude = sharedPref.getLong("old_longitude",0);
		double longitude = Double.longBitsToDouble(codedLongitude);
		return longitude;
	}
	
	/*
	 *  For completeness, read the old time
	 */
	private long readOldTime() {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		return sharedPref.getLong("old_time", -1);
	}
	
	/*
	 *  Saves the location and movement points out to a key-value 
	 *  store, if it meets the necessary criteria to be a valid
	 *  update
	 */
	@Override
	public void onDestroy() {
		System.out.println("In method onDestroy()");
		int additionalMovementPoints = 0; int newMovementPoints = 0; int oldMovementPoints = 0;
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_main_file_key), Context.MODE_PRIVATE);
		boolean wasSignificant = true;    // just used for testing
		

		/*** Has the user moved far enough for this to be a valid update? ***/
		double oldLatitude =  readOldLatitude();
		double oldLongitude = readOldLongitude();
		long oldTime =        readOldTime();
		if (oldTime == -1) {       // first time in app - just save out this location
			saveOutLocation(currentLocation);
			System.out.println("Service finished (no prev data).");
			return;
		}
		float[] moveLength = new float[1];
		Location.distanceBetween(oldLatitude, oldLongitude, currentLocation.getLatitude(), currentLocation.getLongitude(), moveLength);
		float distanceMoved = moveLength[0];
		long deltaT = currentLocation.getTime() - oldTime;       // time between the updates
		deltaT = deltaT / 1000;                                  // convert milliseconds -> seconds
		double averageSpeed = distanceMoved/(deltaT);
		System.out.println("Distance moved: "+distanceMoved);
		System.out.println("Threshold distance: "+thresholdDistance);
		int oldDistance; int newDistance;
		
		if (distanceMoved > thresholdDistance) {
			
			System.out.println("Latest movement is significant.");
			
			/*** If so, calculate movement points ***/
			additionalMovementPoints = calculateMovementPoints(currentLocation,averageSpeed,distanceMoved);
			System.out.println("Just achieved "+additionalMovementPoints+" movement points!");
			
			/*** and save out the new location for use in calculations next time ***/
			saveOutLocation(currentLocation);
			
			/*** and finally save out the new movement points and distance ***/
			oldMovementPoints = sharedPref.getInt("movement_points", 0);
			newMovementPoints = oldMovementPoints+additionalMovementPoints;
			saveOutMovementPoints(newMovementPoints);
			oldDistance       = sharedPref.getInt("distance",0);
			newDistance       = oldDistance+(int)Math.round(distanceMoved);
			saveOutDistance(newDistance);
			
		} else {
			System.out.println("Latest movement is not significant.");
			wasSignificant=false;
		}
		
		/*** and ultra-finally (for debugging), log the new location and new movement points ***/
		log(currentLocation, distanceMoved, newMovementPoints, wasSignificant);
		
		System.out.println("Service finished.");
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
	 *  Get single location update
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		System.out.println("GPS service started");
		currentLocationUpdates = 0;
		
		if (gpsWanted()) {
			System.out.println("Looking for a GPS update");
			try {

				locationListener = new LocationListener() {
					
					public void onLocationChanged(Location newLocation) {
						currentLocationUpdates++;
						System.out.println("Received location update.");
						locationManager.removeUpdates(this);
						
						currentLocation = newLocation;
						
						System.out.println("Starting thread");
						if (currentLocationUpdates<2) {
							gpsUpdateThread.start();    	
						}
					}
					
					public void onProviderDisabled(String provider) {
					}
					
					public void onProviderEnabled(String provider) {
					}
					
					public void onStatusChanged(String provider, int status, Bundle extras) {
					}
				};
				
				final int checkInterval = 1;     // these are just dummy parameters since we definitely do want an update!
				final int minDist       = 0;
				
				locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, checkInterval, minDist, locationListener);  
				
			} catch (NullPointerException e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			} catch (SecurityException e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			} catch (IllegalArgumentException e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			} catch (Exception e) {
				System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
			}
			
		} else {
			System.out.println("GPS is not wanted. Ignoring request for update.");
		}

		return Service.START_STICKY;   // indicates service is explicitly started and stopped as needed
	}
	
	/* 
	 *   Calculates the movement points
	 *   Movement points calculated according to M(x,v) = (Ae^{-bv}+cv+d)*xe
	 *   where A, b, c, d and e are coefficients that have been fit to an
	 *   appropriate model.
	 */
	private int calculateMovementPoints(Location newLocation, double averageSpeed, double distance) {
		double A,b,c,d,e;
		
		double v = averageSpeed;
		double x = distance;
		
		A = 0.04997;
		b = 0.8068;
		c = -0.007349;
		d = 0.6175;
		e = 31.7/30;
		
		double movementPoints = e * (A*Math.exp((-1)*b*v)+c*v+d) * x;
		return (int)Math.round(movementPoints);
	}

}
















