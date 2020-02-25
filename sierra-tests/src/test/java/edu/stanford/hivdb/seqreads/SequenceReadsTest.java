package edu.stanford.hivdb.seqreads;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import edu.stanford.hivdb.genotypes.BoundGenotype;
import edu.stanford.hivdb.genotypes.GenotypeResult;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.PositionCodonReads;


public class SequenceReadsTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testFromCodonReadsTable() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertNotNull(seqReads);
		
		
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(-1.0), Long.valueOf(1));
		assertNotNull(seqReads);
		
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(0));
		assertNotNull(seqReads);
		
		PositionCodonReads<HIV> posCodonReads2 = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 103, 1000, allCodonReads);
		allReads.add(posCodonReads2);
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(0));
		assertNotNull(seqReads);
	}
	
	@Test
	public void testGetCodonReadsCoverage() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertFalse(seqReads.getCodonReadsCoverage().isEmpty());
	}
	
	@Test
	public void testGetSize() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getSize(), Integer.valueOf(0));
	}
	
	@Test
	public void testGetCutoffSuggestionLooserLimit() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getCutoffSuggestionLooserLimit(), Double.valueOf(0));
	}
	
	@Test
	public void testGetCutoffSuggestionStricterLimit() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getCutoffSuggestionStricterLimit(), Double.valueOf(0));
	}
	
	@Test
	public void testGetProportionTrimmedPositions() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getProportionTrimmedPositions(), Double.valueOf(0));
	}
	
	@Test
	public void testGetName() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getName(), "test");
	}
	
	@Test
	public void testGetStrain() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getStrain(), hiv.getStrain("HIV1"));
	}
	
	@Test
	public void testIsEmpty() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertFalse(seqReads.isEmpty());
	}
	
	@Test
	public void testGetMinPrevalence() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getMinPrevalence(), 0.01, 0.01);
	}
	
	@Test
	public void testGetMinReadDepth() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertEquals(seqReads.getMinReadDepth(), 1000);
	}
	
	@Test
	public void testGetValidationResults() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertFalse(seqReads.getValidationResults().isEmpty());
	}
	
	@Test
	public void testDescriptiveStatistics() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertTrue(seqReads.getReadDepthStats() instanceof DescriptiveStatistics);
	}
	
	@Test
	public void testGetAllGeneSequenceReads() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertFalse(seqReads.getAllGeneSequenceReads().isEmpty());
	}
	
	@Test
	public void testGetGeneSequenceReads() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertTrue(seqReads.getGeneSequenceReads(hiv.getGene("HIV1RT")) instanceof GeneSequenceReads);
	}
	
	@Test
	public void testGetAvailableGenes() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertFalse(seqReads.getAvailableGenes().isEmpty());
	}
	
	@Test
	public void testGetConcatenatedSeq() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertTrue(seqReads.getConcatenatedSeq().startsWith("....."));
		assertTrue(seqReads.getConcatenatedSeq().contains("AGW"));
	}
	
	@Test
	public void testGetConcatenatedSeqForSubtyping() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		assertTrue(seqReads.getConcatenatedSeqForSubtyping().startsWith("....."));
		assertTrue(seqReads.getConcatenatedSeqForSubtyping().contains("NNN"));
	}
	
	@Test
	public void testGetHistogram() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		SequenceReadsHistogram<HIV> histogram = seqReads.getHistogram(
				0.01, 0.1, 2, true, SequenceReadsHistogram.AggregationOption.Codon);
		
		assertNotNull(histogram);
		
	}
	
	@Test
	public void testGetHistogram2() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		SequenceReadsHistogram<HIV> histogram = seqReads.getHistogram(
				0.01, 0.1, new Double[] {Double.valueOf(0.1), Double.valueOf(0.01)},
				true, SequenceReadsHistogram.AggregationOption.Codon);
		
		assertNotNull(histogram);
		
	}
	
	@Test
	public void testGetMutations() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		
		assertTrue(seqReads.getMutations(0.01) instanceof MutationSet);
		
	}
	
	@Test
	public void testGetMutations2() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		
		assertTrue(seqReads.getMutations() instanceof MutationSet);
		
	}
	
	@Test
	public void testGetSubtypeResult() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		
		assertTrue(seqReads.getSubtypeResult() instanceof GenotypeResult);
		
	}
	
	@Test
	public void testGetBestMatchingSubtype() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		
		assertTrue(seqReads.getBestMatchingSubtype() instanceof BoundGenotype);
		
	}
	
	@Test
	public void testGetSubtypeText() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		
		assertEquals(seqReads.getSubtypeText(), "CRF02_AG (0.00%)");
		
	}
	
	@Test
	public void testGetMixturePcnt() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		
		assertEquals(seqReads.getMixturePcnt(), 33, 1);
		
	}
}