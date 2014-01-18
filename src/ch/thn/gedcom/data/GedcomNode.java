/**
 * 
 */
package ch.thn.gedcom.data;

import java.util.LinkedList;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.store.GedcomStoreBlock;
import ch.thn.gedcom.store.GedcomStoreLine;
import ch.thn.gedcom.store.GedcomStoreStructure;
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

	private GedcomStoreLine storeLine = null;
	
	private String tagOrStructureName = null;
	private String tag = null;
	private boolean lookForXRefAndValueVariation = false;
	private boolean withXRef = false;
	private boolean withValue = false;

	/**
	 * 
	 * 
	 * @param storeBlock The block to get the line from
	 * @param tagOrStructureName
	 * @param tag
	 * @param lookForXRefAndValueVariation
	 * @param withXRef
	 * @param withValue
	 */
	protected GedcomNode(GedcomStoreBlock storeBlock, String tagOrStructureName, 
			String tag, boolean lookForXRefAndValueVariation, boolean withXRef, boolean withValue) {
		super(tagOrStructureName, null);
		
		this.tagOrStructureName = tagOrStructureName;
		this.tag = tag;
		this.lookForXRefAndValueVariation = lookForXRefAndValueVariation;
		this.withXRef = withXRef;
		this.withValue = withValue;

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

		storeLine = storeBlock.getStoreLine(tagOrStructureName);

		if (storeLine.hasStructureName()) {			
			//It is a structure line, thus it does not have a child block but it 
			//is only a "link" to the structure 
			this.storeBlock = storeBlock.getStoreStructure().getStore()
					.getGedcomStructure(tagOrStructureName, tag, 
							lookForXRefAndValueVariation, withXRef, withValue).getStoreBlock();
		} else {
			this.storeBlock = storeLine.getChildBlock();
		}
		
		if (storeLine.hasStructureName()) {
			setNodeValue(new GedcomStructureLine(storeLine, tag, this));
		} else {
			setNodeValue(new GedcomTagLine(storeLine, tagOrStructureName, this));
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param storeBlock
	 */
	protected GedcomNode(GedcomStoreStructure storeStructure) {
		super(storeStructure.getStoreBlock().getStoreStructure().getStructureName(), null);
		this.storeBlock = storeStructure.getStoreBlock();
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomNode newLine() {
		if (isHeadNode()) {
			//The head node is the starting point of the tree
			return null;
		}

		//Add child node to the parent node, using the parameters of this node
		if (lookForXRefAndValueVariation) {
			return ((GedcomNode)getParentNode()).addChildLine(tagOrStructureName, tag, 
					withXRef, withValue);
		} else {
			return ((GedcomNode)getParentNode()).addChildLine(tagOrStructureName, tag);
		}
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
				
		if (maxNumberOfLinesReached(tagOrStructureName)) {
			return null;
		}
		
		GedcomNode newNode = new GedcomNode(storeBlock, tagOrStructureName, tag, 
				lookForXRefAndValueVariation, withXRef, withValue);
		
		int newStoreLinePos = 0;
		
		//Get the node position only if it is not a header. The header has position 
		//0 anyways
		if (!isHeadNode()) {
			newStoreLinePos = newNode.getStoreLine().getPos();
		}
		
		//Look for the position where to add the new line. The position is defined 
		//through the parsed lineage linked grammar with the line order
		for (int i = getNumberOfChildNodes() - 1; i >= 0 ; i--) {
			if (newStoreLinePos >= getChildNode(i).getNodeValue().getStoreLine().getPos()) {
				//Add the new line right before the line with a higher store line position
				try {
					addChildNodeAt(i + 1, newNode);
				} catch (TreeNodeException e) {
					return null;
				}
				
				return newNode;
			}
			
		}
				
		//Add the new line at the end
		try {
			addChildNode(newNode);
		} catch (TreeNodeException e) {
			return null;
		}
		
		return newNode;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getParentNodeKey() {
		if (!isHeadNode()) {
			return ((GedcomNode)getParentNode()).getNodeKey();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the value of this node
	 * 
	 * @param value
	 * @return
	 */
	public GedcomNode setTagLineValue(String value) {
		if (!getNodeValue().isTagLine()) {
			return null;
		}
		
		getNodeValue().getAsTagLine().setValue(value);
		return this;
	}
	
	/**
	 * Sets the xref of this node
	 * 
	 * @param xref
	 * @return
	 */
	public GedcomNode setTagLineXRef(String xref) {
		if (!getNodeValue().isTagLine()) {
			return null;
		}
		
		getNodeValue().getAsTagLine().setXRef(xref);
		return this;
	}

	/**
	 * 
	 *
	 * @param tagName
	 * @param value
	 * @return
	 */
	public boolean hasLineWithValue(String value) {
		LinkedList<TreeNode<String, GedcomLine>> nodes = getChildNodes();
		
		for (TreeNode<String, GedcomLine> node : nodes) {
			GedcomLine line = node.getNodeValue();
			if (line.isTagLine()
					&& line.getAsTagLine().getValue() != null
					&& line.getAsTagLine().getValue().equals(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * 
	 * @param tagName
	 * @param xref
	 * @return
	 */
	public boolean hasLineWithXRef(String xref) {
		LinkedList<TreeNode<String, GedcomLine>> nodes = getChildNodes();
		
		for (TreeNode<String, GedcomLine> node : nodes) {
			GedcomLine line = node.getNodeValue();
			if (line.isTagLine()
					&& line.getAsTagLine().getXRef() != null
					&& line.getAsTagLine().getXRef().equals(xref)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method checks if a new line with the given tag or structure name
	 * can be added to this block. The method does not do any extended checks, like
	 * if the maximum number of the line is already reached. It only checks if
	 * there is such a line defined for this block in the lineage-linked grammar
	 *
	 * @param tagOrStructureName The tag or structure name to look for
	 * @return True if such a line can be added
	 */
	public boolean canAddLine(String tagOrStructureName) {
		//-> multiple variations do not matter, because the min/max values apply 
		//for all variations of a structure
		return storeBlock.hasStoreLine(tagOrStructureName);
	}

	/**
	 * This method returns the maximum number of lines allowed for the line
	 * with the given tag or structure name.<br>
	 * Since all the variations have the 
	 * same min/max limits, there is no need for specifying the variation and 
	 * only the tag or structure name is enough.
	 *
	 * @param tagOrStructureName
	 * @return Returns the maximum number or allowed lines or 0 if there is no
	 * maximum defined. -1 is returned if there is no line with the given tag
	 * or structure line available for this block.
	 */
	public int maxNumberOfLines(String tagOrStructureName) {
		//It does not need the tag for multiple variations, since all the
		//variations are the same

		if (!storeBlock.hasStoreLine(tagOrStructureName)) {
			return -1;
		}

		return storeBlock.getStoreLine(tagOrStructureName).getMax();
	}

	/**
	 * This method checks if the maximum number of lines of the line with the given
	 * tag or structure name has been reached already
	 *
	 * @param tagOrStructureName
	 * @return
	 */
	public boolean maxNumberOfLinesReached(String tagOrStructureName) {
		if (!hasChildNode(tagOrStructureName)) {
			return false;
		}
		
		int lineCount = getNumberOfChildNodes(tagOrStructureName);
		int max = maxNumberOfLines(tagOrStructureName);
		
		return (max != 0 && lineCount >= max);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomStoreBlock getStoreBlock() {
		return storeBlock;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomStoreLine getStoreLine() {
		return storeLine;
	}


	/**
	 * This method checks if the given name is a structure name which is defined
	 * for this block. If this method returns true, it should be possible to
	 * add a structure line with the given name.
	 *
	 * @param name The name to check
	 * @return Returns true if the given name is a structure name, or false if it is not
	 * a structure name or if it does not exist.
	 */
	public boolean nameIsPossibleStructure(String name) {
		if (!getStoreBlock().hasStoreLine(name)) {
			return false;
		}

		return getStoreBlock().getStoreLine(name).hasStructureName();
	}

	/**
	 * This method checks if the given name is a tag name which is defined
	 * for this block. If this method returns true, it should be possible to
	 * add a tag line with the given name.
	 *
	 * @param name The name to check
	 * @return Returns true if the given name is a tag name, or false if it is not
	 * a tag name or if it does not exist.
	 */
	public boolean nameIsPossibleTag(String name) {
		if (!getStoreBlock().hasStoreLine(name)) {
			return false;
		}

		return getStoreBlock().getStoreLine(name).hasTags();        
	}

	/**
	 * Returns true if the current object can have child lines as defined in the
	 * lineage linked grammar
	 *
	 * @return
	 */
	public boolean canHaveChildren() {
		return getStoreBlock().hasChildLines();
	}

	/**
	 * Returns the minimum number of lines of the type of this line which are
	 * required in one block
	 *
	 * @return
	 */
	public int getMinNumberOfLines() {
		return storeLine.getMin();
	}

	/**
	 * Returns the maximum number of lines of the type of this line which are
	 * allowed in one block. A returned number of 0 indicates that there is
	 * not maximum limit (given as M in the lineage linked grammar).
	 *
	 * @return
	 */
	public int getMaxNumberOfLines() {
		return storeLine.getMax();
	}
	
	@Override
	public boolean isInvisibleNode() {
		if (super.isInvisibleNode()) {
			return true;
		}
		
		//Do not print structure lines
		if (getNodeValue().isStructureLine()) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean printNode() {
		if (!getTreePrinter().skipHiddenNodes()) {
			return true;
		}
		
		if (!super.printNode()) {
			return false;
		}
		
		if (isHeadNode()) {
			return true;
		}
		
		return !skipLinePrint(this, false, false);
	}
	
	/**
	 * Checks if the line has to be skipped. A line has to be skipped if value/xref
	 * are required but not set/empty (depending on the given flags). However, 
	 * if there is a line in a lower level which has a value/xref, then this 
	 * line has to be printed in order to print the line with value on the lower level.
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

		if (skip && node.hasChildNodes()) {
			LinkedList<TreeNode<String, GedcomLine>> nodes = node.getChildNodes();

			for (TreeNode<String, GedcomLine> n : nodes) {
				
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

		return skip;
	}


//	/**
//	 * Follows the path given with <code>path</code>. Each array position describes
//	 * one path step, and each step can contain multiple values describing the
//	 * step. The following two lines each show one step in the path with multiple
//	 * values, separated by {@value #PATH_OPTION_DELIMITER}:<br>
//	 * - "structure name;tag;with xref;with value;line number"<br>
//	 * - "structure name;tag;with xref;with value"<br>
//	 * - "tag or structure name;line number"<br>
//	 * ("with xref" and "with value" have to be given as "true" or "false")<br>
//	 * <br>
//	 * If multiple step values are given, they have to be separated with the
//	 * {@link #PATH_OPTION_DELIMITER}. Multiple step values are needed if the
//	 * next path step can not be identified with one step value only. A tag line
//	 * for example can be added multiple times, thus when accessing that line, the
//	 * tag and the line number have to be given. Also, some structures exist in
//	 * different variations (with/without xref, with/without value, ...) and might
//	 * have to be accessed with multiple values for one path step.<br>
//	 * If a path can not be followed, this method throws an {@link GedcomPathAccessError}
//	 * with an error text and the path which caused the error. The error text might
//	 * give a hint to what has gone wrong.
//	 *
//	 * @param path The path to follow. If pieces of the path are not yet created,
//	 * it will try to create them
//	 * @return The {@link GedcomNode} of the last object in the path
//	 * @throws GedcomPathAccessError If following the given path is not possible
//	 * @throws GedcomCreationError If new path pieces have to be created but they
//	 * can not be created (because of invalid structure/tag names, ...)
//	 */
//	public GedcomNode followPath(String... path) {
//		return followPath(false, false, path);
//	}
//
//	/**
//	 * Follows the path given with <code>path</code>. Each array position describes
//	 * one path step, and each step can contain multiple values describing the
//	 * step. The following two lines each show one step in the path with multiple
//	 * values, separated by {@value #PATH_OPTION_DELIMITER}:<br>
//	 * - "structure name;tag;with xref;with value;line number"<br>
//	 * - "structure name;tag;with xref;with value"<br>
//	 * - "tag or structure name;line number"<br>
//	 * ("with xref" and "with value" have to be given as "true" or "false")<br>
//	 * <br>
//	 * If multiple step values are given, they have to be separated with the
//	 * {@link #PATH_OPTION_DELIMITER}. Multiple step values are needed if the
//	 * next path step can not be identified with one step value only. A tag line
//	 * for example can be added multiple times, thus when accessing that line, the
//	 * tag and the line number have to be given. Also, some structures exist in
//	 * different variations (with/without xref, with/without value, ...) and might
//	 * have to be accessed with multiple values for one path step.<br>
//	 * If a path can not be followed, this method throws an {@link GedcomPathAccessError}
//	 * with an error text and the path which caused the error. The error text might
//	 * give a hint to what has gone wrong.
//	 *
//	 * @param addNew If set to <code>true</code>, a new path is created.
//	 * <code>addNewAt</code> defines how and where the split has to be done.
//	 * @param path The path to follow. If pieces of the path are not yet created,
//	 * it will try to create them
//	 * @return The {@link GedcomNode} of the last object in the path
//	 * @throws GedcomPathAccessError If following the given path is not possible
//	 * @throws GedcomCreationError If new path pieces have to be created but they
//	 * can not be created (because of invalid structure/tag names, ...)
//	 */
//	public GedcomNode followPath(boolean addNew, String... path) {
//		return followPath(addNew, false, path);
//	}
//
//
//	/**
//	 * Follows the path given with <code>path</code>. Each array position describes
//	 * one path step, and each step can contain multiple values describing the
//	 * step. The following three lines each show one step in the path with multiple
//	 * values, separated by {@value #PATH_OPTION_DELIMITER}:<br>
//	 * - "structure name;tag;with xref;with value;line number"<br>
//	 * - "structure name;tag;with xref;with value"<br>
//	 * - "tag or structure name;line number"<br>
//	 * ("with xref" and "with value" have to be given as "true" or "false")<br>
//	 * <br>
//	 * If multiple step values are given, they have to be separated with the
//	 * {@link #PATH_OPTION_DELIMITER}. Multiple step values are needed if the
//	 * next path step can not be identified with one step value only. A tag line
//	 * for example can be added multiple times, thus when accessing that line, the
//	 * tag and the line number have to be given. Also, some structures exist in
//	 * different variations (with/without xref, with/without value, ...) and might
//	 * have to be accessed with multiple values for one path step.<br>
//	 * If a path can not be followed, this method throws an {@link GedcomPathAccessError}
//	 * with an error text and the path which caused the error. The error text might
//	 * give a hint to what has gone wrong.
//	 *
//	 * @param addNew If set to <code>true</code>, a new path is created.
//	 * @param silent Just suppresses the ">>" access output
//	 * @param path The path to follow. If pieces of the path are not yet created,
//	 * it will try to create them
//	 * @return The {@link GedcomNode} of the last object in the path
//	 * @throws GedcomPathAccessError If following the given path is not possible
//	 * @throws GedcomCreationError If new path pieces have to be created but they
//	 * can not be created (because of invalid structure/tag names, ...)
//	 */
//	protected GedcomNode followPath(boolean addNew, boolean silent, String... path) {
//		GedcomNode n = this;
//		GedcomNode previousNode = n;
//		GedcomNode lastNodeWithSplit = null;
//		ArrayList<String> addNewPath = new ArrayList<String>();
//		String pathPiece = null;
//		int lineNumber = 0;
//		boolean added = false;
//
//		if (path == null || path.length == 0) {
//			return this;
//		}
//
//		//Go through the whole given path
//		for (int currentPathIndex = 0; currentPathIndex < path.length; currentPathIndex++) {
//			if (!n.canHaveChildren()) {
//				//Nothing else to do, the path ends here
//				throw new GedcomPathAccessError(path, pathPiece + " can not have any children");
//			}
//
//			lineNumber = 0;
//
//			String[] parts = path[currentPathIndex].split(PATH_OPTION_DELIMITER);
//
//			if (parts.length <= 0) {
//				continue;
//			}
//
//			pathPiece = parts[0];
//
//			if (pathPiece.length() == 0) {
//				continue;
//			}
//
//			previousNode = n;
//
//			if (n.nameIsPossibleStructure(pathPiece)) {
//				PathObject po = followStructureLine(pathPiece, parts, lineNumber, path, n,
//						previousNode, currentPathIndex);
//				n = po.o;
//
//				if (po.added) {
//					added = true;
//				}
//			} else if (n.nameIsPossibleTag(pathPiece)) {
//				PathObject po = followTagLine(pathPiece, parts, lineNumber, path, n,
//						previousNode, currentPathIndex);
//				n = po.o;
//
//				if (po.added) {
//					added = true;
//				}
//			} else {
//				throw new GedcomPathAccessError(path, "Path piece " + pathPiece +
//						" does not exist in " + getNodeKey() + ". Possible line id's: " +
//						GedcomFormatter.makeOrList(previousNode.getStoreBlock().getAllLineIDs(), "", ""));
//			}
//
//			//Keep a record of the last line where splitting (adding another line)
//			//would be possible.
//			//If a new line has already been added in the path, do not care about this any more
//			if (addNew) {
//				if (!added && (n.getMaxNumberOfLines() == 0
//						|| n.getNumberOfChildNodes() < n.getMaxNumberOfLines())) {                                
//					lastNodeWithSplit = n;
//					addNewPath.clear();
//				} else {
//					//Start recording the path one path piece after the line in lastLineWithSplit,
//					//so that followPath can be called on the newly created line
//					if (lastNodeWithSplit != null) {
//						addNewPath.add(path[currentPathIndex]);
//					}
//				}
//			}
//
//
//		}
//
//		//A new line is needed and no line has been added yet. This means that
//		//the path we followed already existed -> a new line has to be added at
//		//the last possible split point
//		if (addNew && !added) {
//			//A new line should be added but there is no location in the path
//			//to add a new line
//			if (lastNodeWithSplit == null) {
//				throw new GedcomPathAccessError(path, "A new path " + Arrays.toString(path) +
//						" should be added but the given path seems to have reached its line number limits.");
//			}
//
//			GedcomNode newNode = null;
//
//			//Add a new line and continue following the path from that new line on
//			newNode = lastNodeWithSplit.newLine();
//
//			if (addNewPath.size() > 0) {
//				return newNode.followPath(false, true, addNewPath.toArray(new String[addNewPath.size()]));
//			} else {
//				return newNode;
//			}
//
//		}
//
//		return n;
//	}
//
//
//	/**
//	 *
//	 *
//	 * @param pathPiece
//	 * @param parts
//	 * @param lineNumber
//	 * @param path
//	 * @param o
//	 * @param previousObject
//	 * @param currentPathIndex
//	 * @return
//	 */
//	private PathObject followStructureLine(String pathPiece, String[] parts, int lineNumber,
//			String[] path, GedcomNode o, GedcomNode previousObject, int currentPathIndex) {
//		String tagExtension = null;
//		boolean useXRefValue = false;
//		boolean withXRef = false;
//		boolean withValue = false;
//		boolean added = false;
//
//
//		//Parse the step values
//		if (parts.length > 1) {
//			//Tag
//			tagExtension = parts[1];
//
//			if (parts.length > 2) {
//				//withXRef
//				withXRef = Boolean.parseBoolean(parts[2]);
//
//				useXRefValue = true;
//
//				if (parts.length > 3) {
//					//withValue
//					withValue = Boolean.parseBoolean(parts[3]);
//
//					if (parts.length > 4) {
//						//Line number
//						if (parts[4].length() > 0) {
//							try {
//								lineNumber = Integer.parseInt(parts[4]);
//							} catch (NumberFormatException e) {
//								throw new GedcomPathAccessError(path, "Last part of the path piece " + path[currentPathIndex] +
//										" is not empty and not a number. Failed to parse line number.");
//							}        
//						}
//					}
//
//				}
//			}
//		}
//
//		if (o.hasChildLine(pathPiece, tagExtension, lineNumber)) {
//			o = o.getChildLine(pathPiece, tagExtension, lineNumber);
//		} else {
//			//The line number starts with 0 for the first line, which means if
//			//there are 5 lines, a lineNumber=5 is the 6th (the next) line.
//			if (o.getNumberOfLines(pathPiece, tagExtension) != lineNumber) {
//				throw new GedcomPathAccessError(path, "Line number " + lineNumber +
//						" is too high. There are only " + o.getNumberOfLines(pathPiece) +
//						" lines of " + pathPiece + " available.");
//			}
//
//			GedcomNode b = o;
//
//			if (useXRefValue) {
//				o = b.addChildLine(pathPiece, tagExtension, withXRef, withValue);
//			} else if (tagExtension != null && tagExtension.length() > 0) {
//				o = b.addChildLine(pathPiece, tagExtension);
//			} else {
//				o = b.addChildLine(pathPiece);
//			}
//
//			if (o == null) {
//				String tagString = "";
//
//				if (tagExtension != null && tagExtension.length() > 0) {
//					tagString = "-" + tagExtension;
//				}
//
//				throw new GedcomPathAccessError(path, "Structure " + pathPiece + tagString +
//						" can not be accessed/added. Possible line id's: " +
//						GedcomFormatter.makeOrList(previousObject.getChildNodeKeys(), "", ""));
//			}
//
//			added = true;
//		}
//
//		return new PathObject(o, added);
//	}
//
//	/**
//	 *
//	 *
//	 * @param pathPiece
//	 * @param parts
//	 * @param lineNumber
//	 * @param path
//	 * @param o
//	 * @param previousObject
//	 * @param currentPathIndex
//	 * @return
//	 */
//	private PathObject followTagLine(String pathPiece, String[] parts, int lineNumber,
//			String[] path, GedcomNode o, GedcomNode previousObject, int currentPathIndex) {
//		boolean added = false;
//
//		if (parts.length > 1) {
//			//Line number
//			if (parts[1].length() > 0) {
//				try {
//					lineNumber = Integer.parseInt(parts[1]);
//				} catch (NumberFormatException e) {
//					throw new GedcomPathAccessError(path, "Last part of the path piece " +
//							path[currentPathIndex] +
//							" is not empty and not a number. Failed to parse line number " + parts[1]);
//				}        
//			}
//		}
//
//		if (o.hasChildLine(pathPiece, lineNumber)) {
//			o = o.getChildLine(pathPiece, lineNumber);
//		} else {
//
//			//The line number starts with 0 for the first line, which means if
//			//there are 5 lines, a lineNumber=5 is the 6th (the next) line.
//			if (o.getNumberOfLines(pathPiece) != lineNumber) {
//				throw new GedcomPathAccessError(path, "Line number " + lineNumber +
//						" is too high. There are only " + o.getNumberOfLines(pathPiece) +
//						" lines of " + pathPiece + " available.");
//			}
//
//			GedcomNode b = o;
//
//			o = b.addChildLine(pathPiece);
//
//			if (o == null) {
//				throw new GedcomPathAccessError(path, "Tag line " + pathPiece +
//						" can not be accessed/added. Possible line id's: " +
//						GedcomFormatter.makeOrList(previousObject.getChildNodeKeys(), "", ""));
//			}
//
//			added = true;
//		}
//
//		return new PathObject(o, added);
//	}
//
//
//	/**
//	 *
//	 *
//	 * @author thomas
//	 *
//	 */
//	private class PathObject {
//
//		private GedcomNode o = null;
//
//		private boolean added = false;
//
//
//		public PathObject(GedcomNode o, boolean added) {
//			this.o = o;
//			this.added = added;
//		}
//
//	}


	@Override
	public String toString() {
		if (getNodeValue() == null) {
			if (tag != null) {
				return tagOrStructureName + " (" + tag + ")";
			} else {
				return tagOrStructureName;
			}
		} else {
			return getNodeValue().toString();
		}
	}

}
