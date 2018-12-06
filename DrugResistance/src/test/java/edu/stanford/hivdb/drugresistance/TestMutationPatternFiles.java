/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.drugresistance;

import java.io.InputStream;
import edu.stanford.hivdb.drugs.DrugClass;

/**
 * Converts each of the MutationPatternFiles to an input stream so that they can be used for
 * j-unit testing.
 *
 */
public class TestMutationPatternFiles {

	public enum TestMutationPatterns {
		INSTI_PATTERNS(DrugClass.INSTI, "MutationPatternsFiles/PatternsINSTI.txt"),
		NRTI_PATTERNS(DrugClass.NRTI, "MutationPatternsFiles/PatternsNRTI.txt"),
		NNRTI_PATTERNS(DrugClass.NNRTI, "MutationPatternsFiles/PatternsNNRTI.txt"),
		PI_PATTERNS(DrugClass.PI, "MutationPatternsFiles/PatternsPI.txt");

		private final DrugClass drugClass;
	    private final String filePath;
	    
	    private TestMutationPatterns(final DrugClass drugClass, final String filePath) {
	    	this.drugClass = drugClass;
	    	this.filePath = filePath;
	    }

	    public DrugClass getDrugClass() {
	    	return this.drugClass;
	    }

	    @Override public String toString() { return filePath; }
	}

	public static InputStream getTestMutationPatternsInputStream(TestMutationPatterns testMutationPatterns) {
		return TestMutationPatternFiles.class.getClassLoader().getResourceAsStream(testMutationPatterns.filePath);
	}
}