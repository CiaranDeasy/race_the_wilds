package uk.ac.cam.groupproj.racethewild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Engine {
	
	public final static String NODEPASS_MESSAGE = "uk.ac.cam.groupproj.racethewild.NODEPASS";
	private PlayerStats stats;
	private Map<NodeType, Node> nodes; // Maps an enumerated nodeType to the actual node object.
	private Map<Integer, Animal> animalDictionary; // This will only ever be loaded, never saved,
                                            //so we can do weird stuff with the animals
                                            // at launch time, e.g. load sprites into them.
	
	

	public PlayerStats getStats() { return stats; }
	
	/** Get data from the sat-nav process. */
	public SatNavUpdate fetchSatNavData() {
		// Temporary implementation generates random update.
		Random random = new Random();
		return new SatNavUpdate(50 + random.nextInt(), random.nextInt());
	}
	
	/** Returns a List of all Animals, sorted by ID.
	 ** Needed by the Collection Scene. */
	public List<Animal> getAllAnimals() {
		Collection<Animal> animalCollection = animalDictionary.values();
		List<Animal> animals = new ArrayList<Animal>();
		for(Animal animal : animalCollection) {
			animals.add(animal);
		}
		Collections.sort(animals);
		return animals;
	}
	
	/** Returns the animal corresponding to the input ID */
	public Animal getAnimal(int ID) {
		return animalDictionary.get(ID);
	}
	
	/** Run at bootup to initialise the app data */
	public void initialise() { // Initial setup when app is started.
		// Load the player's stats.
		// Start the sat-nav process, if not running.
		// Load animal data.
		// Load node data.
		// Populate nodes with animals. (separate method)
		
		// Temporary hard-coded setup.
		animalDictionary = new TreeMap<Integer, Animal>();
		animalDictionary.put(1, new Animal(1, "Giraffe", Colour.Black));
		animalDictionary.put(2, new Animal(2, "Turtle", Colour.Grey));
		animalDictionary.put(3, new Animal(3, "Zebra", Colour.White));
	}
	
	/** Part of initialisation. Adds animals to their correct nodes. */
	protected void populateAnimal(Animal animal, boolean isWhite) {};
				//Add an animal to the Nodes in which it should appear.
				//Always get animals from the animalDictionary! Never serialise any animal data.

	public Engine() {
		initialise();
	}
}
