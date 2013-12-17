/**
 * 
 */
package ch.thn.gedcom.store;

/**
 * Shows an error that occurred when parsing the grammar linked file. 
 * 
 * @author thomas
 *
 */
public class GedcomParseException extends Exception {
	private static final long serialVersionUID = -9166944783238433522L;

	/**
	 * 
	 * 
	 * @param message
	 */
	public GedcomParseException(String message) {
		super(message);
	}
	
}
