package uk.ac.cam.groupproj.racethewild;

import uk.ac.cam.groupproj.racethewild.AccelerometerListerner.LocalBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ChallengeJump {

	private Context context;
	private AccelerometerListerner accel;
	private boolean bound = false;
	private int totalJumps;
	private Thread updateThread;

	private ServiceConnection connection = new ServiceConnection() {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Because we have bound to an explicit
			// service that is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			LocalBinder binder = (LocalBinder) service;
			accel = binder.getService();
			bound = true;
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			bound = false;
		}
	};

	public ChallengeJump(Context context) {
		this.context =context;
	}

	public void startChallengeJump() {
		Intent intent = new Intent(context,AccelerometerListerner.class);
		context.bindService(intent,connection,Context.BIND_AUTO_CREATE );
		updateThread = new Thread(new updateJumps());
		totalJumps =0;
		updateThread.start();
	}

	public void haltChallengeJump() {
		if (bound) {
			bound = false;
			context.unbindService(connection);
		}
	}

	public int getJumps() {
			return totalJumps;
	}

	// Algorithm for determining jumps needs to be redesigned.
	private class updateJumps implements Runnable {

		public void run() {
			int i = 0;
			int j = 0;
			while(bound){
				float[] delta = accel.getDeltaValues();
				float sumdelta = delta[0]+delta[1]+delta[2];
				if (sumdelta >=2) {
					i++;
				} else {
					j++;
				}
				if (j>5) i=0;
				if(i>=100){
					totalJumps++;
					i=0;
					j=0;
				}
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
