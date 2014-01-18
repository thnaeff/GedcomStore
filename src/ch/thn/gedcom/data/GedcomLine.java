/**
 * 
 */
package ch.thn.gedcom.data;

import ch.thn.gedcom.store.GedcomStoreLine;

/**
 * @author thomas
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
	 * @param parent
	 */
	public GedcomLine(GedcomStoreLine storeLine, String tag, GedcomNode parent) {
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
	
	public boolean isTagLine() {
		return false;
	}
	
	public GedcomTagLine getAsTagLine() {
		throw new IllegalAccessError("This is not a " + GedcomTagLine.class.getSimpleName());
	}
	
	public boolean isStructureLine() {
		return false;
	}
	
	public GedcomStructureLine getAsStructureLine() {
		throw new IllegalAccessError("This is not a " + GedcomStructureLine.class.getSimpleName());
	}
	
	
}
