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

import ch.thn.gedcom.GedcomFormatter;
import ch.thn.gedcom.data.GedcomLine;
import ch.thn.util.tree.PrintableTreeNode;
import ch.thn.util.tree.printer.GenericTreePrinter;
import ch.thn.util.tree.printer.TreePrinter;

/**
 * A printer which prints the gedcom structure in text format. The output 
 * of this printer can be saved in a gedcom textfile to import the gedcom data 
 * into a software which supports the gedcom standard.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureTextPrinter extends GenericTreePrinter<String, GedcomLine> {

	
	public GedcomStructureTextPrinter() {
		super(
				"", 	//Head
				"", 	//Start
				"", 	//End
				"", 	//Intermediate
				"", 	//Through
				"", 	//After end
				"",		//Additional line through
				"",		//Additional line after end
				"", 	//Start of line
				TreePrinter.LINE_SEPARATOR, 	//End of line
				true, true);
		
	}
	
	@Override
	public StringBuilder getNodeValue(PrintableTreeNode<String, GedcomLine> node) {
		StringBuilder sb = new StringBuilder();
		int level = node.getAsTreeNode().getNodeLevel(true);
		sb.append(GedcomFormatter.makeInset(level));
		sb.append(level);
		sb.append(" ");
		sb.append(node.print());
		return sb;
	}
	
	
}
