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

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Strings;

import edu.stanford.hivdb.drugs.DrugClass;

public class GeneTest {
	
	@Test
	public void testValues() {
		assertArrayEquals(Gene.values("HIV1"), new Gene[] {
			Gene.valueOf("HIV1PR"),
			Gene.valueOf("HIV1RT"),
			Gene.valueOf("HIV1IN")
		});
		assertArrayEquals(Gene.values(Strain.HIV2B), new Gene[] {
			Gene.valueOf("HIV2BPR"),
			Gene.valueOf("HIV2BRT"),
			Gene.valueOf("HIV2BIN")
		});
		assertArrayEquals(Gene.values("HIV-1"), new Gene[] {
			null,
			null,
			null
		});
	}
	
	@Test
	public void testValueOf() {
		assertEquals(
			Gene.valueOf("HIV1", "PR"), Gene.valueOf(Strain.HIV1, GeneEnum.PR));
		assertEquals(
			Gene.valueOf("HIV2BRT"), Gene.valueOf(Strain.HIV2B, GeneEnum.RT));
		assertNotEquals(
			Gene.valueOf("HIV2ART"), Gene.valueOf(Strain.HIV2B, GeneEnum.RT));
		assertEquals(Gene.valueOf("HIV2CRT"), null);
		assertEquals(Gene.valueOf("HIV2A", "Pol"), null);
		assertEquals(Gene.valueOf(Strain.HIV1, "Pol"), null);
	}
	
	@Test
	public void testGetDrugClasses() {
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.PI}),
			Gene.valueOf("HIV1PR").getDrugClasses());
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.NRTI, DrugClass.NNRTI}),
			Gene.valueOf("HIV1RT").getDrugClasses());
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.NRTI}),
			Gene.valueOf("HIV2ART").getDrugClasses());
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.NRTI}),
			Gene.valueOf("HIV2BRT").getDrugClasses());
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.INSTI}),
			Gene.valueOf("HIV1IN").getDrugClasses());
	}

	@Test
	public void testGetMutationTypes() {
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory, MutType.Other}),
			Gene.valueOf("HIV1PR").getMutationTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.NRTI, MutType.NNRTI, MutType.Other}),
			Gene.valueOf("HIV1RT").getMutationTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory, MutType.Other}),
			Gene.valueOf("HIV2ART").getMutationTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory, MutType.Other}),
			Gene.valueOf("HIV2BRT").getMutationTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory, MutType.Other}),
			Gene.valueOf("HIV1IN").getMutationTypes());
	}

	@Test
	public void testGetScoredMutTypes() {
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory}),
			Gene.valueOf("HIV1PR").getScoredMutTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.NRTI, MutType.NNRTI}),
			Gene.valueOf("HIV1RT").getScoredMutTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory}),
			Gene.valueOf("HIV2ART").getScoredMutTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory}),
			Gene.valueOf("HIV2BRT").getScoredMutTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory}),
			Gene.valueOf("HIV1IN").getScoredMutTypes());
	}

	@Test
	public void testGetLength() {
		assertEquals(99, Gene.valueOf("HIV1PR").getLength());
		assertEquals(560, Gene.valueOf("HIV1RT").getLength());
		assertEquals(288, Gene.valueOf("HIV1IN").getLength());
	}

	@Test
	public void testGetConsensus() {
		assertEquals("T", Gene.valueOf("HIV1PR").getReference(4));
		assertEquals("IDK", Gene.valueOf("HIV1IN").getReference(5, 3));
		assertEquals(560, Gene.valueOf("HIV1RT").getReference().length());
	}
	
	@Test
	public void testGetFirstNA() {
		assertEquals(2253, Gene.valueOf("HIV1PR").getFirstNA());
		assertEquals(2550, Gene.valueOf("HIV1RT").getFirstNA());
		assertEquals(4230, Gene.valueOf("HIV1IN").getFirstNA());
	}
	
	@Test
	public void testGetNASize() {
		assertEquals(297, Gene.valueOf("HIV1PR").getNASize());
		assertEquals(1680, Gene.valueOf("HIV1RT").getNASize());
		assertEquals(864, Gene.valueOf("HIV1IN").getNASize());
		assertEquals(297, Gene.valueOf("HIV2APR").getNASize());
		assertEquals(1677, Gene.valueOf("HIV2ART").getNASize());
		assertEquals(879, Gene.valueOf("HIV2AIN").getNASize());
		assertEquals(297, Gene.valueOf("HIV2BPR").getNASize());
		assertEquals(1677, Gene.valueOf("HIV2BRT").getNASize());
		assertEquals(888, Gene.valueOf("HIV2BIN").getNASize());
	}
	
	@Test
	public void testGetStrain() {
		assertEquals(Strain.HIV2A, Gene.valueOf("HIV2APR").getStrain());
		assertEquals(Strain.HIV2B, Gene.valueOf("HIV2BRT").getStrain());
		assertEquals(Strain.HIV1, Gene.valueOf("HIV1IN").getStrain());
	}

	@Test
	public void testGetGeneEnum() {
		assertEquals(GeneEnum.PR, Gene.valueOf("HIV2APR").getGeneEnum());
		assertEquals(GeneEnum.RT, Gene.valueOf("HIV2BRT").getGeneEnum());
		assertEquals(GeneEnum.IN, Gene.valueOf("HIV1IN").getGeneEnum());
	}
	
	@Test
	public void testGetNameWithStrain() {
		assertEquals("HIV1PR", Gene.values("HIV1")[0].getNameWithStrain());
		assertEquals("HIV2ART", Gene.values("HIV2A")[1].getNameWithStrain());
		assertEquals("HIV2BIN", Gene.values("HIV2B")[2].getNameWithStrain());
	}

	@Test
	public void testGetName() {
		assertEquals("PR", Gene.values("HIV1")[0].getName());
		assertEquals("RT", Gene.values("HIV2A")[1].getName());
		assertEquals("IN", Gene.values("HIV2B")[2].getName());
	}
	
	@Test
	public void testAdjustAAAlignment() {
		assertEquals(
			Strings.repeat(".", 4) + "ABCDEF" + Strings.repeat(".", 89),
			Gene.valueOf("HIV1PR").adjustAAAlignment("ABCDEF", 5, 10));
		assertEquals(
			Strings.repeat(".", 342) + "FEDCBA" + Strings.repeat(".", 212),
			Gene.valueOf("HIV1RT").adjustAAAlignment("FEDCBA", 343, 348));
		assertEquals(
			// RT346 Deletion
			Strings.repeat(".", 342) + "FED.CBA" + Strings.repeat(".", 211),
			Gene.valueOf("HIV2ART").adjustAAAlignment("FEDCBA", 343, 348));
		assertEquals(
			// RT346 Deletion
			Strings.repeat(".", 342) + "FED.CBA" + Strings.repeat(".", 211),
			Gene.valueOf("HIV2BRT").adjustAAAlignment("FEDCBA", 343, 348));
		assertEquals(
			// IN272 Insertion
			Strings.repeat(".", 270) + "ABEF" + Strings.repeat(".", 14),
			Gene.valueOf("HIV2AIN").adjustAAAlignment("ABCDEF", 271, 276));
		assertEquals(
			// IN283 Insertion + IN272 two AAs shift
			Strings.repeat(".", 278) + "ABCDEG....",
			Gene.valueOf("HIV2AIN").adjustAAAlignment("ABCDEFG", 281, 287));
		assertEquals(
			// IN after 288 (IN272 + IN283 three AAs shift)
			Strings.repeat(".", 283) + "ABCDE",
			Gene.valueOf("HIV2AIN").adjustAAAlignment("ABCDEFG", 287, 293));
	}
	
	@Test(expected = RuntimeException.class)
	public void testAdjustAAAlignmentWithException1() {
		Gene fakePRGene = new Gene(
			Strain.HIV2A, GeneEnum.PR,
			"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
			"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
			new Integer[] {
				+22, -1
			}, 2253);
		fakePRGene.adjustAAAlignment("ABCDEF", 20, 25);
	}

	@Test(expected = RuntimeException.class)
	public void testAdjustAAAlignmentWithException2() {
		Gene fakePRGene = new Gene(
			Strain.HIV2A, GeneEnum.PR,
			"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
			"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
			new Integer[] {
				-22, -1
			}, 2253);
		fakePRGene.adjustAAAlignment("ABCDEF", 20, 25);
	}

	@Test
	public void testAdjustNAAlignment() {
		assertEquals(
			Strings.repeat("...", 4) + "AAABBBCCCDDDEEEFFF" + Strings.repeat("...", 89),
			Gene.valueOf("HIV1PR").adjustNAAlignment("AAABBBCCCDDDEEEFFF", 5, 10));
		assertEquals(
			Strings.repeat("...", 342) + "FFFEEEDDDCCCBBBAAA" + Strings.repeat("...", 212),
			Gene.valueOf("HIV1RT").adjustNAAlignment("FFFEEEDDDCCCBBBAAA", 343, 348));
		assertEquals(
			// RT346 Deletion
			Strings.repeat("...", 342) + "FFFEEEDDD...CCCBBBAAA" + Strings.repeat("...", 211),
			Gene.valueOf("HIV2ART").adjustNAAlignment("FFFEEEDDDCCCBBBAAA", 343, 348));
		assertEquals(
			// RT346 Deletion
			Strings.repeat("...", 342) + "FFFEEEDDD...CCCBBBAAA" + Strings.repeat("...", 211),
			Gene.valueOf("HIV2BRT").adjustNAAlignment("FFFEEEDDDCCCBBBAAA", 343, 348));
		assertEquals(
			// IN272 Insertion
			Strings.repeat("...", 270) + "AAABBBEEEFFF" + Strings.repeat("...", 14),
			Gene.valueOf("HIV2AIN").adjustNAAlignment("AAABBBCCCDDDEEEFFF", 271, 276));
		assertEquals(
			// IN283 Insertion + IN272 two AAs shift
			Strings.repeat("...", 278) + "AAABBBCCCDDDEEEGGG............",
			Gene.valueOf("HIV2AIN").adjustNAAlignment("AAABBBCCCDDDEEEFFFGGG", 281, 287));
		assertEquals(
			// IN after 288 (IN272 + IN283 three AAs shift)
			Strings.repeat("...", 283) + "AAABBBCCCDDDEEE",
			Gene.valueOf("HIV2AIN").adjustNAAlignment("AAABBBCCCDDDEEEFFFGGG", 287, 293));
	}
	
	@Test(expected = RuntimeException.class)
	public void testAdjustNAAlignmentWithException1() {
		Gene fakePRGene = new Gene(
			Strain.HIV2A, GeneEnum.PR,
			"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
			"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
			new Integer[] {
				+22, -1
			}, 2253);
		fakePRGene.adjustNAAlignment("ABCDEF", 20, 25);
	}

	@Test(expected = RuntimeException.class)
	public void testAdjustNAAlignmentWithException2() {
		Gene fakePRGene = new Gene(
			Strain.HIV2A, GeneEnum.PR,
			"PQFSLWRRPVVKATIEGQSVEVLLDTGADDSIVAGIELGSNYTPKIVGGI" +
			"GGFINTNEYKNVEIEVVGKRVRATVMTGDTPINIFGRNILNSLGMTLNF",
			new Integer[] {
				-22, -1
			}, 2253);
		fakePRGene.adjustNAAlignment("ABCDEF", 20, 25);
	}

	@Test
	public void testCompareTo() {
		assertEquals(0, Gene.valueOf("HIV1", "PR").compareTo(Gene.valueOf("HIV1PR")));
		assertEquals(-1, Gene.valueOf("HIV1", "PR").compareTo(Gene.valueOf("HIV1RT")));
		assertEquals(2, Gene.valueOf("HIV2AIN").compareTo(Gene.valueOf("HIV2APR")));
		assertEquals(-1, Gene.valueOf("HIV1IN").compareTo(Gene.valueOf("HIV2APR")));
		assertEquals(-2, Gene.valueOf("HIV1IN").compareTo(Gene.valueOf("HIV2ART")));
		assertEquals(-3, Gene.valueOf("HIV1IN").compareTo(Gene.valueOf("HIV2AIN")));
		assertEquals(-4, Gene.valueOf("HIV1IN").compareTo(Gene.valueOf("HIV2BPR")));
		assertEquals(-5, Gene.valueOf("HIV1RT").compareTo(Gene.valueOf("HIV2BPR")));
		assertEquals(4, Gene.valueOf("HIV2ART").compareTo(Gene.valueOf("HIV1PR")));
	}
	
	@Test(expected = NullPointerException.class)
	public void testCompareToNull() {
		Gene.valueOf("HIV1RT").compareTo(null);
	}
	
	@Test
	public void testHashCode() {
		assertEquals(Gene.values("HIV1")[0].hashCode(), Gene.valueOf("HIV1PR").hashCode());
	}
	
}