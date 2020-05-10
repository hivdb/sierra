package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import edu.stanford.hivdb.mutations.Mutation;

public class HIVDataLoaderTest {

	@Test(expected=ExceptionInInitializerError.class)
	public void testLoadResourceError() {
		HIVDataLoader.loadResource("hiv12345.json");
	}
	
	@Test
	public void testExtractMutationGeneInvalid() {
		HIV hiv = HIV.getInstance();
		assertNull(hiv.extractMutationGene("RT:69B"));
	}
	
	@Test(expected=Mutation.InvalidMutationException.class)
	public void testParseMutationStringInvalid() {
		HIV hiv = HIV.getInstance();
		hiv.parseMutationString("RT:69B");
	}


}
