package edu.stanford.hivdb.hivfacts.hiv2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.seqreads.SequenceReads;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIV2DefaultSequenceReadsValidatorTest {

	final static HIV2 hiv = HIV2.getInstance();
	
	@Test
	public void test() {
		HIV2DefaultSequenceReadsValidator validator = new HIV2DefaultSequenceReadsValidator();
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV2> posCodonReads = new PositionCodonReads<HIV2>(
				hiv.getGene("HIV2ART"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV2>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV2> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV2A"), allReads, 0.01, 0L, 1000L);
		
		List<ValidationResult> results = validator.validate(seqReads);
		assertEquals(results.size(), 1);
		
		// Empty
		allReads = new ArrayList<>();
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV2A"), allReads, 0.01, 0L, 1000L);
		results = validator.validate(seqReads);
		assertEquals(results.size(), 1);
		
		allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV2A"), allReads, 0.01, 0L, 10000L);
		results = validator.validateTrimmedPositions(seqReads);
		assertEquals(results.size(), 1);
		
		
		allReads = new ArrayList<>();
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV2A"), allReads, 0.01, 0L, 1000L);
		results = validator.validateNoMissingPositions(seqReads);
		assertEquals(results.size(), 0);
		
		
		// Apobec mutations
		allReads = new ArrayList<>();
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AAG", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV2>(
					hiv.getGene("HIV2APR"), 16, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("GAA", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV2>(
				hiv.getGene("HIV2APR"), 27, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("GAA", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV2>(
				hiv.getGene("HIV2APR"), 40, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AAU", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV2>(
				hiv.getGene("HIV2APR"), 30, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AGC", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV2>(
				hiv.getGene("HIV2APR"), 73, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV2A"), allReads, 0.01, 0L, 1000L);
		results = validator.validateNoTooManyApobec(seqReads);
		assertEquals(results.size(), 0);
	}
}
	