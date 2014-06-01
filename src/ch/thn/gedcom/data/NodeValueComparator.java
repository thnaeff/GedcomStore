/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
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
package ch.thn.gedcom.data;

import java.util.Comparator;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class NodeValueComparator implements Comparator<GedcomNode> {

	@Override
	public int compare(GedcomNode node1, GedcomNode node2) {
		return node1.getNodeValue().getUniqueId().compareTo(node2.getNodeValue().getUniqueId());
	}

}
