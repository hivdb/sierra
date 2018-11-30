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

import edu.stanford.hivdb.mutations.Mutation.InvalidMutationException;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class IUPACMutationTest {
	
	@Test
	public void testFromNucAminoMut() {
		final Map<String, Object> mutMap = new HashMap<>();
		mutMap.put("Position", 1.0);
		mutMap.put("CodonText", "AAC");
		mutMap.put("IsInsertion", false);
		mutMap.put("IsDeletion", false);
		
		final Mutation mut = IUPACMutation.fromNucAminoMutation(Gene.PR, 1, mutMap);
		final Mutation eMut = new IUPACMutation(Gene.PR, 1, "N");
		assertTrue(mut.equals(eMut));
	}
	
	@Test
	public void testFromNucAminoMutWithDeletion() {
		final Map<String, Object> mutMap = new HashMap<>();
		mutMap.put("Position", 1.0);
		mutMap.put("CodonText", "AAC");
		mutMap.put("IsInsertion", false);
		mutMap.put("IsDeletion", true);
		
		final Mutation mut = IUPACMutation.fromNucAminoMutation(Gene.PR, 1, mutMap);
		final Mutation eMut = new IUPACMutation(Gene.PR, 1, "-");
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
		
		final Mutation mut = IUPACMutation.fromNucAminoMutation(Gene.PR, 1, mutMap);
		final Mutation eMut = new IUPACMutation(Gene.PR, 1, "N_N", "AAC", "AAC");			
		assertTrue(mut.equals(eMut));
	}
	
	@Test
	public void testNormalizeAAs() {
		assertEquals(null, IUPACMutation.normalizeAAs(null));
		assertEquals("_", IUPACMutation.normalizeAAs("#"));
		assertEquals("-", IUPACMutation.normalizeAAs("~"));
		assertEquals("*", IUPACMutation.normalizeAAs("Z"));
		assertEquals("*", IUPACMutation.normalizeAAs("."));
		assertEquals("_", IUPACMutation.normalizeAAs("Insertion"));
		assertEquals("-", IUPACMutation.normalizeAAs("Deletion"));
		assertEquals("ACDE", IUPACMutation.normalizeAAs("DECA"));
		assertEquals("-ACE", IUPACMutation.normalizeAAs("deca"));
	}
	
	@Test
	public void testExtractGene() {
		assertEquals(Gene.PR, IUPACMutation.extractGene("PR100A"));
		assertEquals(Gene.IN, IUPACMutation.extractGene("IN100A"));
		assertEquals(Gene.RT, IUPACMutation.extractGene("RT100A"));
		assertEquals(null, IUPACMutation.extractGene("not a mutation"));
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testExtractGeneWithMalformedMut() {
		assertEquals(null, IUPACMutation.extractGene("P100D"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testPositionOutOfGene() {
		new IUPACMutation(Gene.PR, 100, 'A');
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSameGene() {
		new IUPACMutation(Gene.PR, 68, 'A')
			.mergesWith(new IUPACMutation(Gene.RT, 68, "C"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testMergesWithNotSamePos() {
		new IUPACMutation(Gene.RT, 67, 'A')
			.mergesWith(new IUPACMutation(Gene.RT, 68, "C"));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testMergesWithIndel() {
		new IUPACMutation(Gene.RT, 67, 'A')
			.mergesWith(new IUPACMutation(Gene.RT, 67, "-"));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testIndelMergesWith() {
		new IUPACMutation(Gene.RT, 67, '-')
			.mergesWith(new IUPACMutation(Gene.RT, 67, "A"));
	}

	@Test
	public void testMergesWith() {
		assertEquals(
			new IUPACMutation(Gene.RT, 67, "AC"),
			new IUPACMutation(Gene.RT, 67, 'A')
				.mergesWith(new IUPACMutation(Gene.RT, 67, "C")));
		assertEquals(
			new IUPACMutation(Gene.RT, 67, "AC"),
			new IUPACMutation(Gene.RT, 67, "AC")
				.mergesWith(new IUPACMutation(Gene.RT, 67, "C")));
	}
	
	@Test
	public void testGetAAs() {
		assertEquals("AG", new IUPACMutation(Gene.PR, 1, "AG").getDisplayAAs());
		assertEquals("X", new IUPACMutation(Gene.PR, 1, "ABCEFG").getDisplayAAs());
		assertEquals("T_T", new IUPACMutation(Gene.RT, 69, "T_T").getDisplayAAs());
		assertEquals("X_T", new IUPACMutation(Gene.RT, 69, "ABCEFG_T").getDisplayAAs());
	}
	
	@Test
	public void testGetOriginalAAs() {
		assertEquals("ABCEFG", new IUPACMutation(Gene.PR, 1, "ABCEFG").getAAs());
	}

	@Test
	public void testGetAAsWithoutConsensus() {
		assertEquals("N", new IUPACMutation(Gene.RT, 65, "KN").getAAsWithoutReference());
		assertEquals("N_D", new IUPACMutation(Gene.RT, 65, "KN_D").getAAsWithoutReference());
	}

	@Test
	public void testHasBDHVN() {
		assertFalse(new IUPACMutation(Gene.PR, 32, "K", "AAA").hasBDHVN());
		assertFalse(new IUPACMutation(Gene.PR, 32, "K", "AAR").hasBDHVN());
		assertTrue(new IUPACMutation(Gene.PR, 32, "NK", "AAB").hasBDHVN());
		assertTrue(new IUPACMutation(Gene.PR, 32, "NK", "AAD").hasBDHVN());
		assertTrue(new IUPACMutation(Gene.PR, 32, "NK", "AAH").hasBDHVN());
		assertTrue(new IUPACMutation(Gene.PR, 32, "NK", "AAV").hasBDHVN());
		assertTrue(new IUPACMutation(Gene.PR, 32, "NK", "AAN").hasBDHVN());
	}

	@Test
	public void testContainsSharedAA() {
		// contains shared consensus
		assertTrue(
			new IUPACMutation(Gene.RT, 65, "A_D")
			.containsSharedAA(new IUPACMutation(Gene.RT, 65, "KN_D")));
	}

	@Test
	public void testGetShortText() {
		assertEquals(
			"T69T_GG", new IUPACMutation(Gene.RT, 69, "T_GG").getShortText());
	}

	@Test
	public void testGetASIFormat() {
		assertEquals(
			"T69i", new IUPACMutation(Gene.RT, 69, "T_GG").getASIFormat());
	}

	@Test
	public void testGetHIVDBFormat() {
		assertEquals(
			"69#", new IUPACMutation(Gene.RT, 69, "T_GG").getHIVDBFormat());
	}

	@Test
	public void testEqualsAndHashCode() {
		final Mutation mut1 = new IUPACMutation(Gene.RT, 69, "T_GG");
		final Mutation mut2 = new IUPACMutation(Gene.RT, 69, "_");
		assertEquals(mut1, mut2);
		assertEquals(mut1, mut1);
		assertEquals(mut1, new AAMutation(Gene.RT, 69, '_'));
		assertEquals(new AAMutation(Gene.RT, 69, '_'), mut1);
		assertNotEquals(mut1, null);
		assertNotEquals(mut1, "T69_");
	}
	
	@Test
	public void testGetHumanFormat() {
		Mutation mut1 = new IUPACMutation(Gene.RT, 65, "KN");
		Mutation mut2 = new IUPACMutation(Gene.RT, 65, "NK");
		Mutation mut3 = new IUPACMutation(Gene.RT, 118, "_");
		Mutation mut4 = new IUPACMutation(Gene.RT, 118, "#");
		Mutation mut5 = new IUPACMutation(Gene.RT, 118, "Insertion");
		Mutation mut6 = new IUPACMutation(Gene.RT, 69, "-");
		Mutation mut7 = new IUPACMutation(Gene.RT, 69, "Deletion");
		Mutation mut8 = new IUPACMutation(Gene.IN, 155, "S");
		Mutation mut9 = new IUPACMutation(Gene.IN, 155, "NS");
		Mutation mut10 = new IUPACMutation(Gene.RT, 10, "S");
		Mutation mut11 = new IUPACMutation(Gene.PR, 10, "S");
		Mutation mut12 = new IUPACMutation(Gene.RT, 215, "FIST");
		Mutation mut13 = new IUPACMutation(Gene.RT, 215, "TSNY");
		Mutation mut14 = new IUPACMutation(Gene.RT, 188, "YL*");
		Mutation mut15 = new IUPACMutation(Gene.RT, 188, "*");
		Mutation mut16 = new IUPACMutation(Gene.IN, 263, "RKGY");
		Mutation mut17 = new IUPACMutation(Gene.IN, 263, "X");
		Mutation mut18 = new IUPACMutation(Gene.RT, 118, "V_V");

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
		// we consider insertions are the same
		assertEquals(mut18, mut4);
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
	public void testGetHumanFormatWithGene	() {
		Mutation mut1 = new IUPACMutation(Gene.RT, 65, "KN");
		Mutation mut2 = new IUPACMutation(Gene.RT, 65, "NK");
		Mutation mut3 = new IUPACMutation(Gene.RT, 118, "_");
		Mutation mut4 = new IUPACMutation(Gene.RT, 118, "#");
		Mutation mut5 = new IUPACMutation(Gene.RT, 118, "Insertion");
		Mutation mut6 = new IUPACMutation(Gene.RT, 69, "-");
		Mutation mut7 = new IUPACMutation(Gene.RT, 69, "Deletion");
		Mutation mut8 = new IUPACMutation(Gene.IN, 155, "S");
		Mutation mut9 = new IUPACMutation(Gene.IN, 155, "NS");
		Mutation mut10 = new IUPACMutation(Gene.RT, 10, "S");
		Mutation mut11 = new IUPACMutation(Gene.PR, 10, "S");
		Mutation mut12 = new IUPACMutation(Gene.RT, 215, "FIST");
		Mutation mut13 = new IUPACMutation(Gene.RT, 215, "TSNY");
		Mutation mut14 = new IUPACMutation(Gene.RT, 188, "YL*");
		Mutation mut15 = new IUPACMutation(Gene.RT, 188, "*");
		Mutation mut16 = new IUPACMutation(Gene.IN, 263, "RKGY");
		Mutation mut17 = new IUPACMutation(Gene.IN, 263, "X");
		Mutation mut18 = new IUPACMutation(Gene.RT, 118, "V_V");
		assertEquals("RT_K65KN", mut1.getHumanFormatWithGene());
		assertEquals("RT_K65KN", mut2.getHumanFormatWithGene());
		assertEquals("RT_V118Insertion", mut3.getHumanFormatWithGene());
		assertEquals("RT_V118Insertion", mut4.getHumanFormatWithGene());
		assertEquals("RT_V118Insertion", mut5.getHumanFormatWithGene());
		assertEquals("RT_T69Deletion", mut6.getHumanFormatWithGene());
		assertEquals("RT_T69Deletion", mut7.getHumanFormatWithGene());
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
		assertEquals("RT_V118V_V", mut18.getHumanFormatWithGene());
	}
	
	@Test
	public void testGetHumanFormatWithoutCons() {
		assertEquals(
			"69T_TT",
			new IUPACMutation(Gene.RT, 69, "T_TT").getHumanFormatWithoutLeadingRef());
		assertEquals(
			"67Deletion",
			new IUPACMutation(Gene.RT, 67, "-").getHumanFormatWithoutLeadingRef());
	}
	
	@Test
	public void testParseString() {		
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '_'),
				IUPACMutation.parseString(Gene.RT, "T69Insertion"));	
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '_'),
				IUPACMutation.parseString(Gene.RT, "T69insertion"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '_'),
				IUPACMutation.parseString(Gene.RT, "T69ins"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '_'),
				IUPACMutation.parseString(Gene.RT, "T69i"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '_'),
				IUPACMutation.parseString(Gene.RT, "T69#"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '_'),
				IUPACMutation.parseString(Gene.RT, "T69_"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, "T_D"),
				IUPACMutation.parseString(Gene.RT, "T69T#D"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, "INS"),
				IUPACMutation.parseString(Gene.RT, "T69INS"));
		
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '-'),
				IUPACMutation.parseString(Gene.RT, "T69Deletion"));	
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '-'),
				IUPACMutation.parseString(Gene.RT, "T69deletion"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '-'),
				IUPACMutation.parseString(Gene.RT, "T69del"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '-'),
				IUPACMutation.parseString(Gene.RT, "T69d"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, '-'),
				IUPACMutation.parseString(Gene.RT, "T69~"));
		assertEquals(
				new IUPACMutation(Gene.RT, 69, "DEL"),
				IUPACMutation.parseString(Gene.RT, "T69DEL"));
		
		assertEquals(
				new IUPACMutation(Gene.RT, 77, 'V'),
				IUPACMutation.parseString("  RT:77V"));
		assertEquals(
				new IUPACMutation(Gene.RT, 77, 'V'),
				IUPACMutation.parseString("RT:77V  "));
		assertEquals(
				new IUPACMutation(Gene.RT, 77, 'V'),
				IUPACMutation.parseString("  RT:77V  "));
		assertEquals(
			new IUPACMutation(Gene.RT, 77, 'V'),
			IUPACMutation.parseString(Gene.RT, "77V"));
		assertEquals(
			"AAG",
			IUPACMutation.parseString("RT:77K:AAG").getTriplet());
		assertEquals(
			"*FLY",
			IUPACMutation.parseString("RT:Y188ZFLY").getDisplayAAs());
	}

	@Test(expected=InvalidMutationException.class)
	public void testDelet() {
		IUPACMutation.parseString(Gene.RT, "S68Delet");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testDeletLowercase() {
		IUPACMutation.parseString(Gene.RT, "S68delet");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testInser() {
		IUPACMutation.parseString(Gene.RT, "S68Insert");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testInserLowercase() {
		IUPACMutation.parseString(Gene.RT, "S68insert");
	}
		
	@Test(expected=InvalidMutationException.class)
	public void testLeadingPoundInAA() {
		IUPACMutation.parseString(Gene.RT, "T69#ACD");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testTrailingPoundInAA() {
		IUPACMutation.parseString(Gene.RT, "T69T#");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testAAWithUnderscore() {
		IUPACMutation.parseString(Gene.RT, "T69T_");
	}
	@Test(expected=InvalidMutationException.class)
	public void testInsWithDel() {
		IUPACMutation.parseString(Gene.RT, "T69-_");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testLeadingTildeInAA() {
		IUPACMutation.parseString(Gene.RT, "T69~T");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testTrailingTildeInAA() {
		IUPACMutation.parseString(Gene.RT, "T69T~");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testInsertionAbbreviationWithAA() {
		IUPACMutation.parseString(Gene.RT, "T69iT");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testDoubleInsertionAbbreviationInAA() {
		IUPACMutation.parseString(Gene.RT, "T69insins");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testDoubleDeletionAbbreviationInAA() {
		IUPACMutation.parseString(Gene.RT, "T69Tdeletiondeletion");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testInsertionAbbreviationBetweenAAs() {
		IUPACMutation.parseString(Gene.RT, "T69TinsD");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testDeletionAbbreviationBetweenAAs() {
		IUPACMutation.parseString(Gene.RT, "T69TdelD");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testTrailingPoundWithMultipleAAs() {
		IUPACMutation.parseString(Gene.RT, "T69T#T#");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testParseStringWithNullGene() {
		IUPACMutation.parseString(null, "77V");
	}
	
	@Test(expected=InvalidMutationException.class)
	public void testParseStringWithGeneAndMalformedAA() {
		IUPACMutation.parseString(Gene.RT, "77V`");
	}
	
	@Test
	public void testTripletAndInsertedNAs() {
		assertEquals(
			"AAA",
			new IUPACMutation(Gene.RT, 76, "K", "AAA").getTriplet());
		assertEquals(
			"AAAAAA",
			new IUPACMutation(Gene.RT, 76, "K_KK", "AAA", "AAAAAA").getInsertedNAs());
	}

	@Test
	public void testHasReference() {
		assertTrue(
			new IUPACMutation(Gene.RT, 69, "T").hasReference());
		assertTrue(
			new IUPACMutation(Gene.RT, 69, "TV").hasReference());
		// debatable: T_TT is considered an insertion;
		// however T is the reference
		assertFalse(
			new IUPACMutation(Gene.RT, 69, "T_TT").hasReference());
		assertFalse(
			new IUPACMutation(Gene.RT, 69, "V_TT").hasReference());
	}

	@Test 
	public void testIsUnsequenced() {
		final Mutation mut = new IUPACMutation(Gene.PR, 1, "X");
		final Mutation mutSeq = new IUPACMutation(Gene.PR, 1, "_X", "NN-");
		final Mutation mutUnseqNN = new IUPACMutation(Gene.PR, 1, "X", "NN-");
		final Mutation mutUnseqNNN = new IUPACMutation(Gene.PR, 1, "X", "NNN");
		final Mutation mutUnseqNNG = new IUPACMutation(Gene.PR, 1, "X", "NNG");
		assertFalse(mut.isUnsequenced());
		assertFalse(mutSeq.isUnsequenced());
		assertTrue(mutUnseqNN.isUnsequenced());
		assertTrue(mutUnseqNNN.isUnsequenced());
		assertTrue(mutUnseqNNG.isUnsequenced());
	}
	
	
	@Test
	public void testIsAmbiguous() {
		final Mutation tripMut = new IUPACMutation(Gene.PR, 24, "N", "AAC");
		final Mutation tripMutX = new IUPACMutation(Gene.PR, 24, "X", "AAC");
		final Mutation bTripMut = new IUPACMutation(Gene.PR, 24, "N", "AAB");
		final Mutation dTripMut = new IUPACMutation(Gene.PR, 24, "N", "DAC");
		final Mutation hTripMut = new IUPACMutation(Gene.PR, 24, "S", "THT");
		final Mutation vTripMut = new IUPACMutation(Gene.PR, 24, "S", "TCV");
		final Mutation nTripMut = new IUPACMutation(Gene.PR, 24, "S", "TNA");
		assertFalse(tripMut.isAmbiguous());
		assertTrue(tripMutX.isAmbiguous());
		assertTrue(bTripMut.isAmbiguous());
		assertTrue(dTripMut.isAmbiguous());
		assertTrue(hTripMut.isAmbiguous());
		assertTrue(vTripMut.isAmbiguous());
		assertTrue(nTripMut.isAmbiguous());
	}
	
}