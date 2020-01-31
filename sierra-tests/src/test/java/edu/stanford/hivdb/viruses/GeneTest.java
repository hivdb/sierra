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
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.MutationType;
import edu.stanford.hivdb.viruses.Gene;


public class GeneTest {

	final static HIV hiv = HIV.getInstance();

	@Test
	public void testGetGeneInstance() {
		assertNotNull(hiv.getGene("HIV1PR"));
	}

	@Test
	public void testGetGenes() {

		assertArrayEquals(hiv.getGenes(hiv.getStrain("HIV1")).toArray(), new Gene[] {
				hiv.getGene("HIV1PR"),
				hiv.getGene("HIV1RT"),
				hiv.getGene("HIV1IN")
			});

		assertArrayEquals(hiv.getGenes(hiv.getStrain("HIV2B")).toArray(), new Gene[] {
				hiv.getGene("HIV2BPR"),
				hiv.getGene("HIV2BRT"),
				hiv.getGene("HIV2BIN")
			});

		assertArrayEquals(hiv.getGenes(hiv.getStrain("HIV-1")).toArray(), new Gene[] {});
	}

	@Test
	public void testGetDrugClasses() {

		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("PI")},
				hiv.getGene("HIV1PR").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("NRTI"), hiv.getDrugClass("NNRTI")},
				hiv.getGene("HIV1RT").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("NRTI")},
				hiv.getGene("HIV2ART").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("NRTI")},
				hiv.getGene("HIV2BRT").getDrugClasses().toArray()
				);
		assertArrayEquals(
				new DrugClass[] {hiv.getDrugClass("INSTI")},
				hiv.getGene("HIV1IN").getDrugClasses().toArray()
				);
	}

	@Test
	public void testGetMutationTypes() {

		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("Major"),
					hiv.getMutationType("Accessory"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV1PR").getMutationTypes().toArray());
		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("NRTI"),
					hiv.getMutationType("NNRTI"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV1RT").getMutationTypes().toArray());
		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("Major"),
					hiv.getMutationType("Accessory"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV2ART").getMutationTypes().toArray());
		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("Major"),
					hiv.getMutationType("Accessory"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV2BRT").getMutationTypes().toArray()
				);
		assertArrayEquals(
				new MutationType[] {
					hiv.getMutationType("Major"),
					hiv.getMutationType("Accessory"),
					hiv.getMutationType("Other")
				},
				hiv.getGene("HIV1IN").getMutationTypes().toArray());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetLength() {
		assertEquals(99, hiv.getGene("HIV1PR").getLength());
		assertEquals(560, hiv.getGene("HIV1RT").getLength());
		assertEquals(288, hiv.getGene("HIV1IN").getLength());
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
	public void testGetNASize() {
		assertEquals(297, hiv.getGene("HIV1PR").getNASize());
		assertEquals(1680, hiv.getGene("HIV1RT").getNASize());
		assertEquals(864, hiv.getGene("HIV1IN").getNASize());
		assertEquals(297, hiv.getGene("HIV2APR").getNASize());
		assertEquals(1677, hiv.getGene("HIV2ART").getNASize());
		assertEquals(879, hiv.getGene("HIV2AIN").getNASize());
		assertEquals(297, hiv.getGene("HIV2BPR").getNASize());
		assertEquals(1677, hiv.getGene("HIV2BRT").getNASize());
		assertEquals(888, hiv.getGene("HIV2BIN").getNASize());
	}

	@Test
	public void testGetStrain() {
		assertEquals(hiv.getGene("HIV2APR").getStrain(), hiv.getStrain("HIV2A"));
        assertEquals(hiv.getGene("HIV2BRT").getStrain(), hiv.getStrain("HIV2B"));
        assertEquals(hiv.getGene("HIV2BRT").getStrain(), hiv.getStrain("HIV2B"));
	}

	@Test
	public void testGetAbstractGene() {
		assertEquals("PR", hiv.getGene("HIV2APR").getAbstractGene());
		assertEquals("RT", hiv.getGene("HIV2BRT").getAbstractGene());
		assertEquals("IN", hiv.getGene("HIV1IN").getAbstractGene());
	}

    @Test
    public void testGetName() {
        assertEquals(hiv.getGene("HIV1RT").getName(), "HIV1RT");
		assertEquals(hiv.getGene("HIV2ART").getName(), "HIV2ART");
		assertEquals(hiv.getGene("HIV2BRT").getName(), "HIV2BRT");
    }

    @Test
    public void testApplyCodonModifiersForAASeq() {
		assertEquals(
			Strings.repeat(".", 4) + "ABCDEF" + Strings.repeat(".", 89),
			hiv.getGene("HIV1PR").applyCodonModifiersForAASeq(
                "ABCDEF", 5, 10, hiv.getStrain("HIV1")));

		assertEquals(
			Strings.repeat(".", 342) + "FEDCBA" + Strings.repeat(".", 212),
			hiv.getGene("HIV1RT").applyCodonModifiersForAASeq(
                "FEDCBA", 343, 348, hiv.getStrain("HIV1")));

		assertEquals(
			// RT346 Deletion
			Strings.repeat(".", 342) + "FED.CBA" + Strings.repeat(".", 211),
			hiv.getGene("HIV2ART").applyCodonModifiersForAASeq(
				"FEDCBA", 343, 348, hiv.getStrain("HIV1")));

		assertEquals(
			// RT346 Deletion
			Strings.repeat(".", 342) + "FED.CBA" + Strings.repeat(".", 211),
			hiv.getGene("HIV2BRT").applyCodonModifiersForAASeq(
                "FEDCBA", 343, 348, hiv.getStrain("HIV1")));

		assertEquals(
			// IN272 Insertion
			Strings.repeat(".", 270) + "ABEF" + Strings.repeat(".", 14),
			hiv.getGene("HIV2AIN").applyCodonModifiersForAASeq(
                "ABCDEF", 271, 276, hiv.getStrain("HIV1")));

		assertEquals(
			// IN283 Insertion + IN272 two AAs shift
			Strings.repeat(".", 278) + "ABCDEG....",
			hiv.getGene("HIV2AIN").applyCodonModifiersForAASeq(
                "ABCDEFG", 281, 287, hiv.getStrain("HIV1")));

		assertEquals(
			// IN after 288 (IN272 + IN283 three AAs shift)
			Strings.repeat(".", 283) + "ABCDE",
			hiv.getGene("HIV2AIN").applyCodonModifiersForAASeq(
                "ABCDEFG", 287, 293, hiv.getStrain("HIV1")));

    }

    // Inherit from old Unittest
    //
	// @Test(expected = RuntimeException.class)
	// public void testApplyCodonModifiersForAASeqWithException1() {
	// 	HIVGene fakePRGene = new HIVGene(
	// 		HIVStrain.HIV2A, HIVAbstractGene.PR,
	// 		"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
	// 		"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
	// 		new Integer[] {
	// 			+22, -1
	// 		}, 2253);
	// 	fakePRGene.adjustAAAlignment("ABCDEF", 20, 25);
	// }

	// @Test(expected = RuntimeException.class)
	// public void testApplyCodonModifiersForAASeqWithException2() {
	// 	HIVGene fakePRGene = new HIVGene(
	// 		HIVStrain.HIV2A, HIVAbstractGene.PR,
	// 		"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
	// 		"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
	// 		new Integer[] {
	// 			-22, -1
	// 		}, 2253);
	// 	fakePRGene.adjustAAAlignment("ABCDEF", 20, 25);
	// }

	@Test
	public void testapplyCodonModifiersForNASeq() {
		assertEquals(
			Strings.repeat("...", 4) + "AAABBBCCCDDDEEEFFF" + Strings.repeat("...", 89),
			hiv.getGene("HIV1PR").applyCodonModifiersForNASeq(
				"AAABBBCCCDDDEEEFFF", 5, 10, hiv.getStrain("HIV1")));
		assertEquals(
			Strings.repeat("...", 342) + "FFFEEEDDDCCCBBBAAA" + Strings.repeat("...", 212),
			hiv.getGene("HIV1RT").applyCodonModifiersForNASeq(
				"FFFEEEDDDCCCBBBAAA", 343, 348, hiv.getStrain("HIV1")));
		assertEquals(
			// RT346 Deletion
			Strings.repeat("...", 342) + "FFFEEEDDD...CCCBBBAAA" + Strings.repeat("...", 211),
			hiv.getGene("HIV2ART").applyCodonModifiersForNASeq(
				"FFFEEEDDDCCCBBBAAA", 343, 348, hiv.getStrain("HIV1")));
		assertEquals(
			// RT346 Deletion
			Strings.repeat("...", 342) + "FFFEEEDDD...CCCBBBAAA" + Strings.repeat("...", 211),
			hiv.getGene("HIV2BRT").applyCodonModifiersForNASeq(
				"FFFEEEDDDCCCBBBAAA", 343, 348, hiv.getStrain("HIV1")));
		assertEquals(
			// IN272 Insertion
			Strings.repeat("...", 270) + "AAABBBEEEFFF" + Strings.repeat("...", 14),
			hiv.getGene("HIV2AIN").applyCodonModifiersForNASeq(
				"AAABBBCCCDDDEEEFFF", 271, 276, hiv.getStrain("HIV1")));
		assertEquals(
			// IN283 Insertion + IN272 two AAs shift
			Strings.repeat("...", 278) + "AAABBBCCCDDDEEEGGG............",
			hiv.getGene("HIV2AIN").applyCodonModifiersForNASeq(
				"AAABBBCCCDDDEEEFFFGGG", 281, 287, hiv.getStrain("HIV1")));
		assertEquals(
			// IN after 288 (IN272 + IN283 three AAs shift)
			Strings.repeat("...", 283) + "AAABBBCCCDDDEEE",
			hiv.getGene("HIV2AIN").applyCodonModifiersForNASeq(
				"AAABBBCCCDDDEEEFFFGGG", 287, 293, hiv.getStrain("HIV1")));
	}

    // Inherit from old Unittest
    //
	/*@Test(expected = RuntimeException.class)
	public void testAdjustNAAlignmentWithException1() {
		HIVGene fakePRGene = new HIVGene(
			HIVStrain.HIV2A, HIVAbstractGene.PR,
			"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
			"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
			new Integer[] {
				+22, -1
			}, 2253);
		fakePRGene.adjustNAAlignment("ABCDEF", 20, 25);
	}

	@Test(expected = RuntimeException.class)
	public void testAdjustNAAlignmentWithException2() {
		HIVGene fakePRGene = new HIVGene(
			HIVStrain.HIV2A, HIVAbstractGene.PR,
			"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
			"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
			new Integer[] {
				-22, -1
			}, 2253);
		fakePRGene.adjustNAAlignment("ABCDEF", 20, 25);
	}*/


	@Test
	public void testCompareTo() {
        assertSame(0, hiv.getGene("HIV1PR").compareTo(hiv.getGene("HIV1PR")));
        assertEquals(-1, hiv.getGene("HIV1PR").compareTo(hiv.getGene("HIV1RT")));
        assertEquals(1,  hiv.getGene("HIV2AIN").compareTo(hiv.getGene("HIV2APR")));
        assertEquals(-1, hiv.getGene("HIV1IN").compareTo(hiv.getGene("HIV2APR")));
        assertEquals(-1, hiv.getGene("HIV1IN").compareTo(hiv.getGene("HIV2ART")));
        assertEquals(-1, hiv.getGene("HIV1IN").compareTo(hiv.getGene("HIV2AIN")));
        assertEquals(-1, hiv.getGene("HIV1IN").compareTo(hiv.getGene("HIV2BPR")));
        assertEquals(-1, hiv.getGene("HIV1RT").compareTo(hiv.getGene("HIV2BPR")));
        assertEquals(1,  hiv.getGene("HIV2ART").compareTo(hiv.getGene("HIV1PR")));
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