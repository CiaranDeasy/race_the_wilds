package uk.ac.cam.groupproj.racethewilds;

import java.util.List;

public class PlayerStats implements Serialisable {

	int currentMovePoints;
	int totalMovePoints;
	int totalDistance;
	List<Integer> foundAnimals;
	List<Integer> greyAnimals;
	NodeType currentNode;
	
	public static PlayerStats load(); // Loads savedata and gives back a stats object.
	public void save(); // Save stats to a file.

}