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

package edu.stanford.hivdb.filetestutils;

import java.io.InputStream;

public class TestMutationsFiles {

	public enum TestMutationsProperties
	{
		SIMPLE_MUTATIONS_TEST("test_mutation_lists/AlgTestMutations.txt", true),
		SDRM_TEST_MUTATIONS("test_mutation_lists/SdrmTestMutations.txt", true),
		TSM_TEST_MUTATIONS("test_mutation_lists/TsmTestMutations.txt", true),
		APOBEC_TEST("test_mutation_lists/ApobecTestMutations.txt", true),
		VALIDATION_TEST("test_mutation_lists/Validation.txt", true),
		MUTATION_SET_UTILS_TEST("test_mutation_lists/MutationSetUtilsTestMutations.txt", true),
		MIXTURE_AT_DRP("test_mutation_lists/MixtureAtDRP.txt", true);
	    public final String propertyName;
	    public final boolean forRoutineTesting;

	    private TestMutationsProperties(final String propertyName, final boolean forRoutineTesting) {
	    	this.propertyName = propertyName;
	    	this.forRoutineTesting = forRoutineTesting;
	    }

	    @Override public String toString() { return propertyName; }
	}

	public static InputStream getTestMutationsInputStream(TestMutationsProperties testMutationsProperties)
	{
		return TestMutationsFiles.class.getClassLoader().getResourceAsStream(testMutationsProperties.propertyName);
	}

}
