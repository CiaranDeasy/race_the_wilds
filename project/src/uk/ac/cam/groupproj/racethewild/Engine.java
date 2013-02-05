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
	public final static String ANIMAL_NUMBER_MESSAGE = "uk.ac.cam.groupproj.racethewild.ANIMAL_NUMBER";

	private PlayerStats stats;
	private List<Node> nodes; // Maps an enumerated nodeType to the actual node object.
	private Map<Integer, Animal> animalDictionary; // This will only ever be loaded, never saved,
                                            //so we can do weird stuff with the animals
                                            // at launch time, e.g. load sprites into them.
	private Resources resources;
	
	private static Engine engine;
	
	public static Engine get() { return engine; }
	public static void initEngine(Resources resources) {
		engine = new Engine(resources);
		engine.initialise();
	}
	

	public PlayerStats getStats() { return stats; }
	
	public List<Node> getNodeList() {
		return nodes;
	}
	
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
		// TODO: Load node data.
		// Temporary hard-coded implementation.
		nodes = new ArrayList<Node>();
		nodes.add(new Node("Island", "island.jpg", "sample_node.png", 0.3f, 0.3f));
		nodes.add(new Node("Arctic", "arcticsample.jpg", "sample_node.png", 0.6f, 0.6f));
		// Load animal data.
		try {
			animalDictionary = XmlParser.createDictionary(resources.getXml(R.xml.animaldata));
		} catch(DictionaryReadException e) {
			System.err.println(e.getMessage());
			System.err.println("No animal dictionary, so expect badness!");
			animalDictionary = null;
		}
		// Rig a few colours, for the lolz. TODO: Remove.
		this.getAnimal(1).setColour(Colour.White);
		this.getAnimal(2).setColour(Colour.Grey);
		this.getAnimal(3).setColour(Colour.Black);
		this.getAnimal(4).setColour(Colour.Grey);
		this.getAnimal(5).setColour(Colour.Black);
		// Populate nodes with animals. (separate method)
		List<Animal> animals = this.getAllAnimals();
		for (Animal animal : animals) this.populateAnimal(animal);
	}
	
	/** Sets the colour of the animal with the given ID to the given colour. */
	public void changeColour(int animalID, Colour colour) {
		// Update the Animal object.
		Animal animal = this.getAnimal(animalID);
		animal.setColour(colour);
		// If from white, add to nodes.
		if (colour == Colour.Grey) populateAnimal(animal);
		// TODO: Update in PlayerStats.
	}
	
	/** Updates the player's current in-game location. */
	public void setCurrentNode(String name) {  //Thanks for not implementing this Ciaran. We forgot, and had to bugfix for half an hour to fix a nonexistant bug in our code.
		// TODO!
		
	}
	
	/** Takes the name of a node, and returns the object associated with it. */
	public Node lookupNode(String name) throws NodeNotFoundException {
		for (Node node : nodes) {
			if (node.hasName(name)) return node;
		}
		// Fail if the node isn't found.
		throw new NodeNotFoundException();
	}
	
	/** Part of initialisation. Adds animals to their correct nodes. */
	protected void populateAnimal(Animal animal) {
		// Don't populate undiscovered animals.
		if (animal.getColour() == Colour.White) return;
		
		Node[] animalNodes = animal.getNodes();
		for (int i = 0; i < animalNodes.length; i++) {
			animalNodes[i].addAnimal(animal);
		}
	};
				//Add an animal to the Nodes in which it should appear.

	/** Create the engine at app start.
	 *  Takes a Resources reference to access resource files. */
	private Engine(Resources resources) {
		this.resources = resources;
	}
}
