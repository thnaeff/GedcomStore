/**
 * 
 */
package ch.thn.gedcom.store;

/**
 * 
 * 
 * The following example shows a part of the lineage-linked definition of the 
 * header structure.
 * <pre><code>
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
 * </code></pre>
 * 
 * Divided in blocks, it looks somewhat like the following diagram. It shows 
 * that Block 0 has the line HEAD and since it is the first line, no parent line. 
 * Block 1 has the lines SOUR, DEST, DATE and SUBM and the parent line HEAD etc.
 * <pre><code>
 * HEADER:=
 * |-----------------------------------------------------------------------Block 0|
 * |n HEAD									|
 * | |-------------------------------------------------------------Block 1|	|
 * | |+1 SOUR &lt;APPROVED_SYSTEM_ID&gt;					|	|
 * | | |-------------------------------------------------Block 2.1|	|	|
 * | | |+2 VERS &lt;VERSION_NUMBER&gt;					|	|	|
 * | | |+2 NAME &lt;NAME_OF_PRODUCT&gt;					|	|	|
 * | | |+2 CORP &lt;NAME_OF_BUSINESS&gt;				|	|	|
 * | | | |---------------------------------------Block 3.1|	|	|	|
 * | | | |+3 &lt;&lt;ADDRESS_STRUCTURE&gt;&gt;			|	|	|	|
 * | | | |------------------------------------------------|	|	|	|
 * | | |+2 DATA &lt;NAME_OF_SOURCE_DATA&gt;				|	|	|
 * | | | |---------------------------------------Block 3.2|	|	|	|
 * | | | |+3 DATE &lt;PUBLICATION_DATE&gt;			|	|	|	|
 * | | | |+3 COPR &lt;COPYRIGHT_SOURCE_DATA&gt;			|	|	|	|
 * | | | | |-------------------------------Block 4|	|	|	|	|
 * | | | | |+4 [CONT|CONC]&lt;COPYRIGHT_SOURCE_DATA&gt;	|	|	|	|	|
 * | | | | |--------------------------------------|	|	|	|	|
 * | | | |------------------------------------------------|	|	|	|
 * | | |----------------------------------------------------------|	|	|
 * | |+1 DEST &lt;RECEIVING_SYSTEM_NAME&gt;					|	|
 * | |+1 DATE &lt;TRANSMISSION_DATE&gt;						|	|
 * | | |-------------------------------------------------Block 2.2|	|	|
 * | | |+2 TIME &lt;TIME_VALUE&gt;					|	|	|
 * | | |----------------------------------------------------------|	|	|
 * | |+1 SUBM @&lt;XREF:SUBM&gt;@						|	|
 * | |--------------------------------------------------------------------|	|
 * |------------------------------------------------------------------------------|
 * </code></pre>
 * 
 * 
 * @author thomas
 *
 */
public abstract class GedcomObject {
	
	public static final String PATH_OPTION_DELIMITER = ";";
		
	
	/**
	 * Returns the parent line under which this object is located
	 * 
	 * @return
	 */
	public abstract GedcomLine getParentLine();
	
	/**
	 * Returns the parent block which contains this object
	 * 
	 * @return
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
	 * @return
	 */
	public abstract GedcomLine getChildLine(String structureName, String tag, int lineNumber);
	
	
	public abstract boolean hasChildLine(String tagOrStructureName);
	
	public abstract boolean hasChildLine(String tagOrStructureName, int lineNumber);
	
	public abstract boolean hasChildLine(String structureName, String tag);
	
	public abstract boolean hasChildLine(String structureName, String tag, int lineNumber);
	
	
	
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
	 * Returns true if this object is a {@link GedcomTagLine}
	 * 
	 * @return
	 */
	public boolean isTagLine() {
		return false;
	}
	
	/**
	 * Returns this object as {@link GedcomTagLine}, but only if it is a 
	 * {@link GedcomTagLine}. Otherwise, null is returned.
	 * 
	 * @return
	 */
	public GedcomTagLine getTagLine() {
		if (isTagLine()) {
			return (GedcomTagLine)this;
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a starting point for following the path, which means that if this 
	 * object is a {@link GedcomBlock} it returns itself, and if it is 
	 * a {@link GedcomLine} it returns its parent block.
	 * 
	 * @return
	 */
	protected abstract GedcomBlock getStartBlock();
	
	/**
	 * Returns the block which can be used to deal with the following lines. This 
	 * means that if this object is a {@link GedcomBlock} it returns itself, and 
	 * if it is a {@link GedcomLine} it returns its child block.
	 * 
	 * @return
	 */
	protected abstract GedcomBlock getFollowingBlock();
	
	
	
	
	/**
	 * 
	 * "structure name;line number;tag;with xref;with value"<br>
	 * "tag or structure name;line number"
	 * 
	 * @param path
	 * @return
	 */
	public GedcomObject followPath(String... path) {
		GedcomObject o = (GedcomObject)getStartBlock();
		GedcomObject previousObject = o;
		String pathPiece = null;
		String tagExtension = null;
		boolean useXRefValue = false;
		boolean withXRef = false;
		boolean withValue = false;
		int lineNumber = 0;
		
		for (int currentPathIndex = 0; currentPathIndex < path.length; currentPathIndex++) {
			if (!o.canHaveChildren()) {
				System.out.println("[ERROR] " + pathPiece + " can not have any children. Returning " + pathPiece);
				return o;
			}
			
			tagExtension = null;
			useXRefValue = false;
			withXRef = false;
			withValue = false;
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
				//=== Continue with a structure line ===
				
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
										System.out.println("[ERROR] Last part of the path piece " + path[currentPathIndex] + 
												" is not empty and not a number. Failed to parse line number.");
										return null;
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
						System.out.println("[ERROR] Line number " + lineNumber + 
								" is too high. There are only " + o.getFollowingBlock().getNumberOfLines(pathPiece) + 
								" lines of " + pathPiece + " available.");
						return null;
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
						
						System.out.println("[ERROR] Structure " + pathPiece + tagString + 
								" does not exist. Possible line id's: " + 
								GedcomFormatter.makeOrList(previousObject.getFollowingBlock().getAvailableLines(), "", ""));
						return null;
					}
				}
				
			} else if (o.nameIsPossibleTag(pathPiece)) {
				//=== Continue with a tag line ===
				
				if (parts.length > 1) {
					//Line number
					if (parts[1].length() > 0) {
						try {
							lineNumber = Integer.parseInt(parts[1]);
						} catch (NumberFormatException e) {
							System.out.println("[ERROR] Last part of the path piece " + path[currentPathIndex] + 
									" is not empty and not a number. Failed to parse line number.");
							return null;
						}	
					}
				}
				
				if (o.hasChildLine(pathPiece, lineNumber)) {
					o = o.getChildLine(pathPiece, lineNumber);
				} else {
					
					//The line number starts with 0 for the first line, which means if 
					//there are 5 lines, a lineNumber=5 is the 6th (the next) line.
					if (o.getFollowingBlock().getNumberOfLines(pathPiece) != lineNumber) {
						System.out.println("[ERROR] Line number " + lineNumber + 
								" is too high. There are only " + o.getFollowingBlock().getNumberOfLines(pathPiece) + 
								" lines of " + pathPiece + " available.");
						return null;
					}
					
					GedcomBlock b = o.getFollowingBlock();
					
					o = b.addTagLine(pathPiece);
					
					if (o == null) {
						System.out.println("[ERROR] Tag line " + pathPiece + 
								" does not exist. Possible line id's: " + 
								GedcomFormatter.makeOrList(previousObject.getFollowingBlock().getAvailableLines(), "", ""));
						return null;
					}
				}
				
			} else {
				System.out.println("[ERROR] Path piece " + pathPiece + 
						" does not exist. Possible line id's: " + 
						GedcomFormatter.makeOrList(previousObject.getFollowingBlock().getAvailableLines(), "", ""));
				return null;
			}
			
			
			
			
			
			
		}
		
		
		
		return o;
	}
	
	
	
	

}
