package edu.stanford.hivdb.sequences;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.genotypes.GenotypeReference;
import edu.stanford.hivdb.hivfacts.HIV;

public class PrettyAlignmentsTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testConstructor() {
		
		GenotypeReference<HIV> refGene = hiv.getGenotypeReferences().get(0);
		Sequence seq = new Sequence("> test", refGene.getSequence());

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = aligner.align(seq).getAlignedGeneSequences();
		
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
		assertNotNull(prettyAlignment);
	}
	
	@Test
	public void testGetSequenceAllPosAAs() {
		GenotypeReference<HIV> refGene = hiv.getGenotypeReferences().get(0);
		Sequence seq = new Sequence("> test", refGene.getSequence());

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = aligner.align(seq).getAlignedGeneSequences();
		
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
		assertFalse(prettyAlignment.getSequenceAllPosAAs().isEmpty());
	}
	
	@Test
	public void testToString() {
		GenotypeReference<HIV> refGene = hiv.getGenotypeReferences().get(0);
		Sequence seq = new Sequence("> test", refGene.getSequence());

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = aligner.align(seq).getAlignedGeneSequences();
		
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
		assertTrue(prettyAlignment.toString() instanceof String);
	}
}