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
		
		// Test UnUsual mutations
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "1A, 2T");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1RT"), "181R, 180S");
		results = validator.validate(muts);
		assertEquals(results.size(), 2);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1RT"), "181R, 180S, 178K");
		results = validator.validate(muts);
		assertEquals(results.size(), 2);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		// Test Apobec
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "16K, 17K, 25N, 29N, 30N, 40K, 46I, 48S");
		results = validator.validate(muts);
		assertEquals(results.size(), 2);
		assertEquals(results.get(0).getLevel().name(), "SEVERE_WARNING");
		assertEquals(results.get(0).getMessage(),
				"The following 5 APOBEC muts were present in the sequence. The following 3 DRMs in this sequence could reflect APOBEC activity: PR: D30N, M46I, G48S.");
		assertEquals(results.get(1).getLevel().name(), "WARNING");
		assertEquals(results.get(1).getMessage(),
				"There are 3 unusual mutations: HIV1PR_G16K, HIV1PR_G17K, HIV1PR_G40K.");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "17K, 25N");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		assertEquals(results.get(0).getMessage(), "The following 2 APOBEC muts were present in the sequence.");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "40K");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "NOTE");
		assertEquals(results.get(0).getMessage(), "The following 1 APOBEC muts were present in the sequence.");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "48K");
		results = validator.validate(muts);
		assertEquals(results.size(), 3);
		assertEquals(results.get(0).getLevel().name(), "NOTE");
		assertEquals(results.get(0).getMessage(), "The following 1 APOBEC muts were present in the sequence.");
	}
}