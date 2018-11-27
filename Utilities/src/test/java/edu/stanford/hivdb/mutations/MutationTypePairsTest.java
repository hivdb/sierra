package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.MutationTypePairs.MutationTypePair;

public class MutationTypePairsTest {	
	
	@BeforeClass
	public static void testInit() {
		assertFalse(MutationTypePairs.getMutationTypes().isEmpty());
	}
	
	@Test
	public void testMutTypePairConstruction() {
		final MutationTypePair mutTypePair = new MutationTypePair(Gene.RT, DrugClass.NNRTI, 234, "I", MutType.NNRTI, false);
		assertEquals(Gene.RT, mutTypePair.getGene());
		assertEquals(DrugClass.NNRTI, mutTypePair.getDrugClass());
		assertEquals(Integer.valueOf(234), mutTypePair.getPosition());
		assertEquals("L", mutTypePair.getConsensus());
		assertEquals("I", mutTypePair.getTriggeredAAs());
		assertEquals(MutType.NNRTI, mutTypePair.getType());
		assertFalse(mutTypePair.isUnusual());
	}
		
//	@Test
//	public void testGetUniqueId() {
//		final MutationTypePair mutTypePairPiMajor = new MutationTypePair(Gene.IN, DrugClass.INSTI, 66, "I", MutType.Major, false);
//		String eID = "IN_POS66I_Major";
//		assertEquals(eID, mutTypePairPiMajor.getUniqueID());
//	}
//	
//	@Test
//	public void testGetUniqueIdPiMajor() {
//		final MutationTypePair mutTypePairPiMajor = new MutationTypePair(Gene.PR, DrugClass.PI, 32, "I", MutType.Major, false);
//		String eID = "PR_POS32I_PIMajor";
//		assertEquals(eID, mutTypePairPiMajor.getUniqueID());
//	}
//	
//	@Test
//	public void testGetUniqueIdPiMinor() {
//		final MutationTypePair mutTypePairPiMinor = new MutationTypePair(Gene.PR, DrugClass.PI, 23, "I", MutType.Accessory, false);
//		String eID = "PR_POS23I_PIMinor";
//		assertEquals(eID, mutTypePairPiMinor.getUniqueID());
//	}
		
	@Test
	public void testIsMatched() {
		final Mutation mut = Mutation.parseString("PR:23I");
		final MutationTypePair mutTypePair = new MutationTypePair(Gene.PR, DrugClass.PI, 23, "I", MutType.Accessory, false);
		assertTrue(mutTypePair.isMutationMatched(mut));
	}
	
	@Test
	public void testIsUnmatchedByGene() {
		final Mutation mut = Mutation.parseString("RT:23I");
		final MutationTypePair mutTypePair = new MutationTypePair(Gene.PR, DrugClass.PI, 23, "I", MutType.Accessory, false);
		assertFalse(mutTypePair.isMutationMatched(mut));
	}
	
	@Test
	public void testIsUnmatchedByPos() {
		final Mutation mut = Mutation.parseString("PR:22I");
		final MutationTypePair mutTypePair = new MutationTypePair(Gene.PR, DrugClass.PI, 23, "I", MutType.Accessory, false);
		assertFalse(mutTypePair.isMutationMatched(mut));
	}
	
	@Test
	public void testIsUnmatchedByAAs() {
		final Mutation mut = Mutation.parseString("PR:23A");
		final MutationTypePair mutTypePair = new MutationTypePair(Gene.PR, DrugClass.PI, 23, "I", MutType.Accessory, false);
		assertFalse(mutTypePair.isMutationMatched(mut));
	}
	
	@Test
	public void testIsMatchedWithInsertion() {
		final MutationTypePair mutTypePair = new MutationTypePair(Gene.RT, DrugClass.NRTI, 70, "ACDFHILMPVWY_", MutType.NRTI, true);
		final Mutation matchedMut = Mutation.parseString("RT:70ACDFHILMPVW_Y");
		final Mutation unmatchedMutPos = Mutation.parseString("RT:71ACDFHILMPVW_Y");
		assertTrue(mutTypePair.isMutationMatched(matchedMut));
		assertFalse(mutTypePair.isMutationMatched(unmatchedMutPos));
	}
	
	@Test
	public void testLookupByPosition() {
		final List<MutType> mutTypes = MutationTypePairs.lookupByPosition(Gene.PR, 90);
		final List<MutType> eMutTypes = Arrays.asList(MutType.Major, MutType.Other);
		assertEquals(eMutTypes, mutTypes);
	}
	
	@Test
	public void testLookupByMutationWithSingleMutType() {
		final Mutation mut = Mutation.parseString("IN:66CDEFGHL");
		final List<MutType> mutType = MutationTypePairs.lookupByMutation(mut);
		final List<MutType> eMutType = Arrays.asList(MutType.Major);
		assertEquals(eMutType, mutType);
	}
	
	@Test
	public void testLookupByMutationWithTwoMutTypes() {
		final Mutation mut = Mutation.parseString("RT:106MC");
		final List<MutType> mutTypes = MutationTypePairs.lookupByMutation(mut);
		List<MutType> eMutTypes = Arrays.asList(MutType.NNRTI, MutType.Other);
		assertEquals(eMutTypes, mutTypes);
	}
	
	@Test
	public void testLookupByMutationWithRepeatedMutTypes() {
		final Mutation mut = Mutation.parseString("IN:66ADIK");
		final List<MutType> mutTypes = MutationTypePairs.lookupByMutation(mut);
		List<MutType> eMutTypes = Arrays.asList(MutType.Major, MutType.Major, MutType.Major, MutType.Major);
		assertEquals(eMutTypes, mutTypes);
	}

	@Test
	public void testLookupByMutations() {
		final Mutation mut1 = Mutation.parseString("RT:106M");
		final Mutation mut2 = Mutation.parseString("PR:20TIVR");
		final Mutation mut3 = Mutation.parseString("IN:66ADIK");
		final Mutation mut4 = Mutation.parseString("IN:74A");
		final MutationSet muts = new MutationSet(mut1, mut2, mut3, mut4);
		final List<MutType> eMut1Types = MutationTypePairs.lookupByMutation(mut1);
		final List<MutType> eMut2Types = MutationTypePairs.lookupByMutation(mut2);
		final List<MutType> eMut3Types = MutationTypePairs.lookupByMutation(mut3);
		final Map<Mutation, List<MutType>> mutTypes = MutationTypePairs.lookupByMutations(muts);
		assertEquals(3, mutTypes.size());
		assertEquals(eMut1Types, mutTypes.get(mut1));
		assertEquals(eMut2Types, mutTypes.get(mut2));
		assertEquals(eMut3Types, mutTypes.get(mut3));
	} 
	
	@Test
	public void testLookupByMutationsWithNullMutType() {
		final MutationSet muts = new MutationSet("IN:74A");
		final Map<Mutation, List<MutType>> mutTypes = MutationTypePairs.lookupByMutations(muts);
		assertNull(mutTypes.get(Mutation.parseString("IN:74A")));
	} 
	
	@Test
	public void testLookupByMutationsOfGene() {
		final Mutation rtMut = Mutation.parseString("RT:106M");
		final Mutation inMut1 = Mutation.parseString("IN:66A");
		final Mutation inMut2 = Mutation.parseString("IN:95K");
		final MutationSet muts = new MutationSet(rtMut, inMut1, inMut2);
		final List<MutType> eInMut1Types = MutationTypePairs.lookupByMutation(inMut1);
		final List<MutType> eInMut2Types = MutationTypePairs.lookupByMutation(inMut2);
		final Map<Mutation, List<MutType>> mutTypes = MutationTypePairs.lookupByMutations(Gene.IN, muts);
		assertEquals(2, mutTypes.size());
		assertEquals(eInMut1Types, mutTypes.get(inMut1));
		assertEquals(eInMut2Types, mutTypes.get(inMut2));
		assertNull(mutTypes.get(rtMut));
	}
}
