/**
 * 
 */
package ch.thn.gedcom.printer;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.printer.HTMLTreePrinter;
import ch.thn.util.tree.printer.TreePrinter;

/**
 * This gedcom data printer prints the HTML code to view the gedcom structure 
 * as HTML file, for example in a web browser.
 * 
 * @author thomas
 *
 */
public class GedcomStructureHTMLPrinter extends HTMLTreePrinter<String, GedcomLine> {

	
	public GedcomStructureHTMLPrinter(String treeTitle) {
		//Do not use colors since the tree lines are not shown anyways
		super(treeTitle, false, true, true, null);
		
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
