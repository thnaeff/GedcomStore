/**
 * 
 */
package ch.thn.gedcom.data;

import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * A {@link GedcomLine} represents on line of a {@link GedcomBlock}. Each line 
 * has a parent block and a child block. The parent block is the block which contains 
 * this line, and the child block contains all the child lines of this line.<br>
 * <br>
 * See {@link GedcomObject} for an example structure with blocks and lines.
 * 
 * 
 * @author thomas
 * @see GedcomObject
 */
public abstract class GedcomLine extends GedcomObject {
	
		
	private GedcomStoreLine storeLine = null;
	
	private GedcomBlock parentBlock = null;
	private GedcomBlock childBlock = null;
		
	/**
	 * A {@link GedcomLine} represents on line of a {@link GedcomBlock}. Each line 
	 * has a parent block and a child block. The parent block is the block which contains 
 	 * this line, and the child block contains all the child lines of this line.
 	 * 
	 * @param storeLine
	 * @param parentBlock
	 */
	public GedcomLine(GedcomStoreLine storeLine, GedcomBlock parentBlock) {
		this.storeLine = storeLine;
		this.parentBlock = parentBlock;

	}
	
	/**
	 * Sets the child block for this line
	 * 
	 * @param childBlock
	 */
	protected void setChildBlock(GedcomBlock childBlock) {
		this.childBlock = childBlock;
	}
	
	/**
	 * Returns the store line which contains all the parsed information for 
	 * this line
	 * 
	 * @return
	 */
	public GedcomStoreLine getStoreLine() {
		return storeLine;
	}
	
	
	/**
	 * Sets the value for this line. If a value can not be set for this line, 
	 * for example if it is a structure line or if the line does not have a value 
	 * name, a {@link GedcomAccessError} is thrown.
	 * 
	 * @param value
	 * @return
	 */
	public GedcomTagLine setValue(String value) {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).setValue(value);
		} else {
			throw new GedcomAccessError("This is not a tag line. Can not set a value for the structure line " + this.getId());
		}
	}
	
	/**
	 * Sets the xref for this line. If a xref can not be set for this line, 
	 * for example if it is a structure line or if the line does not have a xref 
	 * name, a {@link GedcomAccessError} is thrown.
	 * 
	 * @param xref
	 * @return
	 */
	public GedcomTagLine setXRef(String xref) {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).setXRef(xref);
		} else {
			throw new GedcomAccessError("This is not a tag line. Can not set an xref for the structure line " + this.getId());
		}
	}
	
	/**
	 * Returns the value which is set for this line. If a value can not be set for this line, 
	 * for example if it is a structure line or if the line does not have a value 
	 * name, a {@link GedcomAccessError} is thrown.
	 * 
	 * @return
	 */
	public String getValue() {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).getValue();
		} else {
			throw new GedcomAccessError("This is not a tag line. Can not return a value from the structure line " + this.getId());
		}
	}
	
	/**
	 * Returns the xref which is set for this line. If a xref can not be set for this line, 
	 * for example if it is a structure line or if the line does not have a xref 
	 * name, a {@link GedcomAccessError} is thrown.
	 * 
	 * @return
	 */
	public String getXRef() {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).getXRef();
		} else {
			throw new GedcomAccessError("This is not a tag line. Can not return " +
					"an xref from the structure line " + this.getId());
		}
	}
	
	public GedcomBlock getChildBlock() {
		return childBlock;
	}
	
	@Override
	public GedcomBlock getParentBlock() {
		return parentBlock;
	}
	
	@Override
	public GedcomLine getParentLine() {
		if (parentBlock == null) {
			return null;
		}
		
		return parentBlock.getParentLine();
	}
	
	/**
	 * Checks if this line has any child lines
	 * 
	 * @return
	 */
	public boolean hasChildLines() {
		return (childBlock != null);
	}
	
	/**
	 * Clears all the values of this line and resets the flag which indicates 
	 * if a value for this line has been set.
	 * 
	 */
	public abstract void clear();
	
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
	
	/**
	 * Counts how many of this lines are already added to its block
	 * 
	 * @return
	 */
	public int getNumberOfLines() {
		if (isTagLine()) {
			return getParentBlock().getNumberOfLines(getAsTagLine().getTag());
		} else {
			return getParentBlock().getNumberOfLines(getAsStructureLine().getStructureName(), 
					getAsStructureLine().getStructureVariationTag());
		}
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
	public GedcomLine getChildLine(String structureName, String tag,
			int lineNumber) {
		if (childBlock == null) {
			throw new GedcomAccessError("This " + getId() + " line does not have any child lines added.");
		}
		
		return childBlock.getChildLine(structureName, tag, lineNumber);
	}
	
	
	@Override
	public GedcomBlock getStartBlock() {
		return parentBlock;
	}
	
	@Override
	public GedcomBlock getFollowingBlock() {
		return childBlock;
	}
	
	@Override
	public boolean isLine() {
		return true;
	}
	
	@Override
	public GedcomLine getAsLine() {
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
		if (childBlock == null) {
			return false;
		}
		
		return childBlock.hasChildLine(structureName, tag, lineNumber);
	}
	
	
}
