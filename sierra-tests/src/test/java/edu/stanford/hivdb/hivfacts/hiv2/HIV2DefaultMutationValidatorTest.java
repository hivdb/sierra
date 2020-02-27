package edu.stanford.hivdb.hivfacts.hiv2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIV2DefaultMutationValidatorTest {

	final static HIV2 hiv = HIV2.getInstance();
	
	@Test
	public void testValidate() {
		
		HIV2DefaultMutationsValidator validator =  new HIV2DefaultMutationsValidator();
		
		MutationSet<HIV2> muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "M184V, E44A");
		List<ValidationResult> results = validator.validate(muts);
		assertEquals(results.size(), 0);
		
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "M184VZ, E44A");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "M184VZ, E44AZ");
		results = validator.validate(muts);
		assertEquals(results.size(), 2);
		assertEquals(results.get(0).getLevel().name(), "SEVERE_WARNING");
		
		// Test UnUsual mutations
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "1A, 2T");
		results = validator.validate(muts);
		assertEquals(results.size(), 0);
		
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "181R, 180S");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "181R, 180S, 178K");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		
		// Test Apobec
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "16K, 17K, 25N, 29N, 30N, 40K, 46I, 48S");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		assertEquals(results.get(0).getMessage(),
				"There are 5 unusual mutations: HIV2ART_D17K, HIV2ART_P25N, HIV2ART_E29N, HIV2ART_K30N, HIV2ART_Q46I.");
		
		muts = hiv.newMutationSet(hiv.getGene("HIV2ART"), "17K, 25N");
		results = validator.validate(muts);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getLevel().name(), "WARNING");
		assertEquals(results.get(0).getMessage(), "There are 2 unusual mutations: HIV2ART_D17K, HIV2ART_P25N.");
		
	}
}