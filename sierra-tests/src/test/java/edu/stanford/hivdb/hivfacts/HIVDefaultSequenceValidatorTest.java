package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.Aligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIVDefaultSequenceValidatorTest {

	final static HIV hiv = HIV.getInstance();
	
	@Test
	public void test() {
		HIVDefaultSequenceValidator validator =  new HIVDefaultSequenceValidator();
		
		Sequence seq = new Sequence("empty", "EMPTY");
		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);
		
		List<ValidationResult> results = validator.validate(alignedSeq);
		assertEquals(results.size(), 1);
		
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		
		alignedSeq = Aligner.getInstance(hiv).align(testSeq);
		
		results = validator.validate(alignedSeq);
		assertEquals(results.size(), 0);
	}

}