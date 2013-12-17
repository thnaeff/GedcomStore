/**
 * 
 */
package ch.thn.gedcom.data;

/**
 * An error which occurred while accessing gedcom objects. The error message contains 
 * an error description and the access path is the path which caused the error 
 * (retrieve the path with {@link #getAccessPath()}).
 * 
 * @author thomas
 *
 */
public class GedcomPathAccessError extends GedcomError {
	private static final long serialVersionUID = 2159417452645645856L;

	private String[] accessPath = null;
	
	/**
	 * 
	 * 
	 * @param accessPath The path which caused the error
	 * @param message The error message
	 */
	public GedcomPathAccessError(String[] accessPath, String message) {
		super(message);
		this.accessPath = accessPath;
	}
	
	/**
	 * The path which caused the error
	 * 
	 * @return
	 */
	public String[] getAccessPath() {
		return accessPath;
	}

}
