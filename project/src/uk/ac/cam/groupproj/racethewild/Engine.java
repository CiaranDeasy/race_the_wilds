package uk.ac.cam.groupproj.racethewild;

import java.util.Stack;
import java.util.Map;

public abstract class Engine {

	private Stack<Scene> sceneStack;
	private PlayerStats stats;
	private Map<NodeType, Node> nodes; // Maps an enumerated nodeType to the actual node object.
	private Map<Integer, Animal> animalDictionary; // This will only ever be loaded, never saved,
                                            //so we can do weird stuff with the animals
                                            // at launch time, e.g. load sprites into them.

	public PlayerStats getStats() { return stats; }

	//public void update(); // Re-render the screen. Not needed anymore
	
	public abstract SatNavUpdate fetchSatNavData(); // Get data from the sat-nav process.
	public void initialise() { // Initial setup when app is started.
		// Create the scene stack.
		// Push a menu scene.
		// Load the player's stats.
		// Start the sat-nav process, if not running.
		// Load animal data.
		// Load node data.
		// Populate nodes with animals. (separate method)
	}
	
	protected abstract void populateAnimal(Animal animal, boolean isBlack);
				//Add an animal to the Nodes in which it should appear.
				//Always get animals from the animalDictionary! Never serialise any animal data.

}
