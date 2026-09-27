package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.genotypes.BoundGenotype;
import edu.stanford.hivdb.genotypes.GenotypeReference;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.Aligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.ValidationLevel;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIVDefaultSequenceValidatorTest {

	final static HIV hiv = HIV.getInstance();
	final static List<String> includeGenes = List.of("PR", "RT", "IN");

	private static BoundGenotype<HIV> boundSubtypeKAtDistance(double targetDistance) {
		GenotypeReference<HIV> ref = hiv.getGenotypeReferences().stream()
			.filter(r -> r.getGenotype().getIndexName().equals("K"))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No subtype K reference found"));
		int length = ref.getLastNA() - ref.getFirstNA() + 1;
		List<Integer> discordance = new ArrayList<>();
		for (int i = 0; i < Math.round(length * targetDistance); i++) {
			discordance.add(Integer.valueOf(i));
		}
		return ref.getBoundGenotype(
			ref.getSequence(), ref.getFirstNA(), ref.getLastNA(), discordance);
	}

	@Test
	public void testValidateSubtypeDistance() {
		// "point 5": a pure subtype reported above its distance upper-limit
		// (K limit is 6%; here ~9%, still below the 11% "unknown" cutoff).
		List<ValidationResult> results =
			HIVDefaultSequenceValidator.validateSubtypeDistance(boundSubtypeKAtDistance(0.09));
		assertEquals(1, results.size());
		assertEquals(ValidationLevel.WARNING, results.get(0).getLevel());
		assertTrue(results.get(0).getMessage().contains("subtype K"));
		assertTrue(results.get(0).getMessage().contains("more"));

		// Below the upper-limit: no warning.
		assertTrue(
			HIVDefaultSequenceValidator.validateSubtypeDistance(
				boundSubtypeKAtDistance(0.03)).isEmpty());

		// Above the "unknown" threshold: no warning.
		assertTrue(
			HIVDefaultSequenceValidator.validateSubtypeDistance(
				boundSubtypeKAtDistance(0.15)).isEmpty());

		// No closest match: no warning, no exception.
		assertTrue(
			HIVDefaultSequenceValidator.validateSubtypeDistance((BoundGenotype<HIV>) null).isEmpty());
	}

	@Test
	public void test() {
		HIVDefaultSequenceValidator validator =  new HIVDefaultSequenceValidator();
		
		Sequence seq = new Sequence("empty", "EMPTY");
		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);
		
		List<ValidationResult> results = validator.validate(alignedSeq, includeGenes);
		assertEquals(results.size(), 1);
		
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		
		alignedSeq = Aligner.getInstance(hiv).align(testSeq);
		
		results = validator.validate(alignedSeq, includeGenes);
		assertEquals(1, results.size());
	}

}