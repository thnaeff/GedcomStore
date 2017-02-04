/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
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

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public abstract class GedcomDataValidator {
	
	
	/**
	 * Is called just before a value is set on a {@link GedcomTagLine}.<br>
	 * <br>
	 * <i>Info:</i><br>
	 * It is theoretically possible (but unlikely with the current GEDCOM specifications) 
	 * that there is more than one value type for a 
	 * tag value (an or-list like [&lt;TYPE_1&gt; | &lt;TYPE_2&gt; | &lt;TYPE_3&gt;]. 
	 * If that is the case, then validateValue is called once for each type in the 
	 * list. Since it is an OR-list, validateValue has to return <code>true</code> 
	 * for at least one of the types in order for the value to be set. If 
	 * validateValue returns <code>false</code>, it is called for the next type 
	 * and if it returns <code>true</code>, no next type is validated and the 
	 * value is set.
	 * 
	 * @param tagLine
	 * @param valueName
	 * @param value
	 * @return
	 */
	public abstract boolean validateValue(GedcomTagLine tagLine, String valueName, String value);
	
	
	
	/**
	 * Is called just before a xref is set on a {@link GedcomTagLine}.<br>
	 * <br>
	 * <i>Info:</i><br>
	 * It is theoretically possible (but unlikely with the current GEDCOM specifications) 
	 * that there is more than one xref type for an 
	 * xref (an or-list like [&lt;TYPE_1&gt; | &lt;TYPE_2&gt; | &lt;TYPE_3&gt;]. 
	 * If that is the case, then validateXRef is called once for each type in the 
	 * list. Since it is an OR-list, validateXRef has to return <code>true</code> 
	 * for at least one of the types in order for the xref to be set. If 
	 * validateXRef returns <code>false</code>, it is called for the next type 
	 * and if it returns <code>true</code>, no next type is validated and the 
	 * xref is set.
	 * 
	 * 
	 * @param tagLine
	 * @param xrefName
	 * @param xref
	 * @return
	 */
	public abstract boolean validateXRef(GedcomTagLine tagLine, String xrefName, String xref);
	
	
}
