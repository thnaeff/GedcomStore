/**
 * 
 */
package ch.thn.gedcom.data;

import java.util.HashSet;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.GedcomToString;
import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * A tag line has one tag name can hold values and xrefs. The tag name can either 
 * appear before the xref/value field or after.
 * 
 * @author thomas
 * @see GedcomObject
 */
public class GedcomTagLine extends GedcomLine {
	
	private String tag1 = null;
	private String tag2 = null;
	private String xref = null;
	private String value = null;	
	
	private boolean isValueSet = false;
	private boolean isXRefSet = false;
	

	/**
	 * Creates a new tag line out ot the given store line and with the given tag 
	 * and parent block
	 * 
	 * @param storeLine
	 * @param parentBlock
	 * @param tag
	 * @param copyMode
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
			if (copyMode == GedcomBlock.ADD_MANDATORY && storeLine.getChildBlock().hasMandatoryLines()) {
				//Copy mandatory
				setChildBlock(new GedcomBlock(storeLine.getChildBlock(), this, null, copyMode));
			} else if (copyMode == GedcomBlock.ADD_ALL) {
				//Copy all
				setChildBlock(new GedcomBlock(storeLine.getChildBlock(), this, null, copyMode));
			} else {
				//Add empty block
				setChildBlock(new GedcomBlock(storeLine.getChildBlock(), this, copyMode));
			}
		}
		
		
	}
	
	/**
	 * Set this flag to true if the value of this line has been set. A flag is 
	 * needed here because setting the value to <code>NULL</code> is also considered 
	 * as "setting the value".
	 * 
	 * @param isValueSet
	 */
	protected void isValueSet(boolean isValueSet) {
		this.isValueSet = isValueSet;
	}
	
	/**
	 * Set this flag to true if the xref of this line has been set. A flag is 
	 * needed here because setting the xref to <code>NULL</code> is also considered 
	 * as "setting the xref".
	 * 
	 * @param isValueSet
	 */
	protected void isXRefSet(boolean isXRefSet) {
		this.isXRefSet = isXRefSet;
	}
	
	/**
	 * Returns the tag name of this line
	 * 
	 * @return
	 */
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
	
	/**
	 * Returns the xref of this line
	 * 
	 */
	public String getXRef() {
		return xref;
	}
	
	/**
	 * Returns the value of this line
	 * 
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns <code>true</code> if the line (value and xref) is empty (<code>NULL</code> or with 
	 * a length of 0). It does not check if a value/xref has actually been set. 
	 * Use {@link #isValueSet()} to check for that.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return isValueEmpty() && isXRefEmpty();
	}
	
	/**
	 * Returns <code>true</code> if the value on this line is empty (<code>NULL</code> or with 
	 * a length of 0). It does not check if a value has actually been set. 
	 * Use {@link #isValueSet()} to check for that.
	 * 
	 * @return
	 */
	public boolean isValueEmpty() {
		return (value == null) || (value != null && value.length() == 0);
	}
	
	/**
	 * Returns <code>true</code> if the xref on this line is empty (<code>NULL</code> or with 
	 * a length of 0). It does not check if a xref has actually been set. 
	 * Use {@link #isValueSet()} to check for that.
	 * 
	 * @return
	 */
	public boolean isXRefEmpty() {
		return (xref == null) || (xref != null && xref.length() == 0);
	}
	
	/**
	 * Returns <code>true</code> if the value of this line has been set at least 
	 * once. Setting the value to <code>null</code> is also considered as 
	 * setting the value. Only {@link #clear()} resets the value-set flag.
	 * 
	 * @return
	 */
	public boolean isValueSet() {
		return isValueSet;
	}
	
	/**
	 * Returns <code>true</code> if the xref of this line has been set at least 
	 * once. Setting the xref to <code>null</code> is also considered as 
	 * setting the xref. Only {@link #clear()} resets the xref-set flag.
	 * 
	 * @return
	 */
	public boolean isXRefSet() {
		return isXRefSet;
	}
	
	/**
	 * Checks if an xref is required for this line (The xref is required if this 
	 * line has a xref field)
	 * 
	 * @return
	 */
	public boolean requiresXRef() {
		return getStoreLine().hasXRefNames();
	}
	
	/**
	 * Checks if a value is required for this line (The value is required if this 
	 * line has a value field)
	 * 
	 * @return
	 */
	public boolean requiresValue() {
		return getStoreLine().hasValueNames();
	}
	
	/**
	 * Checks if for this line, the tag name has to appear before the xref. This 
	 * is needed when printing the line because value and xref have to be in 
	 * the right order.
	 * 
	 * @return
	 */
	public boolean hasTagBeforeXRef() {
		return (tag1 != null);
	}
	
	/**
	 * Checks if for this line, the tag name has to appear after the xref. This 
	 * is needed when printing the line because value and xref have to be in 
	 * the right order.
	 * 
	 * @return
	 */
	public boolean hasTagAfterXRef() {
		return (tag2 != null);
	}
	
	@Override
	public GedcomTagLine setValue(String value) {
		if (!getStoreLine().hasValueNames()) {
			throw new GedcomAccessError("Line " + getTag() + " under " + getParentLine().getId() + 
					" in " + getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					" does not have an value-field.");
		}
		
		HashSet<String> possibleValues = getStoreLine().getValuePossibilities();
		
		if (possibleValues.size() > 0 && !possibleValues.contains(value)) {
			throw new GedcomAccessError(value + " is not a possible value for line " + 
					getTag() + " under " + getParentLine().getId() + 
					" in " + getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					". Possible values are: " + GedcomFormatter.makeOrList(possibleValues, "", ""));
		}
		
		if (showAccessOutput()) {
			System.out.println(getId() + ": " + this.value + " => " + value);
		}
		
		this.value = value;
		isValueSet(true);
		return this;
	}
	
	@Override
	public GedcomTagLine setXRef(String xref) {
		if (!getStoreLine().hasXRefNames()) {
			throw new GedcomAccessError("[ERROR] Line " + getTag() + " under " + getParentLine().getId() + 
					" in " + getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					" does not have an xref-field.");
		}
		
		if (showAccessOutput()) {
			System.out.println(getId() + ": " + this.xref + " => " + xref);
		}
		
		this.xref = xref;
		isXRefSet(true);
		return this;
	}
	
	@Override
	public void clear() {
		xref = null;
		value = null;
		isValueSet(false);
		isXRefSet(false);
	}
	
	@Override
	public boolean isLine() {
		return true;
	}
	
	@Override
	public boolean isTagLine() {
		return true;
	}
	
	@Override
	public GedcomTagLine getAsTagLine() {
		return this;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = GedcomToString.preparePrint(this);
		
		if (sb.length() == 0) {
			sb.append("Empty tag line " + getTag());
		}
		
		return sb.toString();
	}
	
}
