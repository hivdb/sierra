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

import edu.stanford.hivdb.mutations.Mutation.InvalidMutationStringException;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MutationTest {

	@Test
	public void testFromNucAminoMut() {
		final Map<String, Object> mutMap = new HashMap<>();
		mutMap.put("Position", 1.0);
		mutMap.put("CodonText", "AAC");
		mutMap.put("IsInsertion", false);
		mutMap.put("IsDeletion", false);
		
		final Mutation mut = Mutation.fromNucAminoMutation(Gene.PR, 1, mutMap);
		final Mutation eMut = new Mutation(Gene.PR, 1, "N");
		assertTrue(mut.equals(eMut));
	}
	
	@Test
	public void testFromNucAminoMutWithDeletion() {
		final Map<String, Object> mutMap = new HashMap<>();
		mutMap.put("Position", 1.0);
		mutMap.put("CodonText", "AAC");
		mutMap.put("IsInsertion", false);
		mutMap.put("IsDeletion", true);
		
		final Mutation mut = Mutation.fromNucAminoMutation(Gene.PR, 1, mutMap);
		final Mutation eMut = new Mutation(Gene.PR, 1, "-");
		assertTrue(mut.equals(eMut));
	}
	
	@Test
	public void testFromNucAminoMutWithInsertion() {
		final Map<String, Object> mutMap = new HashMap<>(); 
		mutMap.put("Position", 1.0);
		mutMap.put("CodonText", "AAC");
		mutMap.put("InsertedCodonsText", "AAC");
		mutMap.put("IsInsertion", true);
		mutMap.put("IsDeletion", false);
		
		final Mutation mut = Mutation.fromNucAminoMutation(Gene.PR, 1, mutMap);
		final Mutation eMut = new Mutation(Gene.PR, 1, "N_N", "AAC", "AAC");			
		assertTrue(mut.equals(eMut));
	}
	
	@Test
	public void testNormalizeAAs() {
		assertEquals(null, Mutation.normalizeAAs(null));
		assertEquals("_", Mutation.normalizeAAs("#"));
		assertEquals("-", Mutation.normalizeAAs("~"));
		assertEquals("*", Mutation.normalizeAAs("Z"));
		assertEquals("*", Mutation.normalizeAAs("."));
		assertEquals("_", Mutation.normalizeAAs("Insertion"));
		assertEquals("-", Mutation.normalizeAAs("Deletion"));
		assertEquals("ACDE", Mutation.normalizeAAs("DECA"));
		assertEquals("ACDE", Mutation.normalizeAAs("deca"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testPositionOutOfGene() {
		new Mutation(Gene.PR, 100, 'A');
	}

	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSameGene() {
		new Mutation(Gene.PR, 68, 'A')
			.mergesWith(new Mutation(Gene.RT, 68, "C"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSamePos() {
		new Mutation(Gene.RT, 67, 'A')
			.mergesWith(new Mutation(Gene.RT, 68, "C"));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testMergesWithIndel() {
		new Mutation(Gene.RT, 67, 'A')
			.mergesWith(new Mutation(Gene.RT, 67, "-"));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testIndelMergesWith() {
		new Mutation(Gene.RT, 67, '-')
			.mergesWith(new Mutation(Gene.RT, 67, "A"));
	}

	@Test
	public void testMergesWith() {
		assertEquals(
			new Mutation(Gene.RT, 67, "AC"),
			new Mutation(Gene.RT, 67, 'A')
				.mergesWith(new Mutation(Gene.RT, 67, "C")));
		assertEquals(
			new Mutation(Gene.RT, 67, "AC"),
			new Mutation(Gene.RT, 67, "AC")
				.mergesWith(new Mutation(Gene.RT, 67, "C")));
	}

	@Test
	public void testSubtractsBy() {
		Mutation pr67ANXDMut = new Mutation(Gene.PR, 67, "ANXD");
		Mutation pr67NMut = new Mutation(Gene.PR, 67, 'N');
		Mutation pr67XMut = new Mutation(Gene.PR, 67, 'X');
		Mutation pr67ADMut = new Mutation(Gene.PR, 67, "AD");
		Mutation pr67ADXMut = new Mutation(Gene.PR, 67, "AXD");
		Mutation eDiffN = pr67ADXMut;
		Mutation eDiffX = new Mutation(Gene.PR, 67, "ADN");
		Mutation eDiffAD = new Mutation(Gene.PR, 67, "XN");
		Mutation eDiffADX = new Mutation(Gene.PR, 67, "N");
		assertEquals(eDiffN, pr67ANXDMut.subtractsBy(pr67NMut));
		assertEquals(eDiffX, pr67ANXDMut.subtractsBy(pr67XMut));
		assertEquals(eDiffAD, pr67ANXDMut.subtractsBy(pr67ADMut));
		assertEquals(eDiffADX, pr67ANXDMut.subtractsBy(pr67ADXMut));
	}
	
	@Test
	public void testSubtractsByEdgeCases() {
		Mutation pr67AMut = new Mutation(Gene.PR, 67, 'A');
		Mutation pr68AMut = new Mutation(Gene.PR, 68, 'A');
		Mutation rt67AMut = new Mutation(Gene.RT, 68, 'A');
		assertEquals(null, new Mutation(Gene.PR, 68, 'A').subtractsBy(pr68AMut));
		assertEquals(pr67AMut, new Mutation(Gene.PR, 67, 'A').subtractsBy(null));
		assertEquals(pr68AMut, new Mutation(Gene.PR, 68, 'A').subtractsBy(pr67AMut));
		assertEquals(pr67AMut, new Mutation(Gene.PR, 67, 'A').subtractsBy(rt67AMut));	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIntersectsWithNotSameGene() {
		new Mutation(Gene.PR, 68, "AC")
			.intersectsWith(new Mutation(Gene.RT, 68, "C"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIntersectsWithNotSamePos() {
		new Mutation(Gene.RT, 67, "AC")
			.intersectsWith(new Mutation(Gene.RT, 68, "C"));
	}

	@Test
	public void testIntersectsWith() {
		assertEquals(
			new Mutation(Gene.RT, 67, "C"),
			new Mutation(Gene.RT, 67, "AC")
				.intersectsWith(new Mutation(Gene.RT, 67, "CD")));
	}

	@Test
	public void testSplit() {
		Set<Mutation> expected = new HashSet<>();
		expected.add(new Mutation(Gene.IN, 151, 'L'));
		expected.add(new Mutation(Gene.IN, 151, 'M'));
		expected.add(new Mutation(Gene.IN, 151, 'Q'));
		assertEquals(expected, new Mutation(Gene.IN, 151, "LMQ").split());
		expected = new HashSet<>();
		expected.add(new Mutation(Gene.RT, 67, '-'));
		assertEquals(expected, new Mutation(Gene.RT, 67, '-').split());
		expected = new HashSet<>();
		expected.add(new Mutation(Gene.RT, 69, '_'));
		assertEquals(expected, new Mutation(Gene.RT, 69, '_').split());
	}

	@Test
	public void testIsAtDrugResistancePosition() {
		Mutation mutIN151Major = new Mutation(Gene.IN, 151, "L");
		assertTrue(mutIN151Major.isAtDrugResistancePosition());
		assertTrue(mutIN151Major.isAtDrugResistancePosition());

		Mutation mutIN150Other = new Mutation(Gene.IN, 150, "L");
		assertFalse(mutIN150Other.isAtDrugResistancePosition());
		assertFalse(mutIN150Other.isAtDrugResistancePosition());

		Mutation mutIN151Accessory = new Mutation(Gene.IN, 151, "A");
		assertTrue(mutIN151Accessory.isAtDrugResistancePosition());

		Mutation mutIN151Other = new Mutation(Gene.IN, 151, "W");
		assertTrue(mutIN151Other.isAtDrugResistancePosition());

		Mutation mutIN151InsertionOther = new Mutation(Gene.IN, 151, "_L");
		assertTrue(mutIN151InsertionOther.isAtDrugResistancePosition());

		Mutation mutIN151DeletionOther = new Mutation(Gene.IN, 151, "-");
		assertTrue(mutIN151DeletionOther.isAtDrugResistancePosition());

		Mutation mutIN151MixtureWithMajor = new Mutation(Gene.IN, 151, "ALW");
		assertTrue(mutIN151MixtureWithMajor.isAtDrugResistancePosition());

		Mutation mutIN151MixtureWithoutMajor = new Mutation(Gene.IN, 151, "AW");
		assertTrue(mutIN151MixtureWithoutMajor.isAtDrugResistancePosition());

		Mutation mutRT75NRTI = new Mutation(Gene.RT, 75, "I");
		assertTrue(mutRT75NRTI.isAtDrugResistancePosition());

		Mutation mutRT98NNRTI = new Mutation(Gene.RT, 98, "G");
		assertTrue(mutRT98NNRTI.isAtDrugResistancePosition());

		Mutation mutRT99Other = new Mutation(Gene.RT, 99, "G");
		assertFalse(mutRT99Other.isAtDrugResistancePosition());
	}

	@Test
	public void testGetAAsWithoutConsensus() {
		assertEquals("N", new Mutation(Gene.RT, 65, "KN").getAAsWithoutConsensus());
	}

	@Test
	public void testIsApobecDRM() {
		assertTrue(new Mutation(Gene.RT, 184, "I").isApobecDRM());
		assertFalse(new Mutation(Gene.RT, 184, "A").isApobecDRM());
		assertFalse(new Mutation(Gene.RT, 183, "I").isApobecDRM());
	}

	@Test
	public void testIsApobecMut() {
		assertTrue(new Mutation(Gene.PR, 25, "KN").isApobecMutation());
		assertTrue(new Mutation(Gene.PR, 25, "K").isApobecMutation());
		assertTrue(new Mutation(Gene.PR, 25, "DN").isApobecMutation());
		assertTrue(new Mutation(Gene.PR, 25, "DKN").isApobecMutation());
		assertFalse(new Mutation(Gene.PR, 25, "D").isApobecMutation());
		assertFalse(new Mutation(Gene.PR, 25, "A").isApobecMutation());
	}

	@Test
	public void testHasBDHVN() {
		assertFalse(new Mutation(Gene.PR, 32, "K", "AAA").hasBDHVN());
		assertFalse(new Mutation(Gene.PR, 32, "K", "AAR").hasBDHVN());
		assertTrue(new Mutation(Gene.PR, 32, "NK", "AAB").hasBDHVN());
		assertTrue(new Mutation(Gene.PR, 32, "NK", "AAD").hasBDHVN());
		assertTrue(new Mutation(Gene.PR, 32, "NK", "AAH").hasBDHVN());
		assertTrue(new Mutation(Gene.PR, 32, "NK", "AAV").hasBDHVN());
		assertTrue(new Mutation(Gene.PR, 32, "NK", "AAN").hasBDHVN());
	}

	@Test
	public void testContainsSharedAA() {
		// contains shared consensus
		assertFalse(
			new Mutation(Gene.RT, 65, "KN")
			.containsSharedAA(new Mutation(Gene.RT, 65, "KA")));
		assertTrue(
			new Mutation(Gene.RT, 65, "BKN")
			.containsSharedAA(new Mutation(Gene.RT, 65, "NA")));
		assertFalse(
			new Mutation(Gene.RT, 65, "BK")
			.containsSharedAA(new Mutation(Gene.RT, 65, "NA")));
		assertFalse(
			new Mutation(Gene.PR, 65, "NA")
			.containsSharedAA(new Mutation(Gene.RT, 65, "NA")));
		assertFalse(
			new Mutation(Gene.RT, 67, "NA")
			.containsSharedAA(new Mutation(Gene.RT, 65, "NA")));
		assertFalse(
			new Mutation(Gene.RT, 65, "*")
			.containsSharedAA(new Mutation(Gene.RT, 65, "*")));
	}

	@Test
	public void testGetShortText() {
		assertEquals(
			"T69i", new Mutation(Gene.RT, 69, "_").getShortText());
		assertEquals(
			"D67d", new Mutation(Gene.RT, 67, "-").getShortText());
		assertEquals(
			"T69T_GG", new Mutation(Gene.RT, 69, "T_GG").getShortText());
		assertEquals(
			"K65KA", new Mutation(Gene.RT, 65, "KA").getShortText());
		assertEquals(
			"K65KA", new Mutation(Gene.RT, 65, "AK").getShortText());
		assertEquals(
			"K65N", new Mutation(Gene.RT, 65, "N").getShortText());
	}

	@Test
	public void testGetASIFormat() {
		assertEquals(
			"T69i", new Mutation(Gene.RT, 69, "_").getASIFormat());
		assertEquals(
			"D67d", new Mutation(Gene.RT, 67, "-").getASIFormat());
		assertEquals(
			"T69i", new Mutation(Gene.RT, 69, "T_GG").getASIFormat());
		assertEquals(
			"K65Z", new Mutation(Gene.RT, 65, "*").getASIFormat());
	}

	@Test
	public void testGetHIVDBFormat() {
		assertEquals(
			"69#", new Mutation(Gene.RT, 69, "_").getHIVDBFormat());
		assertEquals(
			"67~", new Mutation(Gene.RT, 67, "-").getHIVDBFormat());
		assertEquals(
			"69#", new Mutation(Gene.RT, 69, "T_GG").getHIVDBFormat());
		assertEquals(
			"65N", new Mutation(Gene.RT, 65, "N").getHIVDBFormat());
	}

	@Test
	public void testGetTypes() {
		Mutation mut1 = new Mutation(Gene.PR, 50, "VEF");
		List<MutType> expecteds = new ArrayList<>();
		expecteds.add(MutType.Major);
		expecteds.add(MutType.Accessory);
		assertEquals(expecteds, mut1.getTypes());
		assertEquals(expecteds, mut1.getTypes());
		Mutation mut2 = new Mutation(Gene.PR, 51, "VEF");
		expecteds = new ArrayList<>();
		expecteds.add(MutType.Other);
		assertEquals(expecteds, mut2.getTypes());
		assertEquals(expecteds, mut2.getTypes());
	}

	/*@Test
	public void testGetComment() {
		Mutation mut1 = new Mutation(Gene.PR, 50, "VEF");
		assertEquals(mut1.getComments().size(), 3);
		Mutation mut2 = new Mutation(Gene.PR, 59, "A");
		assertTrue(mut2.getComments().isEmpty());
	}*/

	@Test
	public void testEqualsAndHashCode() {
		Mutation mut1 = new Mutation(Gene.RT, 69, "_");
		Mutation mut2 = new Mutation(Gene.RT, 69, "_");
		assertEquals(mut1, mut2);
		assertEquals(mut1, mut1);
		assertNotEquals(mut1, null);
		assertNotEquals(mut1, "T69_");
	}

	@Test
	public void testGetHumanFormat() {
		System.out.println("\nMethod: MutationTest:test");
		Mutation mut1 = new Mutation(Gene.RT, 65, "KN");
		Mutation mut2 = new Mutation(Gene.RT, 65, "NK");
		Mutation mut3 = new Mutation(Gene.RT, 118, "_");
		Mutation mut4 = new Mutation(Gene.RT, 118, "#");
		Mutation mut5 = new Mutation(Gene.RT, 118, "Insertion");
		Mutation mut6 = new Mutation(Gene.RT, 69, "-");
		Mutation mut7 = new Mutation(Gene.RT, 69, "Deletion");
		Mutation mut8 = new Mutation(Gene.IN, 155, "S");
		Mutation mut9 = new Mutation(Gene.IN, 155, "NS");
		Mutation mut10 = new Mutation(Gene.RT, 10, "S");
		Mutation mut11 = new Mutation(Gene.PR, 10, "S");
		Mutation mut12 = new Mutation(Gene.RT, 215, "FIST");
		Mutation mut13 = new Mutation(Gene.RT, 215, "TSNY");
		Mutation mut14 = new Mutation(Gene.RT, 188, "YL*");
		Mutation mut15 = new Mutation(Gene.RT, 188, "*");
		Mutation mut16 = new Mutation(Gene.IN, 263, "RKGY");
		Mutation mut17 = new Mutation(Gene.IN, 263, "X");
		Mutation mut18 = new Mutation(Gene.RT, 118, "V_V");

		assertEquals("K65KN", mut1.getHumanFormat());
		assertEquals("K65KN", mut2.getHumanFormat());
		assertEquals(mut1, mut2);
		assertEquals(mut1.hashCode(), mut2.hashCode());

		assertEquals("V118Insertion", mut3.getHumanFormat());
		assertEquals("V118Insertion", mut4.getHumanFormat());
		assertEquals("V118Insertion", mut5.getHumanFormat());
		assertEquals("V118V_V", mut18.getHumanFormat());
		assertNotEquals(mut1, mut3);
		assertNotEquals(mut2, mut4);
		assertNotEquals(mut1, mut5);
		assertEquals(mut3, mut4);
		assertEquals(mut5, mut4);
		assertNotEquals(mut18, mut4);
		assertEquals(mut3.hashCode(), mut4.hashCode());
		assertEquals(mut3.hashCode(), mut5.hashCode());

		assertEquals("T69Deletion", mut6.getHumanFormat());
		assertEquals("T69Deletion", mut7.getHumanFormat());
		assertEquals(mut6, mut7);

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
	public void testParseString() {
		assertEquals(
			new Mutation(Gene.RT, 77, 'V'),
			Mutation.parseString("RT:77V"));
		assertEquals(
			new Mutation(Gene.RT, 77, 'V'),
			Mutation.parseString(Gene.RT, "77V"));
		assertEquals(
			"AAG",
			Mutation.parseString("RT:77K:AAG").getTriplet());
		assertEquals(
			"*FLY",
			Mutation.parseString("RT:Y188ZFLY").getAAs());

		try {
			Mutation.parseString(null, "77V");
			assertTrue(false);
		} catch (InvalidMutationStringException e) {
			// pass
		}

		try {
			Mutation.parseString(Gene.RT, "77V`");
			assertTrue(false);
		} catch (InvalidMutationStringException e) {
			// pass
		}
	}

	@Test
	public void testTripletAndInsertedNAs() {
		assertEquals(
			"AAA",
			new Mutation(Gene.RT, 76, "K", "AAA").getTriplet());
		assertEquals(
			"AAAAAA",
			new Mutation(Gene.RT, 76, "K_KK", "AAA", "AAAAAA").getInsertedNAs());
	}

	@Test
	public void testIsIndel() {
		assertTrue(
			new Mutation(Gene.RT, 69, "_").isIndel());
		assertTrue(
			new Mutation(Gene.RT, 67, "-").isIndel());
		assertFalse(
			new Mutation(Gene.RT, 67, "V").isIndel());
	}

	@Test
	public void testIsMixture() {
		assertTrue(
			new Mutation(Gene.RT, 66, "BE").isMixture());
		assertTrue(
			new Mutation(Gene.RT, 66, "X").isMixture());
		assertFalse(
			new Mutation(Gene.RT, 66, "V").isMixture());
	}

	@Test
	public void testHasConsensus() {
		assertTrue(
			new Mutation(Gene.RT, 69, "T").hasConsensus());
		assertTrue(
			new Mutation(Gene.RT, 69, "TV").hasConsensus());
		assertTrue(
			new Mutation(Gene.RT, 69, "T_TT").hasConsensus());
		assertFalse(
			new Mutation(Gene.RT, 69, "V_TT").hasConsensus());
	}

	@Test
	public void testGetHumanFormatWithoutCons() {
		assertEquals(
			"69T_TT",
			new Mutation(Gene.RT, 69, "T_TT").getHumanFormatWithoutCons());
		assertEquals(
			"67Deletion",
			new Mutation(Gene.RT, 67, "-").getHumanFormatWithoutCons());
	}
}