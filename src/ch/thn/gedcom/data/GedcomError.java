/**
 * 
 */
package ch.thn.gedcom.data;

/**
 * @author thomas
 *
 */
public class GedcomError extends Error {
	private static final long serialVersionUID = 4253655083501757676L;

	public GedcomError(String message) {
		super(message);
	}

}
