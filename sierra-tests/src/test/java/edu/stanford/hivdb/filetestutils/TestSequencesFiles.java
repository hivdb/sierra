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

public class TestSequencesFiles {

	public enum TestSequencesProperties
	{
		SMALL("test_sequences/AlgDataSet-small.txt", true),
		INVALID("test_sequences/InvalidSequences.txt", true),
		T69Test("test_sequences/T69InsertionTest.txt", true),
		PRRTIN("test_sequences/_PRRTIN_n10.txt", true),
		RT_DELETIONS_69("test_sequences/_RTDeletions69_n10.txt", true),
		RT_DELETIONS_67("test_sequences/_RTDeletions67_n10.txt", true),
		RT_EXTENDED_AAS("test_sequences/_ExtendedAAsRT.txt", false),
		RT_INSERTIONS_69("test_sequences/_RTInsertions69_n10.txt", true),
		PROBLEM_SEQUENCES("test_sequences/_ProblemSequences.txt", true),
		SUBTYPE_TESTS("test_sequences/SubtypeTests.txt", false),
		SUBTYPE_TEST1("test_sequences/SubtypeTest1.txt", false),
		SUBTYPE_TESTS_ALL("test_sequences/SubtypeTestsAll.txt", false),
		CRF_SUBTYPE_TEST("test_sequences/X51_eq_B.txt", false),
		JUST_IN("test_sequences/_JustIN.txt",true),
		ZA_FRAME_SHIFTS("test_sequences/KimSequences.txt", false),
		POST_MAN_TEST("test_sequences/PostmanText.txt", false),
		WM45_2_1("test_sequences/WM45-2_r1", false),
		WM45_2_2("test_sequences/WM45-2_r2", false),
		HIV2AYOUBA("test_sequences/AyoubaHIV2.txt", false),
		VGI("test_sequences/_vgi.txt", true),
		COMPOUND_SCORES("test_sequences/CompoundScoresTest.txt", false),
		HIV2TAYLOR("test_sequences/HIV2_TaylorN.txt", false/*true*/), // TODO: add test case
		HIV2REF("test_sequences/HIV2_RefSeq_M30895.txt", false/*true*/), // TODO: add test case
		APOBEC("test_sequences/ApobecTestSequences.txt", false/*true*/), // TODO: add test case
		MALDARELLI("test_sequences/MaldarelliSeqs.txt", false),
		MALDARELLI2("test_sequences/FM_MK_PLOSPathAln2.txt", false),
		FRENKEL2003("test_sequences/LfrenkelJV2003.txt",false),
		PARIKH2006("test_sequences/ParikhJID2006.txt", false),
		KEARNEY2009("test_sequences/KearneyJV2009.txt", false),
		JASON_TEST_CASES("test_sequences/JasonTestCases.txt", false);

	    public final String propertyName;
	    public final boolean forRoutineTesting;

	    private TestSequencesProperties(final String propertyName, final boolean forRoutineTesting) {
	    	this.propertyName = propertyName;
	    	this.forRoutineTesting = forRoutineTesting;
	    }

	    @Override public String toString() { return propertyName; }
	}

	public static InputStream getTestSequenceInputStream(TestSequencesProperties testSequencesProperties)
	{
		return TestSequencesFiles.class.getClassLoader().getResourceAsStream(testSequencesProperties.propertyName);
	}
}
