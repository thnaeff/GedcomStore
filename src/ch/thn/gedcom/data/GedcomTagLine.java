/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.gedcom.data;

import java.util.HashSet;
import java.util.LinkedHashSet;

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.store.GedcomStoreLine;


/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomTagLine extends GedcomLine {
	
	private String tag1 = null;
	private String tag2 = null;
	private String xref = null;
	private String value = null;	
	
	private boolean isValueSet = false;
	private boolean isXRefSet = false;
	

	/**
	 * 
	 * 
	 * @param storeLine
	 * @param tag
	 */
	public GedcomTagLine(GedcomStoreLine storeLine, String tag) {
		super(storeLine, tag);
				
		if (getStoreLine().hasTagBeforeXRef()) {
			tag1 = tag;
		} else {
			tag2 = tag;
		}
		
	}
	
	
	@Override
	public String getTag() {
		if (tag1 != null) {
			return tag1;
		} else {
			return tag2;
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
	
	/**
	 * Sets the value field of this tag line
	 * 
	 * @param value
	 * @return <code>null</code> if setting the value failed
	 */
	public GedcomTagLine setValue(String value) {
		if (!getStoreLine().hasValueNames()) {
			throw new GedcomAccessError("Line " + getTag() + " in " + 
					getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					" does not have an value-field.");
		}
		
		GedcomStoreLine storeLine = getStoreLine();
		
		HashSet<String> possibleValues = storeLine.getValuePossibilities();
		
		if (possibleValues.size() > 0 && !possibleValues.contains(value)) {
			throw new GedcomAccessError(value + " is not a possible value for line " + 
					getTag() + " in " + storeLine.getParentBlock().getStoreStructure().getStructureName() + 
					". Possible values are: " + GedcomFormatter.makeOrList(possibleValues, "", ""));
		}
		
		GedcomDataValidator validator = storeLine.getParentBlock().getStoreStructure().getStore().getValidator();
		LinkedHashSet<String> valueNames = storeLine.getValueNames();
		
		if (validator != null) {
			boolean ok = true;
			
			for (String valueName : valueNames) {
				if (!validator.validateValue(this, valueName, value)) {
					ok = false;
				} else {
					ok = true;
					break;
				}
			}
			
			if (!ok) {
				return null;
			}
		}
		
		this.value = value;
		isValueSet(true);
		return this;
	}
	
	/**
	 * Sets the xref field of this tag line
	 * 
	 * @param xref
	 * @return <code>null</code> if setting the xref failed
	 */
	public GedcomTagLine setXRef(String xref) {
		if (!getStoreLine().hasXRefNames()) {
			throw new GedcomAccessError("[ERROR] Line " + getTag() + " in " + 
					getStoreLine().getParentBlock().getStoreStructure().getStructureName() + 
					" does not have an xref-field.");
		}
		
		GedcomStoreLine storeLine = getStoreLine();
		
		GedcomDataValidator validator = storeLine.getParentBlock().getStoreStructure().getStore().getValidator();
		LinkedHashSet<String> xrefNames = storeLine.getXRefNames();
		
		if (validator != null) {
			boolean ok = true;
			
			for (String xrefName : xrefNames) {
				if (!validator.validateXRef(this, xrefName, xref)) {
					ok = false;
				} else {
					ok = true;
					break;
				}
			}
			
			if (!ok) {
				return null;
			}
		}
		
		this.xref = xref;
		isXRefSet(true);
		return this;
	}
	
	
	/**
	 * Clears all the values of this tag line and resets the flag which indicates 
	 * if a value for this line has been set.
	 */
	public void clear() {
		xref = null;
		value = null;
		isValueSet(false);
		isXRefSet(false);
	}
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	@Override
	public String getId() {
		return getTag();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//TAG before XREF
		if (getStoreLine().hasTagBeforeXRef()) {
			sb.append(getTag());
		}
		
		// XREF
		if (requiresXRef()) {
			if (sb.length() > 0) {
				sb.append(DELIM);
			}
			
			sb.append("@" + xref + "@");
		}
		
		//TAG after XREF
		if (getStoreLine().hasTagAfterXRef()) {
			if (sb.length() > 0) {
				sb.append(DELIM);
			}
			
			sb.append(getTag());
		}
		
		//VALUE
		if (requiresValue()) {
			if (sb.length() > 0) {
				sb.append(DELIM);
			}
			
			sb.append(value);
		}
				
		
		return sb.toString();
	}


	@Override
	public boolean isTagLine() {
		return true;
	}


	@Override
	public GedcomTagLine getAsTagLine() {
		return this;
	}
	

}
