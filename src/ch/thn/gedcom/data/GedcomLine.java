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
	
	public abstract boolean isTagLine();
	
	public abstract GedcomTagLine getAsTagLine();
	
	public abstract boolean isStructureLine();
	
	public abstract GedcomStructureLine getAsStructureLine();
	
	
}
