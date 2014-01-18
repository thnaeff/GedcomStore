/**
 * 
 */
package ch.thn.gedcom.data;

import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * @author thomas
 *
 */
public class GedcomStructureLine extends GedcomLine {
		

	/**
	 * 
	 * 
	 * @param storeLine
	 * @param tag
	 * @param parent
	 */
	public GedcomStructureLine(GedcomStoreLine storeLine, String tag, GedcomNode parent) {
		super(storeLine, tag, parent);
		
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
		return getStructureName();
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
