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
package ch.thn.gedcom.data;

import ch.thn.gedcom.store.GedcomStoreStructure;

/**
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomTree extends GedcomNode {
	
	private GedcomStoreStructure storeStructure = null;
		
	private String structureName = null;
	
	
	/**
	 * 
	 * 
	 * @param storeStructure
	 */
	public GedcomTree(GedcomStoreStructure storeStructure) {
		super(storeStructure);
		
		this.storeStructure = storeStructure;
		this.structureName = storeStructure.getStructureName();
		
		ignoreNode(true);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public GedcomStoreStructure getStoreStructure() {
		return storeStructure;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public String getStructureName() {
		return structureName;
	}
	
	
	@Override
	public GedcomNode nodeFactory(GedcomNode node) {
		//Factory method needed for the tree copy with OnOffTreeUtil
		return new GedcomTree(((GedcomTree)node).getStoreStructure());
	}
	
	
	@Override
	public String toString() {
		return structureName;
	}

}
