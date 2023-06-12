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
import com.google.common.collect.Sets;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MutationTest {

	private static HIV hiv = HIV.getInstance();

	@Test
	public void testNormalizeAAChars() {
		assertEquals(null, AAMutation.normalizeAAChars(null));
		assertEquals(Sets.newHashSet('_'), AAMutation.normalizeAAChars(Sets.newHashSet('#')));
		assertEquals(Sets.newHashSet('_'), AAMutation.normalizeAAChars(Sets.newHashSet('i')));
		assertEquals(Sets.newHashSet('-'), AAMutation.normalizeAAChars(Sets.newHashSet('~')));
		assertEquals(Sets.newHashSet('-'), AAMutation.normalizeAAChars(Sets.newHashSet('d')));
		assertEquals(Sets.newHashSet('*'), AAMutation.normalizeAAChars(Sets.newHashSet('Z')));
		assertEquals(Sets.newHashSet('*'), AAMutation.normalizeAAChars(Sets.newHashSet('.')));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testPositionOutOfGene() {
		new AAMutation<>(hiv.getGene("HIV1PR"), 100, 'A');
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSameGene() {
		new AAMutation<>(hiv.getGene("HIV1PR"), 68, 'A')
			.mergesWith(new AAMutation<>(hiv.getGene("HIV1RT"), 68, 'C'));
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSamePos() {
		new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'A')
			.mergesWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 68, 'C'));
	}

	@SuppressWarnings("deprecation")
	public void testMergesWithIndel() {
		assertEquals(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, new char[] {'A', '-'}),
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'A')
				.mergesWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-')));
	}

	@SuppressWarnings("deprecation")
	public void testIndelMergesWith() {
		assertEquals(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, new char[] {'A', '-'}),
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-')
				.mergesWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'A')));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testMergesWith() {
		assertEquals(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AC".toCharArray()),
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'A')
				.mergesWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'C')));
		assertEquals(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AC".toCharArray()),
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AC".toCharArray())
				.mergesWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'C')));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsBy() {
		AAMutation<HIV> pr67ANXDMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, "ANXD".toCharArray());
		AAMutation<HIV> pr67NMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, 'N');
		AAMutation<HIV> pr67XMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, 'X');
		AAMutation<HIV> pr67ADMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, "AD".toCharArray());
		AAMutation<HIV> pr67ADXMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, "AXD".toCharArray());
		AAMutation<HIV> eDiffN = pr67ADXMut;
		AAMutation<HIV> eDiffX = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, "ADN".toCharArray());
		AAMutation<HIV> eDiffAD = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, "XN".toCharArray());
		AAMutation<HIV> eDiffADX = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, 'N');
		assertEquals(eDiffN, pr67ANXDMut.subtractsBy(pr67NMut));
		assertEquals(eDiffX, pr67ANXDMut.subtractsBy(pr67XMut));
		assertEquals(eDiffAD, pr67ANXDMut.subtractsBy(pr67ADMut));
		assertEquals(eDiffADX, pr67ANXDMut.subtractsBy(pr67ADXMut));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSubtractsByEdgeCases() {
		AAMutation<HIV> pr68AMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'A');
		assertEquals(null, new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'A').subtractsBy(pr68AMut));
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testSubtractsByNull() {
		new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, 'A').subtractsBy((AAMutation<HIV>) null);
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testSubtractsByNotSamePos() {
		AAMutation<HIV> pr67AMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, 'A');
		new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'A').subtractsBy(pr67AMut);
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testSubtractsByNotSameGene() {
		AAMutation<HIV> rt67AMut = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 68, 'A');
		new AAMutation<HIV>(hiv.getGene("HIV1PR"), 67, 'A').subtractsBy(rt67AMut);
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testIntersectsWithNotSameGene() {
		new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, "AC".toCharArray())
			.intersectsWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 68, 'C'));
	}

	@SuppressWarnings("deprecation")
	@Test(expected=IllegalArgumentException.class)
	public void testIntersectsWithNotSamePos() {
		new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AC".toCharArray())
			.intersectsWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 68, 'C'));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testIntersectsWith() {
		assertEquals(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'C'),
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AC".toCharArray())
				.intersectsWith(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "CD".toCharArray())));
	}

	@Test
	public void testSplit() {
		Set<AAMutation<HIV>> expected = new HashSet<>();
		expected.add(new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, 'L'));
		expected.add(new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, 'M'));
		expected.add(new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, 'Q'));
		assertEquals(expected, new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, "LMQ".toCharArray()).split());
		expected = new HashSet<>();
		expected.add(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-'));
		assertEquals(expected, new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-').split());
		expected = new HashSet<>();
		expected.add(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_'));
		assertEquals(expected, new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_').split());
		expected = new HashSet<>();
		expected.add(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'L'));
		assertEquals(expected, new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, "PL".toCharArray()).split());

		// Don't split an ambiguous mutation if the ambiguity is not caused by BDHVN
		AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 101, "ADEHKNPQT".toCharArray());
		expected = new HashSet<>();
		expected.add(mut);
		assertEquals(expected, mut.split());
	}

	@Test
	public void testIsAtDrugResistancePosition() {
		AAMutation<HIV> mutIN151Major = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, 'L');
		assertTrue(mutIN151Major.isAtDrugResistancePosition());
		assertTrue(mutIN151Major.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN150Other = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 150, 'L');
		assertFalse(mutIN150Other.isAtDrugResistancePosition());
		assertFalse(mutIN150Other.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN151Accessory = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, 'A');
		assertTrue(mutIN151Accessory.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN151Other = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, 'W');
		assertTrue(mutIN151Other.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN151InsertionOther = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, "_L".toCharArray());
		assertTrue(mutIN151InsertionOther.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN151DeletionOther = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, '-');
		assertTrue(mutIN151DeletionOther.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN151MixtureWithMajor = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, "ALW".toCharArray());
		assertTrue(mutIN151MixtureWithMajor.isAtDrugResistancePosition());

		AAMutation<HIV> mutIN151MixtureWithoutMajor = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 151, "AW".toCharArray());
		assertTrue(mutIN151MixtureWithoutMajor.isAtDrugResistancePosition());

		AAMutation<HIV> mutRT75NRTI = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 75, 'I');
		assertTrue(mutRT75NRTI.isAtDrugResistancePosition());

		AAMutation<HIV> mutRT98NNRTI = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 98, 'G');
		assertTrue(mutRT98NNRTI.isAtDrugResistancePosition());

		AAMutation<HIV> mutRT99Other = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 99, 'G');
		assertFalse(mutRT99Other.isAtDrugResistancePosition());
	}

	@Test
	public void testGetDisplayAAs() {
		assertEquals("N", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, 'N').getDisplayAAs());
		assertEquals("X", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "ACDEFG".toCharArray(), 4).getDisplayAAs());
		assertEquals("ACDEFG", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "ACDEFG".toCharArray(), 7).getDisplayAAs());
	}

	@Test
	public void testGetAAs() {
		assertEquals("N", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, 'N').getAAs());
		assertEquals("ACDEFG", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "ACDEFG".toCharArray(), 4).getAAs());
		assertEquals("ACDEFG", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "ACDEFG".toCharArray(), 7).getAAs());
	}

	@Test
	public void testGetAAsWithoutConsensus() {
		assertEquals("N", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "KN".toCharArray()).getAAsWithoutReference());
	}

	@Test
	public void testGetTriplet() {
		// not support for an HIVMutation
		assertEquals("", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, 'A').getTriplet());
	}

	@Test
	public void testGetInsertedNAs() {
		// not support for an HIVMutation
		assertEquals("", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, '_').getInsertedNAs());
	}

	@Test
	public void testIsApobecDRM() {
		assertTrue(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184, 'I').isApobecDRM());
		assertFalse(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 184, 'A').isApobecDRM());
		assertFalse(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 183, 'I').isApobecDRM());
	}

	@Test
	public void testIsApobecMut() {
		assertTrue(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 25, "KN".toCharArray()).isApobecMutation());
		assertTrue(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 25, "DN".toCharArray()).isApobecMutation());
		assertTrue(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 25, "DKN".toCharArray()).isApobecMutation());
		assertFalse(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 25, 'D').isApobecMutation());
		assertFalse(new AAMutation<HIV>(hiv.getGene("HIV1PR"), 25, 'A').isApobecMutation());
	}

	@Test
	public void testContainsSharedAA() {
		// contains shared consensus
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "KN".toCharArray())
			.containsSharedAA(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "KA".toCharArray())));
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "BKN".toCharArray())
			.containsSharedAA(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "BK".toCharArray())
			.containsSharedAA(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1PR"), 65, "NA".toCharArray())
			.containsSharedAA(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "NA".toCharArray())
			.containsSharedAA(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "NA".toCharArray())));
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, '*')
			.containsSharedAA(new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, '*')));
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, '*')
			.containsSharedAA(Sets.newHashSet('*'), true));
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, '*')
			.containsSharedAA(Sets.newHashSet('*'), false));
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, "TD".toCharArray())
			.containsSharedAA(Sets.newHashSet('T'), true));
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, "TD".toCharArray())
			.containsSharedAA(Sets.newHashSet('T'), false));
	}

	@Test
	public void testGetShortText() {
		assertEquals(
			"T69i", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_').getShortText());
		assertEquals(
			"D67d", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-').getShortText());
		assertEquals(
			"K65KA", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "KA".toCharArray()).getShortText());
		assertEquals(
			"K65KA", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "AK".toCharArray()).getShortText());
		assertEquals(
			"K65N", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, 'N').getShortText());
	}

	@Test
	public void testGetASIFormat() {
		assertEquals(
			"T69i", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_').getASIFormat());
		assertEquals(
			"D67d", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-').getASIFormat());
		assertEquals(
			"K65Z", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, '*').getASIFormat());
	}

	@Test
	public void testGetHIVDBFormat() {
		assertEquals(
			"69#", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_').getHIVDBFormat());
		assertEquals(
			"67~", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-').getHIVDBFormat());
		assertEquals(
			"65N", new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, 'N').getHIVDBFormat());
	}

	@Test
	public void testGetTypes() {
		AAMutation<HIV> mut1 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 50, "VEF".toCharArray());
		AAMutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 51, "VEF".toCharArray());
		List<MutationType<HIV>> eTyoes1 = new ArrayList<>();
		eTyoes1.add(hiv.getMutationType("Major"));
		eTyoes1.add(hiv.getMutationType("Accessory"));
		List<MutationType<HIV>>eTyoes2 = new ArrayList<>();
		eTyoes2.add(hiv.getMutationType("Other"));
		assertEquals(eTyoes1, mut1.getTypes());
		assertEquals(eTyoes1, mut1.getTypes()); // post instantiation of types
		assertEquals(eTyoes2, mut2.getTypes());
	}

	@Test
	public void testGetPrimaryType() {
		final AAMutation<HIV> majorMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 50, "VEF".toCharArray());
		final AAMutation<HIV> otherMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 51, "VEF".toCharArray());
		assertEquals(hiv.getMutationType("Major"), majorMut.getPrimaryType());
		assertEquals(hiv.getMutationType("Other"), otherMut.getPrimaryType());
	}

	@Test
	public void testEqualsAndHashCode() {
		final AAMutation<HIV> mut1 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_');
		final AAMutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, 'i');
		assertEquals(mut1, mut2);
		assertEquals(mut1, mut1);
		assertNotEquals(mut1, null);
		assertNotEquals(mut1, "T69_");
	}

	@Test
	public void testGetHumanFormat() {
		AAMutation<HIV> mut1 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "KN".toCharArray());
		AAMutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "NK".toCharArray());
		AAMutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 118, Sets.newHashSet('_'));
		AAMutation<HIV> mut4 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 118, Sets.newHashSet('#'));
		AAMutation<HIV> mut6 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '-');
		AAMutation<HIV> mut8 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 155, "S".toCharArray());
		AAMutation<HIV> mut9 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 155, "NS".toCharArray());
		AAMutation<HIV> mut10 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10, 'S');
		AAMutation<HIV> mut11 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'S');
		AAMutation<HIV> mut12 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, "FIST".toCharArray());
		AAMutation<HIV> mut13 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, Sets.newHashSet('T', 'S', 'N', 'Y'));
		AAMutation<HIV> mut14 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 188, "YL*".toCharArray());
		AAMutation<HIV> mut15 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 188, '*');
		AAMutation<HIV> mut16 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, "RKGY".toCharArray());
		AAMutation<HIV> mut17 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'X');

		assertEquals("K65KN", mut1.getHumanFormat());
		assertEquals("K65KN", mut2.getHumanFormat());
		assertEquals(mut1, mut2);
		assertEquals(mut1.hashCode(), mut2.hashCode());

		assertEquals("V118ins", mut3.getHumanFormat());
		assertEquals("V118ins", mut4.getHumanFormat());
		assertNotEquals(mut1, mut3);
		assertNotEquals(mut2, mut4);
		assertEquals(mut3, mut4);
		// we consider insertions are the same
		assertEquals(mut3.hashCode(), mut4.hashCode());

		assertEquals("T69del", mut6.getHumanFormat());

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
		AAMutation<HIV> mut1 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "KN".toCharArray());
		AAMutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 65, "NK".toCharArray());
		AAMutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 118, "_".toCharArray());
		AAMutation<HIV> mut4 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 118, "#".toCharArray());
		AAMutation<HIV> mut6 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, "-".toCharArray());
		AAMutation<HIV> mut8 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 155, "S".toCharArray());
		AAMutation<HIV> mut9 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 155, "NS".toCharArray());
		AAMutation<HIV> mut10 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 10, "S".toCharArray());
		AAMutation<HIV> mut11 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 10, "S".toCharArray());
		AAMutation<HIV> mut12 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, "FIST".toCharArray());
		AAMutation<HIV> mut13 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, "TSNY".toCharArray());
		AAMutation<HIV> mut14 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 188, "YL*".toCharArray());
		AAMutation<HIV> mut15 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 188, "*".toCharArray());
		AAMutation<HIV> mut16 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, "RKGY".toCharArray());
		AAMutation<HIV> mut17 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, "X".toCharArray());
		assertEquals("HIV1RT:K65KN", mut1.getHumanFormatWithGene());
		assertEquals("RT:K65KN", mut1.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:K65KN", mut2.getHumanFormatWithGene());
		assertEquals("RT:K65KN", mut2.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:V118ins", mut3.getHumanFormatWithGene());
		assertEquals("RT:V118ins", mut3.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:V118ins", mut4.getHumanFormatWithGene());
		assertEquals("RT:V118ins", mut4.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:T69del", mut6.getHumanFormatWithGene());
		assertEquals("RT:T69del", mut6.getHumanFormatWithAbstractGene());
		assertEquals("HIV1IN:N155S", mut8.getHumanFormatWithGene());
		assertEquals("IN:N155S", mut8.getHumanFormatWithAbstractGene());
		assertEquals("HIV1IN:N155NS", mut9.getHumanFormatWithGene());
		assertEquals("IN:N155NS", mut9.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:V10S", mut10.getHumanFormatWithGene());
		assertEquals("RT:V10S", mut10.getHumanFormatWithAbstractGene());
		assertEquals("HIV1PR:L10S", mut11.getHumanFormatWithGene());
		assertEquals("PR:L10S", mut11.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:T215TFIS", mut12.getHumanFormatWithGene());
		assertEquals("RT:T215TFIS", mut12.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:T215TNSY", mut13.getHumanFormatWithGene());
		assertEquals("RT:T215TNSY", mut13.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:Y188Y*L", mut14.getHumanFormatWithGene());
		assertEquals("RT:Y188Y*L", mut14.getHumanFormatWithAbstractGene());
		assertEquals("HIV1RT:Y188*", mut15.getHumanFormatWithGene());
		assertEquals("RT:Y188*", mut15.getHumanFormatWithAbstractGene());
		assertEquals("HIV1IN:R263RGKY", mut16.getHumanFormatWithGene());
		assertEquals("IN:R263RGKY", mut16.getHumanFormatWithAbstractGene());
		assertEquals("HIV1IN:R263X", mut17.getHumanFormatWithGene());
		assertEquals("IN:R263X", mut17.getHumanFormatWithAbstractGene());
	}

	@Test
	public void testGetHumanFormatWithoutCons() {
		assertEquals(
			"69ins",
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '#').getHumanFormatWithoutLeadingRef());
		assertEquals(
			"67del",
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-').getHumanFormatWithoutLeadingRef());
	}

	@Test
	public void testIsIndel() {
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, '_').isIndel());
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '-').isIndel());
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'V').isIndel());
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, new char[] {'i', 'V'}).isIndel());
	}

	@Test
	public void testIsMixture() {
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 66, "BE".toCharArray()).isMixture());
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 66, 'X').isMixture());
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 66, 'V').isMixture());
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, new char[] {'i', 'V'}).isMixture());
	}

	@Test
	public void testHasReference() {
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, 'T').hasReference());
		assertTrue(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, "TV".toCharArray()).hasReference());
		assertFalse(
			new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, 'V').hasReference());
	}

	@Test
	public void testIsUnsequenced() {
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, 'X');
		// always false in HIVMutation
		assertFalse(mut.isUnsequenced());
	}

	@Test
	public void testGenePosition() {
		final AAMutation<HIV> mutPR68 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'N');
		final AAMutation<HIV> mutRT67 = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, 'N');
		final AAMutation<HIV> mutIN155 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 155, 'N');
		assertEquals(mutPR68.getGenePosition(), new GenePosition<>(hiv.getGene("HIV1PR"), 68));
		assertEquals(mutRT67.getGenePosition(), new GenePosition<>(hiv.getGene("HIV1RT"), 67));
		assertEquals(mutIN155.getGenePosition(), new GenePosition<>(hiv.getGene("HIV1IN"), 155));
	}

	@Test
	public void testIsInsertion() {
		final AAMutation<HIV> ins = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, '_');
		final AAMutation<HIV> insMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, "N_".toCharArray());
		final AAMutation<HIV> del = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, '-');
		final AAMutation<HIV> delIns = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "_-".toCharArray());
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'N');
		assertTrue(ins.isInsertion());
		assertFalse(del.isInsertion());
		assertFalse(mut.isInsertion());
		assertTrue(insMut.isInsertion());
		assertTrue(delIns.isInsertion());
	}

	@Test
	public void testIsDeletion() {
		final AAMutation<HIV> del = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, '-');
		final AAMutation<HIV> delMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, "N-".toCharArray());
		final AAMutation<HIV> ins = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, '_');
		final AAMutation<HIV> delIns = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, "_-".toCharArray());
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'N');
		assertTrue(del.isDeletion());
		assertFalse(ins.isDeletion());
		assertFalse(mut.isDeletion());
		assertTrue(delMut.isDeletion());
		assertTrue(delIns.isDeletion());
	}

	@Test
	public void testHasStop() {
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 68, 'N');
		final AAMutation<HIV> stop = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 67, '*');
		final AAMutation<HIV> stopMut = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 155, "N*".toCharArray());
		assertFalse(mut.hasStop());
		assertTrue(stop.hasStop());
		assertTrue(stopMut.hasStop());
	}

	@Test
	public void testIsUnusual() {
		final AAMutation<HIV> unusualMut = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 1, 'A');
		final AAMutation<HIV> usualMut = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 1, 'L');
		final AAMutation<HIV> usualMuts = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 1, "LPS".toCharArray());
		final AAMutation<HIV> unusualMuts = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 1, "ACDEFG".toCharArray());
		final AAMutation<HIV> mixedMuts = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 75, "AILMVSTY".toCharArray());
		final AAMutation<HIV> unusualMutX = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 1, 'X');
		assertFalse(String.format("RT:%s should be usual", usualMut.toString()), usualMut.isUnusual());
		assertFalse(String.format("RT:%s should be usual", usualMuts.toString()), usualMuts.isUnusual());
		assertTrue(String.format("RT:%s should be unusual", unusualMut.toString()), unusualMut.isUnusual());
		assertTrue(String.format("RT:%s should be unusual", unusualMuts.toString()), unusualMuts.isUnusual());
		assertTrue(String.format("PR:%s should contain unusual mutation", mixedMuts.toString()), mixedMuts.isUnusual());
		assertTrue(String.format("RT:%s should be unusual", unusualMutX.toString()), unusualMutX.isUnusual());
	}

	@Test
	public void testIsSDRM() {
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 24, 'N');
		final AAMutation<HIV> sdrmMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 24, 'I');
		final AAMutation<HIV> mixedMuts = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 230, "LI".toCharArray());
		assertFalse(mut.isSDRM());
		assertTrue(sdrmMut.isSDRM());
		assertTrue(mixedMuts.isSDRM());
	}

	@Test
	public void testIsDRM() {
		final AAMutation<HIV> RT69ins = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, 'i');
		final AAMutation<HIV> IN263NK = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, "NK".toCharArray());
		assertTrue(RT69ins.isDRM());
		assertTrue(IN263NK.isDRM());
	}

	@Test
	public void testHasBDHVN() {
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 24, 'N');
		// always false
		assertFalse(mut.hasBDHVN());
	}

	@Test
	public void testIsAmbiguous() {
		final AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 24, 'N');
		final AAMutation<HIV> xMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 24, 'X');
		final AAMutation<HIV> haMut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 24, "ABDEFGH".toCharArray());
		assertFalse(mut.isAmbiguous());
		assertTrue(xMut.isAmbiguous());
		assertTrue(haMut.isAmbiguous());
	}

	@Test
	public void testGetHighestMutPrevalence() {
		// Since we update prevalence data periodically, we
		// expects the following assertions to ultimately fail.
		// Hence we must manually update these assertions every time
		// we upload new prevalence data.
		final AAMutation<HIV> prevMut = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 45, 'G');
		final AAMutation<HIV> prevMuts = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 45, "HKQ".toCharArray());
		final AAMutation<HIV> prevMutZero = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 45, 'C');
		final AAMutation<HIV> prevMutsZero = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 45, "CDEFH".toCharArray());
		final AAMutation<HIV> prevMutsWCons = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 45, "LHKQ".toCharArray());
		final AAMutation<HIV> prevMutsWConsAndStop = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 45, "*LHKQ".toCharArray());
		assertEquals(0.02, prevMut.getHighestMutPrevalence(), 1e-2);
		assertEquals(4., prevMuts.getHighestMutPrevalence(), 1);
		assertEquals(0.0, prevMutZero.getHighestMutPrevalence(), 1e-8);
		assertEquals(0.004, prevMutsZero.getHighestMutPrevalence(), 1e-3);
		assertEquals(4., prevMutsWCons.getHighestMutPrevalence(), 1);
		assertEquals(4., prevMutsWConsAndStop.getHighestMutPrevalence(), 1);
		assertEquals(0.0, new AAMutation<HIV>(hiv.getGene("HIV1PR"), 1, "PX".toCharArray()).getHighestMutPrevalence(), 1e-8);
	}

	@Test
	public void testAmbiguousTSMDrugClass() {
		AAMutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 101, "ADE".toCharArray());
		assertEquals(hiv.getDrugClass("NNRTI"), mut.getTSMDrugClass());
		mut = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 101, "ADEHKNPQT".toCharArray());
		assertFalse(mut.isTSM());
		assertNull(mut.getTSMDrugClass());
	}

}
