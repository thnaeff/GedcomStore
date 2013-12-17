/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.GedcomToString;
import ch.thn.gedcom.data.GedcomBlock;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.StringUtil;

/**
 * A store block contains all the lines of the same level. Furthermore, it has a 
 * list of mandatory lines and a list which links all the line ID's (tag or structure 
 * names) to their line objects. Each block also links back to its parent line 
 * and to the structure it is located in.<br>
 * <br>
 * The class {@link GedcomStoreStructure} has more information about the hierarchy 
 * of structures, blocks and lines.
 * 
 * @author thomas
 *
 */
public class GedcomStoreBlock {
	
	/**
	 * All the lines of this block which are defined in the lineage-linked grammar 
	 * in their parsing order.
	 */
	private LinkedList<GedcomStoreLine> storeLines = null;
	
	/**
	 * A sublist of the {@link #storeLines} which only contains the mandatory lines.
	 */
	private LinkedList<GedcomStoreLine> mandatoryLines = null;
	
	/**
	 * The line ID's (tag or structure names) linked to their lines. If a line 
	 * has multiple tag possibilities (like [ANUL|CENS|DIV|DIVF]), the line appears 
	 * multiple times, once for every tag.
	 */
	private HashMap<String, GedcomStoreLine> idToLineLinks = null;
	
	/**
	 * The structure which contains this block
	 */
	private GedcomStoreStructure storeStructure = null;
	
	/**
	 * The line which this block is located under.
	 */
	private GedcomStoreLine parentStoreLine = null;
	
		
	/**
	 * Creates a new gedcom store block in the given store structure. 
	 * 
	 * @param storeStructrue
	 * @param parentStoreLine
	 */
	public GedcomStoreBlock(GedcomStoreStructure storeStructrue, GedcomStoreLine parentStoreLine) {
		this.storeStructure = storeStructrue;
		this.parentStoreLine = parentStoreLine;
		
		storeLines = new LinkedList<GedcomStoreLine>();
		mandatoryLines = new LinkedList<GedcomStoreLine>();
		idToLineLinks = new LinkedHashMap<String, GedcomStoreLine>();
		
	}
	
	/**
	 * Parses the given block with all the lines
	 * 
	 * @param block
	 * @return
	 * @throws GedcomParseException
	 */
	protected boolean parse(LinkedList<String> block) throws GedcomParseException {
		/* Example:
		 * n TAG something
		 *   +1 TAG something
		 *   +1 TAG something
		 *   
		 * n [TAG1 | TAG2] something
		 * 
		 * n <<STRUCTURE_NAME>>
		 * 
		 * ...
		 */

		if (block.size() == 0) {
			//Nothing to do -> just continue
			return true;
		}
		
		//Gets the line index which is at the very beginning of a line
		//The line index is either "n" or "+NUMBER", where NUMBER is 0-99
		String lineIndex = StringUtil.getMatchingFirst(GedcomHelper.levelPattern, block.get(0));
		
		if (lineIndex == null) {
			//This error should already be captured by GedcomStore.parsingErrorCheck
			throw new GedcomParseException("On line '" + block.get(0) + "'. The format of the line index is not valid. " +
					"A index can either be 'n' or '+' followed by a number 1-99.");
		}
		
		LinkedList<String> subBlock = new LinkedList<String>();
		GedcomStoreLine lastStoreLine = null;
		
		for (String line : block) {
			
			if (line.startsWith(lineIndex)) {
				//It is a line for this block on the same level
				
				//If there are sub block lines (lines with a higher level), 
				//process them first
				if (subBlock.size() > 0) {
					if (!parseSubBlock(subBlock, lastStoreLine)) {
						return false;
					}
				}
				
				GedcomStoreLine storeLine = new GedcomStoreLine(this);
				storeLine.parse(line);
				addLine(storeLine);
				
				lastStoreLine = storeLine;
			} else {
				//It is a line of the sub block of the last line
				
				subBlock.add(line);
			}
			
		}
		
		//Process the last sub block
		if (subBlock.size() > 0) {
			if (!parseSubBlock(subBlock, lastStoreLine)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Process a sub block
	 * 
	 * @param subBlock
	 * @param lastStoreLine
	 * @return
	 * @throws GedcomParseException
	 */
	private boolean parseSubBlock(LinkedList<String> subBlock, GedcomStoreLine lastStoreLine) throws GedcomParseException {
		GedcomStoreBlock storeSubBlock = new GedcomStoreBlock(storeStructure, lastStoreLine);
		
		if (!storeSubBlock.parse(subBlock)) {
			return false;
		}
		
		subBlock.clear();
		
		//Add the new sub block as a child to its parent line
		lastStoreLine.setChildBlock(storeSubBlock);
		
		return true;
	}
	
	/**
	 * Adds a new store line to this block
	 * 
	 * @param newLine
	 */
	private void addLine(GedcomStoreLine newLine) {
		storeLines.add(newLine);
		
		if (newLine.getMin() > 0) {
			mandatoryLines.add(newLine);
		}
		
		if (newLine.hasStructureName()) {
			//Link the structure name to the new line
			
			String id = newLine.getStructureName();
			
			idToLineLinks.put(id, newLine);
		} else {
			//Link each tag to the new line
			
			HashSet<String> allTags = newLine.getTagNames();
			
			for (String tag : allTags) {				
				idToLineLinks.put(tag, newLine);
			}
		}
		
	}
	
	/**
	 * Returns the line from this block which has the given tag or structure name
	 * 
	 * @param tagOrStructureName
	 * @return
	 */
	public GedcomStoreLine getStoreLine(String tagOrStructureName) {
		return idToLineLinks.get(tagOrStructureName);
	}
	
	/**
	 * Returns a list of all the store lines which are in this store block
	 * 
	 * @return
	 */
	public LinkedList<GedcomStoreLine> getStoreLines() {
		return storeLines;
	}
	
	/**
	 * Returns the structure in which this store block is in
	 * 
	 * @return
	 */
	public GedcomStoreStructure getStoreStructure() {
		return storeStructure;
	}
	
	/**
	 * Returns the line ID's (tag or structure names) of all the lines in this store block
	 * 
	 * @return
	 */
	public LinkedList<String> getAllLineIDs() {
		return new LinkedList<String>(idToLineLinks.keySet());
	}
	
	/**
	 * Returns the store line which is the parent of this block
	 * 
	 * @return
	 */
	public GedcomStoreLine getParentStoreLine() {
		return parentStoreLine;
	}
	
	/**
	 * Returns a list of all the mandatory lines in this block
	 * 
	 * @return
	 */
	public LinkedList<GedcomStoreLine> getMandatoryLines() {
		return mandatoryLines;
	}
	
	
	/**
	 * Returns the level of this block. The level of this block is one higher 
	 * than the parent line of this block.
	 * 
	 * @return
	 */
	public int getLevel() {
		if (parentStoreLine == null) {
			return 0;
		}
		
		return parentStoreLine.getLevel() + 1;
	}
	
	/**
	 * Returns a new {@link GedcomBlock} instance with the configuration which has 
	 * been parsed for this {@link GedcomStoreBlock}
	 * 
	 * @param parentLine
	 * @param tag
	 * @param copyMode
	 * @return
	 */
	public GedcomBlock getBlockInstance(GedcomLine parentLine, String tag, int copyMode) {
		return new GedcomBlock(this, parentLine, tag, copyMode);
	}
	
	/**
	 * Returns <code>true</code> if this block has one or more mandatory lines
	 * 
	 * @return
	 */
	public boolean hasMandatoryLines() {
		return (mandatoryLines.size() > 0);
	}
	
	/**
	 * Returns <code>true</code> if this block has a line with the given line 
	 * ID (tag or structure name).
	 * 
	 * @param lineId
	 * @return
	 */
	public boolean hasStoreLine(String lineId) {
		return idToLineLinks.containsKey(lineId);
	}
	
	
	@Override
	public String toString() {
		return GedcomToString.preparePrint(this, 1, false).toString();
	}

}
