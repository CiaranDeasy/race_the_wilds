package uk.ac.cam.groupproj.racethewild;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class AccelerometerListerner extends Service implements SensorEventListener{

	private final IBinder binder = new LocalBinder();

	private Sensor accelerometer;
	private  SensorManager sensorManager;
	private  boolean initialized = false;
	float x,y,z;
	private float lastX, lastY,lastZ;
	private boolean used = false;
	private float deltaX, deltaY, deltaZ;
	private final float NOISE = (float) 0.5;

	public class LocalBinder extends Binder {
		AccelerometerListerner getService() {
			return AccelerometerListerner.this; // Return this instance of LocalService so clients can call public methods
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate () {
		startListening();
	}

	@Override
	public void onDestroy () {
		stopListening();
	}

	public boolean isRunning(){
		return initialized;
	}

	public void stopListening(){
		initialized = false;
		sensorManager.unregisterListener(this);
	}

	public void startListening(){
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		initialized = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public float[] getValues(){
		return new float[] {x,y,z};
	}

	public float[] getDeltaValues(){
		if (!used){
			return new float[] {(float) 0.0, (float) 0.0, (float) 0.0};
		} else {
			return new float[] {deltaX, deltaY, deltaZ};
		}

	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//Not needed	
	}

	public void onSensorChanged(SensorEvent event) {

		x = event.values[0];
		y = event.values[1];
		z = event.values[2];

		if(!used) {
			lastX = x;
			lastY = y;
			lastZ = z;
			deltaX = (float) 0.0;
			deltaY = (float) 0.0;
			deltaZ = (float) 0.0;
			used = true;
		} else {
			deltaX = Math.abs(lastX - x);
			deltaY = Math.abs(lastY - y);
			deltaZ = Math.abs(lastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			lastX = x;
			lastY = y;
			lastZ = z;
		}
	}
}
