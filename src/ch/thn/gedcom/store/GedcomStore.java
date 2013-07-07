/**
 * 
 */
package ch.thn.gedcom.store;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class GedcomStore {
		
	
	/**
	 * All structures in an ordered list with the parsing order
	 */
	private LinkedList<GedcomStoreStructure> structures = null;
	
	/**
	 * This map contains all the available structure names and links the to the 
	 * structures. If multiple variations of a structure are available, the variation 
	 * can only be determined by the line ID of one of the top-lines of the first 
	 * block (a top-line is a line with the index "n" in the lineage-linked grammar). 
	 * The sub-map of this map holds those line ID's of all the top-lines.
	 * However, the same line ID can occur in multiple variations, thus a list is 
	 * used to link multiple store structures to one line ID if necessary.<br>
	 * <br>
	 * &lt;Structure name &lt;Line ID &lt;List of structures&gt;&gt;&gt;
	 */
	private HashMap<String, HashMap<String, LinkedList<GedcomStoreStructure>>> idToVariationsLinks = null;
	
	/**
	 * This map holds a list for each structure. The list contains all the variations for 
	 * that structure.<br>
	 * <br>
	 * &lt;Structure name &lt;List of structures&gt;&gt;
	 */
	private HashMap<String, LinkedList<GedcomStoreStructure>> variations = null;
	
	
	private boolean showParsingOutput = true;
	private boolean showAccessOutput = true;
	
	/**
	 * 
	 */
	public GedcomStore() {
		
		structures = new LinkedList<GedcomStoreStructure>();
		idToVariationsLinks = new HashMap<String, HashMap<String,LinkedList<GedcomStoreStructure>>>();
		variations = new HashMap<String, LinkedList<GedcomStoreStructure>>();
	}
	
	
	
	public void parse(String grammarFile) {
		
		
		System.out.println("Adding objects from: " + grammarFile);
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(grammarFile));
			
			String line = null;
			LinkedList<String> block = new LinkedList<String>();
			
			while((line = br.readLine()) != null) {
				
				//Remove all leading and trailing extra stuff (spaces, tags, newlines, linefeeds)
				line = GedcomHelper.removeAll(GedcomHelper.leadingTrailingPatternWhole, line);
				//Remove any excessive spaces
				line = GedcomHelper.replaceAll(GedcomHelper.spacesPattern, line, " ");
				//No spaces around or-signs
				line = GedcomHelper.replaceAll(GedcomHelper.orPattern, line, "|");
				//No spaces around open brackets
				line = GedcomHelper.replaceAll(GedcomHelper.bracketOpen, line, "[");
				//No spaces around closing brackets
				line = GedcomHelper.replaceAll(GedcomHelper.bracketClose, line, "]");
				
				//Skip empty lines
				if (line.length() == 0) {
					continue;
				}
				
				errorCheck(line);
				
				if (GedcomHelper.matches(GedcomHelper.structureNamePatternWhole, line)) {
					//A new structure starts
					
					if (block.size() > 0) {
						//Process current block...
						processBlock(block);
						//...and reset the block after processing
						block.clear();
					}
					
					//Add current line to block
					block.add(line);
					
				} else {
					//Only add the current line to the block if the start of a 
					//structure has been found and the bock already contains one 
					//line (the line with the structure name)
					if (block.size() > 0) {
						block.add(line);
					}
				}
				
				
			}
			
			if (block.size() > 0) {
				//And process the last block
				processBlock(block);
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("File " + grammarFile + " not found!");
		} catch (IOException e) {
			System.err.println("Failed to read line");
		}
		
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("Failed to close file reader");
		}
		
		System.out.println("\nAdding objects done (" + structures.size() + " objects parsed)");
		
		
	}
	
	
	/**
	 * Process a block, starting from the block name (linke FAMILY_EVENT_DETAIL etc.) 
	 * to the last line, just before a new block name begins. A block contains the 
	 * block name on the first line, and might contain multiple block variations
	 * 
	 * @param block
	 */
	private void processBlock(LinkedList<String> block) {
		
		//The first line is the structure name
		String structureName = GedcomHelper.getPattern(GedcomHelper.idPattern, block.get(0));
		
		if (showParsingOutput) {
			System.out.println("\n=== " + structureName + " ===");
		}
		
		//The second line defines if the block has variations or not
		if (block.get(1).startsWith("[")) {
			//It has variations
			
			int lastDivider = 2;
			for (int i = 2; i < block.size(); i++) {
				String line = block.get(i);
				
				//Process all sub-blocks one by one
				if (GedcomHelper.matches(GedcomHelper.subBlockDivider, line)) {
					processSubBlock(new LinkedList<String>(block.subList(lastDivider, i)), structureName);
					lastDivider = i + 1;
				}
				
			}
			
		} else {
			//No variations -> process the whole block without the structure-ID
			processSubBlock(new LinkedList<String>(block.subList(1, block.size())), structureName);
		}
		
		
	}
	
	
	/**
	 * Processes a sub-block, which only contains gedcom lines (without 
	 * structure name and without variations)
	 * 
	 * @param subBlock
	 * @param structureName
	 */
	private void processSubBlock(LinkedList<String> subBlock, String structureName) {
		
		GedcomStoreStructure storeStructure = new GedcomStoreStructure(this, structureName);
		if (storeStructure.parse(subBlock, structureName)) {
			//Create a simple list of all the available structures
			structures.add(storeStructure);
			
			
			//Link all the line ID's of the first block to their structure
			
			if (!idToVariationsLinks.containsKey(structureName)) {
				idToVariationsLinks.put(structureName, new HashMap<String, LinkedList<GedcomStoreStructure>>());
			}
			
			LinkedList<String> allIds = storeStructure.getStoreBlock().getAllLineIDs();
			
			for (String id : allIds) {
				if (!idToVariationsLinks.get(structureName).containsKey(id)) {
					idToVariationsLinks.get(structureName).put(id, new LinkedList<GedcomStoreStructure>());
				}
				
				idToVariationsLinks.get(structureName).get(id).add(storeStructure);
			}
			
			
			//Create the list of all the variations
			
			if (!variations.containsKey(structureName)) {
				variations.put(structureName, new LinkedList<GedcomStoreStructure>());
			}
			
			variations.get(structureName).add(storeStructure);
			
			
		}
		
	}
	
	
	/**
	 * Just do some checks on the line to verify that the line is usable
	 * 
	 * @param gedcomLine
	 */
	private boolean errorCheck(String gedcomLine) {
		
		//Ignore comment lines
		if (gedcomLine.startsWith("/*")) {
			return true;
		}
		
		//Ignore structure name lines
		if (gedcomLine.endsWith(":=")) {
			return true;
		}
		
		//Ignore OR-lines
		if (gedcomLine.startsWith("[") || gedcomLine.startsWith("]") || gedcomLine.startsWith("|")) {
			return true;
		}
		
		if (GedcomHelper.matches(GedcomHelper.errorCheckSpacingAfter, gedcomLine)) {
			System.out.println("[WARNING] On line '" + gedcomLine + "'. One or more spaces might be missing to identify parts of the line. " +
					"Make sure there is at least one space after characters like '>', '@', ']'.");
		}
		
		if (GedcomHelper.matches(GedcomHelper.errorCheckSpacingAfter, gedcomLine)) {
			System.out.println("[WARNING] On line '" + gedcomLine + "'. One or more spaces might be missing to identify parts of the line. " +
					"Make sure there is at least one space before characters like '<', '@', '[', '{'.");
		}
		
		if (!GedcomHelper.matches(GedcomHelper.errorCheckIndexFormat, gedcomLine)) {
			System.out.println("[WARNING] On line '" + gedcomLine + "'. The format of the line index is not valid. " +
					"A index can either be 'n' or a '+' followed by a number 0-99.");
		}
		
		if (!GedcomHelper.matches(GedcomHelper.errorCheckMinMax, gedcomLine)) {
			System.out.println("[WARNING] On line '" + gedcomLine + "'. The min/max item is not valid or missing. " +
					"A min/max item looks like the following: {min:max}");
		}
		
		
		return true;
	}
	
	
	/**
	 * Gets an instance of the {@link GedcomBlock} with the given structure name. This 
	 * method only works if the structure does not have multiple variations.
	 * 
	 * @param structureName
	 * @param copyMode
	 * @return
	 */
	public GedcomBlock getGedcomBlock(String structureName, int copyMode) {
		return getGedcomBlock(null, structureName, null, copyMode, false, false, false);
	}
	
	/**
	 * Gets an instance of the {@link GedcomBlock} with the given structure name and 
	 * the variation defined with the given tag. Only works if each variation is 
	 * defined with a different tag.
	 * 
	 * 
	 * @param structureName
	 * @param tag
	 * @param copyMode
	 * @return
	 */
	public GedcomBlock getGedcomBlock(String structureName, String tag, int copyMode) {
		return getGedcomBlock(null, structureName, tag, copyMode, false, false, false);
	}
	
	/**
	 * Gets an instance of the {@link GedcomBlock} with the given structure name and 
	 * the variation defined with the given tag and the xref/value fields. This method 
	 * searches through all available variations and returns the {@link GedcomBlock} which 
	 * matches the given xref/variable requirements.
	 * 
	 * @param structureName
	 * @param tag
	 * @param withXRef
	 * @param withValue
	 * @param copyMode
	 * @return
	 */
	public GedcomBlock getGedcomBlock(String structureName, String tag, boolean withXRef, boolean withValue, int copyMode) {
		return getGedcomBlock(null, structureName, tag, copyMode, true, withXRef, withValue);
	}
	
	
	protected GedcomBlock getGedcomBlock(GedcomLine parentLine, String structureName, String lineId, int copyMode, 
			boolean lookForXRefAndValueVariation, boolean withXRef, boolean withValue) {
		
		if (idToVariationsLinks.containsKey(structureName)) {
			
			if (lineId == null) {
				//The line ID can only be omitted if there is only one variation available
				if (variations.get(structureName).size() == 1) {
					//There is only one variation available -> get the first line ID 
					//of the first variation
					lineId = variations.get(structureName).get(0).getStoreBlock().getAllLineIDs().get(0);
				} else {
					System.out.println("[ERROR] Can not get structure " + structureName + " with only the structure name. " +
							"This structure has multiple variations " + 
							GedcomFormatter.makeOrList(new LinkedList<String>(idToVariationsLinks.get(structureName).keySet()), "", "") + ".");
					return null;
				}
			}
			
			if (!idToVariationsLinks.get(structureName).containsKey(lineId)) {
				System.out.println("[ERROR] Structure " + structureName + " with line ID " + lineId + " does not exist.");
				return null;
			}
			
			int variation = 0;
			
			if (lookForXRefAndValueVariation) {
				variation = lookForXRefAndValueVariation(idToVariationsLinks.get(structureName).get(lineId), structureName, lineId, withXRef, withValue);
				
				if (variation == -1) {
					return null;
				}
			}
			
			return idToVariationsLinks.get(structureName).get(lineId).get(variation).getStoreBlock().getBlockInstance(parentLine, lineId, copyMode);
			
		}
		
		return null;
	}
	
	
	/**
	 * This method loops through the given list of variations and looks for a match 
	 * of the given parameters withXRef and withValue.
	 * 
	 * @param variations
	 * @param lineId
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	private int lookForXRefAndValueVariation(LinkedList<GedcomStoreStructure> variations, String structureName, 
			String lineId, boolean withXRef, boolean withValue) {
		
		for (int i = 0; i < variations.size(); i++) {
			GedcomStoreLine storeLine = variations.get(i).getStoreBlock().getStoreLine(lineId);
			
			if (storeLine.hasTags()) {
				if (storeLine.hasXRefNames() == withXRef && storeLine.hasValueNames() == withValue) {
					return i;
				}
			}
			
		}
		
		String error = "[ERROR] Structure " + structureName + " with line ID " + lineId;
		
		if (withXRef) {
			error = error + " and XRef-field";
		}
		
		if (withValue) {
			error = error + " and value-field";
		}
		
		System.out.println(error + " does not exist.");
		
		return -1;
	}
	
	
	/**
	 * Returns a list of all the available structures
	 * 
	 * @return
	 */
	protected LinkedList<GedcomStoreStructure> getStructures() {
		return structures;
	}
	
	/**
	 * Returns a map which contains all the variations for the structure with the given 
	 * structure name.
	 * 
	 * @param structureName
	 * @return
	 */
	protected LinkedList<GedcomStoreStructure> getVariations(String structureName) {
		return variations.get(structureName);
	}
	
	
	
	
	public int getNumberOfStructureVariations(String structureName) {
		if (!variations.containsKey(structureName)) {
			return 0;
		}
		
		return variations.get(structureName).size();
	}
	
	public boolean structureHasVariations(String structureName) {
		return (getNumberOfStructureVariations(structureName) > 0);
	}
	
	/**
	 * Returns a map with all the tags which are available to access the variations 
	 * of the structure with the given structure name.
	 * 
	 * @param structureName
	 * @return
	 */
	public LinkedList<String> getVariationTags(String structureName) {
		return new LinkedList<String>(idToVariationsLinks.get(structureName).keySet());
	}
	
	public boolean hasStructure(String structureName) {
		return idToVariationsLinks.containsKey(structureName);
	}
	
	
	public boolean showParsingOutput() {
		return showParsingOutput;
	}
	
	public void showParsingOutput(boolean show) {
		showParsingOutput = show;
	}
	
	public boolean showAccessOutput() {
		return showAccessOutput;
	}
	
	public void showAccessOutput(boolean show) {
		showAccessOutput = show;
	}
	
	@Override
	public String toString() {
		return GedcomPrinter.preparePrint(this, 1, false).toString();
	}

}
