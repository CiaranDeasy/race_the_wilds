package uk.ac.cam.groupproj.racethewild;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class SatNavUpdate implements Serializable, Parcelable {

	// Encapsulates the SatNav update in an inter-process-communicable object.

	private static final long serialVersionUID = -4065210670654242390L;
	private int distance;
	private int movePoints;
	
	public int getDistance() { return distance; }
	public int getMovePoints() { return movePoints; }
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public SatNavUpdate(int distance, int movePoints) {
		this.distance = distance;
		this.movePoints = movePoints;
	}

}
