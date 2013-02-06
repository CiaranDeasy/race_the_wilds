package uk.ac.cam.groupproj.racethewild;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import android.content.Context;

	public class PlayerStats implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 0L;
		private int currentMovePoints;
		private int totalMovePoints;
		private int totalDistance;
		private List<Integer> whiteAnimals;
		private List<Integer> greyAnimals;
		private String currentNode;
		private static String fileName = "PlayerStats";
		
		public PlayerStats(String currentNode){
			currentMovePoints = 0;
			totalMovePoints = 0;
			totalDistance = 0;
			this.currentNode = currentNode;
		}

		public int getCurrentMovePoints(){return currentMovePoints;}
		public int getTotalMovePoints(){return totalMovePoints;}
		public int getTotalDistance(){return totalDistance;}
		public List<Integer> getFoundAnimals(){return whiteAnimals;}
		public List<Integer> getGreyAnimals(){return greyAnimals;}
		public String getCurrentNode(){return currentNode;}
		
		public void addMovePoints(int movePoints){
			this.currentMovePoints =  this.currentMovePoints + movePoints;
			this.totalMovePoints = this.totalMovePoints + movePoints;
		}
		
		public void resetMovePoints(){
			this.currentMovePoints = 0;
		}
		
		public void addDistance(int distance){
			this.totalDistance = this.totalDistance + distance;
		}
		
		public void setCurrentNode(String node){
			this.currentNode = node;
		}
		
		
		//TODO: Add proper exception handling
		// Loads savedata and gives back a stats object.
		public static PlayerStats load(Context c) throws IOException, ClassNotFoundException{
			FileInputStream fis = c.openFileInput(fileName);
			ObjectInputStream is = new ObjectInputStream(fis);
			PlayerStats stats = (PlayerStats) is.readObject();
			is.close();
			return stats;
		}
		
		
		public void save(Context c) throws IOException{
			FileOutputStream fos;
			fos =  c.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();
		}

}
