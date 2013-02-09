package uk.ac.cam.groupproj.racethewild;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public static class SatNavProcess implements Serializable, Parcelable {

	/*
	 *  TODO
	 *  Public interface method - used for IPC
	 */
	public SatNavUpdate getMovementPoints() {
		// TODO
		return new SatNavUpdate(0,0);
	}
	
	/* 
	 *  TODO
	 *  Called by main method whenever movement points are retrieved
	 */
	private void saveData(int movementPoints) {
		// TODO
	}
	
	/*
	 *  TODO
	 *  Used by the public interface data to get the latest set of
	 *  movement points
	 */
	private int getLatestData() {
		// TODO
		return 0;
	}
	
	/*
	 *  Main method that constantly runs within the process
	 *  Interacts with the android GPS methods, calculates movement points
	 *  and saves them using the saveData method
	 */
	private static void main(String args[]) {
		// TODO
	}
	
}
