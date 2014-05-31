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

import java.util.LinkedList;

import ch.thn.gedcom.data.GedcomLine;
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.util.tree.onoff.OnOffTreeUtil;
import ch.thn.util.tree.printer.html.HTMLTreePrinter;
import ch.thn.util.tree.printer.text.TextTreePrinterLines;
/**
 * This gedcom data printer prints the HTML code to view the gedcom structure 
 * as HTML file, for example in a web browser.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomStructureHTMLPrinter extends HTMLTreePrinter<GedcomLine, GedcomNode> {

	
	/**
	 * 
	 * 
	 * @param printerMode
	 * @param useColors
	 */
	public GedcomStructureHTMLPrinter(LeftRightTextPrinterMode printerMode, 
			boolean useColors, boolean showLines) {
		super(printerMode, false, useColors);
		
		if (!showLines) {
			HEAD = null;
			FIRST_CHILD = null;
			START = HTMLSPACE + HTMLSPACE + HTMLSPACE;
			END = HTMLSPACE + HTMLSPACE + HTMLSPACE;
			INTERMEDIATE = HTMLSPACE + HTMLSPACE + HTMLSPACE;
			THROUGH = HTMLSPACE + HTMLSPACE;
			AFTEREND = "";
			ADDITIONALLINETHROUGH = HTMLSPACE + HTMLSPACE;
			ADDITIONALLINEAFTEREND = null;
		}
				
	}
	
	

	@Override
	protected TextTreePrinterLines getNodeData(GedcomNode node) {
		TextTreePrinterLines lines = new TextTreePrinterLines();
		
		if (node.getNodeValue() != null) {
			int index = lines.addNewLine();
			lines.addValue(index, node.getNodeDepth() + HTMLSPACE + HTMLSPACE);
			lines.addValue(index, node.getNodeValue().toString());
		}
		
		return lines;
	}
	
	@Override
	public StringBuilder print(GedcomNode printNode) {
		LinkedList<GedcomNode> trees = OnOffTreeUtil.convertToSimpleTree(printNode, true, true);
		//There is only one tree since only the structure name is ignored and it 
		//continues with the first tag line which is not ignored
		return super.print(trees.get(0));
	}

	
}
