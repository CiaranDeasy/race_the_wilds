package uk.ac.cam.groupproj.racethewild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class XmlParser {

	// All the tags which are parsed. Defined up-front for easy modification.
	private static final String animalDictionaryTag = "animaldictionary";
	private static final String animalTag = "animal";
	private static final String idTag = "id";
	private static final String nameTag = "name";
	private static final String descriptionTag = "description";
	private static final String hintTag = "hint";
	private static final String photoTag = "photo";
	private static final String spriteTag = "sprite";
	private static final String distanceTag = "distance";
	private static final String hitpointTag = "hitpoints";
	private static final String speedTag = "speed";
	private static final String foundInTag = "node";
	
	private static final String nodeListTag = "nodelist";
	private static final String nodeTag = "node";
	private static final String nodeNameTag = "name";
	private static final String backgroundTag = "background";
	private static final String previewTag = "preview";
	private static final String nodeSpriteTag = "sprite";
	private static final String relativeXTag = "relX";
	private static final String relativeYTag = "relY";
	
	private static final String challengeListTag = "challengelist";
	private static final String challengeTag = "challenge";
	private static final String challengeIDTag = "id";
	private static final String challengeTypeTag = "type";
	private static final String repetitionsTag = "repetitions";
	private static final String timeTag = "time";
	private static final String textTag = "text";
	private static final String challengeAnimalIDTag = "animal";
	
	private static final String challengeTypeDistanceTime = "distancetime";
	private static final String challengeTypeAccelerometer = "accelerometer";
	
	/** Reads the XML file to produce a dictionary of animal data. */
	public static Map<Integer, Animal> createDictionary(XmlPullParser parser) 
			throws XmlReadException {
		try {
			Map<Integer, Animal> dictionary = new TreeMap<Integer, Animal>();
			// Skip the preamble.
			parser.next();
			parser.next();
			// Check the <animaldictionary> tag.
			parseOpeningTag(parser, animalDictionaryTag);
			
			// Loop and read animals.
			while(parser.getName().equals(animalTag)) {
				// Read the opening <animal> tag.
				parseOpeningTag(parser, animalTag);
				// Read the animal data.
				Animal nextAnimal = readNewAnimal(parser);
				dictionary.put(nextAnimal.getID(), nextAnimal);
				// Read the closing </animal> tag.
				parseClosingTag(parser, animalTag);
			}
			// Read the closing </animaldictionary> tag.
			parseClosingTag(parser, animalDictionaryTag);
			return dictionary;
			
		} catch(XmlPullParserException e) {
			e.printStackTrace();
			throw new XmlReadException(
					"Experienced an XmlPullParserException, trace was printed.");
		} catch(IOException e) {
			throw new XmlReadException("IOException when reading animals.");
		}
	}
	
	/** Reads an XML file to produce a list of node data objects. */
	public static List<Node> createNodes(XmlPullParser parser) 
			throws XmlReadException {
		List<Node> nodes = new ArrayList<Node>();
		try {
			// Skip the preamble.
			parser.next();
			parser.next();
			// Check the <nodelist> tag.
			parseOpeningTag(parser, nodeListTag);
			
			// Loop and read nodes.
			while(parser.getName().equals(nodeTag)) {
				// Read the opening <node> tag.
				parseOpeningTag(parser, nodeTag);
				// Read the node data.
				nodes.add(readNewNode(parser));
				// Read the closing </node> tag.
				parseClosingTag(parser, nodeTag);
			}
			// Read the closing </nodelist> tag.
			parseClosingTag(parser, nodeListTag);
			return nodes;
			
		} catch(XmlPullParserException e) {
			e.printStackTrace();
			throw new XmlReadException(
					"Experienced an XmlPullParserException, trace was printed.");
		} catch(IOException e) {
			throw new XmlReadException("IOException when reading animals.");
		}
	}
	
	/** Reads an XML file to produce a list of node data objects. */
	public static List<Challenge> createChallenges(XmlPullParser parser) 
			throws XmlReadException {
		List<Challenge> challenges = new ArrayList<Challenge>();
		try {
			// Skip the preamble.
			parser.next();
			parser.next();
			// Check the <challengelist> tag.
			parseOpeningTag(parser, challengeListTag);
			
			// Loop and read nodes.
			while(parser.getName().equals(challengeTag)) {
				// Read the opening <challenge> tag.
				parseOpeningTag(parser, challengeTag);
				// Read the challenge data.
				challenges.add(readNewChallenge(parser));
				// Read the closing </challenge> tag.
				parseClosingTag(parser, challengeTag);
			}
			// Read the closing </challengelist> tag.
			parseClosingTag(parser, challengeListTag);
			return challenges;
			
		} catch(XmlPullParserException e) {
			e.printStackTrace();
			throw new XmlReadException(
					"Experienced an XmlPullParserException, trace was printed.");
		} catch(IOException e) {
			throw new XmlReadException("IOException when reading challenges.");
		}
	}
	
	/** Parse XML animal data, given a parser pointing at the opening tag of the first data item. 
	 * */
	private static Animal readNewAnimal(XmlPullParser parser) 
			throws IOException, XmlPullParserException, XmlReadException {
		
		// Parse each data item in turn.
		int id = parseIntegerTag(parser, idTag);
		String name = parseTag(parser, nameTag);
		String description = parseTag(parser, descriptionTag);
		String hint = parseTag(parser, hintTag);
		String photo = parseTag(parser, photoTag);
		String sprite = parseTag(parser, spriteTag);
		int distancePerDay = parseIntegerTag(parser, distanceTag);
		int hitpoints = parseIntegerTag(parser, hitpointTag);
		int speed = parseIntegerTag(parser, speedTag);
		List<String> nodes = parseSequence(parser, foundInTag);
		
		// And return a new Animal object containing the extracted data.
		return new Animal(id, name, description, hint, photo, sprite, distancePerDay, hitpoints, 
				speed, nodes);
	}
	
	/** Parse XML node data, given a parser pointing at the opening tag of the first data item. 
	 * */
	private static Node readNewNode(XmlPullParser parser) 
			throws IOException, XmlPullParserException, XmlReadException {
		
		// Parse each data item in turn.
		String name = parseTag(parser, nodeNameTag);
		String background = parseTag(parser, backgroundTag);
		String preview = parseTag(parser, previewTag);
		String sprite = parseTag(parser, nodeSpriteTag);
		float relativeX = parseFloatTag(parser, relativeXTag);
		float relativeY = parseFloatTag(parser, relativeYTag);
		
		// And return a new Node object containing the extracted data.
		return new Node(name, background, preview, sprite, relativeX, relativeY);
	}
	
	/** Parse XML challenge data, given a parser pointing at the opening tag of the first data 
	 * item. */
	private static Challenge readNewChallenge(XmlPullParser parser) 
			throws IOException, XmlPullParserException, XmlReadException {
		
		// Parse each data item in turn.
		int id = parseIntegerTag(parser, challengeIDTag);
		String rawType = parseTag(parser, challengeTypeTag);
		
		// Handle the challenge type
		ChallengeType type = null;
		if(rawType.equals(challengeTypeDistanceTime)) {
			type = ChallengeType.distanceTime;
		}
		else if(rawType.equals(challengeTypeAccelerometer)) {
			type = ChallengeType.accelerometer;
		}
		else {
			throw new XmlReadException("Invalid challenge type for challenge #" + id + 
					", exterminate!");
		}
		// Resume regular parsing.
		int repetitions = parseIntegerTag(parser, repetitionsTag);
		int time = parseIntegerTag(parser, timeTag);
		String text = parseTag(parser, textTag);
		int animalID = parseIntegerTag(parser, challengeAnimalIDTag);
		
		// And return a new Challenge object containing the extracted data.
		return new Challenge(id, repetitions, time, text, animalID, type);
	}
	
	/** Parses a variable-length (possibly empty) sequence of the same tag. */
	private static List<String> parseSequence(XmlPullParser parser, String tag) 
			throws XmlPullParserException, IOException, XmlReadException {
		List<String> list = new ArrayList<String>();
		// Loop as long as there's another matching tag.
		while(parser.getName().equals(tag)) {
			list.add(parseTag(parser, tag));
		}
		// Return the list of extracted strings.
		return list;
	}
	
	/** Parse a single data item with the given tag, assuming the parser points to the opening tag
	 *  of that data item. */
	private static String parseTag(XmlPullParser parser, String tag) 
			throws IOException, XmlPullParserException, XmlReadException {
		
		// Check that the opening tag is correct.
		parseOpeningTag(parser, tag);
		// Get the actual data.
		String text = parser.getText();
		parser.next();
		// Check that the closing tag is correct.
		parseClosingTag(parser, tag);
		// And return the extracted data.
		return text;
	}
	
	/** Parse a single opening tag with the given name and type. */
	private static void parseOpeningTag(XmlPullParser parser, String tag) 
			throws XmlPullParserException, XmlReadException, IOException {
		// Check that it's an opening tag.
		if(parser.getEventType() != XmlPullParser.START_TAG) 
			throw new XmlReadException("Found closing tag </" + parser.getName() + 
					"> instead of opening tag <" + tag + "> on line " + parser.getLineNumber() 
					+ ".");
		// Check that it's the right tag.
		if(!parser.getName().equals(tag)) 
			throw new XmlReadException("Found tag <" + parser.getName() + "> instead of tag <" + 
					tag + "> on line " + parser.getLineNumber() + ".");
		// Accept it.
		parser.next();
		return;
	}
	
	/** Parse a single closing tag with the given name and type. */
	private static void parseClosingTag(XmlPullParser parser, String tag) 
			throws XmlPullParserException, XmlReadException, IOException {
		// Check that it's a closing tag.
		if(parser.getEventType() != XmlPullParser.END_TAG) 
			throw new XmlReadException("Found opening tag <" + parser.getName() + 
					"> instead of closing tag </" + tag + "> on line " + parser.getLineNumber() 
					+ ".");
		// Check that it's the right tag.
		if(!parser.getName().equals(tag)) 
			throw new XmlReadException("Found tag </" + parser.getName() + "> instead of tag </" + 
					tag + "> on line " + parser.getLineNumber() + ".");
		// Accept it.
		parser.next();
		return;
	}
	
	/** Parses a boolean tag and throws an exception if the contents aren't valid. */
	public static boolean parseBooleanTag(XmlPullParser parser, String tag) 
			throws XmlPullParserException, XmlReadException, IOException {
		String raw = parseTag(parser, tag);
		if (raw.equals("true")) return true;
		else if (raw.equals("false")) return false;
		else throw new XmlReadException("Tag <" + tag + "> didn't contain a valid boolean.");
	}
	
	/** Parses an integer tag and throws an exception if the contents aren't valid. */
	public static int parseIntegerTag(XmlPullParser parser, String tag) 
			throws XmlPullParserException, XmlReadException, IOException {
		String raw = parseTag(parser, tag);
		try {
			return Integer.parseInt(raw);
		} catch(NumberFormatException e) {
			throw new XmlReadException("Tag <" + tag + "> didn't contain a valid integer.");
		}
	}
	
	/** Parses a float tag and throws an exception if the contents aren't valid. */
	public static float parseFloatTag(XmlPullParser parser, String tag) 
			throws XmlPullParserException, XmlReadException, IOException {
		String raw = parseTag(parser, tag);
		try {
			return Float.parseFloat(raw);
		} catch(NumberFormatException e) {
			throw new XmlReadException("Tag <" + tag + "> didn't contain a valid float.");
		}
	}
	
}
