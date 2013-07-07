/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @author thomas
 *
 */
public class GedcomStoreBlock {
	
	/**
	 * All the lines of this block which are defined in the lineage-linked grammar.
	 */
	private LinkedList<GedcomStoreLine> storeLines = null;
	
	/**
	 * A sublist of the storeLines which only contains the mandatory lines.
	 */
	private LinkedList<GedcomStoreLine> mandatoryLines = null;
	
	/**
	 * The line ID's (tag or structure names) linked to their lines. If a line 
	 * has multiple tag possibilities, the line appears multiple times, once for 
	 * every tag.
	 */
	private HashMap<String, GedcomStoreLine> idToLineLinks = null;
	
	/**
	 * The structure which contains this block
	 */
	private GedcomStoreStructure storeStructure = null;
	
	/**
	 * The line of which this block is located under
	 */
	private GedcomStoreLine parentStoreLine = null;
	
		
	/**
	 * 
	 * 
	 * @param storeStructrue
	 */
	public GedcomStoreBlock(GedcomStoreStructure storeStructrue) {
		this.storeStructure = storeStructrue;
		
		storeLines = new LinkedList<GedcomStoreLine>();
		mandatoryLines = new LinkedList<GedcomStoreLine>();
		idToLineLinks = new LinkedHashMap<String, GedcomStoreLine>();
		
	}
	
	
	protected void setParentStoreLine(GedcomStoreLine parentStoreLine) {
		this.parentStoreLine = parentStoreLine;
	}
	
	
	protected boolean parse(LinkedList<String> block) {
		/* Example:
		 * n TAG something
		 *   +1 TAG something
		 *   +1 TAG something
		 * n [TAG1 | TAG2] something
		 * n <<STRUCTURE_NAME>>
		 * ...
		 */

		if (block.size() == 0) {
			return true;
		}
		
		String lineIndex = block.get(0).substring(0, block.get(0).indexOf(" "));
		
		//The line index is either "n" or "+NUMBER", where NUMBER is 0-99
		if (lineIndex.length() < 1 || lineIndex.length() > 3) {
			return false;
		}
		
		LinkedList<String> subBlock = new LinkedList<String>();
		GedcomStoreLine lastStoreLine = null;
		
		for (String line : block) {
			
			if (line.startsWith(lineIndex)) {
				//It is a line for this block
				
				//If there are sub block lines, process the sub block first
				if (subBlock.size() > 0) {
					if (!parseSubBlock(subBlock, lastStoreLine)) {
						return false;
					}
				}
				
				GedcomStoreLine storeLine = new GedcomStoreLine(this);
				storeLine.setPos(storeLines.size());
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
	
	
	private boolean parseSubBlock(LinkedList<String> subBlock, GedcomStoreLine lastStoreLine) {
		GedcomStoreBlock storeSubBlock = new GedcomStoreBlock(storeStructure);
		storeSubBlock.setParentStoreLine(lastStoreLine);
		
		if (!storeSubBlock.parse(subBlock)) {
			return false;
		}
		
		subBlock.clear();
		
		lastStoreLine.setChildBlock(storeSubBlock);
		
		return true;
	}
	
	
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
			
			LinkedList<String> allTags = newLine.getTagNames();
			
			for (String tag : allTags) {				
				idToLineLinks.put(tag, newLine);
			}
		}
		
	}
	
	protected GedcomStoreLine getStoreLine(String tagOrStructureName) {
		return idToLineLinks.get(tagOrStructureName);
	}
	
	protected LinkedList<GedcomStoreLine> getStoreLines() {
		return storeLines;
	}
	
	protected GedcomStoreStructure getStoreStructure() {
		return storeStructure;
	}
	
	protected LinkedList<String> getAllLineIDs() {
		return new LinkedList<String>(idToLineLinks.keySet());
	}
	
	protected GedcomStoreLine getParentStoreLine() {
		return parentStoreLine;
	}
	
	protected LinkedList<GedcomStoreLine> getMandatoryLines() {
		return mandatoryLines;
	}
	
	
		
	public int getLevel() {
		if (parentStoreLine == null) {
			return 0;
		}
		
		return parentStoreLine.getLevel() + 1;
	}
	
	public GedcomBlock getBlockInstance(GedcomLine parentLine, String tag, int copyMode) {
		return new GedcomBlock(this, parentLine, tag, copyMode);
	}
	
	public boolean hasMandatoryLines() {
		return (mandatoryLines.size() > 0);
	}
	
	public boolean hasStoreLine(String lineId) {
		return idToLineLinks.containsKey(lineId);
	}
	
	
	@Override
	public String toString() {
		return GedcomPrinter.preparePrint(this, 1, false).toString();
	}

}
