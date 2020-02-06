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

package edu.stanford.hivdb.drugresistance.mutationpattern;

import java.io.InputStream;


/**
 * Converts each of the MutationPatternFiles to an input stream so that they can be used for
 * j-unit testing.
 *
 */
//public class TestMutationPatternFiles {
//
//	public enum TestMutationPatterns {
//		INSTI_PATTERNS(HIVDrugClass.INSTI, "MutationPatternsFiles/PatternsINSTI.txt"),
//		NRTI_PATTERNS(HIVDrugClass.NRTI, "MutationPatternsFiles/PatternsNRTI.txt"),
//		NNRTI_PATTERNS(HIVDrugClass.NNRTI, "MutationPatternsFiles/PatternsNNRTI.txt"),
//		PI_PATTERNS(HIVDrugClass.PI, "MutationPatternsFiles/PatternsPI.txt");
//
//		private final HIVDrugClass drugClass;
//	    private final String filePath;
//
//	    private TestMutationPatterns(final HIVDrugClass drugClass, final String filePath) {
//	    	this.drugClass = drugClass;
//	    	this.filePath = filePath;
//	    }
//
//	    public HIVDrugClass getDrugClass() {
//	    	return this.drugClass;
//	    }
//
//	    @Override public String toString() { return filePath; }
//	}
//
//	public static InputStream getTestMutationPatternsInputStream(TestMutationPatterns testMutationPatterns) {
//		return TestMutationPatternFiles.class.getClassLoader().getResourceAsStream(testMutationPatterns.filePath);
//	}
//}
