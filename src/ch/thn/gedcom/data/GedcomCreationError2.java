/**
 * 
 */
package ch.thn.gedcom.data;

/**
 * Just a wrapper for {@link GedcomCreationError}. This class is rarely used. 
 * Only for example if a gedcom creation error has to be passed on upwards 
 * and there needs to be a distinction.
 * 
 * @author thomas
 *
 */
public class GedcomCreationError2 extends GedcomCreationError {
	private static final long serialVersionUID = 6527964880640797747L;


	/**
	 * 
	 * 
	 * @param message
	 */
	public GedcomCreationError2(String message) {
		super(message);
	}

}
