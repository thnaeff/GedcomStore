/**
 * 
 */
package ch.thn.gedcom.store;

/**
 * @author thomas
 *
 */
public class GedcomStructureLine extends GedcomLine {
		

	private String structureVariationTag = null;
	
	
	/**
	 * 
	 * @param storeLine
	 * @param parentBlock
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, int copyMode) {
		super(storeLine, parentBlock);
				
		GedcomBlock childBlock = storeLine.getParentBlock().getStoreStructure().getStore().getGedcomBlock(this, storeLine.getStructureName(), null, copyMode, false, false, false);
		
		setChildBlock(childBlock);
		
	}
	
	/**
	 * 
	 * @param storeLine
	 * @param parentBlock
	 * @param tag
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, String structureVariationTag, int copyMode) {
		super(storeLine, parentBlock);
		this.structureVariationTag = structureVariationTag;
		
		GedcomBlock childBlock = storeLine.getParentBlock().getStoreStructure().getStore().getGedcomBlock(this, storeLine.getStructureName(), structureVariationTag, copyMode, false, false, false);
		
		setChildBlock(childBlock);
	}
	
	/**
	 * 
	 * @param storeLine
	 * @param parentBlock
	 * @param tag
	 * @param withXRef
	 * @param withValue
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, String structureVariationTag, boolean withXRef, boolean withValue, int copyMode) {
		super(storeLine, parentBlock);
		this.structureVariationTag = structureVariationTag;
		
		GedcomBlock childBlock = storeLine.getParentBlock().getStoreStructure().getStore().getGedcomBlock(this, storeLine.getStructureName(), structureVariationTag, copyMode, true, withXRef, withValue);
		
		setChildBlock(childBlock);
	}
	
	
	public String getStructureVariationTag() {
		return structureVariationTag;
	}
	
	@Override
	public String getId() {
		return getStoreLine().getStructureName();
	}
	
	@Override
	public int getLevel() {
		//Skip structure lines when calculating the level
		return getParentLine().getLevel();
	}
	
	@Override
	public void clear() {
		
	}
	
	@Override
	public String toString() {
		StringBuffer sb = GedcomPrinter.preparePrint(this);
		
		if (sb.length() == 0) {
			sb.append("Empty structure line " + getStoreLine().getStructureName());
		}
		
		return sb.toString();
	}
	

}
