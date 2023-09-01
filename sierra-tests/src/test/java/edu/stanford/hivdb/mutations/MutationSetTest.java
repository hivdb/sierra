package edu.stanford.hivdb.mutations;
import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class MutationSetTest {
	
	private static final HIV hiv = HIV.getInstance();
	private static final HIV2 hiv2 = HIV2.getInstance();
	
	@Test
	public void testParseString() {
		MutationSet<HIV> mutSet = MutationSet.parseString(
				hiv,
				"RT215V");
		assertNotNull(mutSet);	
	}
	
	@Test
	public void testParseString2() {
		List<String> mutationList = new ArrayList<>();
		mutationList.add("RT215V");
		MutationSet<HIV> mutSet = MutationSet.parseString(
				hiv, mutationList);
		assertNotNull(mutSet);	
	}
	
	@Test
	public void testParseString3() {

		List<String> mutationList = new ArrayList<>();
		mutationList.add("L10V");
		MutationSet<HIV> mutSet = MutationSet.parseString(
				hiv.getGene("HIV1PR"),
				mutationList);
		
		assertNotNull(mutSet);
	}
	
	@Test
	public void testParseString4() {
		String formattedMuts = null;
		MutationSet<HIV> mutSet = MutationSet.parseString(
				hiv.getGene("HIV1PR"),
				formattedMuts);
		
		assertNotNull(mutSet);
		
		mutSet = MutationSet.parseString(
				hiv.getGene("HIV1PR"),
				"L10V,E35D,M36I,N37D,I54V,Q58E,I62IV,L63P,I64V,A71V,G73T,L90M");
		
		assertNotNull(mutSet);
	}
	
	@Test
	public void testParseString5() {
		// BiFunction
		String formattedMuts = null;
		MutationSet<HIV> mutSet = MutationSet.parseString(
				hiv.getGene("HIV1PR"),
				formattedMuts, null);
		
		assertNotNull(mutSet);
	}
	
	@Test(expected=NullPointerException.class)
	public void testParseString6() {
		// BiFunction
		List<String> mutationList = new ArrayList<>();
		mutationList.add("L10V");
		MutationSet<HIV> mutSet = MutationSet.parseString(
			null,
			mutationList, null);
				
		assertNotNull(mutSet);
	}
	
	@Test
	public void testConstructor() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertNotNull(mSet);
		
	}
	
	
	@Test
	public void testConstructor2() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		assertNotNull(mSet);
	}
	
	@Test
	public void testDisplayAmbiguities() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		assertTrue(mSet.displayAmbiguities() instanceof MutationSet);
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testAddAll() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.addAll(null);
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testAdd() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.add(null);
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testClear() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.clear();
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveAll() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.removeAll(null);
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRetainAll() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.retainAll(null);
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemove() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.remove(null);
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testPollFirst() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.pollFirst();
		
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testPollLast() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		mSet.pollLast();
		
	}
	
	@Test
	public void testMergeWith() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT67E"));
		
		assertNotNull(mSet.mergesWith(mutations));
	}
	
	@Test
	public void testMergeWith2() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		
		assertNotNull(mSet.mergesWith(hiv.parseMutationString("RT67E")));
	}

	@Test
	public void testIntersectsWith() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		List<Mutation<HIV>> mutations2 = new ArrayList<>();
		mutations2.add(hiv.parseMutationString("RT67E"));
		mutations2.add(hiv.parseMutationString("RT70Q"));
		
		assertNotNull(mSet.intersectsWith(mutations2));
	}
	
	@Test
	public void testIntersectsWith2() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		
		assertNotNull(mSet.intersectsWith(hiv.parseMutationString("RT67E")));
	}
	
	@Test
	public void testSubtractBy() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		List<Mutation<HIV>> mutations2 = new ArrayList<>();
		mutations2.add(hiv.parseMutationString("RT67E"));
		mutations2.add(hiv.parseMutationString("RT70Q"));
		
		assertNotNull(mSet.subtractsBy(mutations2));
	}
	
	@Test
	public void testSubtractBy2() {
		Mutation<HIV> mutation1 = hiv.parseMutationString("RT215V");
		Mutation<HIV> mutation2 = hiv.parseMutationString("RT215I");
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutation1, mutation2);
		
		
		assertNotNull(mSet.subtractsBy(hiv.parseMutationString("RT67E")));
	}
	
	@Test
	public void testFilterBy() {
		
	}
	
	@Test
	public void testFilterAndGroupBy() {
		
	}
	
	@Test
	public void testGroupBy() {
		
	}
	
	@Test
	public void testGroupByMutType() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertFalse(mSet.groupByMutType(hiv.getGene("HIV1RT")).isEmpty());
	}
	
	@Test
	public void testGetByMutType() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getByMutType(hiv.getMutationType("NNRTI")));
	}
	
	@Test
	public void testGroupByGene() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.groupByGene());
	}
	
	
	@Test
	public void testGetGeneMutations() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getGeneMutations(hiv.getGene("HIV1PR")));
	}
	
	@Test
	public void testGetInsertions() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getInsertions());
	}
	
	@Test
	public void testGetDeletions() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getDeletions());
	}
	
	@Test
	public void testGetStopCodonsCase1() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getStopCodons());
		assertTrue(mSet.getStopCodons().isEmpty());
	}

	@Test
	public void testGetStopCodonsCase2() {
		
		List<Mutation<HIV2>> mutations = new ArrayList<>();
		mutations.add(hiv2.parseMutationString("RT215*"));
		mutations.add(hiv2.parseMutationString("RT67X:NNN"));
		
		MutationSet<HIV2> mSet = new MutationSet<HIV2>(mutations);
		MutationSet<HIV2> stops = mSet.getStopCodons();
		assertNotNull(stops);
		assertFalse(stops.isEmpty());
		assertEquals(1, stops.size());
		assertTrue(stops.contains(mutations.get(0)));
		assertFalse(stops.contains(mutations.get(1)));
	}

	@Test
	public void testGetAmbiguousCodons() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		 // AHT is the codon for triggering isAmbiguous() returning true
		mutations.add(hiv.parseMutationString("RT123INT:AHT"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertEquals(
			new MutationSet<>(hiv.parseMutationString("RT123INT")),
			mSet.getAmbiguousCodons()
		);
	}
	
	@Test
	public void testGetUnusualMutations() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getUnusualMutations());
	}
	
	@Test
	public void testHighestMutPrevalences() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getHighestMutPrevalences());
	}

	@Test
	public void testGetAtDRPMutations() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getAtDRPMutations());
	}
	
	@Test
	public void testGetMutationTypes() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getMutationTypes(mSet));
	}
	
	@Test
	public void testGetTSMs() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getTSMs());
	}
	
	@Test
	public void testGetTSMs2() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getTSMs(hiv.getDrugClass("HIV1RT")));
	}
	
	@Test
	public void testGetDRMs() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getDRMs());
	}
	
	@Test
	public void testGetDRMs2() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getDRMs(hiv.getDrugClass("HIV1RT")));
	}
	
	@Test
	public void testGetSDRMs() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getSDRMs());
	}
	
	@Test
	public void testGetSDRMs2() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getSDRMs(hiv.getDrugClass("HIV1RT")));
	}
	
	@Test
	public void testGetApobecMutations() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getApobecMutations());
	}
	
	@Test
	public void testGetApobecDRMs() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getApobecDRMs());
	}
	
	@Test
	public void testGet() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		GenePosition<HIV> gp = new GenePosition<HIV>(hiv.getGene("HIV1RT"), 215);
		assertNotNull(mSet.get(gp));
	}
	
	@Test
	public void testGet2() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.get(hiv.getGene("HIV1RT"), 215));
	}

	@Test
	public void testGetSplitted() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getSplitted());
	}
	
	@Test
	public void testGetPositions() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getPositions());
	}

	@Test
	public void testHasInsertionAt() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertFalse(mSet.hasInsertionAt(hiv.getGene("HIV1RT"), 215));

		mutations.add(hiv.parseMutationString("RT216_"));
		
		mSet = new MutationSet<HIV>(mutations);
		assertTrue(mSet.hasInsertionAt(hiv.getGene("HIV1RT"), 216));
	}
	
	@Test
	public void testHasDeletionAt() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertFalse(mSet.hasInsertionAt(hiv.getGene("HIV1RT"), 215));

		mutations.add(hiv.parseMutationString("RT216-"));
		
		mSet = new MutationSet<HIV>(mutations);
		assertTrue(mSet.hasDeletionAt(hiv.getGene("HIV1RT"), 216));
	}
	
	@Test
	public void testHasSharedAAMutation() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertTrue(mSet.hasSharedAAMutation(hiv.parseMutationString("RT215V")));
	}
	
	@Test
	public void testHasSharedAAMutation2() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertTrue(mSet.hasSharedAAMutation(hiv.parseMutationString("RT215V"), true));
	}
	
	@Test
	public void testGetPrevalences() {
		
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		assertNotNull(mSet.getPrevalences());
	}
	
	@Test
	public void testJoin2() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertEquals(mSet.join("\t"), "D67N\tT215V");
	}

	@Test
	public void testJoin3() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertEquals(mSet.join('\t'), "D67N\tT215V");
	}
	
	@Test
	public void testJoin5() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertEquals(mSet.join(), "D67N,T215V");
	}
	
	@Test
	public void testToStringList() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertEquals(mSet.toStringList().size(), 2);
	}
	
	@Test
	public void testToASIFormat() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertEquals(mSet.toASIFormat().size(), 2);
	}
	
	@Test
	public void testCompareTo() {
		List<Mutation<HIV>> mutations = new ArrayList<>();
		mutations.add(hiv.parseMutationString("RT215V"));
		mutations.add(hiv.parseMutationString("RT67N"));
		
		MutationSet<HIV> mSet = new MutationSet<HIV>(mutations);
		
		assertTrue(mSet.compareTo(mSet) == 0);
		
		List<Mutation<HIV>> mutations2 = new ArrayList<>();
		mutations2.add(hiv.parseMutationString("RT66N"));
		
		MutationSet<HIV> mSet2 = new MutationSet<HIV>(mutations2);
		
		assertTrue(mSet.compareTo(mSet2) > 0);
	}
	
}
