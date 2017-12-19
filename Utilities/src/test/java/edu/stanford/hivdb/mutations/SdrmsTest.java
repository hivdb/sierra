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

package edu.stanford.hivdb.mutations;

//import java.io.InputStream;
//import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

//import edu.stanford.hivdb.testutils.TestMutationsFiles;
//import edu.stanford.hivdb.testutils.TestMutationsFiles.TestMutationsProperties;
//import edu.stanford.hivdb.utilities.MutationFileReader;

public class SdrmsTest {

	/*@Test
	public void lookUpSdrms() {
		final InputStream testMutationsInputStream =
			TestMutationsFiles.getTestMutationsInputStream(
				TestMutationsProperties.SDRM_TEST_MUTATIONS);
		final List<MutationSet> mutationLists =
			MutationFileReader.readMutationLists(testMutationsInputStream);

		for (MutationSet mutSet : mutationLists) {
			MutationSet sdrms = Sdrms.getSdrms(mutSet);
			System.out.println("Submitted list: " + mutSet.join(",", Mutation::getHumanFormatWithGene));
			System.out.println("SDRM list: " + sdrms.join(",", Mutation::getHumanFormatWithGene));
		}
	}*/

	@Test
	public void lookUpSdrms2() {
		assertSdrmsResult(
			/*expects*/"", /*mutations*/"PR_L10I,PR_L33F,PR_V82I,RT_K65N");
		assertSdrmsResult(
			"PR_G48V,RT_K103N", "PR_L10I,PR_L33F,PR_G48V,PR_V82I,RT_K65N,RT_K103N");
		assertSdrmsResult(
			"PR_G48V,RT_69_,RT_K103N",
		   	"PR_L10I,PR_L33F,PR_G48V,PR_V82I,RT_69ins,RT_K103N");
		assertSdrmsResult(
			"PR_G48V,RT_69D,RT_K103N",
		   	"PR_L10I,PR_L33F,PR_G48V,PR_V82I,RT_69D,RT_K103N");
		assertSdrmsResult(
			"PR_G48V,RT_67EG,RT_K103N",
		   	"PR_L10I,PR_L33F,PR_G48V,PR_V82I,RT_67EGA,RT_K103N");
	}

	private void assertSdrmsResult(String expects, String sample) {
		assertEquals(
			new MutationSet(expects),
			Sdrms.getSdrms(new MutationSet(sample)));
	}

}
