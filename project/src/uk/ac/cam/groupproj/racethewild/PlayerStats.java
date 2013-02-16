package uk.ac.cam.groupproj.racethewild;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

	public class PlayerStats implements Serializable {

		private static final long serialVersionUID = 0L;
		private int currentMovePoints;
		private int totalMovePoints;
		private int totalDistance;
		private List<Integer> blackAnimals;
		private List<Integer> greyAnimals;
		private String currentNode;
		
		// The filename under which player data is saved.
		private static final String fileName = "PlayerStats";
		// The node the player is in when data is originally created.
		private static final String startingNode = "Arctic";
		
		public PlayerStats(){
			currentMovePoints = 500; //changed to allow our demonstration to happen tomorrow.
			totalMovePoints = 0; 
			totalDistance = 0;
			this.currentNode = startingNode;
			this.blackAnimals = new ArrayList<Integer>();
			this.greyAnimals = new ArrayList<Integer>();
		}

		public int getCurrentMovePoints(){return currentMovePoints;}
		public int getTotalMovePoints(){return totalMovePoints;}
		public int getTotalDistance(){return totalDistance;}
		public List<Integer> getBlackAnimals(){return blackAnimals;}
		public List<Integer> getGreyAnimals(){return greyAnimals;}
		public String getCurrentNode(){return currentNode;}
		
		public void addMovePoints(int movePoints){
			this.currentMovePoints =  this.currentMovePoints + movePoints;
			this.totalMovePoints = this.totalMovePoints + movePoints;
		}
		
		/** Attempts to deduct the given number of movement points from the player's current.
		 *  Returns true if this succeeds, or false if the player hasn't enough points. */
		public boolean payMovePoints(int amount) {
			if (this.currentMovePoints < amount) return false;
			currentMovePoints -= amount;
			return true;
		}
		
		public void addDistance(int distance) {
			this.totalDistance = this.totalDistance + distance;
		}
		
		public void setCurrentNode(String node) {
			this.currentNode = node;
		}
		
		/** Adds the given animal ID to the list of grey animals, if not present. */
		public void addGreyAnimal(int id) {
			if (!greyAnimals.contains(id)) this.greyAnimals.add(id);
		}
		
		/** Adds the given animal ID to the list of black animals.
		 *  Also removes from the list of grey animals. */
		public void addBlackAnimal(int id) {
			if (!blackAnimals.contains(id)) this.blackAnimals.add(id);
			// Remove the ID from the grey list.
			greyAnimals.remove((Integer) id);
		}
		
		/** Adds distance and movement points from a SatNavUpdate. */
		public void processSatNav(SatNavUpdate snu) {
			this.currentMovePoints += snu.getMovePoints();
			this.totalDistance += snu.getDistance();
		}
		
		/** Loads savedata and gives back a stats object. 
		 *  Creates new data if old data doesn't exist or can't be read. */
		public static PlayerStats load(Context c) {
			try {
				FileInputStream fis = c.openFileInput(fileName);
				ObjectInputStream is = new ObjectInputStream(fis);
				PlayerStats stats = (PlayerStats) is.readObject();
				is.close();
				System.out.println("Loaded existing save data.");
				System.out.println("Loaded movement points are " + stats.currentMovePoints);
				System.out.println("GREY:");
				for(Integer animal : stats.greyAnimals) System.out.println(animal);
				System.out.println("BLACK:");
				for(Integer animal : stats.blackAnimals) System.out.println(animal);
				return stats;
			} catch(FileNotFoundException e) {
				// If there's nothing to load, start fresh.
				System.out.println("No save file found. Created new save file.");
				return new PlayerStats();
			} catch(ClassNotFoundException e) {
				System.err.println("Cannot read existing save data. New save data created.");
				return new PlayerStats();
			} catch(IOException e) {
				System.err.println("Cannot read existing save data. New save data created.");
				return new PlayerStats();
			}
		}
		
		public void save(Context c) {
			try {
				FileOutputStream fos;
				fos =  c.openFileOutput(fileName, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.writeObject(this);
				os.close();
			} catch(IOException e) {
				System.err.println("Failed to save: IOException.");
				e.printStackTrace();
			}
		}

}
