/**
 * 
 */
package ch.thn.gedcom.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.store.GedcomStoreBlock;
import ch.thn.gedcom.store.GedcomStoreLine;
import ch.thn.util.tree.TreeNode;
import ch.thn.util.tree.TreeNodeException;

/**
 * @author thomas
 *
 */
public class GedcomNode extends TreeNode<String, GedcomLine> {
	
	/** The delimiter for multiple step values used in {@link #followPath(String...)} **/
	public static final String PATH_OPTION_DELIMITER = ";";
	
	/** Create all the available lines automatically */
	public static final int ADD_ALL = 0;
	/** Only create mandatory lines automatically */
	public static final int ADD_MANDATORY = 1;
	/** Do not create any lines automatically */
	public static final int ADD_NONE = 2;
		
	private GedcomStoreBlock storeBlock = null;
	
	private HashMap<String, Integer> lineCount = null;
	
	
	/**
	 * 
	 * 
	 * @param storeBlock
	 * @param tagOrStructureName
	 * @param tag
	 * @param lookForXRefAndValueVariation
	 * @param withXRef
	 * @param withValue
	 */
	protected GedcomNode(GedcomStoreBlock storeBlock, String tagOrStructureName, 
			String tag, boolean lookForXRefAndValueVariation, boolean withXRef, boolean withValue) {
		super(tagOrStructureName + "-" + tag + "-" + withXRef + "-" + withValue, null);
		
		lineCount = new LinkedHashMap<String, Integer>();
		
		//Line with that tag or structure name does not exist
		if (!storeBlock.hasStoreLine(tagOrStructureName)) {
			String s = "";
			
			if (storeBlock.getParentStoreLine() == null) {
				s = "Structure " + storeBlock.getStoreStructure().getStructureName();
			} else {
				s = "Store block " + storeBlock.getParentStoreLine().getId();
			}
			
			throw new GedcomCreationError(s + " does not have a tag " + 
					tagOrStructureName + ". Available tags: " + 
					GedcomFormatter.makeOrList(storeBlock.getAllLineIDs(), null, null));
		}
		
		GedcomStoreLine storeLine = storeBlock.getStoreLine(tagOrStructureName);
		
		if (storeLine.hasStructureName()) {
			//It is a structure line, thus it does not have a child block but it 
			//is only a "link" to the structure 
			this.storeBlock = storeBlock.getStoreStructure().getStore()
					.getGedcomStructure(tagOrStructureName, tag, 
							lookForXRefAndValueVariation, withXRef, withValue).getStoreBlock();
		} else {
			this.storeBlock = storeLine.getChildBlock();
		}
	}
	
	/**
	 * 
	 * 
	 * @param storeBlock
	 */
	protected GedcomNode(GedcomStoreBlock storeBlock) {
		super(storeBlock.getStoreStructure().getStructureName(), null);
		
		this.storeBlock = storeBlock;
		
		lineCount = new LinkedHashMap<String, Integer>();
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomNode newLine() {
		if (isHeadNode()) {
			return null;
		}
		
		GedcomNode newNode = null;
		
		GedcomStoreLine storeLine = getNodeValue().getStoreLine();
		
		if (storeLine.hasStructureName()) {
			GedcomStructureLine structureLine = (GedcomStructureLine)getNodeValue();
			
			if (storeLine.hasMultipleTagNames()) {
				newNode = ((GedcomNode)getParentNode()).addChildLine(storeLine.getStructureName(), 
						structureLine.getTag(), storeLine.hasXRefNames(), storeLine.hasValueNames());
			} else {
				newNode = ((GedcomNode)getParentNode()).addChildLine(storeLine.getStructureName());
			}
		} else {
			newNode = ((GedcomNode)getParentNode()).addChildLine(getNodeValue().getTag());
		}
		
		return newNode;
	}
	
	/**
	 * 
	 * 
	 * @param tagOrStructureName
	 * @return
	 */
	public GedcomNode addChildLine(String tagOrStructureName) {
		return addChildLine(tagOrStructureName, null, false, false, false);
	}
	
	/**
	 * 
	 * 
	 * @param tagOrStructureName
	 * @param tag
	 * @return
	 */
	public GedcomNode addChildLine(String tagOrStructureName, String tag) {
		return addChildLine(tagOrStructureName, tag, false, false, false);
	}
	
	/**
	 * 
	 * 
	 * @param tagOrStructureName
	 * @param tag
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	public GedcomNode addChildLine(String tagOrStructureName, String tag, 
			boolean withXRef, boolean withValue) {
		return addChildLine(tagOrStructureName, tag, true, false, false);
	}
	
	/**
	 * 
	 * 
	 * @param tagOrStructureName
	 * @param tag
	 * @param lookForXRefAndValueVariation
	 * @param withXRef
	 * @param withValue
	 * @return
	 */
	private GedcomNode addChildLine(String tagOrStructureName, String tag, 
			boolean lookForXRefAndValueVariation, boolean withXRef, boolean withValue) {
		
		GedcomStoreLine storeLine = storeBlock.getStoreLine(tagOrStructureName);
		
		if (!lineCount.containsKey(tagOrStructureName)) {
			lineCount.put(tagOrStructureName, 0);
		} else {
			if (storeLine.getMax() != 0 && storeLine.getMax() <= lineCount.get(tagOrStructureName)) {
				return null;
			}
		}
		
		GedcomNode newNode = new GedcomNode(storeBlock, tagOrStructureName, tag, 
				lookForXRefAndValueVariation, withXRef, withValue);
		
		int newStoreLinePos = 0;
		
		//Get the node position only if it is not a header. The header has position 
		//0 anyways
		if (!isHeadNode()) {
			newStoreLinePos = storeLine.getPos();
		}
		
		//Set structure nodes as invisible
		if (storeLine.hasStructureName()) {
			newNode.setInvisibleNode(true);
		}
		
		//Look for the position where to add the new line. The position is defined 
		//through the parsed lineage linked grammar with the line order
		for (int i = 0; i < getNumberOfChildNodes(); i++) {
			GedcomLine line = getChildNode(i).getNodeValue();
			
			if (newStoreLinePos < line.getStoreLine().getPos()) {
				//Add the new line right before the line with a higher store line position
				try {
					addChildNodeAt(i - 1, newNode);
					lineCount.put(tagOrStructureName, lineCount.get(tagOrStructureName) + 1);
				} catch (TreeNodeException e) {
					return null;
				}
				
				newNode.setGedcomLine(storeLine, tagOrStructureName, tag);
				return newNode;
			}
			
		}
		
		newNode.setGedcomLine(storeLine, tagOrStructureName, tag);
		
		//Add the new line at the end
		try {
			addChildNode(newNode);
			lineCount.put(tagOrStructureName, lineCount.get(tagOrStructureName) + 1);
		} catch (TreeNodeException e) {
			return null;
		}
		
		return newNode;
	}
	
	/**
	 * Sets the value (the {@link GedcomLine}) of this node by creating a 
	 * new {@link GedcomTagLine} or {@link GedcomStructureLine} and setting it 
	 * as value.
	 * 
	 * @param storeLine
	 * @param tagOrStructureName
	 * @param tag
	 */
	private void setGedcomLine(GedcomStoreLine storeLine, String tagOrStructureName, String tag) {
		
		GedcomLine newLine = null;
		
		if (storeLine.hasStructureName()) {
			newLine = new GedcomStructureLine(storeLine, tag);
		} else {
			newLine = new GedcomTagLine(storeLine, tagOrStructureName);
		}
		
		setNodeValue(newLine);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getParentId() {
		if (!isHeadNode()) {
			return ((GedcomNode)getParentNode()).getNodeKey();
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * 
	 * @param value
	 * @return
	 */
	public GedcomTagLine setTagLineValue(String value) {
		return getNodeValue().getAsTagLine().setValue(value);
	}
	
	/**
	 * 
	 * 
	 * @param xref
	 * @return
	 */
	public GedcomTagLine setTagLineXRef(String xref) {
		return getNodeValue().getAsTagLine().setXRef(xref);
	}

	/**
	 * Checks if the line has to be skipped. A line has to be skipped if value/xref
	 * are required but not set/empty (depending on the given flags)
	 *
	 * @param node
	 * @param printEmptyLines
	 * @param printLinesWithNoValueSet
	 * @return
	 */
	private boolean skipLinePrint(GedcomNode node, boolean printEmptyLines,
			boolean printLinesWithNoValueSet) {
		GedcomLine line = node.getNodeValue();
		boolean skip = false;

		if (line.isTagLine()) {
			GedcomTagLine tagLine = line.getAsTagLine();

			if (!printEmptyLines) {
				//Skip empty lines which actually require a value
				if ((tagLine.isValueSet() || tagLine.isXRefSet()) && tagLine.isEmpty()) {
					skip = true;
				}
			}

			if (!printLinesWithNoValueSet) {
				//Skip lines which have no value and xref set, but which require a value
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
		//does not need to be printed if skip has already been set to true

		if (node.hasChildNodes()) {
			LinkedList<TreeNode<String, GedcomLine>> nodes = node.getChildNodes();

			for (TreeNode<String, GedcomLine> n : nodes) {
				GedcomLine blockLine = n.getNodeValue();
				
				if (blockLine.isTagLine()) {
					if (!skipLinePrint((GedcomNode)n, printEmptyLines, printLinesWithNoValueSet)) {
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
	
	@Override
	public boolean printNode() {
		if (!super.printNode()) {
			return false;
		}
		
		if (isHeadNode()) {
			return true;
		}
		
		if (getNodeValue().isTagLine()) {
			if (skipLinePrint(this, false, false)) {
				return false;
			}
			
			return true;
		}
		
		return true;
	}
	
	
	@Override
	public String print() {
		StringBuilder sb = new StringBuilder();
		
		if (!isInvisibleNode()) {
			sb.append(getNodeLevel(true));
			sb.append(GedcomLine.DELIM);
		}
		
		sb.append(getNodeValue());
		
		return sb.toString();
	}
	
	
}
