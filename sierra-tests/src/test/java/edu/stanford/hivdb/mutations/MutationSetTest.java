package edu.stanford.hivdb.mutations;
import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

import static org.junit.Assert.*;

public class MutationSetTest {
	
	private static final HIV hiv = HIV.getInstance();
	
	@Test
	public void testParseString() {
		MutationSet<HIV> mutSet = MutationSet.parseString(
				hiv.getGene("HIV1PR"),
				"L10V,E35D,M36I,N37D,I54V,Q58E,I62IV,L63P,I64V,A71V,G73T,L90M");
		
		assertNotNull(mutSet);
	}
}