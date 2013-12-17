/**
 * 
 */
package ch.thn.gedcom.data;

/**
 * An error which occurred while accessing gedcom objects.
 * 
 * @author thomas
 *
 */
public class GedcomAccessError extends GedcomError {
	private static final long serialVersionUID = 2159417452645645856L;
	
	/**
	 * 
	 * 
	 * @param message The error message
	 */
	public GedcomAccessError(String message) {
		super(message);
	}
	

}
