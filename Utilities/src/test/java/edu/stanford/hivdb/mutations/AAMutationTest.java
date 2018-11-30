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

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AAMutationTest {
	
	@Test
	public void testNormalizeAAChars() {
		assertEquals(null, AAMutation.normalizeAAChars(null));
		assertEquals(Sets.newSet('_'), AAMutation.normalizeAAChars(Sets.newSet('#')));
		assertEquals(Sets.newSet('_'), AAMutation.normalizeAAChars(Sets.newSet('i')));
		assertEquals(Sets.newSet('-'), AAMutation.normalizeAAChars(Sets.newSet('~')));
		assertEquals(Sets.newSet('-'), AAMutation.normalizeAAChars(Sets.newSet('d')));
		assertEquals(Sets.newSet('*'), AAMutation.normalizeAAChars(Sets.newSet('Z')));
		assertEquals(Sets.newSet('*'), AAMutation.normalizeAAChars(Sets.newSet('.')));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPositionOutOfGene() {
		new AAMutation(Gene.PR, 100, 'A');
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSameGene() {
		new AAMutation(Gene.PR, 68, 'A')
			.mergesWith(new AAMutation(Gene.RT, 68, 'C'));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSamePos() {
		new AAMutation(Gene.RT, 67, 'A')
			.mergesWith(new AAMutation(Gene.RT, 68, 'C'));
	}

	public void testMergesWithIndel() {
		assertEquals(
			new AAMutation(Gene.RT, 69, new char[] {'A', '-'}),
			new AAMutation(Gene.RT, 67, 'A')
				.mergesWith(new AAMutation(Gene.RT, 67, '-')));
	}

	public void testIndelMergesWith() {
		assertEquals(
			new AAMutation(Gene.RT, 69, new char[] {'A', '-'}),
			new AAMutation(Gene.RT, 67, '-')
				.mergesWith(new AAMutation(Gene.RT, 67, 'A')));
	}

	@Test
	public void testMergesWith() {
		assertEquals(
			new AAMutation(Gene.RT, 67, "AC".toCharArray()),
			new AAMutation(Gene.RT, 67, 'A')
				.mergesWith(new AAMutation(Gene.RT, 67, 'C')));
		assertEquals(
			new AAMutation(Gene.RT, 67, "AC".toCharArray()),
			new AAMutation(Gene.RT, 67, "AC".toCharArray())
				.mergesWith(new AAMutation(Gene.RT, 67, 'C')));
	}

	@Test
	public void testSubtractsBy() {
		Mutation pr67ANXDMut = new AAMutation(Gene.PR, 67, "ANXD".toCharArray());
		Mutation pr67NMut = new AAMutation(Gene.PR, 67, 'N');
		Mutation pr67XMut = new AAMutation(Gene.PR, 67, 'X');
		Mutation pr67ADMut = new AAMutation(Gene.PR, 67, "AD".toCharArray());
		Mutation pr67ADXMut = new AAMutation(Gene.PR, 67, "AXD".toCharArray());
		Mutation eDiffN = pr67ADXMut;
		Mutation eDiffX = new AAMutation(Gene.PR, 67, "ADN".toCharArray());
		Mutation eDiffAD = new AAMutation(Gene.PR, 67, "XN".toCharArray());
		Mutation eDiffADX = new AAMutation(Gene.PR, 67, 'N');
		assertEquals(eDiffN, pr67ANXDMut.subtractsBy(pr67NMut));
		assertEquals(eDiffX, pr67ANXDMut.subtractsBy(pr67XMut));
		assertEquals(eDiffAD, pr67ANXDMut.subtractsBy(pr67ADMut));
		assertEquals(eDiffADX, pr67ANXDMut.subtractsBy(pr67ADXMut));
	}
	
	@Test
	public void testSubtractsByEdgeCases() {
		Mutation pr67AMut = new AAMutation(Gene.PR, 67, 'A');
		Mutation pr68AMut = new AAMutation(Gene.PR, 68, 'A');
		Mutation rt67AMut = new AAMutation(Gene.RT, 68, 'A');
		assertEquals(null, new AAMutation(Gene.PR, 68, 'A').subtractsBy(pr68AMut));
		assertEquals(pr67AMut, new AAMutation(Gene.PR, 67, 'A').subtractsBy((Mutation) null));
		assertEquals(pr68AMut, new AAMutation(Gene.PR, 68, 'A').subtractsBy(pr67AMut));
		assertEquals(pr67AMut, new AAMutation(Gene.PR, 67, 'A').subtractsBy(rt67AMut));	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIntersectsWithNotSameGene() {
		new AAMutation(Gene.PR, 68, "AC".toCharArray())
			.intersectsWith(new AAMutation(Gene.RT, 68, 'C'));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIntersectsWithNotSamePos() {
		new AAMutation(Gene.RT, 67, "AC".toCharArray())
			.intersectsWith(new AAMutation(Gene.RT, 68, 'C'));
	}

	@Test
	public void testIntersectsWith() {
		assertEquals(
			new AAMutation(Gene.RT, 67, 'C'),
			new AAMutation(Gene.RT, 67, "AC".toCharArray())
				.intersectsWith(new AAMutation(Gene.RT, 67, "CD".toCharArray())));
	}

	@Test
	public void testSplit() {
		Set<Mutation> expected = new HashSet<>();
		expected.add(new AAMutation(Gene.IN, 151, 'L'));
		expected.add(new AAMutation(Gene.IN, 151, 'M'));
		expected.add(new AAMutation(Gene.IN, 151, 'Q'));
		assertEquals(expected, new AAMutation(Gene.IN, 151, "LMQ".toCharArray()).split());
		expected = new HashSet<>();
		expected.add(new AAMutation(Gene.RT, 67, '-'));
		assertEquals(expected, new AAMutation(Gene.RT, 67, '-').split());
		expected = new HashSet<>();
		expected.add(new AAMutation(Gene.RT, 69, '_'));
		assertEquals(expected, new AAMutation(Gene.RT, 69, '_').split());
		expected = new HashSet<>();
		expected.add(new AAMutation(Gene.PR, 1, 'L'));
		assertEquals(expected, new AAMutation(Gene.PR, 1, "PL".toCharArray()).split());
	}

	@Test
	public void testIsAtDrugResistancePosition() {
		Mutation mutIN151Major = new AAMutation(Gene.IN, 151, 'L');
		assertTrue(mutIN151Major.isAtDrugResistancePosition());
		assertTrue(mutIN151Major.isAtDrugResistancePosition());

		Mutation mutIN150Other = new AAMutation(Gene.IN, 150, 'L');
		assertFalse(mutIN150Other.isAtDrugResistancePosition());
		assertFalse(mutIN150Other.isAtDrugResistancePosition());

		Mutation mutIN151Accessory = new AAMutation(Gene.IN, 151, 'A');
		assertTrue(mutIN151Accessory.isAtDrugResistancePosition());

		Mutation mutIN151Other = new AAMutation(Gene.IN, 151, 'W');
		assertTrue(mutIN151Other.isAtDrugResistancePosition());

		Mutation mutIN151InsertionOther = new AAMutation(Gene.IN, 151, "_L".toCharArray());
		assertTrue(mutIN151InsertionOther.isAtDrugResistancePosition());

		Mutation mutIN151DeletionOther = new AAMutation(Gene.IN, 151, '-');
		assertTrue(mutIN151DeletionOther.isAtDrugResistancePosition());

		Mutation mutIN151MixtureWithMajor = new AAMutation(Gene.IN, 151, "ALW".toCharArray());
		assertTrue(mutIN151MixtureWithMajor.isAtDrugResistancePosition());

		Mutation mutIN151MixtureWithoutMajor = new AAMutation(Gene.IN, 151, "AW".toCharArray());
		assertTrue(mutIN151MixtureWithoutMajor.isAtDrugResistancePosition());

		Mutation mutRT75NRTI = new AAMutation(Gene.RT, 75, 'I');
		assertTrue(mutRT75NRTI.isAtDrugResistancePosition());

		Mutation mutRT98NNRTI = new AAMutation(Gene.RT, 98, 'G');
		assertTrue(mutRT98NNRTI.isAtDrugResistancePosition());

		Mutation mutRT99Other = new AAMutation(Gene.RT, 99, 'G');
		assertFalse(mutRT99Other.isAtDrugResistancePosition());
	}
	
	@Test
	public void testGetDisplayAAs() {
		assertEquals("N", new AAMutation(Gene.RT, 65, 'N').getDisplayAAs());
		assertEquals("X", new AAMutation(Gene.RT, 65, "ACDEFG".toCharArray(), 4).getDisplayAAs());
		assertEquals("ACDEFG", new AAMutation(Gene.RT, 65, "ACDEFG".toCharArray(), 7).getDisplayAAs());
	}
	
	@Test
	public void testGetAAs() {
		assertEquals("N", new AAMutation(Gene.RT, 65, 'N').getAAs());
		assertEquals("ACDEFG", new AAMutation(Gene.RT, 65, "ACDEFG".toCharArray(), 4).getAAs());
		assertEquals("ACDEFG", new AAMutation(Gene.RT, 65, "ACDEFG".toCharArray(), 7).getAAs());
	}

	@Test
	public void testGetAAsWithoutConsensus() {
		assertEquals("N", new AAMutation(Gene.RT, 65, "KN".toCharArray()).getAAsWithoutReference());
	}
	
	@Test
	public void testGetTriplet() {
		// not support for an AAMutation
		assertEquals("", new AAMutation(Gene.RT, 65, 'A').getTriplet());
	}
	
	@Test
	public void testGetInsertedNAs() {
		// not support for an AAMutation
		assertEquals("", new AAMutation(Gene.RT, 65, '_').getInsertedNAs());
	}

	@Test
	public void testIsApobecDRM() {
		assertTrue(new AAMutation(Gene.RT, 184, 'I').isApobecDRM());
		assertFalse(new AAMutation(Gene.RT, 184, 'A').isApobecDRM());
		assertFalse(new AAMutation(Gene.RT, 183, 'I').isApobecDRM());
	}

	@Test
	public void testIsApobecMut() {
		assertTrue(new AAMutation(Gene.PR, 25, "KN".toCharArray()).isApobecMutation());
		assertTrue(new AAMutation(Gene.PR, 25, 'K').isApobecMutation());
		assertTrue(new AAMutation(Gene.PR, 25, "DN".toCharArray()).isApobecMutation());
		assertTrue(new AAMutation(Gene.PR, 25, "DKN".toCharArray()).isApobecMutation());
		assertFalse(new AAMutation(Gene.PR, 25, 'D').isApobecMutation());
		assertFalse(new AAMutation(Gene.PR, 25, 'A').isApobecMutation());
	}

	@Test
	public void testContainsSharedAA() {
		// contains shared consensus
		assertFalse(
			new AAMutation(Gene.RT, 65, "KN".toCharArray())
			.containsSharedAA(new AAMutation(Gene.RT, 65, "KA".toCharArray())));
		assertTrue(
			new AAMutation(Gene.RT, 65, "BKN".toCharArray())
			.containsSharedAA(new AAMutation(Gene.RT, 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation(Gene.RT, 65, "BK".toCharArray())
			.containsSharedAA(new AAMutation(Gene.RT, 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation(Gene.PR, 65, "NA".toCharArray())
			.containsSharedAA(new AAMutation(Gene.RT, 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation(Gene.RT, 67, "NA".toCharArray())
			.containsSharedAA(new AAMutation(Gene.RT, 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation(Gene.RT, 65, '*')
			.containsSharedAA(new AAMutation(Gene.RT, 65, '*')));
		assertFalse(
			new AAMutation(Gene.RT, 65, '*')
			.containsSharedAA(Sets.newSet('*'), true));
		assertTrue(
			new AAMutation(Gene.RT, 65, '*')
			.containsSharedAA(Sets.newSet('*'), false));
		assertFalse(
			new AAMutation(Gene.RT, 69, "TD".toCharArray())
			.containsSharedAA(Sets.newSet('T'), true));
		assertTrue(
			new AAMutation(Gene.RT, 69, "TD".toCharArray())
			.containsSharedAA(Sets.newSet('T'), false));
	}

	@Test
	public void testGetShortText() {
		assertEquals(
			"T69i", new AAMutation(Gene.RT, 69, '_').getShortText());
		assertEquals(
			"D67d", new AAMutation(Gene.RT, 67, '-').getShortText());
		assertEquals(
			"K65KA", new AAMutation(Gene.RT, 65, "KA".toCharArray()).getShortText());
		assertEquals(
			"K65KA", new AAMutation(Gene.RT, 65, "AK".toCharArray()).getShortText());
		assertEquals(
			"K65N", new AAMutation(Gene.RT, 65, 'N').getShortText());
	}

	@Test
	public void testGetASIFormat() {
		assertEquals(
			"T69i", new AAMutation(Gene.RT, 69, '_').getASIFormat());
		assertEquals(
			"D67d", new AAMutation(Gene.RT, 67, '-').getASIFormat());
		assertEquals(
			"K65Z", new AAMutation(Gene.RT, 65, '*').getASIFormat());
	}

	@Test
	public void testGetHIVDBFormat() {
		assertEquals(
			"69#", new AAMutation(Gene.RT, 69, '_').getHIVDBFormat());
		assertEquals(
			"67~", new AAMutation(Gene.RT, 67, '-').getHIVDBFormat());
		assertEquals(
			"65N", new AAMutation(Gene.RT, 65, 'N').getHIVDBFormat());
	}

	@Test
	public void testGetTypes() {
		Mutation mut1 = new AAMutation(Gene.PR, 50, "VEF".toCharArray());
		Mutation mut2 = new AAMutation(Gene.PR, 51, "VEF".toCharArray());
		List<MutType> eTyoes1 = new ArrayList<>();
		eTyoes1.add(MutType.Major);
		eTyoes1.add(MutType.Accessory);
		List<MutType>eTyoes2 = new ArrayList<>();
		eTyoes2.add(MutType.Other);
		assertEquals(eTyoes1, mut1.getTypes());
		assertEquals(eTyoes1, mut1.getTypes()); // post instantiation of types
		assertEquals(eTyoes2, mut2.getTypes());
	}

	@Test
	public void testGetPrimaryType() {
		final Mutation majorMut = new AAMutation(Gene.PR, 50, "VEF".toCharArray());
		final Mutation otherMut = new AAMutation(Gene.PR, 51, "VEF".toCharArray());
		assertEquals(MutType.Major, majorMut.getPrimaryType());
		assertEquals(MutType.Other, otherMut.getPrimaryType());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		final Mutation mut1 = new AAMutation(Gene.RT, 69, '_');
		final Mutation mut2 = new AAMutation(Gene.RT, 69, 'i');
		assertEquals(mut1, mut2);
		assertEquals(mut1, mut1);
		assertNotEquals(mut1, null);
		assertNotEquals(mut1, "T69_");
	}
	
	@Test
	public void testGetHumanFormat() {
		Mutation mut1 = new AAMutation(Gene.RT, 65, "KN".toCharArray());
		Mutation mut2 = new AAMutation(Gene.RT, 65, "NK".toCharArray());
		Mutation mut3 = new AAMutation(Gene.RT, 118, Sets.newSet('_'));
		Mutation mut4 = new AAMutation(Gene.RT, 118, Sets.newSet('#'));
		Mutation mut6 = new AAMutation(Gene.RT, 69, '-');
		Mutation mut8 = new AAMutation(Gene.IN, 155, "S".toCharArray());
		Mutation mut9 = new AAMutation(Gene.IN, 155, "NS".toCharArray());
		Mutation mut10 = new AAMutation(Gene.RT, 10, 'S');
		Mutation mut11 = new AAMutation(Gene.PR, 10, 'S');
		Mutation mut12 = new AAMutation(Gene.RT, 215, "FIST".toCharArray());
		Mutation mut13 = new AAMutation(Gene.RT, 215, Sets.newSet('T', 'S', 'N', 'Y'));
		Mutation mut14 = new AAMutation(Gene.RT, 188, "YL*".toCharArray());
		Mutation mut15 = new AAMutation(Gene.RT, 188, '*');
		Mutation mut16 = new AAMutation(Gene.IN, 263, "RKGY".toCharArray());
		Mutation mut17 = new AAMutation(Gene.IN, 263, 'X');

		assertEquals("K65KN", mut1.getHumanFormat());
		assertEquals("K65KN", mut2.getHumanFormat());
		assertEquals(mut1, mut2);
		assertEquals(mut1.hashCode(), mut2.hashCode());

		assertEquals("V118Insertion", mut3.getHumanFormat());
		assertEquals("V118Insertion", mut4.getHumanFormat());
		assertNotEquals(mut1, mut3);
		assertNotEquals(mut2, mut4);
		assertEquals(mut3, mut4);
		// we consider insertions are the same
		assertEquals(mut3.hashCode(), mut4.hashCode());

		assertEquals("T69Deletion", mut6.getHumanFormat());

		assertEquals("N155S", mut8.getHumanFormat());
		assertEquals("N155NS", mut9.getHumanFormat());
		assertNotEquals(mut8, mut9);

		assertEquals("V10S", mut10.getHumanFormat());
		assertEquals("L10S", mut11.getHumanFormat());
		assertNotEquals(mut10, mut11);
		assertNotEquals(mut10.hashCode(), mut11.hashCode());

		assertEquals("T215TFIS", mut12.getHumanFormat());
		assertEquals("T215TNSY", mut13.getHumanFormat());
		assertNotEquals(mut12, mut13);
		assertNotEquals(mut12.hashCode(), mut13.hashCode());

		assertEquals("Y188Y*L", mut14.getHumanFormat());
		assertEquals("Y188*", mut15.getHumanFormat());
		assertNotEquals(mut14, mut15);
		assertNotEquals(mut14.hashCode(), mut15.hashCode());

		// TODO: shouldn't these two the same?
		assertEquals("R263RGKY", mut16.getHumanFormat());
		assertEquals("R263X", mut17.getHumanFormat());
		assertNotEquals(mut16, mut17);
		assertNotEquals(mut16.hashCode(), mut17.hashCode());
	}
	
	@Test
	public void testGetHumanFormatWithGene() {
		Mutation mut1 = new AAMutation(Gene.RT, 65, "KN".toCharArray());
		Mutation mut2 = new AAMutation(Gene.RT, 65, "NK".toCharArray());
		Mutation mut3 = new AAMutation(Gene.RT, 118, "_".toCharArray());
		Mutation mut4 = new AAMutation(Gene.RT, 118, "#".toCharArray());
		Mutation mut6 = new AAMutation(Gene.RT, 69, "-".toCharArray());
		Mutation mut8 = new AAMutation(Gene.IN, 155, "S".toCharArray());
		Mutation mut9 = new AAMutation(Gene.IN, 155, "NS".toCharArray());
		Mutation mut10 = new AAMutation(Gene.RT, 10, "S".toCharArray());
		Mutation mut11 = new AAMutation(Gene.PR, 10, "S".toCharArray());
		Mutation mut12 = new AAMutation(Gene.RT, 215, "FIST".toCharArray());
		Mutation mut13 = new AAMutation(Gene.RT, 215, "TSNY".toCharArray());
		Mutation mut14 = new AAMutation(Gene.RT, 188, "YL*".toCharArray());
		Mutation mut15 = new AAMutation(Gene.RT, 188, "*".toCharArray());
		Mutation mut16 = new AAMutation(Gene.IN, 263, "RKGY".toCharArray());
		Mutation mut17 = new AAMutation(Gene.IN, 263, "X".toCharArray());
		assertEquals("RT_K65KN", mut1.getHumanFormatWithGene());
		assertEquals("RT_K65KN", mut2.getHumanFormatWithGene());
		assertEquals("RT_V118Insertion", mut3.getHumanFormatWithGene());
		assertEquals("RT_V118Insertion", mut4.getHumanFormatWithGene());
		assertEquals("RT_T69Deletion", mut6.getHumanFormatWithGene());
		assertEquals("IN_N155S", mut8.getHumanFormatWithGene());
		assertEquals("IN_N155NS", mut9.getHumanFormatWithGene());
		assertEquals("RT_V10S", mut10.getHumanFormatWithGene());
		assertEquals("PR_L10S", mut11.getHumanFormatWithGene());
		assertEquals("RT_T215TFIS", mut12.getHumanFormatWithGene());
		assertEquals("RT_T215TNSY", mut13.getHumanFormatWithGene());
		assertEquals("RT_Y188Y*L", mut14.getHumanFormatWithGene());
		assertEquals("RT_Y188*", mut15.getHumanFormatWithGene());
		assertEquals("IN_R263RGKY", mut16.getHumanFormatWithGene());
		assertEquals("IN_R263X", mut17.getHumanFormatWithGene());
	}
	
	@Test
	public void testGetHumanFormatWithoutCons() {
		assertEquals(
			"69Insertion",
			new AAMutation(Gene.RT, 69, '#').getHumanFormatWithoutLeadingRef());
		assertEquals(
			"67Deletion",
			new AAMutation(Gene.RT, 67, '-').getHumanFormatWithoutLeadingRef());
	}

	@Test
	public void testIsIndel() {
		assertTrue(
			new AAMutation(Gene.RT, 69, '_').isIndel());
		assertTrue(
			new AAMutation(Gene.RT, 67, '-').isIndel());
		assertFalse(
			new AAMutation(Gene.RT, 67, 'V').isIndel());
		assertTrue(
			new AAMutation(Gene.RT, 67, new char[] {'i', 'V'}).isIndel());
	}

	@Test
	public void testIsMixture() {
		assertTrue(
			new AAMutation(Gene.RT, 66, "BE".toCharArray()).isMixture());
		assertTrue(
			new AAMutation(Gene.RT, 66, 'X').isMixture());
		assertFalse(
			new AAMutation(Gene.RT, 66, 'V').isMixture());
		assertTrue(
			new AAMutation(Gene.RT, 67, new char[] {'i', 'V'}).isMixture());
	}

	@Test
	public void testHasReference() {
		assertTrue(
			new AAMutation(Gene.RT, 69, 'T').hasReference());
		assertTrue(
			new AAMutation(Gene.RT, 69, "TV".toCharArray()).hasReference());
		assertFalse(
			new AAMutation(Gene.RT, 69, 'V').hasReference());
	}

	@Test 
	public void testIsUnsequenced() {
		final Mutation mut = new AAMutation(Gene.PR, 1, 'X');
		// always false in AAMutation
		assertFalse(mut.isUnsequenced());
	}
	
	@Test
	public void testGenePosition() {
		final Mutation mutPR68 = new AAMutation(Gene.PR, 68, 'N');
		final Mutation mutRT67 = new AAMutation(Gene.RT, 67, 'N');
		final Mutation mutIN155 = new AAMutation(Gene.IN, 155, 'N');
		assertEquals(mutPR68.getGenePosition(), new GenePosition(Gene.PR, 68));
		assertEquals(mutRT67.getGenePosition(), new GenePosition(Gene.RT, 67));
		assertEquals(mutIN155.getGenePosition(), new GenePosition(Gene.IN, 155));
	}
	
	@Test
	public void testIsInsertion() {
		final Mutation ins = new AAMutation(Gene.PR, 68, '_');
		final Mutation insMut = new AAMutation(Gene.PR, 68, "N_".toCharArray());
		final Mutation del = new AAMutation(Gene.PR, 68, '-');
		final Mutation delIns = new AAMutation(Gene.RT, 67, "_-".toCharArray());
		final Mutation mut = new AAMutation(Gene.PR, 68, 'N');
		assertTrue(ins.isInsertion());
		assertFalse(del.isInsertion());
		assertFalse(mut.isInsertion());
		assertTrue(insMut.isInsertion());
		assertTrue(delIns.isInsertion());
	}

	@Test
	public void testIsDeletion() {
		final Mutation del = new AAMutation(Gene.PR, 68, '-');
		final Mutation delMut = new AAMutation(Gene.PR, 68, "N-".toCharArray());
		final Mutation ins = new AAMutation(Gene.PR, 68, '_');
		final Mutation delIns = new AAMutation(Gene.RT, 67, "_-".toCharArray());
		final Mutation mut = new AAMutation(Gene.PR, 68, 'N');
		assertTrue(del.isDeletion());
		assertFalse(ins.isDeletion());
		assertFalse(mut.isDeletion());
		assertTrue(delMut.isDeletion());
		assertTrue(delIns.isDeletion());
	}
	
	@Test
	public void testHasStop() {
		final Mutation mut = new AAMutation(Gene.PR, 68, 'N');
		final Mutation stop = new AAMutation(Gene.RT, 67, '*');
		final Mutation stopMut = new AAMutation(Gene.IN, 155, "N*".toCharArray());
		assertFalse(mut.hasStop());
		assertTrue(stop.hasStop());
		assertTrue(stopMut.hasStop());
	}
	
	@Test
	public void testIsUnusual() {
		final Mutation unusualMut = new AAMutation(Gene.RT, 1, 'A');
		final Mutation usualMut = new AAMutation(Gene.RT, 1, 'L');
		final Mutation usualMuts = new AAMutation(Gene.RT, 1, "LPS".toCharArray());
		final Mutation unusualMuts = new AAMutation(Gene.RT, 1, "ACDEFG".toCharArray());
		final Mutation mixedMuts = new AAMutation(Gene.PR, 75, "AILMVSTY".toCharArray());
		final Mutation unusualMutX = new AAMutation(Gene.RT, 1, 'X');
		assertFalse(String.format("RT:%s should be usual", usualMut.toString()), usualMut.isUnusual());
		assertFalse(String.format("RT:%s should be usual", usualMuts.toString()), usualMuts.isUnusual());
		assertTrue(String.format("RT:%s should be unusual", unusualMut.toString()), unusualMut.isUnusual());
		assertTrue(String.format("RT:%s should be unusual", unusualMuts.toString()), unusualMuts.isUnusual());
		assertTrue(String.format("PR:%s should contain unusual mutation", mixedMuts.toString()), mixedMuts.isUnusual());
		assertTrue(String.format("RT:%s should be unusual", unusualMutX.toString()), unusualMutX.isUnusual());
	}
	
	@Test
	public void testIsSDRM() {
		final Mutation mut = new AAMutation(Gene.PR, 24, 'N');
		final Mutation sdrmMut = new AAMutation(Gene.PR, 24, 'I');
		final Mutation mixedMuts = new AAMutation(Gene.RT, 230, "LI".toCharArray());
		assertFalse(mut.isSDRM());
		assertTrue(sdrmMut.isSDRM());
		assertTrue(mixedMuts.isSDRM());
	}
	
	@Test
	public void testIsDRM() {
		final Mutation RT69ins = new AAMutation(Gene.RT, 69, 'i');
		final Mutation IN263NK = new AAMutation(Gene.IN, 263, "NK".toCharArray());
		assertTrue(RT69ins.isDRM());
		assertTrue(IN263NK.isDRM());
	}
	
	@Test
	public void testHasBDHVN() {
		final Mutation mut = new AAMutation(Gene.PR, 24, 'N');
		// always false
		assertFalse(mut.hasBDHVN());
	}
	
	@Test
	public void testIsAmbiguous() {
		final Mutation mut = new AAMutation(Gene.PR, 24, 'N');
		final Mutation xMut = new AAMutation(Gene.PR, 24, 'X');
		final Mutation haMut = new AAMutation(Gene.PR, 24, "ACDEFG".toCharArray());
		assertFalse(mut.isAmbiguous());
		assertTrue(xMut.isAmbiguous());
		assertTrue(haMut.isAmbiguous());
	}
	
	@Test
	public void testGetHighestMutPrevalance() {
		// Since we update prevalence data periodically, we  
		// expects the following assertions to ultimately fail. 
		// Hence we must manually update these assertions every time
		// we upload new prevalence data. 
		final Mutation prevMut = new AAMutation(Gene.IN, 45, 'G');
		final Mutation prevMuts = new AAMutation(Gene.IN, 45, "HKQ".toCharArray());
		final Mutation prevMutZero = new AAMutation(Gene.IN, 45, 'C');
		final Mutation prevMutsZero = new AAMutation(Gene.IN, 45, "CDEFH".toCharArray());
		final Mutation prevMutsWCons = new AAMutation(Gene.IN, 45, "LHKQ".toCharArray());
		final Mutation prevMutsWConsAndStop = new AAMutation(Gene.IN, 45, "*LHKQ".toCharArray());
		assertEquals(0.03587, prevMut.getHighestMutPrevalence(), 1e-5);
		assertEquals(3.77107, prevMuts.getHighestMutPrevalence(), 1e-5);
		assertEquals(0.0, prevMutZero.getHighestMutPrevalence(), 1e-5);
		assertEquals(0.0, prevMutsZero.getHighestMutPrevalence(), 1e-5);
		assertEquals(3.77107, prevMutsWCons.getHighestMutPrevalence(), 1e-5);
		assertEquals(3.77107, prevMutsWConsAndStop.getHighestMutPrevalence(), 1e-5);
		assertEquals(0.0, new AAMutation(Gene.PR, 1, "PX".toCharArray()).getHighestMutPrevalence(), 1e-5);
	}
}