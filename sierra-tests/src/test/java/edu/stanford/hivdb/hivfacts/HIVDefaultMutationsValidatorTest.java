package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIVDefaultMutationsValidatorTest {

	final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testValidate() {
		
		HIVDefaultMutationsValidator validator =  new HIVDefaultMutationsValidator();
		
		MutationSet<HIV> muts = hiv.newMutationSet(hiv.getGene("HIV1RT"), "M184V, E44A");
		List<ValidationResult> results = validator.validate(muts);
		assertEquals(results.size(), 0);
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1RT"), "M184VZ, E44A");
		results = validator.validate(muts);
		assertEquals(results.size(), 2);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1RT"), "M184VZ, E44AZ");
		results = validator.validate(muts);
		assertEquals(results.size(), 2);
		assertEquals(results.get(0).getLevel().name(), "SEVERE_WARNING");
				
	}
}