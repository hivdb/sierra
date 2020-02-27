package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


import edu.stanford.hivdb.hivfacts.HIV;

public class CodonMutationTest {
	
	final private static HIV hiv = HIV.getInstance();
	

	@Test
	public void testFromNucAminoMutation() {
		Map<String, Object> mut = new HashMap<>();
		
		mut.put("Position", 10.0);
		mut.put("IsInsertion", false);
		mut.put("IsDeletion", false);
		mut.put("CodonText", "GGT");
		mut.put("InsertedCodonsText", "GGA");
		
		assertNotNull(CodonMutation.fromNucAminoMutation(hiv.getGene("HIV1PR"), 10, mut));
		
	}
	
	@Test
	public void testConstructor1() {
		new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "A", "GGT");
	}
	
	@Test
	public void testConstructor2() {
		new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "A");
	}
	
	@Test
	public void testConstructor3() {
		new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
	}
	
	@SuppressWarnings("deprecation")
	@Test(expected=UnsupportedOperationException.class)
	public void testMergeWith() {
		CodonMutation<HIV> mutation1 = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		CodonMutation<HIV> mutation2 = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, '_');
		
		mutation1.mergesWith(mutation2);
	}
	
	@Test
	public void testIsUnsequenced() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "A", "NNN");
		assertTrue(mutation.isUnsequenced());

		mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "_", "NNN");
		assertFalse(mutation.isUnsequenced());
	}
	
	@Test
	public void testGetDisplayAAs() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		
		assertEquals(mutation.getDisplayAAs(), "A");
	}
	
	@Test
	public void testGetAAs() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		
		assertEquals(mutation.getAAs(), "A");
	}
	
	@Test
	public void testGetTriplet() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		
		assertEquals(mutation.getTriplet(), "");
	}
	
	@Test
	public void testGetInsertedNAs() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, 'A');
		
		assertEquals(mutation.getInsertedNAs(), "");
	}
	
	@Test
	public void testAAsWithRefFirst() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "AL");
		
		assertEquals(mutation.getAAsWithRefFirst(), "LA");
		
		mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "A");
		
		assertEquals(mutation.getAAsWithRefFirst(), "A");
	}
	
	@Test
	public void testGetAAsWithoutReference() {
		CodonMutation<HIV> mutation = new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 10, "AL");
		
		assertEquals(mutation.getAAsWithoutReference(), "A");
	}
}