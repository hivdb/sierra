/*

    Copyright (C) 2020 Stanford HIVDB team

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

package edu.stanford.hivdb.viruses;

import static org.junit.Assert.*;


import org.junit.Test;

import com.google.common.base.Strings;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.StrainModifier;

public class GeneTest {

	private final static HIV hiv = HIV.getInstance();
	private final static HIV2 hiv2 = HIV2.getInstance();

	@Test
	public void testGetGeneInstance() {
		assertNotNull(hiv.getGene("HIV1PR"));
	}

    @Test
    public void tesName() {
        assertEquals(hiv.getGene("HIV1RT").name(), "HIV1RT");
		assertEquals(hiv2.getGene("HIV2ART").name(), "HIV2ART");
		assertEquals(hiv2.getGene("HIV2BRT").name(), "HIV2BRT");
    }
	
    @Test
    public void testGetName() {
        assertEquals(hiv.getGene("HIV1RT").getName(), "HIV1RT");
		assertEquals(hiv2.getGene("HIV2ART").getName(), "HIV2ART");
		assertEquals(hiv2.getGene("HIV2BRT").getName(), "HIV2BRT");
    }

	@SuppressWarnings("deprecation")
	@Test
	public void testGetLength() {
		assertEquals(99, hiv.getGene("HIV1PR").getLength());
		assertEquals(560, hiv.getGene("HIV1RT").getLength());
		assertEquals(288, hiv.getGene("HIV1IN").getLength());
	}
	
	@Test
	public void testGetAASize() {
		assertEquals(hiv.getGene("HIV1PR").getAASize(), 99);
	}
	
	@Test
	public void testGetNASize() {
		assertEquals(297, hiv.getGene("HIV1PR").getNASize());
		assertEquals(1680, hiv.getGene("HIV1RT").getNASize());
		assertEquals(864, hiv.getGene("HIV1IN").getNASize());
		assertEquals(297, hiv2.getGene("HIV2APR").getNASize());
		assertEquals(1677, hiv2.getGene("HIV2ART").getNASize());
		assertEquals(879, hiv2.getGene("HIV2AIN").getNASize());
		assertEquals(297, hiv2.getGene("HIV2BPR").getNASize());
		assertEquals(1677, hiv2.getGene("HIV2BRT").getNASize());
		assertEquals(888, hiv2.getGene("HIV2BIN").getNASize());
	}
	
	
	@Test
	public void testGetRefChar() {
		assertSame(hiv.getGene("HIV1PR").getRefChar(4), 'T');
	}

	@Test
	public void testGetRefSequence() {
		assertEquals("IDK", hiv.getGene("HIV1IN").getRefSequence(5, 3));
		assertEquals(560, hiv.getGene("HIV1RT").getRefSequence().length());
	}



	@Test
	public void testGetStrain() {
		assertEquals(hiv2.getGene("HIV2APR").getStrain(), hiv2.getStrain("HIV2A"));
        assertEquals(hiv2.getGene("HIV2BRT").getStrain(), hiv2.getStrain("HIV2B"));
        assertEquals(hiv2.getGene("HIV2BRT").getStrain(), hiv2.getStrain("HIV2B"));
	}

	@Test
	public void testGetAbstractGene() {
		assertEquals("PR", hiv2.getGene("HIV2APR").getAbstractGene());
		assertEquals("RT", hiv2.getGene("HIV2BRT").getAbstractGene());
		assertEquals("IN", hiv.getGene("HIV1IN").getAbstractGene());
	}
	
	@Test
	public void testGetTargetStrainModifier() {
		assertNotNull(hiv.getGene("HIV1PR").getTargetStrainModifier("HIV1"));
	}
	
	@Test
	public void testGetDrugClasses() {
		assertArrayEquals(hiv.getGene("HIV1PR").getDrugClasses().toArray(), new DrugClass[] {hiv.getDrugClass("PI")});
	}

	@Test
	public void testGetMutationTypes() {
		assertEquals(hiv.getGene("HIV1PR").getMutationTypes().size(), 3);
		assertEquals(hiv.getGene("HIV1RT").getMutationTypes().size(), 3);
		assertEquals(hiv.getGene("HIV1IN").getMutationTypes().size(), 3);
	}


    @Test
    public void testApplyCodonModifiersForAASeq() {
    	StrainModifier modifier = hiv.getGene("HIV1PR").getTargetStrainModifier("HIV1");
		assertEquals(
			Strings.repeat(".", 4) + "ABCDEF" + Strings.repeat(".", 89),
			modifier.modifyAASeq(hiv.getGene("HIV1PR"), "ABCDEF", 5, 10)
			);

		modifier = hiv.getGene("HIV1RT").getTargetStrainModifier("HIV1");
		assertEquals(
			Strings.repeat(".", 342) + "FEDCBA" + Strings.repeat(".", 212),
			modifier.modifyAASeq(hiv.getGene("HIV1RT"), "FEDCBA", 343, 348));

		modifier = hiv2.getGene("HIV2ART").getTargetStrainModifier("HIV1");
		assertEquals(
			// RT346 Deletion
			Strings.repeat(".", 342) + "FED.CBA" + Strings.repeat(".", 211),
			modifier.modifyAASeq(hiv2.getGene("HIV2ART"), "FEDCBA", 343, 348));

		modifier = hiv2.getGene("HIV2BRT").getTargetStrainModifier("HIV1");
		assertEquals(
			// RT346 Deletion
			Strings.repeat(".", 342) + "FED.CBA" + Strings.repeat(".", 211),
			modifier.modifyAASeq(hiv2.getGene("HIV2BRT"), "FEDCBA", 343, 348));

		modifier = hiv2.getGene("HIV2AIN").getTargetStrainModifier("HIV1");
		assertEquals(
			// IN272 Insertion
			Strings.repeat(".", 270) + "ABEF" + Strings.repeat(".", 14),
			modifier.modifyAASeq(hiv2.getGene("HIV2AIN"), "ABCDEF", 271, 276));

		modifier = hiv2.getGene("HIV2AIN").getTargetStrainModifier("HIV1");
		assertEquals(
			// IN283 Insertion + IN272 two AAs shift
			Strings.repeat(".", 278) + "ABCDEG....",
			modifier.modifyAASeq(hiv2.getGene("HIV2AIN"), "ABCDEFG", 281, 287));

		modifier = hiv2.getGene("HIV2AIN").getTargetStrainModifier("HIV1");
		assertEquals(
			// IN after 288 (IN272 + IN283 three AAs shift)
			Strings.repeat(".", 283) + "ABCDE",
			modifier.modifyAASeq(hiv2.getGene("HIV2AIN"), "ABCDEFG", 287, 293));

    }

	@Test
	public void testapplyCodonModifiersForNASeq() {
		StrainModifier modifier = hiv.getGene("HIV1PR").getTargetStrainModifier("HIV1");
		assertEquals(
			Strings.repeat("...", 4) + "AAABBBCCCDDDEEEFFF" + Strings.repeat("...", 89),
			modifier.modifyNASeq(hiv.getGene("HIV1PR"), "AAABBBCCCDDDEEEFFF", 5, 10));
		
		modifier = hiv.getGene("HIV1RT").getTargetStrainModifier("HIV1");
		assertEquals(
			Strings.repeat("...", 342) + "FFFEEEDDDCCCBBBAAA" + Strings.repeat("...", 212),
			modifier.modifyNASeq(hiv.getGene("HIV1RT"), "FFFEEEDDDCCCBBBAAA", 343, 348));
	}

	@Test
	public void testCompareTo() {
        assertSame(0, hiv.getGene("HIV1PR").compareTo(hiv.getGene("HIV1PR")));
        assertEquals(-1, hiv.getGene("HIV1PR").compareTo(hiv.getGene("HIV1RT")));
        assertEquals(1,  hiv2.getGene("HIV2AIN").compareTo(hiv2.getGene("HIV2APR")));
        assertEquals(-1, hiv2.getGene("HIV2AIN").compareTo(hiv2.getGene("HIV2BPR")));
        assertEquals(-1, hiv2.getGene("HIV2AIN").compareTo(hiv2.getGene("HIV2BRT")));
        assertEquals(-1, hiv2.getGene("HIV2AIN").compareTo(hiv2.getGene("HIV2BIN")));
        assertEquals(1,  hiv2.getGene("HIV2BRT").compareTo(hiv2.getGene("HIV2APR")));
	}

	@Test(expected = NullPointerException.class)
	public void testCompareToNull() {
		hiv.getGene("HIV1RT").compareTo(null);
	}

	@Test
	public void testHashCode() {
		assertEquals(hiv.getGene("HIV1PR").hashCode(), hiv.getGene("HIV1PR").hashCode());
	}

}