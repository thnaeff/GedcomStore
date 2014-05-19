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
import ch.thn.gedcom.data.GedcomNode;
import ch.thn.util.tree.printer.text.DebugTextTreePrinter;

/**
 * A printer which is very useful for debugging a gedcom tree when building it. It 
 * prints the whole tree with lines connecting the nodes and additional information 
 * like the line index and child index.
 * 
 * @author Thomas Naeff (github.com/thnaeff)
 * 
 */
public class GedcomStructureTreePrinter extends DebugTextTreePrinter<GedcomLine, GedcomNode> {
	
	
	
	@Override
	public StringBuilder print(GedcomNode printNode) {
		return super.print(printNode);
	}
	
	
}
