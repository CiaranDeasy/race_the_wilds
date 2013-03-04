package uk.ac.cam.groupproj.racethewild;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author Ciaran
 *
 *  This Service runs the challenge logic. An instance is created when the user requests a new 
 *  distanceTime challenge. The service listens to the device's GPS to track distance traveled.
 *  
 *  IMPORTANT: The service copies ChallengeController's static totalDistance and totalTime
 *  fields when it starts, to use as its challenge information. It also references continuously
 *  to the ChallengeController's static startTime field. The service will start tracking when this
 *  field is non-zero.
 */

public class ChallengeGPS extends Service {
	
	private LocationListener locationListener;	
	private LocationManager locationManager;
	List<Location> pastLocations;
	private long totalTime; // Time allowed for the challenge, in milliseconds.
	private int totalDistance; // Distance to be traveled for the challenge.
	private boolean challengeOver = false;
	private boolean challengePassed = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private int sumDistances() {
		Location l = pastLocations.get(0);
		int summedDistance = 0;
		for (Location m : pastLocations) {
			summedDistance += m.distanceTo(l);
			l = m;
		}
		System.out.println(summedDistance);
		return summedDistance;
	}
	
	/** Writes information about the challenge progress to a publicly accessible location. */
	private void writeOutStatus(int distanceLeft) {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_challenge_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("distance_left", distanceLeft);
		editor.putBoolean("challenge_over", challengeOver);
		editor.putBoolean("challenge_passed", challengePassed);
		editor.commit();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		// Get the challenge information from the ChallengeController.
		this.totalTime = ChallengeController.totalTime;
		this.totalDistance = ChallengeController.totalDistance;
		
		System.out.println("GPSc service started.");
		
		pastLocations = new LinkedList<Location>();
		
		try {
			
			locationListener = new LocationListener() {
				
				public void onLocationChanged(Location newLocation) {
					System.out.println("GPSc received location update.");
					
					// If the challenge hasn't started yet, ignore update.
					if (ChallengeController.startTime == 0) return;
					
					// If the challenge is over, ignore update.
					if (challengeOver) return;
					
					pastLocations.add(newLocation);
					try {
						System.out.println(newLocation.getLatitude()+","+newLocation.getLongitude()+newLocation.distanceTo(pastLocations.get(pastLocations.size()-1)));
					} catch (IndexOutOfBoundsException e) {
						// do nothing
					}
					int distanceMoved = sumDistances();
					long currentTime = System.currentTimeMillis() - ChallengeController.startTime;
					int distanceLeft = totalDistance - distanceMoved;
					long timeLeft = totalTime - currentTime;
					
					// Is it over yet?
					if (distanceLeft <= 0) {
						challengeOver = true;
						challengePassed = true;
					}
					else if (timeLeft <= 0) {
						challengeOver = true;
						challengePassed = false;
					}
					
					// Update the status.
					writeOutStatus(distanceLeft);
				}
				
				public void onProviderDisabled(String provider) {
				}
				
				public void onProviderEnabled(String provider) {
				}
				
				public void onStatusChanged(String provider, int status, Bundle extras) {
				}
			};
			
			locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, locationListener);  
			
		} catch (NullPointerException e) {
			System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
		} catch (SecurityException e) {
			System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
		} catch (IllegalArgumentException e) {
			System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
		} catch (Exception e) {
			System.err.println("GPS error. Is GPS turned on/is your emulator configured correctly?");
		}
		
	
		return Service.START_STICKY;   // indicates service is explicitly started and stopped as needed
	}
	
	/** Stop tracking GPS updates when service ends. */
	@Override
	public void onDestroy() {
		System.out.println("GPSc service destroyed.");
		locationManager.removeUpdates(locationListener);
	}
	
	
}
