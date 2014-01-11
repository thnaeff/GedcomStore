/**
 * 
 */
package ch.thn.gedcom.printer;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.store.GedcomStore;
import ch.thn.gedcom.store.GedcomStoreBlock;
import ch.thn.gedcom.store.GedcomStoreLine;
import ch.thn.gedcom.store.GedcomStoreStructure;
import ch.thn.util.tree.TreeNode;


/**
 * This printer only exists since the {@link GedcomStore} and its block/line 
 * structure has not been changed to a {@link TreeNode} construct yet.
 * 
 * @author thomas
 *
 */
public class GedcomStorePrinter {
	
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
	
	

}
