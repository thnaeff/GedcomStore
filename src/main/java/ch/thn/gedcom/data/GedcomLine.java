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
public abstract class GedcomLine {
	
	/**
	 * The delimiter between level numbers and tags, tags and ranges, etc. 
	 */
	public static final String DELIM = " ";
	

	private GedcomStoreLine storeLine = null;
	
	private String tag = null;
	
	/**
	 * 
	 * 
	 * @param storeLine
	 * @param tag
	 */
	public GedcomLine(GedcomStoreLine storeLine, String tag) {
		this.storeLine = storeLine;
		this.tag = tag;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public abstract String getId();
	
	/**
	 * Returns the unique ID of this line. This is used for sorting/avoiding 
	 * duplicates
	 * 
	 * @return
	 */
	protected abstract String getUniqueId();
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomStoreLine getStoreLine() {
		return storeLine;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isTagLine() {
		return false;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomTagLine getAsTagLine() {
		throw new IllegalAccessError("This is not a " + GedcomTagLine.class.getSimpleName());
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isStructureLine() {
		return false;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomStructureLine getAsStructureLine() {
		throw new IllegalAccessError("This is not a " + GedcomStructureLine.class.getSimpleName());
	}
	
	
	@Override
	public String toString() {
		return storeLine.getId() + " (" + tag + ")";
	}
	
}
