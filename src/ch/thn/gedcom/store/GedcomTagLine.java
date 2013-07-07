/**
 * 
 */
package ch.thn.gedcom.store;

/**
 * @author thomas
 *
 */
public class GedcomTagLine extends GedcomLine {
	
	private String tag1 = null;
	private String tag2 = null;
	private String xref = null;
	private String value = null;	
	
	private boolean isValueSet = false;
	

	/**
	 * @param storeLine
	 * @param tag
	 */
	public GedcomTagLine(GedcomStoreLine storeLine, GedcomBlock parentBlock, String tag, int copyMode) {
		
		super(storeLine, parentBlock);
		
		if (storeLine.hasTagBeforeXRef()) {
			tag1 = tag;
		} else {
			tag2 = tag;
		}
		
		
		if (storeLine.hasChildBlock()) {
			//Copy mandatory or all lines if necessary
			if (copyMode == GedcomBlock.COPY_MODE_MANDATORY && storeLine.getChildBlock().hasMandatoryLines()) {
				//Copy mandatory
				setChildBlock(new GedcomBlock(storeLine.getChildBlock(), this, null, copyMode));
			} else if (copyMode == GedcomBlock.COPY_MODE_ALL) {
				//Copy all
				setChildBlock(new GedcomBlock(storeLine.getChildBlock(), this, null, copyMode));
			} else {
				//Add empty block
				setChildBlock(new GedcomBlock(storeLine.getChildBlock(), this, copyMode));
			}
		}
		
		
	}
	
	protected void isValueSet(boolean isValueSet) {
		this.isValueSet = isValueSet;
	}
	
	
	
	
	public String getTag() {
		if (tag1 != null) {
			return tag1;
		} else {
			return tag2;
		}
	}
	
	
	@Override
	public String getId() {
		return getTag();
	}
	
	
	@Override
	public int getLevel() {
		if (getParentLine() == null) {
			return 0;
		}
		
		return getParentLine().getLevel() + 1;
	}
	
	public String getXRef() {
		return xref;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isValueSet() {
		return isValueSet;
	}
	
	public boolean requiresXRef() {
		return getStoreLine().hasXRefNames();
	}
	
	public boolean requiresValue() {
		return getStoreLine().hasValueNames();
	}
	
	public boolean hasTagBeforeXRef() {
		return (tag1 != null);
	}
	
	public boolean hasTagAfterXRef() {
		return (tag2 != null);
	}
	
	public GedcomTagLine setValue(String value) {
		if (!getStoreLine().hasValueNames()) {
			System.out.println("[ERROR] Line " + getTag() + " under " + getParentLine().getId() + 
					" in " + getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					" does not have an value-field.");
			return this;
		}
		
		this.value = value;
		isValueSet(true);
		return this;
	}
	
	public GedcomTagLine setXRef(String xref) {
		if (!getStoreLine().hasXRefNames()) {
			System.out.println("[ERROR] Line " + getTag() + " under " + getParentLine().getId() + 
					" in " + getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					" does not have an xref-field.");
			return this;
		}
		
		this.xref = xref;
		isValueSet(true);
		return this;
	}
	
	@Override
	public void clear() {
		xref = null;
		value = null;
		isValueSet(false);
	}
	
	
	@Override
	public boolean isTagLine() {
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = GedcomPrinter.preparePrint(this);
		
		if (sb.length() == 0) {
			sb.append("Empty tag line " + getTag());
		}
		
		return sb.toString();
	}
	
}
