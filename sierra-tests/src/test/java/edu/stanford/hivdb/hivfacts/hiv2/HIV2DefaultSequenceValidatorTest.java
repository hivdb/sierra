package edu.stanford.hivdb.hivfacts.hiv2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.Aligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIV2DefaultSequenceValidatorTest {

	final static HIV2 hiv2 = HIV2.getInstance();
	final static List<String> includeGenes = List.of("PR", "RT", "IN");
	
	@Test
	public void test() {
		HIV2DefaultSequenceValidator validator =  new HIV2DefaultSequenceValidator();
		
		Sequence seq = new Sequence("empty", "EMPTY");
		AlignedSequence<HIV2> alignedSeq = Aligner.getInstance(hiv2).align(seq);
		
		List<ValidationResult> results = validator.validate(alignedSeq, includeGenes);
		assertEquals(1, results.size());
		
		Sequence testSeq = Sequence.fromGenbank("AF096883"); // this is an HIV-1 sequence
		
		alignedSeq = Aligner.getInstance(hiv2).align(testSeq);
		
		results = validator.validate(alignedSeq, includeGenes);
		assertEquals(1, results.size());  // critical warning
	}
}
	