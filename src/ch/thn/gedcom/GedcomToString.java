/**
 * 
 */
package ch.thn.gedcom;

import java.util.LinkedList;

import ch.thn.gedcom.data.GedcomBlock;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.gedcom.data.GedcomStructureLine;
import ch.thn.gedcom.data.GedcomTagLine;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.gedcom.store.GedcomStoreBlock;
import ch.thn.gedcom.store.GedcomStoreLine;
import ch.thn.gedcom.store.GedcomStoreStructure;


/**
 * @author thomas
 *
 */
public class GedcomToString {
	
	/**
	 * The delimiter between level numbers and tags, tags and ranges, etc. 
	 */
	public static final String DELIM = " ";
	
	/**
	 * The line terminator (newline)
	 */
	public static final String TERMINATOR = System.lineSeparator();
	
	/** If true, the line index is printed as well **/
	private static boolean showLevelLineIndex = false;
		
	
	/**
	 * If <code>true</code>, the line index is printed as well. The line index 
	 * is the line number in one block (all the lines on the same level).
	 * 
	 * @param show
	 */
	public static void showLevelLineIndex(boolean show) {
		showLevelLineIndex = show;
	}
	
	
	
	/**
	 * Prints the whole store content. The store content consists of all the structures, 
	 * their lines and their linked structures if <code>includeStructures=true</code>.<br>
	 * This printing only prints the parsed structure and does not contain any values. 
	 * The first number on each line is the line number which counts the lines 
	 * on the level of that line.
	 * 
	 * @param store
	 * @param limitToLevel
	 * @param includeStructures
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStore store, 
			int limitToLevel, boolean includeStructures) {
		StringBuffer sb = new StringBuffer();
		
		if (store == null) {
			return null;
		}
		
		for (GedcomStoreStructure structure : store.getStructures()) {
			sb.append("=== " + structure.getStructureName() + " ===" + TERMINATOR);
			sb.append(preparePrint(structure.getStoreBlock(), limitToLevel, includeStructures) + TERMINATOR);
		}
		
		return sb;
	}
	
	/**
	 * Prints the structure from a block saved in the store. It consists of all 
	 * lines and their linked structures if <code>includeStructures=true</code> in 
	 * the given block.<br>
	 * This printing only prints the parsed structure and does not contain any values.
	 * The first number on each line is the line number which counts the lines 
	 * on the level of that line.
	 * 
	 * @param storeBlock
	 * @param limitToLevel
	 * @param includeStructures
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStoreBlock storeBlock, 
			int limitToLevel, boolean includeStructures) {
		return preparePrint(storeBlock, limitToLevel, includeStructures, 0);
	}
	
	/**
	 * Prints the structure from a block saved in the store. It consists of all 
	 * lines and their linked structures if <code>includeStructures=true</code> in 
	 * the given block.<br>
	 * This printing only prints the parsed structure and does not contain any values.
	 * The first number on each line is the line number which counts the lines 
	 * on the level of that line.
	 * 
	 * @param storeBlock
	 * @param limitToLevel
	 * @param includeStructures
	 * @param levelAdjust
	 * @return
	 */
	private static StringBuffer preparePrint(GedcomStoreBlock storeBlock, 
			int limitToLevel, boolean includeStructures, int levelAdjust) {
		StringBuffer sb = new StringBuffer();
		
		if (storeBlock == null) {
			return null;
		}
		
		
		for (GedcomStoreLine storeLine : storeBlock.getStoreLines()) {
			int lengthBefore = sb.length();
			sb.append(preparePrint(storeLine, limitToLevel, includeStructures, levelAdjust));
			
			if (storeLine.hasStructureName()) {
				int endOfLine = sb.indexOf(TERMINATOR, lengthBefore);
				sb.insert(endOfLine, GedcomFormatter.makeRightAlign(60, endOfLine - lengthBefore) + storeLine.getStructureName());
			}
		}
		
		return sb;
	}
	
	/**
	 * Prints the structure from a line saved in the store. It consists of the line, 
	 * and if its a linked structure and <code>includeStructures=true</code> all 
	 * the following lines.<br>
	 * This printing only prints the parsed structure and does not contain any values.
	 * 
	 * @param storeLine
	 * @param limitToLevel
	 * @param includeStructures
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStoreLine storeLine, 
			int limitToLevel, boolean includeStructures) {
		return preparePrint(storeLine, limitToLevel, includeStructures, 0);
	}
	
	/**
	 * Prints the structure from a line saved in the store. It consists of the line, 
	 * and if its a linked structure and <code>includeStructures=true</code> all 
	 * the following lines.<br>
	 * This printing only prints the parsed structure and does not contain any values.
	 * 
	 * @param storeLine
	 * @param limitToLevel
	 * @param includeStructures
	 * @param levelAdjust
	 * @return
	 */
	private static StringBuffer preparePrint(GedcomStoreLine storeLine, 
			int limitToLevel, boolean includeStructures, int levelAdjust) {
		StringBuffer sb = new StringBuffer();
		
		if (storeLine == null) {
			return null;
		}
		
		//Is it a structure line or a tag line?
		if (includeStructures && storeLine.hasStructureName()) {
			//A structure line
			
			GedcomStoreStructure storeStructure = storeLine.getStoreStructure();
			
			if (storeStructure == null) {
				//No store structure or multiple variations available
				sb.append(preparePrint(storeLine, levelAdjust));
				sb.append(GedcomFormatter.makeRightAlign(60, sb.length()));
				sb.append("Structure not included. Multiple variations available." + TERMINATOR);
			} else {
				//Only one variation available -> include the structure
				
				//Print the whole block. Adjust the level of the block because 
				//it has to be inserted at the position of the current store line 
				//which is a structure line
				sb.append(preparePrint(storeStructure.getStoreBlock(), limitToLevel, includeStructures, levelAdjust + storeLine.getLevel()));;
			}
			
		} else {
			//A tag line
			
			sb.append(preparePrint(storeLine, levelAdjust) + TERMINATOR);
			
			if (storeLine.getLevel() < limitToLevel || limitToLevel == 0) {
				//Add children if there are any and if there is no level restriction
				
				if (storeLine.hasChildBlock()) {				
					sb.append(preparePrint(storeLine.getChildBlock(), limitToLevel, includeStructures, levelAdjust));
				}
				
			}
		}
		
		
		
		
		return sb;
	}
	
	/**
	 * Prints the line like it has been parsed and saved in the store.<br>
	 * This printing only prints the parsed structure and does not contain any values.
	 * 
	 * @param storeLine
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStoreLine storeLine) {
		return preparePrint(storeLine, 0);
	}
	
	/**
	 * Prints the line like it has been parsed and saved in the store.<br>
	 * This printing only prints the parsed structure and does not contain any values.
	 *  
	 * @param storeLine
	 * @param levelAdjust
	 * @return
	 */
	private static StringBuffer preparePrint(GedcomStoreLine storeLine, int levelAdjust) {
		StringBuffer sb = new StringBuffer();
		
		if (storeLine == null) {
			return null;
		}
		
		if (showLevelLineIndex) {
			String posString = null;
			
			if (storeLine.getPos() > 9) {
				posString = String.valueOf(storeLine.getPos());
			} else {
				posString = " " + storeLine.getPos();
			}
			
			sb.append(posString + " ");
		}
		
		sb.append(GedcomFormatter.makeInset(storeLine.getLevel() + levelAdjust));
		sb.append((storeLine.getLevel() + levelAdjust) + DELIM);
		
		if (storeLine.hasTagBeforeXRef()) {
			sb.append(GedcomFormatter.makeOrList(storeLine.getTagNames(), "", "") + DELIM);
		}
		
		if (storeLine.hasXRefNames()) {
			sb.append(GedcomFormatter.makeOrList(storeLine.getXRefNames(), "@<", ">@") + DELIM);
		}
		
		if (storeLine.hasTagAfterXRef()) {
			sb.append(GedcomFormatter.makeOrList(storeLine.getTagNames(), "", "") + DELIM);
		}
		
		if (storeLine.hasValueNames()) {
			sb.append(GedcomFormatter.makeOrList(storeLine.getValueNames(), "<", ">") + DELIM);
		}
		
		if (storeLine.hasStructureName()) {
			sb.append("<<" + storeLine.getStructureName() + ">>" + DELIM);
		}
		
		
		//MIN:MAX
		sb.append("{" + storeLine.getMin() + ":");
		
		if (storeLine.getMax() > 0) {
			sb.append(storeLine.getMax() + "}");
		} else {
			sb.append("M}");
		}
		
				
		return sb;
	}
	
	
	
	
	/**
	 * Prints the given block and any child lines, limited to the given level of 
	 * child lines.
	 * 
	 * @param block
	 * @param limitToLevel
	 * @param includeStructures
	 * @param printEmptyLines If set to <code>true</code>, empty lines are printed 
	 * too. An empty line is a line with the value/xref set to <code>null</code> or 
	 * with a length of 0. If a value/xref has never been set for the line it is 
	 * not considered as an empty line. See the flag <code>printLinesWithNoValueSet</code> 
	 * for that case.
	 * @param printLinesWithNoValueSet If set to <code>true</code>, lines for 
	 * which the value/xref has never been set will be printed too.
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomBlock block, int limitToLevel, 
			boolean includeStructures, boolean printEmptyLines, boolean printLinesWithNoValueSet) {
		StringBuffer sb = new StringBuffer();
		
		if (block == null) {
			return null;
		}
		
		LinkedList<GedcomLine> lines = block.getLines();
		
		//For each line
		for (GedcomLine line : lines) {
			sb.append(preparePrint(line, limitToLevel, includeStructures, 
					printEmptyLines, printLinesWithNoValueSet));
		}
		
		return sb;
	}
	
	/**
	 * Prints the given line and any child lines, limited to the given level of 
	 * child lines.
	 * 
	 * @param line
	 * @param limitToLevel
	 * @param includeStructures
	 * @param printEmptyLines
	 * @param printLinesWithNoValueSet
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomLine line, int limitToLevel, 
			boolean includeStructures, boolean printEmptyLines, boolean printLinesWithNoValueSet) {
		StringBuffer sb = new StringBuffer();
		
		if (line == null) {
			return null;
		}
		
		//Is it a structure line or a tag line?
		if (line.isStructureLine()) {
			GedcomStructureLine structureLine = line.getAsStructureLine();
			
			if (includeStructures) {
				sb.append(preparePrint(structureLine.getChildBlock(), limitToLevel, 
						includeStructures, printEmptyLines, printLinesWithNoValueSet));			
			} else {
				sb.append(preparePrint(structureLine) + TERMINATOR);
			}
		} else {
			GedcomTagLine tagLine = line.getAsTagLine();
			
			if (skipLine(tagLine, printEmptyLines, printLinesWithNoValueSet)) {
				//This line has to be skipped because of the given flags
				return sb;
			}
			
			sb.append(preparePrint(line) + TERMINATOR);
			
			
			if (line.getLevel() < limitToLevel || limitToLevel == 0) {
				//Add children if there are any and if there is no level restriction
				
				if (tagLine.hasChildLines()) {
					
					LinkedList<GedcomLine> children = tagLine.getChildBlock().getLines();
					for (GedcomLine l : children) {
						sb.append(preparePrint(l, limitToLevel, includeStructures, 
								printEmptyLines, printLinesWithNoValueSet));
					}
				}
				
			}
			
			
		}

		
		
		return sb;
	}
	
	/**
	 * Prints only the given line
	 * 
	 * @param line
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomLine line) {
		StringBuffer sb = new StringBuffer();
		
		if (line == null) {
			return null;
		}
		
		sb.append(GedcomFormatter.makeInset(line.getLevel()));
		sb.append(line.getLevel());
		
		if (line.isTagLine()) {
			GedcomTagLine tagLine = line.getAsTagLine();
			
			//TAG before XREF
			if (tagLine.getStoreLine().hasTagBeforeXRef()) {
				sb.append(DELIM + tagLine.getTag());
			}
			
			// XREF
			if (tagLine.requiresXRef()) {
				sb.append(DELIM + "@" + tagLine.getXRef() + "@");
			}
			
			//TAG after XREF
			if (tagLine.getStoreLine().hasTagAfterXRef()) {
				sb.append(DELIM + tagLine.getTag());
			}
			
			//VALUE
			if (tagLine.requiresValue()) {
				sb.append(DELIM + tagLine.getValue());
			}
		} else {
			GedcomStructureLine structureLine = line.getAsStructureLine();
			
			sb.append(DELIM + "<<" + structureLine.getStoreLine().getStructureName() + ">>");
		}
				
		
		return sb;
	}
	
	
	/**
	 * Checks if the line has to be skipped. A line has to be skipped if value/xref 
	 * are required but not set/empty (depending on the given flags)
	 * 
	 * @param line
	 * @param printEmptyLines
	 * @param printLinesWithNoValueSet
	 * @return
	 */
	private static boolean skipLine(GedcomLine line, boolean printEmptyLines, 
			boolean printLinesWithNoValueSet) {
		boolean skip = false;
		
		if (line.isTagLine()) {
			GedcomTagLine tagLine = line.getAsTagLine();
			
			if (!printEmptyLines) {
				//Skip empty lines
				if ((!tagLine.requiresValue() && !tagLine.requiresXRef()) 
						|| ((tagLine.isValueSet() || tagLine.isXRefSet()) 
							&& tagLine.isEmpty())) {
					skip = true;
				}
			}
			
			if (!printLinesWithNoValueSet) {
				//Skip lines which have no value and xref set
				if (!tagLine.isValueSet() && !tagLine.isXRefSet()) {
					skip = true;
				}
			}
		} else {
			//Its a structure line
			
		}
		
		//Any child lines which shouldn't be skipped? If yes, then this line 
		//should not be skipped because otherwise the child line with content 
		//will not be printed. If all the child lines can be skipped, this line 
		//does not need to be printed either if skip has already been set to true
		
		if (line.hasChildLines()) {
			LinkedList<GedcomLine> lines = line.getChildBlock().getLines();
			
			for (GedcomLine blockLine : lines) {
				
				if (blockLine.isStructureLine()) {
					for (GedcomLine l : blockLine.getAsStructureLine().getChildBlock().getLines()) {
						if (!skipLine(l, printEmptyLines, printLinesWithNoValueSet)) {
							//A tag line found which should not be skipped
							return false;
						} else {
							//If it is a structure line, just pass the skip state 
							//on to the caller (structure lines are not considered 
							//when checking if lines have to be skipped or not)
							if (line.isStructureLine()) {
								return true;
							}
						}
					}
				} else {
					if (!skipLine(blockLine, printEmptyLines, printLinesWithNoValueSet)) {
						//A tag line found which should not be skipped
						return false;
					} else {
						//If it is a structure line, just pass the skip state 
						//on to the caller (structure lines are not considered 
						//when checking if lines have to be skipped or not)
						if (line.isStructureLine()) {
							return true;
						}
					}
				}
				
			}
		}
		
		return skip;
	}
	

}
