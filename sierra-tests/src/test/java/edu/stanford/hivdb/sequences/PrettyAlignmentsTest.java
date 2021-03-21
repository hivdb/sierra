package edu.stanford.hivdb.sequences;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.stanford.hivdb.genotypes.GenotypeReference;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;

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
		new PrettyAlignments<HIV>(hiv.getGene("HIV1PR"), alignedGeneSeq);
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
	
	@Test
	public void testModifyHIV2BToHIV2A() {
		HIV2 hiv2 = HIV2.getInstance();
		Sequence seq = Sequence.fromGenbank("FJ442006");
		
		NucAminoAligner<HIV2> aligner = NucAminoAligner.getInstance(hiv2);
		
		List<AlignedGeneSeq<HIV2>> alignedGeneSeq = (
			aligner.align(seq).getAlignedGeneSequences()
			.stream()
			.filter(geneseq -> geneseq.getAbstractGene().equals("IN"))
			.collect(Collectors.toList())
		);
		PrettyAlignments<HIV2> prettyAlignment = new PrettyAlignments<>(hiv2.getGene("HIV2AIN"), alignedGeneSeq);
		Map<String, Map<Integer, String>> posAAs = prettyAlignment.getSequenceAllPosAAs();
		
		Map<Integer, String> expecteds = new HashMap<>();
		for (int i = 1; i < 294; i ++) {
			expecteds.put(i, "-");
		}
		expecteds.put(17, "G");
		expecteds.put(34, "K");
		expecteds.put(92, "G");
		expecteds.put(133, "V");
		expecteds.put(155, "H");
		expecteds.put(165, "L");
		expecteds.put(222, "T");
		expecteds.put(224, "Q");
		expecteds.put(260, "V");
		expecteds.put(270, "H");
		expecteds.put(283, "M");
		expecteds.put(285, "N");
		assertEquals(expecteds, posAAs.get(seq.getHeader()));
	}
}