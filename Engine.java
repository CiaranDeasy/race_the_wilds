package uk.ac.cam.groupproj.racethewilds;

import java.util.Stack;
import java.util.Map;

public abstract class Engine {
	
	private Stack<Scene> sceneStack;
	private PlayerStats stats;
	private Map<NodeType, Node> nodes; // Maps an enumerated nodeType to the actual node object.
	// animalDictionary: how will this be represented?
	
	public PlayerStats getStats() { return stats; }
	
	public void update(); // Re-render the screen.
	public SatNavUpdate fetchSatNavData(); // Get data from the sat-nav process.
	public void initialise() { // Initial setup when app is started.
		// Create the scene stack.
		// Push a menu scene.
		// Load the player's stats.
		// Start the sat-nav process, if not running.
		// Load animal data.
		// Load node data.
		// Populate nodes with animals. (separate method)
	}
	public void pushScene(); // Adds a new scene to the stack and displays it.
	public void popScene(); // Removes the current scene from the stack to display the scene below
							// it. Cleanup the removed scene!
	private void populateAnimal(Animal animal, boolean isBlack);
				//Add an animal to the Nodes in which it should appear.
	
}