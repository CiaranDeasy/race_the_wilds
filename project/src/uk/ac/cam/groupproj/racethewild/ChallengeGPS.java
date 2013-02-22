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

public class ChallengeGPS extends Service {
	
	private LocationListener locationListener;	private LocationManager locationManager;
	List<Location> pastLocations;

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
		return summedDistance;
	}
	
	private void writeOutDistance(int totalDistance) {
		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.gps_challenge_file_key), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("distance_moved", totalDistance);
		editor.commit();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		System.out.println("GPSc service started");
		
		pastLocations = new LinkedList<Location>();
		
		
		try {
			
			locationListener = new LocationListener() {
				
				public void onLocationChanged(Location newLocation) {
					pastLocations.add(newLocation);
					int td = sumDistances();
					writeOutDistance(td);
				}
				
				public void onProviderDisabled(String provider) {
				}
				
				public void onProviderEnabled(String provider) {
				}
				
				public void onStatusChanged(String provider, int status, Bundle extras) {
				}
			};
			
			locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20, 50, locationListener);  
			
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
	
	@Override
	public void onDestroy() {
		locationManager.removeUpdates(locationListener);
	}
	
	
}
