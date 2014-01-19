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

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class GedcomPathCreationError extends GedcomError {
	private static final long serialVersionUID = -3725454512049357388L;
	
	
	private String[] accessPath = null;
    
    private int pathIndex = 0;
    
    /**
     *
     *
     * @param accessPath The path which caused the error
     * @param pathIndex The index of the path where the error occurred
     * @param message The error message
     */
    public GedcomPathCreationError(String[] accessPath, int pathIndex, String message) {
            super(message);
            this.pathIndex = pathIndex;
            this.accessPath = accessPath;
    }
    
    /**
     * 
     * 
     * @return
     */
    public int getPathIndex() {
    	return pathIndex;
    }
    
    /**
     * The path which caused the error
     *
     * @return
     */
    public String[] getAccessPath() {
            return accessPath;
    }
    
}
