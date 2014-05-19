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

import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureLine extends GedcomLine {
		

	/**
	 * 
	 * 
	 * @param storeLine
	 * @param tag
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, String tag) {
		super(storeLine, tag);
		
		if (!storeLine.hasStructureName()) {
			throw new GedcomAccessError("The store line " + storeLine.getId() + 
					" is not a structure line");
		}
		
	}
	
	
	public String getStructureName() {
		return getStoreLine().getStructureName();
	}
	
	
	@Override
	public String getId() {
		return getStructureName();
	}
	
	
	@Override
	public String toString() {
		if (getTag() != null) {
			return getStructureName() + " (" + getTag() + ")";
		} else {
			return getStructureName();
		}
	}


	@Override
	public boolean isStructureLine() {
		return true;
	}


	@Override
	public GedcomStructureLine getAsStructureLine() {
		return this;
	}

}
