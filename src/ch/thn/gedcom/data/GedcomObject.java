/**
 * 
 */
package ch.thn.gedcom.data;

import java.util.ArrayList;
import java.util.Arrays;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.store.GedcomStoreStructure;

/**
 * This class holds one block of the lineage linked grammar.<br>
 * <br>
 * The following example shows a part of the lineage-linked definition of the 
 * header structure.
 * <pre>
 * HEADER:=
 *  n HEAD
 *    +1 SOUR &lt;APPROVED_SYSTEM_ID&gt;
 *      +2 VERS &lt;VERSION_NUMBER&gt;
 *      +2 NAME &lt;NAME_OF_PRODUCT&gt;
 *      +2 CORP &lt;NAME_OF_BUSINESS&gt;
 *        +3 &lt;&lt;ADDRESS_STRUCTURE&gt;&gt;
 *      +2 DATA &lt;NAME_OF_SOURCE_DATA&gt;
 *        +3 DATE &lt;PUBLICATION_DATE&gt;
 *        +3 COPR &lt;COPYRIGHT_SOURCE_DATA&gt;
 *          +4 [CONT|CONC]&lt;COPYRIGHT_SOURCE_DATA&gt;
 *    +1 DEST &lt;RECEIVING_SYSTEM_NAME&gt;
 *    +1 DATE &lt;TRANSMISSION_DATE&gt;
 *      +2 TIME &lt;TIME_VALUE&gt;
 *    +1 SUBM @&lt;XREF:SUBM&gt;@
 * </pre>
 * 
 * Divided in blocks, this structure looks somewhat like the following diagram. It shows 
 * that Block 0 has the line HEAD and since it is the first line, no parent line. 
 * Block 1 has the lines SOUR, DEST, DATE and SUBM and the parent line HEAD etc.
 * <pre>
 * HEADER:=
 * |--------------------------------------------------------------Block 0|
 * |n HEAD                                                               |
 * | |-------------------------------------------------------Block 1|    |
 * | |+1 SOUR &lt;APPROVED_SYSTEM_ID&gt;                                  |    |
 * | | |----------------------------------------------Block 2.1|    |    |
 * | | |+2 VERS &lt;VERSION_NUMBER&gt;                               |    |    |
 * | | |+2 NAME &lt;NAME_OF_PRODUCT&gt;                              |    |    |
 * | | |+2 CORP &lt;NAME_OF_BUSINESS&gt;                             |    |    |
 * | | | |---------------------------------------Block 3.1|    |    |    |
 * | | | |+3 &lt;&lt;ADDRESS_STRUCTURE&gt;&gt;                        |    |    |    |
 * | | | |------------------------------------------------|    |    |    |
 * | | |+2 DATA &lt;NAME_OF_SOURCE_DATA&gt;                          |    |    |
 * | | | |---------------------------------------Block 3.2|    |    |    |
 * | | | |+3 DATE &lt;PUBLICATION_DATE&gt;                      |    |    |    |
 * | | | |+3 COPR &lt;COPYRIGHT_SOURCE_DATA&gt;                 |    |    |    |
 * | | | | |-------------------------------------Block 4| |    |    |    |
 * | | | | |+4 [CONT|CONC]&lt;COPYRIGHT_SOURCE_DATA&gt;       | |    |    |    |
 * | | | | |--------------------------------------------| |    |    |    |
 * | | | |------------------------------------------------|    |    |    |
 * | | |-------------------------------------------------------|    |    |
 * | |+1 DEST &lt;RECEIVING_SYSTEM_NAME&gt;                               |    |
 * | |+1 DATE &lt;TRANSMISSION_DATE&gt;                                   |    |
 * | | |----------------------------------------------Block 2.2|    |    |
 * | | |+2 TIME &lt;TIME_VALUE&gt;                                   |    |    |
 * | | |-------------------------------------------------------|    |    |
 * | |+1 SUBM @&lt;XREF:SUBM&gt;@                                         |    |
 * | |--------------------------------------------------------------|    |
 * |---------------------------------------------------------------------|
 * </pre>
 * 
 * The class {@link GedcomStoreStructure} has additional information about how 
 * the parsed lineage-linked grammar is stored internally.
 * 
 * @author thomas
 */
public abstract class GedcomObject {
	
	/** The delimiter for multiple step values used in {@link #followPath(String...)} **/
	public static final String PATH_OPTION_DELIMITER = ";";
		
	/**
	 * Returns the ID of this object. The ID can either be the tag name (if it is 
	 * a tag line) or the structure name (if it is a structure line).<br>
	 * If this is a block, the ID is the one from its parent line. If there is 
	 * no parent line, the block is the top block of a structure and the structure name is returned.
	 * 
	 * @return
	 */
	public abstract String getId();
	
	/**
	 * Returns the level of the line.<br>
	 * If this is a block, the level is the one from its parent line.
	 * 
	 * @return
	 */
	public abstract int getLevel();
	
	/**
	 * Returns the parent line under which this object is located
	 * 
	 * @return
	 */
	public abstract GedcomLine getParentLine();
	
	/**
	 * Returns the parent block which contains this object
	 * 
	 * @return The parent block, or <code>null</code> if there is no parent block
	 */
	public abstract GedcomBlock getParentBlock();
	
	/**
	 * Returns the the child line of this object. If there is more than one line 
	 * available, the first line is returned (equal to {@link #getChildLine(String, int)} 
	 * with lineNumber=0).<br>
	 * <br>
	 * If this object is a {@link GedcomLine}, this method is equal to 
	 * {@link #getChildBlock()}.{@link #getChildLine(String)}
	 * 
	 * @param tagOrStructureName 
	 * @return
	 */
	public abstract GedcomLine getChildLine(String tagOrStructureName);
	
	/**
	 * Returns the the child line of this object.<br>
	 * <br>
	 * If this object is a {@link GedcomLine}, this method is equal to 
	 * {@link #getChildBlock()}.{@link #getChildLine(String, int)}
	 * 
	 * @param tagOrStructureName
	 * @param lineNumber
	 * @return
	 */
	public abstract GedcomLine getChildLine(String tagOrStructureName, int lineNumber);
	
	/**
	 * Returns the the child line of this object.<br>
	 * <br>
	 * If this object is a {@link GedcomLine}, this method is equal to 
	 * {@link #getChildBlock()}.{@link #getChildLine(String, String)}
	 * 
	 * @param structureName
	 * @param tag
	 * @return
	 */
	public abstract GedcomLine getChildLine(String structureName, String tag);
	
	/**
	 * Returns the the child line of this object.<br>
	 * <br>
	 * If this object is a {@link GedcomLine}, this method is equal to 
	 * {@link #getChildBlock()}.{@link #getChildLine(String, String, int)}
	 * 
	 * @param structureName
	 * @param tag
	 * @param lineNumber
	 * @return The gedcom line, or null if that line does not exist
	 */
	public abstract GedcomLine getChildLine(String structureName, String tag, int lineNumber);
	
	/**
	 * Returns <code>true</code> if this gedcom object has a child line with the 
	 * given tag or structure name
	 * 
	 * @param tagOrStructureName
	 * @return
	 */
	public abstract boolean hasChildLine(String tagOrStructureName);
	
	/**
	 * Returns <code>true</code> if this gedcom object has a child line with the 
	 * given tag or structure name and the given line number
	 * 
	 * @param tagOrStructureName
	 * @param lineNumber
	 * @return
	 */
	public abstract boolean hasChildLine(String tagOrStructureName, int lineNumber);
	
	/**
	 * Returns <code>true</code> if this gedcom object has a child line with the 
	 * given structure name and tag name combination (structure variation)
	 * 
	 * @param structureName
	 * @param tag
	 * @return
	 */
	public abstract boolean hasChildLine(String structureName, String tag);
	
	/**
	 * Returns <code>true</code> if this gedcom object has a child line with the 
	 * given structure name and tag name combination (structure variation) and 
	 * the given line number
	 * 
	 * @param structureName
	 * @param tag
	 * @param lineNumber
	 * @return
	 */
	public abstract boolean hasChildLine(String structureName, String tag, int lineNumber);
	
	
	/**
	 * Returns true if the structure with the given structure name has one or 
	 * more variations
	 * 
	 * @param structureName
	 * @return
	 */
	public boolean structureHasVariations(String structureName) {
		return getStartBlock().getStoreBlock().getStoreStructure().getStore().structureHasVariations(structureName);
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
		if (!getFollowingBlock().getStoreBlock().hasStoreLine(name)) {
			return false;
		}
		
		return getFollowingBlock().getStoreBlock().getStoreLine(name).hasStructureName();
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
		if (!getFollowingBlock().getStoreBlock().hasStoreLine(name)) {
			return false;
		}
		
		return getFollowingBlock().getStoreBlock().getStoreLine(name).hasTags();	
	}
	
	/**
	 * Returns true if the current object can have child lines as defined in the 
	 * lineage linked grammar
	 * 
	 * @return
	 */
	public boolean canHaveChildren() {
		return (getFollowingBlock() != null);
	}
	
	/**
	 * Returns <code>true</code> if this object is a {@link GedcomLine} 
	 * ({@link GedcomTagLine} and {@link GedcomStructureLine} are both 
	 * {@link GedcomTagLine}s)
	 * 
	 * @return
	 */
	public boolean isLine() {
		return false;
	}
	
	/**
	 * Returns <code>true</code> if this object is a {@link GedcomTagLine}
	 * 
	 * @return
	 */
	public boolean isTagLine() {
		return false;
	}
	
	/**
	 * Returns <code>true</code> if this object is a {@link GedcomBlock}
	 * 
	 * @return
	 */
	public boolean isBlock() {
		return false;
	}
	
	/**
	 * Returns <code>true</code> if this object is a {@link GedcomStructureLine}
	 * 
	 * @return
	 */
	public boolean isStructureLine() {
		return false;
	}
	
	/**
	 * Returns this object as {@link GedcomTagLine}, but only if it is a 
	 * {@link GedcomTagLine}. Otherwise, a {@link GedcomAccessError} is 
	 * thrown.
	 * 
	 * @return
	 * @throws GedcomAccessError
	 */
	public GedcomTagLine getAsTagLine() {
		throw new GedcomAccessError(getId() + " is not a tag line");
	}
	
	/**
	 * Returns this object as {@link GedcomStructureLine}, but only if it is a 
	 * {@link GedcomStructureLine}. Otherwise, a {@link GedcomAccessError} is 
	 * thrown.
	 * 
	 * @return
	 * @throws GedcomAccessError
	 */
	public GedcomStructureLine getAsStructureLine() {
		throw new GedcomAccessError(getId() + " is not a structure line");
	}
	
	/**
	 * Returns this object as {@link GedcomBlock}, but only if it is a 
	 * {@link GedcomBlock}. Otherwise, a {@link GedcomAccessError} is 
	 * thrown.
	 * 
	 * @return
	 * @throws GedcomAccessError
	 */
	public GedcomBlock getAsBlock() {
		throw new GedcomAccessError(getId() + " is not a block");
	}
	
	/**
	 * Returns this object as {@link GedcomLine}, but only if it is a 
	 * {@link GedcomLine}. Otherwise, a {@link GedcomAccessError} is 
	 * thrown.
	 * 
	 * @return
	 */
	public GedcomLine getAsLine() {
		throw new GedcomAccessError(getId() + " is not a line");
	}
	
	/**
	 * Returns a starting point for following the path, which means that if this 
	 * object is a {@link GedcomBlock} it returns itself, and if it is 
	 * a {@link GedcomLine} it returns its parent block.
	 * 
	 * @return
	 */
	public abstract GedcomBlock getStartBlock();
	
	/**
	 * Returns the block which can be used to deal with the following lines. This 
	 * means that if this object is a {@link GedcomBlock} it returns itself, and 
	 * if it is a {@link GedcomLine} it returns its child block.
	 * 
	 * @return
	 */
	public abstract GedcomBlock getFollowingBlock();
	
	/**
	 * Follows the path given with <code>path</code>. Each array position describes 
	 * one path step, and each step can contain multiple values describing the 
	 * step. The following two lines each show one step in the path with multiple 
	 * values, separated by {@value #PATH_OPTION_DELIMITER}:<br>
	 * - "structure name;tag;with xref;with value;line number"<br>
	 * - "structure name;tag;with xref;with value"<br>
	 * - "tag or structure name;line number"<br>
	 * ("with xref" and "with value" have to be given as "true" or "false")<br>
	 * <br>
	 * If multiple step values are given, they have to be separated with the 
	 * {@link #PATH_OPTION_DELIMITER}. Multiple step values are needed if the 
	 * next path step can not be identified with one step value only. A tag line 
	 * for example can be added multiple times, thus when accessing that line, the 
	 * tag and the line number have to be given. Also, some structures exist in 
	 * different variations (with/without xref, with/without value, ...) and might 
	 * have to be accessed with multiple values for one path step.<br>
	 * If a path can not be followed, this method throws an {@link GedcomPathAccessError} 
	 * with an error text and the path which caused the error. The error text might 
	 * give a hint to what has gone wrong.
	 * 
	 * @param path The path to follow. If pieces of the path are not yet created, 
	 * it will try to create them
	 * @return The {@link GedcomObject} of the last object in the path
	 * @throws GedcomPathAccessError If following the given path is not possible
	 * @throws GedcomCreationError If new path pieces have to be created but they 
	 * can not be created (because of invalid structure/tag names, ...)
	 */
	public GedcomObject followPath(String... path) {
		return followPath(false, false, path);
	}
	
	/**
	 * Follows the path given with <code>path</code>. Each array position describes 
	 * one path step, and each step can contain multiple values describing the 
	 * step. The following two lines each show one step in the path with multiple 
	 * values, separated by {@value #PATH_OPTION_DELIMITER}:<br>
	 * - "structure name;tag;with xref;with value;line number"<br>
	 * - "structure name;tag;with xref;with value"<br>
	 * - "tag or structure name;line number"<br>
	 * ("with xref" and "with value" have to be given as "true" or "false")<br>
	 * <br>
	 * If multiple step values are given, they have to be separated with the 
	 * {@link #PATH_OPTION_DELIMITER}. Multiple step values are needed if the 
	 * next path step can not be identified with one step value only. A tag line 
	 * for example can be added multiple times, thus when accessing that line, the 
	 * tag and the line number have to be given. Also, some structures exist in 
	 * different variations (with/without xref, with/without value, ...) and might 
	 * have to be accessed with multiple values for one path step.<br>
	 * If a path can not be followed, this method throws an {@link GedcomPathAccessError} 
	 * with an error text and the path which caused the error. The error text might 
	 * give a hint to what has gone wrong.
	 * 
	 * @param addNew If set to <code>true</code>, a new path is created. 
	 * <code>addNewAt</code> defines how and where the split has to be done.
	 * @param path The path to follow. If pieces of the path are not yet created, 
	 * it will try to create them
	 * @return The {@link GedcomObject} of the last object in the path
	 * @throws GedcomPathAccessError If following the given path is not possible
	 * @throws GedcomCreationError If new path pieces have to be created but they 
	 * can not be created (because of invalid structure/tag names, ...)
	 */
	public GedcomObject followPath(boolean addNew, String... path) {
		return followPath(addNew, false, path);
	}
	
	
	/**
	 * Follows the path given with <code>path</code>. Each array position describes 
	 * one path step, and each step can contain multiple values describing the 
	 * step. The following three lines each show one step in the path with multiple 
	 * values, separated by {@value #PATH_OPTION_DELIMITER}:<br>
	 * - "structure name;tag;with xref;with value;line number"<br>
	 * - "structure name;tag;with xref;with value"<br>
	 * - "tag or structure name;line number"<br>
	 * ("with xref" and "with value" have to be given as "true" or "false")<br>
	 * <br>
	 * If multiple step values are given, they have to be separated with the 
	 * {@link #PATH_OPTION_DELIMITER}. Multiple step values are needed if the 
	 * next path step can not be identified with one step value only. A tag line 
	 * for example can be added multiple times, thus when accessing that line, the 
	 * tag and the line number have to be given. Also, some structures exist in 
	 * different variations (with/without xref, with/without value, ...) and might 
	 * have to be accessed with multiple values for one path step.<br>
	 * If a path can not be followed, this method throws an {@link GedcomPathAccessError} 
	 * with an error text and the path which caused the error. The error text might 
	 * give a hint to what has gone wrong.
	 * 
	 * @param addNew If set to <code>true</code>, a new path is created.
	 * @param silent Just suppresses the ">>" access output
	 * @param path The path to follow. If pieces of the path are not yet created, 
	 * it will try to create them
	 * @return The {@link GedcomObject} of the last object in the path
	 * @throws GedcomPathAccessError If following the given path is not possible
	 * @throws GedcomCreationError If new path pieces have to be created but they 
	 * can not be created (because of invalid structure/tag names, ...)
	 */
	protected GedcomObject followPath(boolean addNew, boolean silent, String... path) {
		GedcomObject o = (GedcomObject)getFollowingBlock();
		GedcomObject previousObject = o;
		GedcomLine lastLineWithSplit = null;
		ArrayList<String> addNewPath = new ArrayList<String>();
		String pathPiece = null;
		int lineNumber = 0;
		boolean added = false;

		if (!silent && showAccessOutput()) {
			System.out.println(getId() + " >> " + Arrays.toString(path) + ": " + (addNew ? "new" : "follow"));
		}
		
		if (o == null || path == null || path.length == 0) {
			return this;
		}
		
		//Go through the whole given path
		for (int currentPathIndex = 0; currentPathIndex < path.length; currentPathIndex++) {
			if (!o.canHaveChildren()) {
				//Nothing else to do, the path ends here
				throw new GedcomPathAccessError(path, pathPiece + " can not have any children");
			}
			
			lineNumber = 0;
			
			String[] parts = path[currentPathIndex].split(PATH_OPTION_DELIMITER);
			
			if (parts.length <= 0) {
				continue;
			}
			
			pathPiece = parts[0];
			
			if (pathPiece.length() == 0) {
				continue;
			}
			
			previousObject = o;
			
			if (o.nameIsPossibleStructure(pathPiece)) {
				PathObject po = followStructureLine(pathPiece, parts, lineNumber, path, o, 
						previousObject, currentPathIndex);
				o = po.o;
				
				if (po.added) {
					added = true;
				}
			} else if (o.nameIsPossibleTag(pathPiece)) {
				PathObject po = followTagLine(pathPiece, parts, lineNumber, path, o, 
						previousObject, currentPathIndex);
				o = po.o;
				
				if (po.added) {
					added = true;
				}
			} else {
				throw new GedcomPathAccessError(path, "Path piece " + pathPiece + 
						" does not exist. Possible line id's: " + 
						GedcomFormatter.makeOrList(previousObject.getFollowingBlock().getLineIds(), "", ""));
			}
			
			//Keep a record of the last line where splitting (adding another line) 
			//would be possible.
			//If a new line has already been added in the path, do not care about this any more
			if (addNew) {
				if (!added && o.isLine() && (o.getAsLine().getMaxNumberOfLines() == 0 
						|| o.getAsLine().getNumberOfLines() < o.getAsLine().getMaxNumberOfLines())) {				
					lastLineWithSplit = o.getAsLine();
					addNewPath.clear();
				} else {
					//Start recording the path one path piece after the line in lastLineWithSplit, 
					//so that followPath can be called on the newly created line
					if (lastLineWithSplit != null) {
						addNewPath.add(path[currentPathIndex]);
					}
				}
			}
			
			
		}
		
		//A new line is needed and no line has been added yet. This means that 
		//the path we followed already existed -> a new line has to be added at 
		//the last possible split point
		if (addNew && !added) {
			//A new line should be added but there is no location in the path 
			//to add a new line
			if (lastLineWithSplit == null) {
				throw new GedcomPathAccessError(path, "A new path " + Arrays.toString(path) + 
						" should be added but the given path seems to have reached its line number limits.");
			}
						
			GedcomLine newLine = null;
			
			if (showAccessOutput()) {
				System.out.println(GedcomFormatter.makeInset(lastLineWithSplit.getParentLine().getLevel()) + 
						"< " + lastLineWithSplit.getParentLine().getId());
			}
			
			//Add a new line and continue following the path from that new line on
			if (lastLineWithSplit.isStructureLine()) {
				newLine = lastLineWithSplit.getParentBlock().addStructureLine(
						lastLineWithSplit.getAsStructureLine().getStructureName(), 
						lastLineWithSplit.getAsStructureLine().getStructureVariationTag(), 
						lastLineWithSplit.getAsStructureLine().hasStructureVariationXRef(), 
						lastLineWithSplit.getAsStructureLine().hasStructureVariationValue());
			} else {
				newLine = lastLineWithSplit.getParentBlock().addTagLine(
						lastLineWithSplit.getAsTagLine().getTag());
			}
			
			if (addNewPath.size() > 0) {
				return newLine.followPath(false, true, addNewPath.toArray(new String[addNewPath.size()]));
			} else {
				return newLine;
			}
			
		}
		
		return o;
	}
	
	
	/**
	 * 
	 * 
	 * @param pathPiece
	 * @param parts
	 * @param lineNumber
	 * @param path
	 * @param o
	 * @param previousObject
	 * @param currentPathIndex
	 * @return
	 */
	private PathObject followStructureLine(String pathPiece, String[] parts, int lineNumber, 
			String[] path, GedcomObject o, GedcomObject previousObject, int currentPathIndex) {
		String tagExtension = null;
		boolean useXRefValue = false;
		boolean withXRef = false;
		boolean withValue = false;
		boolean added = false;
		
		
		//Parse the step values
		if (parts.length > 1) {
			//Tag
			tagExtension = parts[1];
			
			if (parts.length > 2) {
				//withXRef
				withXRef = Boolean.parseBoolean(parts[2]);
				
				useXRefValue = true;
				
				if (parts.length > 3) {
					//withValue
					withValue = Boolean.parseBoolean(parts[3]);
					
					if (parts.length > 4) {
						//Line number
						if (parts[4].length() > 0) {
							try {
								lineNumber = Integer.parseInt(parts[4]);
							} catch (NumberFormatException e) {
								throw new GedcomPathAccessError(path, "Last part of the path piece " + path[currentPathIndex] + 
										" is not empty and not a number. Failed to parse line number.");
							}	
						}
					}
					
				}
			}
		}
		
		if (o.hasChildLine(pathPiece, tagExtension, lineNumber)) {
			o = o.getChildLine(pathPiece, tagExtension, lineNumber);
		} else {
			//The line number starts with 0 for the first line, which means if 
			//there are 5 lines, a lineNumber=5 is the 6th (the next) line.
			if (o.getFollowingBlock().getNumberOfLines(pathPiece, tagExtension) != lineNumber) {
				throw new GedcomPathAccessError(path, "Line number " + lineNumber + 
						" is too high. There are only " + o.getFollowingBlock().getNumberOfLines(pathPiece) + 
						" lines of " + pathPiece + " available.");
			}
			
			GedcomBlock b = o.getFollowingBlock();
			
			if (useXRefValue) {
				o = b.addStructureLine(pathPiece, tagExtension, withXRef, withValue);
			} else if (tagExtension != null && tagExtension.length() > 0) {
				o = b.addStructureLine(pathPiece, tagExtension);
			} else {
				o = b.addStructureLine(pathPiece);
			}
			
			if (o == null) {
				String tagString = "";
				
				if (tagExtension != null && tagExtension.length() > 0) {
					tagString = "-" + tagExtension;
				}
				
				throw new GedcomPathAccessError(path, "Structure " + pathPiece + tagString + 
						" can not be accessed/added. Possible line id's: " + 
						GedcomFormatter.makeOrList(previousObject.getFollowingBlock().getLineIds(), "", ""));
			}
			
			added = true;
		}
		
		return new PathObject(o, added);
	}
	
	/**
	 * 
	 * 
	 * @param pathPiece
	 * @param parts
	 * @param lineNumber
	 * @param path
	 * @param o
	 * @param previousObject
	 * @param currentPathIndex
	 * @return
	 */
	private PathObject followTagLine(String pathPiece, String[] parts, int lineNumber, 
			String[] path, GedcomObject o, GedcomObject previousObject, int currentPathIndex) {
		boolean added = false;
		
		if (parts.length > 1) {
			//Line number
			if (parts[1].length() > 0) {
				try {
					lineNumber = Integer.parseInt(parts[1]);
				} catch (NumberFormatException e) {
					throw new GedcomPathAccessError(path, "Last part of the path piece " + 
							path[currentPathIndex] + 
							" is not empty and not a number. Failed to parse line number " + parts[1]);
				}	
			}
		}
		
		if (o.hasChildLine(pathPiece, lineNumber)) {
			o = o.getChildLine(pathPiece, lineNumber);
		} else {
			
			//The line number starts with 0 for the first line, which means if 
			//there are 5 lines, a lineNumber=5 is the 6th (the next) line.
			if (o.getFollowingBlock().getNumberOfLines(pathPiece) != lineNumber) {
				throw new GedcomPathAccessError(path, "Line number " + lineNumber + 
						" is too high. There are only " + o.getFollowingBlock().getNumberOfLines(pathPiece) + 
						" lines of " + pathPiece + " available.");
			}
			
			GedcomBlock b = o.getFollowingBlock();
			
			o = b.addTagLine(pathPiece);
			
			if (o == null) {
				throw new GedcomPathAccessError(path, "Tag line " + pathPiece + 
						" can not be accessed/added. Possible line id's: " + 
						GedcomFormatter.makeOrList(previousObject.getFollowingBlock().getLineIds(), "", ""));
			}
			
			added = true;
		}
		
		return new PathObject(o, added);
	}
	
	
	/**
	 * Checks if there should be a console output when navigating through the 
	 * structures by using methods line goToParent, setValue, goToChildBlock etc. 
	 * Showing this output can help with the construction of correct gedcom structures.
	 * 
	 * @return
	 */
	protected boolean showAccessOutput() {
		if (isBlock()) {
			return getAsBlock().getStoreBlock().getStoreStructure().getStore().showAccessOutput();
		} else {
			return getAsLine().getStoreLine().getParentBlock().getStoreStructure().getStore().showAccessOutput();
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * @author thomas
	 *
	 */
	private class PathObject {
		
		private GedcomObject o = null;
		
		private boolean added = false;
		
		
		public PathObject(GedcomObject o, boolean added) {
			this.o = o;
			this.added = added;
		}
		
	}

}
