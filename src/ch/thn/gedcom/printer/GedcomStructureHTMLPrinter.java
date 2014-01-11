/**
 * 
 */
package ch.thn.gedcom.printer;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.printer.HTMLTreePrinter;
import ch.thn.util.tree.printer.TreePrinter;

/**
 * @author thomas
 *
 */
public class GedcomStructureHTMLPrinter extends HTMLTreePrinter<String, GedcomLine> {

	
	public GedcomStructureHTMLPrinter(String treeTitle) {
		super(treeTitle, false, true, null);
		
		CONNECTOR_HEAD = "";
		CONNECTOR_START = "";
		CONNECTOR_END = "";
		CONNECTOR_INTERMEDIATE = "";
		CONNECTOR_THROUGH = "";
		CONNECTOR_AFTEREND = "";
		CONNECTOR_ADDITIONALLINETHROUGH = "";
		CONNECTOR_ADDITIONALLINEAFTEREND = "";
		START_OF_LINE = "<tr>";
		END_OF_LINE = "</tr>" + TreePrinter.LINE_SEPARATOR;
		
	}
	
}
