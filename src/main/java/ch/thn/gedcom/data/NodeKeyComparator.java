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
public class NodeKeyComparator implements Comparator<NodeKey> {

	@Override
	public int compare(NodeKey key1, NodeKey key2) {
		
		/* A note from earlier problems:
		 * -----------------------------
		 * 
		 * When looking for an entry with containsKey, it searches 
		 * for that key only in a range which makes sense. For example in the 
		 * map with the following ordering by OrderString:
		 * 
		 * 02SEX
		 * 03INDIVIDUAL_EVENT_STRUCTURE
		 * 07SPOUSE_TO_FAMILY_LINK
		 * 17CHANGE_DATE
		 * 
		 * When looking for SEX using only the string SEX (comparing the keys
		 * without the ordering), it starts comparing with SPOUSE_TO_FAMILY_LINK 
		 * (which gives a negative number with compareTo), then it compares 
		 * INDIVIDUAL_EVENT_STRUCTURE (which gives a positive number with compareTo). 
		 * The switch from the negative compareTo result to the positive result 
		 * indicates that SEX must be between those two other entries (since it 
		 * is a sorted list) and it stops looking. This results in containsKey 
		 * returning FALSE when looking up SEX.
		 */

		
		return key1.getOrderingString().compareTo(key2.getOrderingString());
	}

}
