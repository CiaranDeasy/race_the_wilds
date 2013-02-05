package uk.ac.cam.groupproj.racethewild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.res.Resources;

public class Engine {
	
	public final static String NODEPASS_MESSAGE = "uk.ac.cam.groupproj.racethewild.NODEPASS";
	private PlayerStats stats;
	private Map<NodeType, Node> nodes; // Maps an enumerated nodeType to the actual node object.
	private Map<Integer, Animal> animalDictionary; // This will only ever be loaded, never saved,
                                            //so we can do weird stuff with the animals
                                            // at launch time, e.g. load sprites into them.
	private Resources resources;
	
	private static Engine engine;
	
	public static Engine get() { return engine; }
	public static void initEngine(Resources resources) {
		engine = new Engine(resources);
	}
	

	public PlayerStats getStats() { return stats; }
	
	/** Get data from the sat-nav process. */
	public SatNavUpdate fetchSatNavData() {
		// Temporary implementation generates random update.
		Random random = new Random();
		return new SatNavUpdate(50 + random.nextInt(), random.nextInt());
		// TODO: Implement fully.
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
		// TODO: Load the player's stats.
		// TODO: Start the sat-nav process, if not running.
		// Load animal data.
		try {
			animalDictionary = XmlParser.createDictionary(resources.getXml(R.xml.animaldata));
		} catch(DictionaryReadException e) {
			System.err.println(e.getMessage());
			System.err.println("No animal dictionary, so expect badness!");
			animalDictionary = null;
		}
		// TODO: Load node data.
		// TODO: Populate nodes with animals. (separate method)
		
	}
	
	/** Part of initialisation. Adds animals to their correct nodes. */
	protected void populateAnimal(Animal animal, boolean isWhite) {};
				//Add an animal to the Nodes in which it should appear.

	/** Create the engine at app start.
	 *  Takes a Resources reference to access resource files. */
	private Engine(Resources resources) {
		this.resources = resources;
		initialise();
	}
}
