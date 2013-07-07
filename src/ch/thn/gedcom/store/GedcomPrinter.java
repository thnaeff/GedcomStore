/**
 * 
 */
package ch.thn.gedcom.store;

import java.util.LinkedList;


/**
 * @author thomas
 *
 */
public class GedcomPrinter {
	
	public static final String DELIM = " ";
	public static final String TERMINATOR = "\n";
		
	
	/**
	 * 
	 * 
	 * @param store
	 * @param limitToLevel
	 * @param includeStructures
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStore store, int limitToLevel, boolean includeStructures) {
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
	 * 
	 * 
	 * @param storeBlock
	 * @param limitToLevel
	 * @param includeStructures
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStoreBlock storeBlock, int limitToLevel, boolean includeStructures) {
		StringBuffer sb = new StringBuffer();
		
		if (storeBlock == null) {
			return null;
		}
		
		
		for (GedcomStoreLine storeLine : storeBlock.getStoreLines()) {
			int lengthBefore = sb.length();
			sb.append(preparePrint(storeLine, limitToLevel, includeStructures));
			
			if (storeLine.hasStructureName()) {
				int endOfLine = sb.indexOf("\n", lengthBefore);
				sb.insert(endOfLine, GedcomFormatter.makeRightAlign(60, endOfLine - lengthBefore) + storeLine.getStructureName());
			}
		}
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param storeLine
	 * @param limitToLevel
	 * @param includeStructures
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStoreLine storeLine, int limitToLevel, boolean includeStructures) {
		StringBuffer sb = new StringBuffer();
		
		if (storeLine == null) {
			return null;
		}
		
		//Is it a structure line or a tag line?
		if (includeStructures && storeLine.hasStructureName()) {
			GedcomStoreStructure storeStructure = storeLine.getStoreStructure();
			
			if (storeStructure == null) {
				sb.append(preparePrint(storeLine));
				sb.append(GedcomFormatter.makeRightAlign(60, sb.length()));
				sb.append("Structure not included. Multiple variations available." + TERMINATOR);
			} else {
				//Set the parent of the block temporary
				storeStructure.getStoreBlock().setParentStoreLine(storeLine.getParentBlock().getParentStoreLine());
				sb.append(preparePrint(storeStructure.getStoreBlock(), limitToLevel, includeStructures));
				//Reset the parent block
				storeStructure.getStoreBlock().setParentStoreLine(null);
			}
			
		} else {
			sb.append(preparePrint(storeLine) + TERMINATOR);
			
			if (storeLine.getLevel() < limitToLevel || limitToLevel == 0) {
				//Add children if there are any and if there is no level restriction
				
				if (storeLine.hasChildBlock()) {				
					sb.append(preparePrint(storeLine.getChildBlock(), limitToLevel, includeStructures));
				}
				
			}
		}
		
		
		
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param storeLine
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomStoreLine storeLine) {
		StringBuffer sb = new StringBuffer();
		
		if (storeLine == null) {
			return null;
		}
		
		sb.append(storeLine.getPos() + " " + GedcomFormatter.makeInset(storeLine.getLevel()));
		sb.append(storeLine.getLevel() + DELIM);
		
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
	 * 
	 * 
	 * @param block
	 * @param limitToLevel
	 * @param includeStructures
	 * @param printEmptyLines
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomBlock block, int limitToLevel, boolean includeStructures, boolean printEmptyLines) {
		StringBuffer sb = new StringBuffer();
		
		if (block == null) {
			return null;
		}
		
		LinkedList<GedcomLine> lines = block.getLines();
		
		//For each line
		for (GedcomLine line : lines) {
			sb.append(preparePrint(line, limitToLevel, includeStructures, printEmptyLines));
		}
		
		return sb;
	}
	
	/**
	 * 
	 * 
	 * @param line
	 * @param limitToLevel
	 * @param includeStructures
	 * @param printEmptyLines
	 * @return
	 */
	public static StringBuffer preparePrint(GedcomLine line, int limitToLevel, boolean includeStructures, boolean printEmptyLines) {
		StringBuffer sb = new StringBuffer();
		
		if (line == null) {
			return null;
		}
		
		//Is it a structure line or a tag line?
		if (line instanceof GedcomStructureLine) {
			GedcomStructureLine structureLine = (GedcomStructureLine)line;
			
			if (includeStructures) {
				sb.append(preparePrint(structureLine.getBlock(), limitToLevel, includeStructures, printEmptyLines));			
			} else {
				sb.append(preparePrint(structureLine) + TERMINATOR);
			}
		} else {
			GedcomTagLine tagLine = (GedcomTagLine)line;
			
			if (!printEmptyLines) {
				//Skip empty lines
				if (tagLine.requiresValue() && tagLine.getValue() == null) {
					return sb;
				}
				
				if (tagLine.requiresXRef() && tagLine.getXRef() == null) {
					return sb;
				}
			}
			
			sb.append(preparePrint(line) + TERMINATOR);
			
			
			if (line.getLevel() < limitToLevel || limitToLevel == 0) {
				//Add children if there are any and if there is no level restriction
				
				if (tagLine.hasChildLines()) {
					
					LinkedList<GedcomLine> children = tagLine.getBlock().getLines();
					for (GedcomLine l : children) {
						sb.append(preparePrint(l, limitToLevel, includeStructures, printEmptyLines));
					}
				}
				
			}
			
			
		}

		
		
		return sb;
	}
	
	/**
	 * 
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
		
		if (line instanceof GedcomTagLine) {
			GedcomTagLine tagLine = (GedcomTagLine)line;
			
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
			GedcomStructureLine structureLine = (GedcomStructureLine)line;
			
			sb.append(DELIM + "<<" + structureLine.getStoreLine().getStructureName() + ">>");
		}	
				
		
		return sb;
	}
	
	

}
