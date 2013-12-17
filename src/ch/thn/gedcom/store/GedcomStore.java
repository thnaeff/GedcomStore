/**
 * 
 */
package ch.thn.gedcom.store;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.GedcomToString;
import ch.thn.gedcom.data.GedcomBlock;
import ch.thn.gedcom.data.GedcomCreationError;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.StringUtil;

/**
 * The {@link GedcomStore} has the functionality to parse a lineage-linked grammar 
 * file and to retrieve the parsed structures from it.
 * 
 * @author thomas
 *
 */
public class GedcomStore {
	
	public static final String GEDCOM_FILENAME_EXTENSION = "gedg";
	
	/**
	 * The gedcom grammar file needs these keywords before the first structure. 
	 * Without these keywords parsing will fail. 
	 */
	public static enum FileHeaderKeywords {
		/** The version of the gedcom grammar */
		GRAMPS_VERSION("GRAMPS_VERSION"), 
		/** The source of the gedcom grammar (The website/file/book/...) */
		GRAMPS_SOURCE("GRAMPS_SOURCE"), 
		/**
		 * A description about the gedcom grammar file. List any modifications 
		 * of the grammar structures here and give any additional information.<br>
		 * The description can have multiple lines. Everything after the GRAMPS_DESCRIPTION 
		 * keyword and the next keyword or the first structure will be taken 
		 * as description.
		 */
		GRAMPS_DESCRIPTION("GRAMPS_DESCRIPTION");
		
		protected String value = null;
		
		private FileHeaderKeywords(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	};
	
	/**
	 * All structures in an ordered list in their parsed order
	 */
	private LinkedList<GedcomStoreStructure> structures = null;
	
	/**
	 * This map contains all the available structure names and links them to the 
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
	
	private String loadedFileVersion = null;
	private String loadedFileSource = null;
	private ArrayList<String> loadedFileDescription = null;
	
	private boolean showParsingOutput = true;
	private boolean showAccessOutput = true;
	
	/**
	 * Creates a new empty store object which can be filled with structures by 
	 * parsing a lineage-linked grammar file.
	 */
	public GedcomStore() {
		
		structures = new LinkedList<GedcomStoreStructure>();
		idToVariationsLinks = new HashMap<String, HashMap<String,LinkedList<GedcomStoreStructure>>>();
		variations = new HashMap<String, LinkedList<GedcomStoreStructure>>();
		loadedFileDescription = new ArrayList<String>();
	}
	
	/**
	 * Clears all objects from the store
	 */
	public void clear() {
		structures.clear();
		idToVariationsLinks.clear();
		variations.clear();
	}
	
	
	/**
	 * Parses the given lineage-linked grammar file and adds all the structures 
	 * to this store.
	 * 
	 * @param grammarFile
	 * @throws GedcomParseException
	 */
	public void parse(String grammarFile) throws GedcomParseException {
		
		System.out.println("Adding objects from: " + grammarFile + "\n");
		
		if (!grammarFile.endsWith("." + GEDCOM_FILENAME_EXTENSION)) {
			throw new GedcomParseException("Invalid GEDCOM grammar file. Only *." + 
						GEDCOM_FILENAME_EXTENSION + " Files supported");
		}
		
		BufferedReader br = null;
		int lineCount = 0;
		boolean firstStructureFound = false;
		boolean descriptionFound = false;
		
		try {
			br = new BufferedReader(new FileReader(grammarFile));
			
			String line = null;
			LinkedList<String> block = new LinkedList<String>();
			
			while((line = br.readLine()) != null) {
				lineCount++;
				
				//Remove all leading and trailing extra stuff (spaces, tags, newlines, linefeeds)
				line = StringUtil.removeAll(GedcomHelper.leadingTrailingPatternWhole, line);
				//Remove any excessive spaces
				line = StringUtil.replaceAll(GedcomHelper.spacesPattern, line, " ");
				
				//Skip empty lines
				if (line.length() == 0) {
					continue;
				}
				
				//As long as the first structure has not yet appeared and the 
				//current line is not the start of a structure, process the 
				//file header lines
				if (!firstStructureFound) {
					if (!StringUtil.matches(GedcomHelper.structureNamePattern, line)) {
						if (line.startsWith(FileHeaderKeywords.GRAMPS_VERSION.value + "=")) {
							loadedFileVersion = line.split("=")[1];
						} else if (line.startsWith(FileHeaderKeywords.GRAMPS_SOURCE.value + "=")) {
							loadedFileSource = line.split("=")[1];
						} else if (line.startsWith(FileHeaderKeywords.GRAMPS_DESCRIPTION.value + "=")) {
							String[] s = line.split("=");
							if (s.length > 0 && s[1].length() > 0) {
								loadedFileDescription.add(s[1]);
							}
							descriptionFound = true;
						} else if (descriptionFound) {
							loadedFileDescription.add(line);
						}
						
						continue;
					} else {
						if (loadedFileVersion == null || loadedFileSource == null 
								|| loadedFileDescription.size() == 0) {
							throw new GedcomParseException("Invalid gedcom grammar file format. " +
									"The file needs a header with the following kewords: " + 
									Arrays.toString(FileHeaderKeywords.values()));
						}
						
						System.out.println("Gramps version: " + loadedFileVersion);
						System.out.println("Source of gedcom grammar: " + loadedFileSource);
						
						for (int i = 0; i < loadedFileDescription.size(); i++) {
							System.out.println(loadedFileDescription.get(i));
						}
						
						firstStructureFound = true;
					}
				}
				
				//No spaces around OR-signs
				line = StringUtil.replaceAll(GedcomHelper.orPattern, line, "|");
				//No spaces around open brackets
				line = StringUtil.replaceAll(GedcomHelper.bracketOpen, line, "[");
				//No spaces around closing brackets
				line = StringUtil.replaceAll(GedcomHelper.bracketClose, line, "]");
				
				parsingErrorCheck(line);
				
				if (StringUtil.matches(GedcomHelper.structureNamePattern, line)) {
					//A new structure starts
					
					if (block.size() > 0) {
						//Process current block...
						parseBlock(block);
						//...and reset the block after processing
						block.clear();
					}
					
					//Add current line to block
					block.add(line);
					
				} else {
					//Only add the current line to the block if the start of a 
					//structure has been found and the block already contains one 
					//line (the line with the structure name)
					if (block.size() > 0) {
						block.add(line);
					}
				}
								
			}
			
			if (block.size() > 0) {
				//And process the last block
				parseBlock(block);
			}
			
		} catch (FileNotFoundException e) {
			throw new GedcomParseException("File " + grammarFile + " not found!");
		} catch (IOException e) {
			throw new GedcomParseException("Failed to read line " + lineCount);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw new GedcomParseException("Failed to close file reader for " + grammarFile);
				}
			}
		}
		
		System.out.println("\nAdding objects done (" + structures.size() + " objects parsed)\n");
	}
	
	
	/**
	 * Parses a block, starting from the block name (like FAMILY_EVENT_DETAIL etc.) 
	 * to the last line, just before a new block name begins. A block contains the 
	 * block name on the first line, and might contain multiple block variations
	 * 
	 * @param block
	 * @throws GedcomParseException
	 */
	private void parseBlock(LinkedList<String> block) throws GedcomParseException {
		
		//The first line is the structure name
		String structureName = StringUtil.getMatchingFirst(GedcomHelper.idPattern, block.get(0));
		
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
				if (StringUtil.contains(GedcomHelper.subBlockDivider, line)) {
					parseSubBlock(new LinkedList<String>(block.subList(lastDivider, i)), structureName);
					lastDivider = i + 1;
				}
				
			}
			
		} else {
			//No variations -> process the whole block without the structure-ID
			parseSubBlock(new LinkedList<String>(block.subList(1, block.size())), structureName);
		}
		
		
	}
	
	
	/**
	 * Processes a sub-block, which only contains gedcom lines (without 
	 * structure name and without variations)
	 * 
	 * @param subBlock
	 * @param structureName
	 * @throws GedcomParseException
	 */
	private void parseSubBlock(LinkedList<String> subBlock, String structureName) throws GedcomParseException {
		
		GedcomStoreStructure storeStructure = new GedcomStoreStructure(this, structureName);
		
		//Parse the sub block and build the new structure
		if (storeStructure.parse(subBlock)) {
			//Create a simple list of all the available structures
			structures.add(storeStructure);
			
			
			//Link all the line ID's of the first block to their structure
			
			if (!idToVariationsLinks.containsKey(structureName)) {
				//Add a new structure
				idToVariationsLinks.put(structureName, new HashMap<String, LinkedList<GedcomStoreStructure>>());
			}
			
			LinkedList<String> allIds = storeStructure.getStoreBlock().getAllLineIDs();
			
			for (String id : allIds) {
				if (!idToVariationsLinks.get(structureName).containsKey(id)) {
					//Add all new line ID's
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
	private boolean parsingErrorCheck(String gedcomLine) throws GedcomParseException {
		
		//Ignore comment-only lines
		if (gedcomLine.startsWith("/*")) {
			return true;
		}
		
		//structure name lines
		if (gedcomLine.endsWith(":=")) {
			if (!StringUtil.matches(GedcomHelper.structureNamePattern, gedcomLine)) {
				throw new GedcomParseException("The structure name line '" + gedcomLine + "' is invalid. " +
						"A structure name line can only contain characters like 'A-Z', '_' and has to end with ':='.");
			}
			
			return true;
		}
		
		//Ignore OR-lines
		if (gedcomLine.startsWith("[") || gedcomLine.startsWith("]") || gedcomLine.startsWith("|")) {
			return true;
		}
		
		if (StringUtil.contains(GedcomHelper.errorCheckSpacingAfter, gedcomLine)) {
			throw new GedcomParseException("On line '" + gedcomLine + "'. One or more spaces might be missing to identify parts of the line. " +
					"Make sure there is at least one space after fields like '<...>', '@...@', '[...]' etc.");
		}
		
		if (StringUtil.contains(GedcomHelper.errorCheckSpacingAfter, gedcomLine)) {
			throw new GedcomParseException("On line '" + gedcomLine + "'. One or more spaces might be missing to identify parts of the line. " +
					"Make sure there is at least one space before characters like fields like '<...>', '@...@', '[...]', '{...}' etc.");
		}
		
		if (!StringUtil.contains(GedcomHelper.errorCheckIndexFormat, gedcomLine)) {
			throw new GedcomParseException("On line '" + gedcomLine + "'. The format of the line index is not valid. " +
					"A index can either be 'n' or '+' followed by a number 1-99.");
		}
		
		if (!StringUtil.contains(GedcomHelper.errorCheckMinMax, gedcomLine)) {
			throw new GedcomParseException("On line '" + gedcomLine + "'. The min/max item is not valid or missing. " +
					"A min/max item looks like the following: {min:max}");
		}
		
		
		return true;
	}
	
	
	/**
	 * Gets an instance of the {@link GedcomBlock} with the given structure name. This 
	 * method only works if the structure does not have multiple variations.<br>
	 * If there the structure has multiple variations, use 
	 * {@link #getGedcomBlock(String, String, int)}
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
	 * defined with a different tag.<br>
	 * If there are multiple variations with the same tag, which differ only by 
	 * the presence of the xref/value fields, use 
	 * {@link #getGedcomBlock(String, String, boolean, boolean, int)}
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
	public GedcomBlock getGedcomBlock(String structureName, String tag, boolean withXRef,
			boolean withValue, int copyMode) {
		return getGedcomBlock(null, structureName, tag, copyMode, true, withXRef, withValue);
	}
	
	/**
	 * <i>For internal use!</i><br>
	 * <br>
	 * Gets an instance of a {@link GedcomBlock}. The parent line of the gedcom 
	 * block will be the one given with <code>parentLine</code>.
	 * 
	 * @param parentLine
	 * @param structureName
	 * @param tag
	 * @param copyMode
	 * @param lookForXRefAndValueVariation
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	public GedcomBlock getGedcomBlock(GedcomLine parentLine, String structureName, String tag, 
			int copyMode, boolean lookForXRefAndValueVariation, boolean withXRef, boolean withValue) {
		
		if (idToVariationsLinks.containsKey(structureName)) {
			
			if (tag == null) {
				//The line ID can only be omitted if there is only one variation available
				if (variations.get(structureName).size() == 1) {
					//There is only one variation available -> get the first line ID 
					//of the first variation
					tag = variations.get(structureName).get(0).getStoreBlock().getAllLineIDs().get(0);
				} else {
					throw new GedcomCreationError("Can not get structure " + structureName + 
							" with only the structure name. " +
							"This structure has multiple variations " + 
							GedcomFormatter.makeOrList(new LinkedList<String>(idToVariationsLinks.get(structureName).keySet()), "", "") + ".");
				}
			}
			
			if (!idToVariationsLinks.get(structureName).containsKey(tag)) {
				throw new GedcomCreationError("Structure " + structureName + 
						" with line ID " + tag + " does not exist.");
			}
			
			int variation = 0;
			
			if (lookForXRefAndValueVariation) {
				variation = lookForXRefAndValueVariation(idToVariationsLinks.get(structureName)
						.get(tag), structureName, tag, withXRef, withValue);
				
				if (variation == -1) {
					return null;
				}
			}
			
			return idToVariationsLinks.get(structureName).get(tag).get(variation)
					.getStoreBlock().getBlockInstance(parentLine, tag, copyMode);
			
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
	 * @return The variation index
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
		
		String error = "Structure " + structureName + " with line ID " + lineId;
		
		if (withXRef) {
			error = error + " and XRef-field";
		}
		
		if (withValue) {
			error = error + " and value-field";
		}
		
		throw new GedcomCreationError(error + " does not exist.");
		
	}
	
	
	/**
	 * Returns a list of all the available structures
	 * 
	 * @return
	 */
	public LinkedList<GedcomStoreStructure> getStructures() {
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
	
	
	
	/**
	 * Returns the number of variatinons for the structure with the given name
	 * 
	 * @param structureName
	 * @return
	 */
	public int getNumberOfStructureVariations(String structureName) {
		if (!variations.containsKey(structureName)) {
			return 0;
		}
		
		return variations.get(structureName).size();
	}
	
	/**
	 * Returns <code>true</code> if the structure with the given name has 
	 * one or more variations
	 * 
	 * @param structureName
	 * @return
	 */
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
	
	/**
	 * Checks if a structure with the given name is available
	 * 
	 * @param structureName
	 * @return
	 */
	public boolean hasStructure(String structureName) {
		return idToVariationsLinks.containsKey(structureName);
	}
	
	/**
	 * Returns whether or not the parsing output is showing
	 * 
	 * @return
	 */
	public boolean showParsingOutput() {
		return showParsingOutput;
	}
	
	/**
	 * Turn showing the parsing output on or off
	 * 
	 * @param show
	 */
	public void showParsingOutput(boolean show) {
		showParsingOutput = show;
	}
	
	/**
	 * Returns whether or not the access output is showing or not showing. The 
	 * access output is shown when navigating through {@link GedcomBlock}s and 
	 * {@link GedcomLine}s. This is a general switch which affects all the 
	 * blocks and lines taken from this store.
	 * 
	 * @return
	 */
	public boolean showAccessOutput() {
		return showAccessOutput;
	}
	
	/**
	 * Turn the access output on or off. The 
	 * access output is shown when navigating through {@link GedcomBlock}s and 
	 * {@link GedcomLine}s. This is a general switch which affects all the 
	 * blocks and lines taken from this store.
	 * 
	 * @param show
	 */
	public void showAccessOutput(boolean show) {
		showAccessOutput = show;
	}
	
	
	@Override
	public String toString() {
		return GedcomToString.preparePrint(this, 1, false).toString();
	}

}
