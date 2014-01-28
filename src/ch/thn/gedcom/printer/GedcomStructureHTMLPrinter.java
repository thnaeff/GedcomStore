/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureHTMLPrinter extends HTMLTreePrinter<String, GedcomLine> {

	
	public GedcomStructureHTMLPrinter(String treeTitle) {
		//Do not use colors since the tree lines are not shown anyways
		super(treeTitle, false, true, true);
		
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
