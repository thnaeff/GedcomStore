/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.gedcom.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.thn.gedcom.GedcomHelper;
import ch.thn.gedcom.data.GedcomAccessError;
import ch.thn.gedcom.printer.GedcomStorePrinter;
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
 * @author Thomas Naeff (github.com/thnaeff)
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
	 * The structure which contains this block. The structure is the starting point 
	 * which contains a block with all the structure lines
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
	 * Searches through the gedcom grammar structure and returns the path 
	 * to the child line with the given tag.
	 * For example the INDIVIDUAL_RECORD structure:<br />
	 * <pre>
	 * 0 INDI
	 *   1 SEX 
	 *   ...
	 *   1 CHAN
	 *     2 DATE 
	 *       3 TIME
	 * </pre>
	 * CHAN is a child line of INDI. However, it is defined in the gedcom grammar 
	 * as follows with a structure in between:<br />
	 * <pre>
	 * 0 INDI
	 *   +1 SEX 
	 *   ...
	 *   CHANGE_DATE
	 *   +1 CHAN
	 *     +2 DATE 
	 *       +3 TIME
	 * </pre>
	 * 
	 * This means that if this method is executed on the INDI block for example 
	 * with the parameter tag="CHAN", it returns [CHANGE_DATE, CHAN]. It only works 
	 * with immediate child lines, thus it is not possible to execute it for "DATE" 
	 * on the INDI block since it would be impossible to determine if the CHAN 
	 * DATE is needed or some other DATE tag in another structure.
	 * 
	 * @param tagOrStructureName
	 * @return
	 */
	public LinkedList<String> getPathToStoreLine(String tagOrStructureName) {
		return getPathToStoreLine(tagOrStructureName, null, false, false, false);
	}
	
	/**
	 * Searches through the gedcom grammar structure and returns the path 
	 * to the child line with the given tag.
	 * For example the INDIVIDUAL_RECORD structure:<br />
	 * <pre>
	 * 0 INDI
	 *   1 SEX 
	 *   ...
	 *   1 CHAN
	 *     2 DATE 
	 *       3 TIME
	 * </pre>
	 * CHAN is a child line of INDI. However, it is defined in the gedcom grammar 
	 * as follows with a structure in between:<br />
	 * <pre>
	 * 0 INDI
	 *   +1 SEX 
	 *   ...
	 *   CHANGE_DATE
	 *   +1 CHAN
	 *     +2 DATE 
	 *       +3 TIME
	 * </pre>
	 * 
	 * This means that if this method is executed on the INDI block for example 
	 * with the parameter tag="CHAN", it returns [CHANGE_DATE, CHAN]. It only works 
	 * with immediate child lines, thus it is not possible to execute it for "DATE" 
	 * on the INDI block since it would be impossible to determine if the CHAN 
	 * DATE is needed or some other DATE tag in another structure.
	 * 
	 * @param tagOrStructureName
	 * @param tag
	 * @return
	 */
	public LinkedList<String> getPathToStoreLine(String tagOrStructureName, String tag) {
		return getPathToStoreLine(tagOrStructureName, tag, false, false, false);
	}
	
	/**
	 * Searches through the gedcom grammar structure and returns the path 
	 * to the child line with the given tag.
	 * For example the INDIVIDUAL_RECORD structure:<br />
	 * <pre>
	 * 0 INDI
	 *   1 SEX 
	 *   ...
	 *   1 CHAN
	 *     2 DATE 
	 *       3 TIME
	 * </pre>
	 * CHAN is a child line of INDI. However, it is defined in the gedcom grammar 
	 * as follows with a structure in between:<br />
	 * <pre>
	 * 0 INDI
	 *   +1 SEX 
	 *   ...
	 *   CHANGE_DATE
	 *   +1 CHAN
	 *     +2 DATE 
	 *       +3 TIME
	 * </pre>
	 * 
	 * This means that if this method is executed on the INDI block for example 
	 * with the parameter tag="CHAN", it returns [CHANGE_DATE, CHAN]. It only works 
	 * with immediate child lines, thus it is not possible to execute it for "DATE" 
	 * on the INDI block since it would be impossible to determine if the CHAN 
	 * DATE is needed or some other DATE tag in another structure.
	 * 
	 * @param tagOrStructureName
	 * @param tag
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	public LinkedList<String> getPathToStoreLine(String tagOrStructureName, String tag, 
			boolean withXRef, boolean withValue) {
		return getPathToStoreLine(tagOrStructureName, tag, true, withXRef, withValue);
	}
	
	/**
	 * Searches through the gedcom grammar structure and returns the path 
	 * to the child line with the given tag.
	 * For example the INDIVIDUAL_RECORD structure:<br />
	 * <pre>
	 * 0 INDI
	 *   1 SEX 
	 *   ...
	 *   1 CHAN
	 *     2 DATE 
	 *       3 TIME
	 * </pre>
	 * CHAN is a child line of INDI. However, it is defined in the gedcom grammar 
	 * as follows with a structure in between:<br />
	 * <pre>
	 * 0 INDI
	 *   +1 SEX 
	 *   ...
	 *   CHANGE_DATE
	 *   +1 CHAN
	 *     +2 DATE 
	 *       +3 TIME
	 * </pre>
	 * 
	 * This means that if this method is executed on the INDI block for example 
	 * with the parameter tag="CHAN", it returns [CHANGE_DATE, CHAN]. It only works 
	 * with immediate child lines, thus it is not possible to execute it for "DATE" 
	 * on the INDI block since it would be impossible to determine if the CHAN 
	 * DATE is needed or some other DATE tag in another structure.
	 * 
	 * @param tagOrStructureName
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	public LinkedList<String> getPathToStoreLine(String tagOrStructureName, 
			boolean withXRef, boolean withValue) {
		return getPathToStoreLine(tagOrStructureName, null, true, withXRef, withValue);
	}
	
	/**
	 * Searches through the gedcom grammar structure and returns the path 
	 * to the child line with the given tag.
	 * For example the INDIVIDUAL_RECORD structure:<br />
	 * <pre>
	 * 0 INDI
	 *   1 SEX 
	 *   ...
	 *   1 CHAN
	 *     2 DATE 
	 *       3 TIME
	 * </pre>
	 * CHAN is a child line of INDI. However, it is defined in the gedcom grammar 
	 * as follows with a structure in between:<br />
	 * <pre>
	 * 0 INDI
	 *   +1 SEX 
	 *   ...
	 *   CHANGE_DATE
	 *   +1 CHAN
	 *     +2 DATE 
	 *       +3 TIME
	 * </pre>
	 * 
	 * This means that if this method is executed on the INDI block for example 
	 * with the parameter tag="CHAN", it returns [CHANGE_DATE, CHAN]. It only works 
	 * with immediate child lines, thus it is not possible to execute it for "DATE" 
	 * on the INDI block since it would be impossible to determine if the CHAN 
	 * DATE is needed or some other DATE tag in another structure.
	 * 
	 * 
	 * @param tagOrStructureName
	 * @param tag
	 * @param lookForXRefAndValueVariation
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	private LinkedList<String> getPathToStoreLine(String tagOrStructureName, String tag, 
			boolean lookForXRefAndValueVariation, boolean withXRef, boolean withValue) {
		LinkedList<String> path = new LinkedList<String>();
		
		if (tag == null) {
			tag = tagOrStructureName;
		}
		
		if (hasStoreLine(tagOrStructureName)) {
			String variation = "";
			
			if (storeStructure.getStore().hasStructure(tagOrStructureName)) {
				//It is a structure
				if (storeStructure.getStore().getVariationTags(tagOrStructureName).contains(tag)) {
					//The structure has the given tag variation
					variation = ";" + tag;
					
					if (lookForXRefAndValueVariation) {
						variation = variation + ";" + Boolean.toString(withXRef) + ";" + Boolean.toString(withValue);
					}
				}
			}
			path.add(tagOrStructureName + variation);
			return path;
		} else {		
			for(GedcomStoreLine storeLine : storeLines) {
				//Only check structure lines
				if (!storeLine.hasStructureName()) {
					continue;
				} else if (!storeStructure.getStore().hasStructure(storeLine.getStructureName())) {
					continue;
				}
				
				GedcomStoreStructure structure = null;
				String variation = "";
				
				try {
					if (storeStructure.getStore().structureHasVariations(storeLine.getStructureName())) {
						structure = storeStructure.getStore().getGedcomStructure(storeLine.getStructureName(), tag, lookForXRefAndValueVariation, withXRef, withValue);
						variation = ";" + tagOrStructureName;
						
						if (lookForXRefAndValueVariation) {
							variation = variation + ";" + Boolean.toString(withXRef) + ";" + Boolean.toString(withValue);
						}
					} else {
						structure = storeStructure.getStore().getGedcomStructure(storeLine.getStructureName(), null, false, false, false);
					}
				} catch (GedcomAccessError e) {
					//Structure and/or variation does not exist
					continue;
				}
				
				if (structure != null) {
					if (structure.getStoreBlock().hasStoreLine(tagOrStructureName)) {
						//Found it!
						path.add(storeLine.getStructureName() + variation);
						path.add(tagOrStructureName);
						return path;
					} else {
						LinkedList<String> path2 = structure.getStoreBlock().getPathToStoreLine(tagOrStructureName, tag, lookForXRefAndValueVariation, withXRef, withValue);
						
						if (path2 == null) {
							//Not found in the path
							continue;
						}
						
						path.add(storeLine.getStructureName() + variation);
						path.addAll(path2);
						return path;
					}
				} else {
					//Not found in this path
				}
			}
			
			return null;
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
	 * Returns <code>true</code> if this block has one or more child lines
	 * 
	 * @return
	 */
	public boolean hasChildLines() {
		return (storeLines.size() > 0);
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
		return GedcomStorePrinter.preparePrint(this, 1, false).toString();
	}

}
