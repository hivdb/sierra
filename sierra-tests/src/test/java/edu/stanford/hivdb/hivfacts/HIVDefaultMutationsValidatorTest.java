package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIVDefaultMutationsValidatorTest {

	final static HIV hiv = HIV.getInstance();
	
	final static List<String> includeGenes = List.of("PR", "RT", "IN");
	
	@Test
	public void testValidate() {
		
		HIVDefaultMutationsValidator validator =  new HIVDefaultMutationsValidator();
		
		MutationSet<HIV> muts = hiv.newMutationSet(
			hiv.getGene("HIV1RT"), "M184V, E44A"
		);
		List<ValidationResult> results = validator.validate(muts, includeGenes);
		assertEquals(results.size(), 0);
		
		muts = hiv.newMutationSet(
			hiv.getGene("HIV1RT"), "M184VZ, E44A"
		);
		results = validator.validate(muts, includeGenes);
		assertEquals(2, results.size());
		assertEquals("WARNING", results.get(0).getLevel().name());
		
		muts = hiv.newMutationSet(
			hiv.getGene("HIV1RT"), "M184VZ, E44AZ"
		);
		results = validator.validate(muts, includeGenes);
		assertEquals(2, results.size());
		assertEquals("SEVERE_WARNING", results.get(0).getLevel().name());
		
		// Test UnUsual mutations
		muts = hiv.newMutationSet(
			hiv.getGene("HIV1PR"), "1A, 2T"
		);
		results = validator.validate(muts, includeGenes);
		assertEquals(1, results.size());
		assertEquals("WARNING", results.get(0).getLevel().name());
		
		muts = hiv.newMutationSet(
			hiv.getGene("HIV1RT"), "181R, 180S"
		);
		results = validator.validate(muts, includeGenes);
		assertEquals(2, results.size());
		assertEquals("WARNING", results.get(0).getLevel().name());
		
		muts = hiv.newMutationSet(
			hiv.getGene("HIV1RT"), "181R, 180S, 178K"
		);
		results = validator.validate(muts, includeGenes);
		assertEquals(2, results.size());
		assertEquals("WARNING", results.get(0).getLevel().name());
		
		// Test Apobec
		muts = hiv.newMutationSet(
			hiv.getGene("HIV1PR"),
			"16K, 17K, 25N, 29N, 30N, 40K, 46I, 48S"
		);
		results = validator.validate(muts, includeGenes);
		assertEquals(2, results.size());
		assertEquals("SEVERE_WARNING", results.get(0).getLevel().name());
		assertEquals(
			"The following 5 APOBEC mutations were present in the sequence: " +
			"PR: G16K, G17K, D25N, D29N, G40K. The following 3 DRMs in this " +
			"sequence could reflect APOBEC activity: PR: D30N, M46I, and G48S.",
			results.get(0).getMessage()
		);
		
		assertEquals("WARNING", results.get(1).getLevel().name());
		assertEquals(
			"There are 3 unusual mutations in PR: G16K, G17K, G40K.",
			results.get(1).getMessage()
		);
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "17K, 25N");
		results = validator.validate(muts, includeGenes);
		assertEquals(1, results.size());
		assertEquals("WARNING", results.get(0).getLevel().name());
		assertEquals(
			"The following 2 APOBEC mutations were present in the sequence: PR: G17K, D25N.",
			results.get(0).getMessage()
		);
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "40K");
		results = validator.validate(muts, includeGenes);
		assertEquals(1, results.size());
		assertEquals("NOTE", results.get(0).getLevel().name());
		assertEquals(
			"This following APOBEC mutation was present in the sequence: PR: G40K.",
			results.get(0).getMessage()
		);
		
		muts = hiv.newMutationSet(hiv.getGene("HIV1PR"), "48K");
		results = validator.validate(muts, includeGenes);
		assertEquals(3, results.size());
		assertEquals("NOTE", results.get(0).getLevel().name());
		assertEquals(
			"This following APOBEC mutation was present in the sequence: PR: G48K.",
			results.get(0).getMessage()
		);
	}
}