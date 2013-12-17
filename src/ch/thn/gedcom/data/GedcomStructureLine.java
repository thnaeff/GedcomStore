/**
 * 
 */
package ch.thn.gedcom.data;

import ch.thn.gedcom.GedcomToString;
import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * A structure line is a line with a structure name. A structure line does not 
 * have any value or xref fields because it only links to the child block 
 * which contains all the lines of the structure.
 * 
 * @author thomas
 * @see GedcomObject
 */
public class GedcomStructureLine extends GedcomLine {
		
	/** If this structure has variations, this tag is one of the identifiers **/
	private String structureVariationTag = null;
	/** If this structure has variations, this value flag is one of the identifiers **/
	private boolean structureVariationValue = false;
	/** If this structure has variations, this xref flag is one of the identifiers **/
	private boolean structureVariationXRef = false;

	
	/**
	 * Creates a new structure line with the given parent block, using the given 
	 * store line. The copy mode defines if none/mandatory/all lines should be 
	 * added automatically.
	 * 
	 * @param storeLine
	 * @param parentBlock
	 * @param copyMode
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, int copyMode) {
		super(storeLine, parentBlock);
				
		GedcomBlock childBlock = storeLine.getParentBlock().getStoreStructure().getStore()
				.getGedcomBlock(this, storeLine.getStructureName(), null, copyMode, false, false, false);
		
		setChildBlock(childBlock);
		
	}
	
	/**
	 * Creates a new structure line with the given parent block, using the given 
	 * store line. The copy mode defines if none/mandatory/all lines should be 
	 * added automatically.<br>
	 * <br>
	 * This constructor has to be used if the structure has multiple variations 
	 * and more information than only the structure name is needed to identify 
	 * the right structure.
	 * 
	 * @param storeLine
	 * @param parentBlock
	 * @param structureVariationTag
	 * @param copyMode
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, 
			String structureVariationTag, int copyMode) {
		super(storeLine, parentBlock);
		this.structureVariationTag = structureVariationTag;
		
		GedcomBlock childBlock = storeLine.getParentBlock().getStoreStructure().getStore()
				.getGedcomBlock(this, storeLine.getStructureName(), structureVariationTag, copyMode, false, false, false);
		
		setChildBlock(childBlock);
	}
	
	/**
	 * Creates a new structure line with the given parent block, using the given 
	 * store line. The copy mode defines if none/mandatory/all lines should be 
	 * added automatically.<br>
	 * <br>
	 * This constructor has to be used if the structure has multiple variations 
	 * and more information than only the structure name is needed to identify 
	 * the right structure.
	 * 
	 * @param storeLine
	 * @param parentBlock
	 * @param structureVariationTag
	 * @param withXRef
	 * @param withValue
	 * @param copyMode
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, 
			String structureVariationTag, boolean withXRef, boolean withValue, int copyMode) {
		super(storeLine, parentBlock);
		this.structureVariationTag = structureVariationTag;
		this.structureVariationValue = withValue;
		this.structureVariationXRef = withXRef;
		
		//Creates the block for this structure
		GedcomBlock childBlock = storeLine.getParentBlock().getStoreStructure()
				.getStore().getGedcomBlock(this, storeLine.getStructureName(), 
						structureVariationTag, copyMode, true, withXRef, withValue);
		
		setChildBlock(childBlock);
	}
	
	/**
	 * Returns the tag name which identifies this structure variation
	 * 
	 * @return
	 */
	public String getStructureVariationTag() {
		return structureVariationTag;
	}
	
	/**
	 * Returns <code>true</code> if this structure variation has a value field
	 * 
	 * @return
	 */
	public boolean hasStructureVariationValue() {
		return structureVariationValue;
	}
	
	/**
	 * Returns <code>true</code> if this structure variation has a xref field
	 * 
	 * @return
	 */
	public boolean hasStructureVariationXRef() {
		return structureVariationXRef;
	}
	
	/**
	 * Returns the name of this structure
	 * 
	 * @return
	 */
	public String getStructureName() {
		return getStoreLine().getStructureName();
	}
	
	@Override
	public boolean isLine() {
		return true;
	}
	
	@Override
	public boolean isStructureLine() {
		return true;
	}
	
	@Override
	public GedcomStructureLine getAsStructureLine() {
		return this;
	}
	
	@Override
	public String getId() {
		return getStructureName();
	}
	
	@Override
	public int getLevel() {
		//Skip structure lines when calculating the level
		return getParentLine().getLevel();
	}
	
	@Override
	public void clear() {
		//Nothing to clear in a structure line
	}
	
	@Override
	public String toString() {
		StringBuffer sb = GedcomToString.preparePrint(this);
		
		if (sb.length() == 0) {
			sb.append("Empty structure line " + getStoreLine().getStructureName());
		}
		
		return sb.toString();
	}
	

}
