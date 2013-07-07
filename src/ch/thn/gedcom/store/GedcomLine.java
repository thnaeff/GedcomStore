/**
 * 
 */
package ch.thn.gedcom.store;

/**
 * A {@link GedcomLine} represents on line of a {@link GedcomBlock}. Each line 
 * has a parent block and a child block. The parent block is the block which contains 
 * this line, and the child block contains all the child lines of this line.<br>
 * <br>
 * See {@link GedcomObject} for an example structure with blocks and lines.
 * 
 * 
 * @author thomas
 *
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
	
	
	protected void setChildBlock(GedcomBlock childBlock) {
		this.childBlock = childBlock;
	}
	
	protected GedcomStoreLine getStoreLine() {
		return storeLine;
	}
	
	
	
	public GedcomTagLine setValue(String value) {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).setValue(value);
		} else {
			System.out.println("[ERROR] Can not set a value for the structure line " + this.getId());
			return null;
		}
	}
	
	public GedcomTagLine setXRef(String xref) {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).setXRef(xref);
		} else {
			System.out.println("[ERROR] Can not set an xref for the structure line " + this.getId());
			return null;
		}
	}
	
	public String getValue() {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).getValue();
		} else {
			System.out.println("[ERROR] Can not return a value from the structure line " + this.getId());
			return null;
		}
	}
	
	public String getXRef() {
		if (this instanceof GedcomTagLine) {
			return ((GedcomTagLine)this).getXRef();
		} else {
			System.out.println("[ERROR] Can not return an xref from the structure line " + this.getId());
			return null;
		}
	}
	
	@Override
	public GedcomBlock getParentBlock() {
		return parentBlock;
	}
	
	public GedcomBlock getBlock() {
		return childBlock;
	}
	
	@Override
	public GedcomLine getParentLine() {
		if (parentBlock == null) {
			return null;
		}
		
		return parentBlock.getParentLine();
	}
	
	
	public abstract String getId();
	
	public abstract int getLevel();
	
	public boolean hasChildLines() {
		return (childBlock != null);
	}
	
	public abstract void clear();
	
	public int getMinNumberOfLines() {
		return storeLine.getMin();
	}
	
	public int getMaxNumberOfLines() {
		return storeLine.getMax();
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
			return null;
		}
		
		return childBlock.getChildLine(structureName, tag, lineNumber);
	}
	
	
	@Override
	protected GedcomBlock getStartBlock() {
		return parentBlock;
	}
	
	@Override
	protected GedcomBlock getFollowingBlock() {
		return childBlock;
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
