package uk.ac.cam.groupproj.racethewild;

import java.io.Serializable;
import java.util.List;

public class PlayerStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6404719382973480586L;
	int currentMovePoints;
	int totalMovePoints;
	int totalDistance;
	List<Integer> foundAnimals;
	List<Integer> greyAnimals;
	String currentNode;
	
	public int getCurrentMovePoints(){return currentMovePoints;}
	public int getTotalMovePoints(){return totalMovePoints;}
	public int getTotalDistance(){return totalDistance;}

	public String getCurrentNode(){return currentNode;};
	
	
	//public static PlayerStats load(); // Loads savedata and gives back a stats object.
	//public void save(); // Save stats to a file.

}
