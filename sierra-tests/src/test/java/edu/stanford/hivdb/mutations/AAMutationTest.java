package edu.stanford.hivdb.mutations;

import org.junit.Test;


import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

public class AAMutationTest {

	private static HIV hiv = HIV.getInstance();

	@Test
	public void testIsApobecMutation() {
		Mutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
		assertTrue(mut.isApobecMutation());
		Mutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
		assertFalse(mut2.isApobecMutation());
		Mutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
		assertFalse(mut3.isApobecMutation());
	}

	 @Test
	 public void testIsApobecDRM() {
		Mutation<HIV> mut = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'E');
		assertFalse(mut.isApobecDRM());
		Mutation<HIV> mut2 = new AAMutation<HIV>(hiv.getGene("HIV1PR"), 27, 'G');
		assertFalse(mut2.isApobecDRM());
		Mutation<HIV> mut3 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'K');
		assertTrue(mut3.isApobecDRM());
		Mutation<HIV> mut4 = new AAMutation<HIV>(hiv.getGene("HIV1IN"), 263, 'R');
		assertFalse(mut4.isApobecDRM());

	 }

}