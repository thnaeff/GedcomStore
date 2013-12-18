/**
 * 
 */
package ch.thn.gedcom.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomToString;
import ch.thn.gedcom.store.GedcomStoreBlock;
import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * A {@link GedcomBlock} is a container for all the lines of one level. Such a 
 * block has s parent line (which is one level higher than the block lines) and 
 * multiple block lines. Each line is an object of {@link GedcomLine}<br>
 * <br>
 * See {@link GedcomObject} for an example structure with blocks and lines.
 * 
 * 
 * @author thomas
 * @see GedcomObject
 */
public class GedcomBlock extends GedcomObject {

	/** Create all the available lines automatically */
	public static final int ADD_ALL = 0;
	/** Only create mandatory lines automatically */
	public static final int ADD_MANDATORY = 1;
	/** Do not create any lines automatically */
	public static final int ADD_NONE = 2;

	/**
	 * A list with all the lines of this block.
	 */
	private LinkedList<GedcomLine> lines = null;
	
	/**
	 * Links the line ID's (tag or structure name) to the lines with that ID. 
	 * There can be multiple lines with the same ID which can be accessed using 
	 * their line number.
	 */
	private HashMap<String, LinkedList<GedcomLine>> idToLineLinks = null;
	
	/**
	 * The store block which holds the lineage-linked grammar data for this block
	 */
	private GedcomStoreBlock storeBlock = null;
	
	/**
	 * The parent line under which this block is added
	 */
	private GedcomLine parentLine = null;
		
	private int copyMode = ADD_MANDATORY;
	
	
	/**
	 * A {@link GedcomBlock} is a container for all the lines of one level. Such a 
	 * block has s parent line (which is one level higher than the block lines) and 
	 * multiple block lines. Each line is an object of {@link GedcomLine}<br>
	 * <br>
	 * This constructor creates an empty block without copying any lines. However, 
	 * giving the copyMode is necessary for any lines added to this block since 
	 * the copyMode is passed on to added lines and their sub blocks.
	 * 
	 * @param storeBlock The {@link GedcomStoreBlock} which has the properties and 
	 * possibilities of this {@link GedcomBlock}
	 * @param parentLine The parent {@link GedcomLine} under which this {@link GedcomBlock} 
	 * lies
	 * @param copyMode The mode for creating this block and its lines.
	 */
	public GedcomBlock(GedcomStoreBlock storeBlock, GedcomLine parentLine, int copyMode) {
		this.storeBlock = storeBlock;
		this.parentLine = parentLine;
		this.copyMode = copyMode;
		
		lines = new LinkedList<GedcomLine>();
		idToLineLinks = new LinkedHashMap<String, LinkedList<GedcomLine>>();
				
	}
	
	/**
	 * A {@link GedcomBlock} is a container for all the lines of one level. Such a 
	 * block has s parent line (which is one level higher than the block lines) and 
	 * multiple block lines. Each line is an object of {@link GedcomLine}
	 * 
	 * @param storeBlock The {@link GedcomStoreBlock} which has the properties and 
	 * possibilities of this {@link GedcomBlock}
	 * @param parentLine The parent {@link GedcomLine} under which this {@link GedcomBlock} 
	 * lies
	 * @param tag 
	 * @param copyMode The mode for creating this block and its lines
	 */
	public GedcomBlock(GedcomStoreBlock storeBlock, GedcomLine parentLine, String tag, int copyMode) {
		this(storeBlock, parentLine, copyMode);
		
		//Copy mandatory or all lines if necessary
		if (copyMode == ADD_MANDATORY && storeBlock.hasMandatoryLines()) {
			addInitialLines(storeBlock.getMandatoryLines(), tag, copyMode);
		} else if (copyMode == ADD_ALL) {
			addInitialLines(storeBlock.getStoreLines(), tag, copyMode);
		}
		
	}
	
	/**
	 * Makes a copy of all the given lines by creating a new instance from each given 
	 * store line.
	 * 
	 * @param linesToCopy The {@link GedcomStoreLine}s to get the instance from
	 * @param tag 
	 * @param copyMode The mode which is passed on to each sub line instance
	 */
	private void addInitialLines(LinkedList<GedcomStoreLine> linesToCopy, String tag, int copyMode) {
		
		for (GedcomStoreLine storeLine : linesToCopy) {
			//Get the structure line, or if its a tag line try to get the 
			//line in case the line only has one tag possibility. If there 
			//is more than one tag possible, null is returned since it can 
			//not be copied.
			GedcomLine line = null;
			
			try {
				if (storeLine.hasMultipleTagNames()) {
					line = storeLine.getLineInstance(this, tag, copyMode);
				} else {
					line = storeLine.getLineInstance(this, copyMode);
				}
				
				if (line != null) {
					addLine(line);
				}
			} catch (GedcomCreationError e) {
				if (!(e instanceof GedcomCreationError2)) {
					throw new GedcomCreationError2("Failed to automatically create line " + storeLine.getId() + ". " + e.getMessage());
				} else {
					throw e;
				}
			}
			
		}
		
	}
	
	/**
	 * Adds the given line to this block. The line is added at the position defined 
	 * in the lineage-linked form definition
	 * 
	 * @param line The line to add
	 * @return Returns false if the maximum number of lines of the given line is 
	 * reached.
	 */
	private boolean addLine(GedcomLine line) {
		if (line.getStoreLine().getMax() != 0 
				&& idToLineLinks.containsKey(line.getId()) 
				&& line.getStoreLine().getMax() <= idToLineLinks.get(line.getId()).size()) {
			
			String lineIdString = " to ";
			
			if (parentLine != null) {
				lineIdString = " to " + parentLine.getId() + " in ";
			}
			
			throw new GedcomCreationError("Can not add another line " + line.getId()  
					+ lineIdString + getStoreBlock().getStoreStructure().getStructureName() 
					+ ". Maximum of " + line.getStoreLine().getMax() + " lines reached.");
		}
		
		int index = lines.size();
		
		//If the new line to add has a lower position than the last item...
		if (index > 0 && line.getStoreLine().getPos() < lines.get(index - 1).getStoreLine().getPos()) {			
			//...look where to put the new line 
			for (index = lines.size() - 1; index >= 0; index--) {
				//Loop until a item with a lower index is passed
				if (lines.get(index).getStoreLine().getPos() <= line.getStoreLine().getPos()) {
					index++;
					break;
				}
			}
			
		}
		
		if (index < 0) {
			index = 0;
		}
		
		//Add line
		lines.add(index, line);

		//Link ID to line
		String id = null;
		
		if (line.isTagLine()) {
			GedcomTagLine tagLine = line.getAsTagLine();
			
			id = tagLine.getTag();
		} else {
			GedcomStructureLine structureLine = line.getAsStructureLine();
			id = structureLine.getStoreLine().getStructureName();
			
			String tag = structureLine.getStructureVariationTag();
			if (tag != null) {
				id = id + "-" + tag;
			}
		}
		
		
		if (!idToLineLinks.containsKey(id)) {
			idToLineLinks.put(id, new LinkedList<GedcomLine>());
		}
		
		idToLineLinks.get(id).add(line);
		
		return true;
	}
	
	/**
	 * Adds a new structure or tag line to this block. 
	 * 
	 * @param tagOrStructureName The tag or structure name of the line to add
	 * @param tag If it is a structure name with multiple variations, the tag can be 
	 * specified here
	 * @param withXRef If it is a structure name with multiple variations, setting 
	 * this parameter to true looks for a variation with an XRef field
	 * @param withValue If it is a structure name with multiple variations, setting 
	 * this parameter to true looks for a variation with an value field
	 * @return The added line if adding was successful, or null if not (because the 
	 * line does not exist).
	 */
	private GedcomLine addUserLine(String tagOrStructureName, String tag, boolean withXRef, boolean withValue) {
		
		if (storeBlock.hasStoreLine(tagOrStructureName)) {
			GedcomStoreLine storeLine = storeBlock.getStoreLine(tagOrStructureName);
			GedcomLine line = null;
			
			if (storeLine.hasStructureName()) {
				
				if (withXRef || withValue) {
					line = storeLine.getLineInstance(this, tag, withXRef, withValue, copyMode);
				} else if (tag != null) {
					line = storeLine.getLineInstance(this, tag, copyMode);
				} else {
					line = storeLine.getLineInstance(this, copyMode);
				}
				
			} else {
				
				if (tag != null) {
					line = storeLine.getLineInstance(this, tag, copyMode);
				} else {
					line = storeLine.getLineInstance(this, copyMode);
				}
				
			}
			
			
			addLine(line);
			
			if (showAccessOutput()) {
				printAllAddedLines(line);
			}
			
			return line;
		} else {
			String lineId = " to ";
			
			if (parentLine != null) {
				lineId = " to " + parentLine.getId() + " in ";
			}
						
			throw new GedcomCreationError("Can not add line " + tagOrStructureName  
					+ lineId + getStoreBlock().getStoreStructure().getStructureName() 
					+ ". Line does not exist.");
			
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param line
	 * @param initialLineCopy
	 */
	private static void printAllAddedLines(GedcomLine line) {
		printInfoOutput(line, false, false);
		//All following lines must have been added as initial lines when the 
		//object was created
		printAllAddedLines(line.getChildBlock(), true);
	}
	
	/**
	 * 
	 * 
	 * @param block
	 */
	private static void printAllAddedLines(GedcomBlock block, boolean initialLineCopy) {
		if (block == null) {
			return;
		}
		
		for (GedcomLine l : block.getLines()) {
			printInfoOutput(l, false, initialLineCopy);
			printAllAddedLines(l.getChildBlock(), initialLineCopy);
		}
	}
	
	/**
	 * 
	 * 
	 * @param line
	 * @param access
	 * @param initialLineCopy
	 */
	private static void printInfoOutput(GedcomLine line, boolean access, boolean initialLineCopy) {
		String structureInfo = null;
		String prefix = null;
		
		if (line.isStructureLine()) {
			String tag = line.getAsStructureLine().getStructureVariationTag();
			
			//Add tag name to the end of the line if available
			if (tag != null) {
				structureInfo = " (" + tag + ")";
			} else {
				structureInfo = "";
			}
			
			prefix = "";
		} else {
			structureInfo = "";
		}
		
		if (access) {
			prefix = "> ";
		} else {
			if (initialLineCopy) {
				prefix = "* ";
			} else {
				prefix = "+ ";
			}
		}
		
		System.out.println(GedcomFormatter.makeInset(line.getLevel()) + prefix + 
				line.getId() + structureInfo);
	}
	
	/**
	 * Returns the {@link GedcomStoreBlock} which has the properties and 
	 * possibilities of this {@link GedcomBlock}
	 * @return
	 */
	public GedcomStoreBlock getStoreBlock() {
		return storeBlock;
	}
	
	/**
	 * Returns all lines which are added to this block
	 * 
	 * @return
	 */
	public LinkedList<GedcomLine> getLines() {
		return lines;
	}
	
	/**
	 * Returns a list of all tag names and structure names which are added to this block
	 * 
	 * @return
	 */
	protected LinkedList<String> getLineIds() {
		return new LinkedList<String>(idToLineLinks.keySet());
	}
	
	
	
	
	/**
	 * Adds a new tag line with the given tag name to this block
	 * 
	 * @param tag The tag name specifies which line has to be added
	 * @return The added {@link GedcomTagLine} if adding was successful, or null if not.
	 */
	public GedcomTagLine addTagLine(String tag) {
		if (!nameIsPossibleTag(tag)) {
			throw new GedcomCreationError(tag + " is not a possible tag name. Possible tag names are: " + 
					GedcomFormatter.makeOrList(getFollowingBlock().getStoreBlock().getAllLineIDs(), "", ""));
		}
		return (GedcomTagLine)addUserLine(tag, tag, false, false);
	}
	
	/**
	 * Adds a new structure line with the given structure name to this block
	 * 
	 * @param structureName The structure name specifies which line has to be added
	 * @return The added {@link GedcomStructureLine} if adding was successful, or null if not.
	 */
	public GedcomStructureLine addStructureLine(String structureName) {
		if (!nameIsPossibleStructure(structureName)) {
			throw new GedcomCreationError(structureName + " is not a structure name");
		}
		return (GedcomStructureLine)addUserLine(structureName, null, false, false);
	}
	
	/**
	 * Adds a new structure line with the given structure name to this block. This method can be 
	 * used if there are multiple structure variations available, using the tag name 
	 * to specify which variation to use.
	 * 
	 * @param structureName The structure name specifies which line has to be added
	 * @param tag The tag name or the structure to use
	 * @return The added {@link GedcomStructureLine} if adding was successful, or null if not.
	 */
	public GedcomStructureLine addStructureLine(String structureName, String tag) {
		if (!nameIsPossibleStructure(structureName)) {
			throw new GedcomCreationError(structureName + " is not a structure name");
		}
		return (GedcomStructureLine)addUserLine(structureName, tag, false, false);
	}
	
	/**
	 * Adds a new structure line with the given structure name to this block. This method can be 
	 * used if there are multiple structure variations available, using the tag name 
	 * to specify which variation to use.
	 * 
	 * @param structureName The structure name specifies which line has to be added
	 * @param tag The tag name or the structure to use
	 * @param withXRef If it is a structure name with multiple variations, setting 
	 * this parameter to true looks for a variation with an XRef field
	 * @param withValue If it is a structure name with multiple variations, setting 
	 * this parameter to true looks for a variation with an value field
	 * @return The added {@link GedcomStructureLine} if adding was successful, or null if not.
	 */
	public GedcomStructureLine addStructureLine(String structureName, String tag, boolean withXRef, boolean withValue) {
		if (!nameIsPossibleStructure(structureName)) {
			throw new GedcomCreationError(structureName + " is not a structure name");
		}
		return (GedcomStructureLine)addUserLine(structureName, tag, withXRef, withValue);
	}
	
	
	@Override
	public boolean isBlock() {
		return true;
	}
	
	@Override
	public GedcomBlock getAsBlock() {
		return this;
	}
	
	
	@Override
	public GedcomBlock getParentBlock() {
		if (parentLine.getParentBlock() != null) {
			if (showAccessOutput()) {
				System.out.println(GedcomFormatter.makeInset(parentLine.getParentBlock().getLevel()) + "< ");
			}
		} else {
			throw new GedcomAccessError("There is no parent block for " + this);
		}
		
		return parentLine.getParentBlock();
	}
	
	@Override
	public GedcomLine getParentLine() {
		return parentLine;
	}
	
	@Override
	public GedcomLine getChildLine(String tagOrStructureName) {
		return getChildLine(tagOrStructureName, null, 0);
	}
	
	
	@Override
	public GedcomLine getChildLine(String tagOrStructureName, int lineNumber) {
		return getChildLine(tagOrStructureName, null, lineNumber);
	}
	
	
	@Override
	public GedcomLine getChildLine(String structureName, String tag) {
		return getChildLine(structureName, tag, 0);
	}
	
	
	@Override
	public GedcomLine getChildLine(String structureName, String tag, int lineNumber) {
		
		String name = structureName;
		
		if (tag != null) {
			name = structureName + "-" + tag;
		}
		
		if (!idToLineLinks.containsKey(name)) {
			throw new GedcomAccessError("No line " + name + " added to this " + 
					parentLine.getId() + " block. " + "Available lines(s): " + 
					GedcomFormatter.makeOrList(new LinkedList<String>(getLineIds()), "", ""));
		}
		
		if (idToLineLinks.get(name).size() <= lineNumber || lineNumber < 0) {
			throw new GedcomAccessError("Line " + name + " number " + lineNumber + 
						" does not exist. There are only " + idToLineLinks.get(name).size() + 
						" lines available (the first line starting at index 0).");
		}
		
		GedcomLine line = idToLineLinks.get(name).get(lineNumber);
		
		if (showAccessOutput()) {
			printInfoOutput(line, true, false);
		}
		
		return line;
	}
	
	/**
	 * Checks if this block has a line with the given tag name 
	 * and if that line has the given value set
	 * 
	 * @param tagName
	 * @param value
	 * @return
	 */
	public boolean hasLineWithValue(String tagName, String value) {
		if (!idToLineLinks.containsKey(tagName)) {
			return false;
		}
		
		int numberOfLines = getNumberOfLines(tagName);
		
		for (int i = 0; i < numberOfLines; i++) {
			GedcomLine line = idToLineLinks.get(tagName).get(i);
			if (line.isTagLine() 
					&& line.getAsTagLine().getValue() != null
					&& line.getAsTagLine().getValue().equals(value)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if this block has a line with the given tag name 
	 * and if that line has the given xref set
	 * 
	 * @param tagName
	 * @param xref
	 * @return
	 */
	public boolean hasLineWithXRef(String tagName, String xref) {
		if (!idToLineLinks.containsKey(tagName)) {
			return false;
		}
		
		int numberOfLines = getNumberOfLines(tagName);
		
		for (int i = 0; i < numberOfLines; i++) {
			GedcomLine line = idToLineLinks.get(tagName).get(i);
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
		return storeBlock.hasStoreLine(tagOrStructureName);
	}
	
	@Override
	public String getId() {
		if (parentLine == null || parentLine.getParentBlock() == null) {
			return getStoreBlock().getStoreStructure().getStructureName();
		}
	
		return parentLine.getId();
	}
	
	/**
	 * Returns the level of this block. The level is defined in the lineage-linked 
	 * grammar with "n" or "+1", "+2", etc.
	 * 
	 * @return
	 */
	@Override
	public int getLevel() {
		if (parentLine == null || parentLine.getParentBlock() == null) {
			return 0;
		}
		
		if (parentLine.isStructureLine()) {
			//If the current block is a block of a structure line, do not consider 
			//the structure line when calculating the level.
			return parentLine.getParentBlock().getParentLine().getParentBlock().getLevel() + 1;
		} else {
			return parentLine.getParentBlock().getLevel() + 1;
		}
	}
	
	/**
	 * Resets all the data of all lines in this block, and of all sub lines if 
	 * selected.
	 * 
	 * @param clearSubLines
	 */
	public void clear(boolean clearSubLines) {
		for (GedcomLine line : lines) {
			line.clear();
			
			if (clearSubLines) {
				line.getChildBlock().clear(clearSubLines);
			}
		}
	}
	
	/**
	 * Returns the number of lines which are currently added to this block
	 * 
	 * @return
	 */
	public int getNumberOfLines() {
		return lines.size();
	}
	
	/**
	 * Returns the current number of lines with the given tag or structure name 
	 * in this block
	 * 
	 * @param tagOrStructureName
	 * @return
	 */
	public int getNumberOfLines(String tagOrStructureName) {
		return getNumberOfLines(tagOrStructureName, null);
	}
	
	/**
	 * Returns the current number of lines in this block which have the given 
	 * structure name and variation. If <code>tag=null</code> it behaves 
	 * like {@link #getNumberOfLines(String)}.
	 * 
	 * @param structureName
	 * @param tag
	 * @return
	 */
	public int getNumberOfLines(String structureName, String tag) {
		String name = structureName;
		
		if (tag != null) {
			name = structureName + "-" + tag;
		}
		
		if (!idToLineLinks.containsKey(name)) {
			return 0;
		}
		
		return idToLineLinks.get(name).size();
	}
	
	/**
	 * This method returns the maximum number of lines allowed for the line 
	 * with the given tag or structure name. 
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
	 * @param structureName
	 * @return
	 */
	public boolean maxNumberOfLinesReached(String structureName) {
		return maxNumberOfLinesReached(structureName, null);
	}
	
	/**
	 * This method checks if the maximum number of lines of the line with the given 
	 * tag or structure name has been reached already
	 * 
	 * @param structureName
	 * @param tag
	 * @return
	 */
	public boolean maxNumberOfLinesReached(String structureName, String tag) {
		return (maxNumberOfLines(structureName) >= getNumberOfLines(structureName, tag));
	}
	
	/**
	 * Returns a list of all the child lines which have the given tag or 
	 * structure name
	 * 
	 * @param tagOrStructureName
	 * @return
	 */
	public List<GedcomLine> getChildLines(String tagOrStructureName) {
		return getChildLines(tagOrStructureName, null);
	}
	
	/**
	 * Returns a list of all the child lines which have the given structure name 
	 * and structure variation
	 * 
	 * @param structureName The structure name
	 * @param tag The tag name to specify the structure variation
	 * @return
	 */
	public List<GedcomLine> getChildLines(String structureName, String tag) {
		
		String name = structureName;
		
		if (tag != null) {
			name = structureName + "-" + tag;
		}
		
		if (!idToLineLinks.containsKey(name)) {
			throw new GedcomAccessError("No line " + name + " added to this " + 
					getId() + " block. " + "Available lines(s): " + 
					GedcomFormatter.makeOrList(new LinkedList<String>(getLineIds()), "", ""));
		}
		
		return idToLineLinks.get(name);
	}
	
	
	@Override
	public String toString() {
		StringBuffer sb = GedcomToString.preparePrint(this, 1, false, true, true);
		
		if (sb.length() == 0) {
			sb.append("Empty block");
		}
		
		return sb.toString();
	}

	
	@Override
	public GedcomBlock getStartBlock() {
		return this;
	}
	
	@Override
	public GedcomBlock getFollowingBlock() {
		return this;
	}

	
	@Override
	public boolean hasChildLine(String tagOrStructureName) {
		return hasChildLine(tagOrStructureName, null, 0);
	}

	
	@Override
	public boolean hasChildLine(String tagOrStructureName, int lineNumber) {
		return hasChildLine(tagOrStructureName, null, lineNumber);
	}

	
	@Override
	public boolean hasChildLine(String structureName, String tag) {
		return hasChildLine(structureName, tag, 0);
	}

	
	@Override
	public boolean hasChildLine(String structureName, String tag, int lineNumber) {
		
		String name = structureName;
		
		if (tag != null) {
			name = structureName + "-" + tag;
		}
		
		if (!idToLineLinks.containsKey(name)) {
			return false;
		}
		
		if (idToLineLinks.get(name).size() <= lineNumber) {
			return false;
		}
		
		return true;
		
	}
	
	
}