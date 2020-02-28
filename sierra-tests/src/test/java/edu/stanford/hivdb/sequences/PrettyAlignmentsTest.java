package edu.stanford.hivdb.sequences;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

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
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = (
			aligner.align(seq).getAlignedGeneSequences()
			.stream()
			.filter(geneseq -> geneseq.getAbstractGene().equals("PR"))
			.collect(Collectors.toList())
		);
		
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
		assertNotNull(prettyAlignment);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testToStringWithException() {
		GenotypeReference<HIV> refGene = hiv.getGenotypeReferences().get(0);
		Sequence seq = new Sequence("> test", refGene.getSequence());

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = aligner.align(seq).getAlignedGeneSequences();
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
	}
	
	@Test
	public void testGetSequenceAllPosAAs() {
		GenotypeReference<HIV> refGene = hiv.getGenotypeReferences().get(0);
		Sequence seq = new Sequence("> test", refGene.getSequence());

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = (
			aligner.align(seq).getAlignedGeneSequences()
			.stream()
			.filter(geneseq -> geneseq.getAbstractGene().equals("PR"))
			.collect(Collectors.toList())
		);
		
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
		assertFalse(prettyAlignment.getSequenceAllPosAAs().isEmpty());
	}
	
	@Test
	public void testToString() {
		GenotypeReference<HIV> refGene = hiv.getGenotypeReferences().get(0);
		Sequence seq = new Sequence("> test", refGene.getSequence());

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeq = (
			aligner.align(seq).getAlignedGeneSequences()
			.stream()
			.filter(geneseq -> geneseq.getAbstractGene().equals("PR"))
			.collect(Collectors.toList())
		);
		
		PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
		
		assertTrue(prettyAlignment.toString() instanceof String);
	}
}