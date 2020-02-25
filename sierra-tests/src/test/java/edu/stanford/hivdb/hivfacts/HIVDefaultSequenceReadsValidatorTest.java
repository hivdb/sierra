package edu.stanford.hivdb.hivfacts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.seqreads.SequenceReads;
import edu.stanford.hivdb.utilities.ValidationResult;

public class HIVDefaultSequenceReadsValidatorTest {

	final static HIV hiv = HIV.getInstance();

	@Test
	public void test() {
		HIVDefaultSequenceReadsValidator validator = new HIVDefaultSequenceReadsValidator();
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		
		SequenceReads<HIV> seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		
		List<ValidationResult> results = validator.validate(seqReads);
		assertEquals(results.size(), 1);
		
		// Empty
		allReads = new ArrayList<>();
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		results = validator.validate(seqReads);
		assertEquals(results.size(), 1);
		
		allReads = new ArrayList<>();
		allReads.add(posCodonReads);
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(10000));
		results = validator.validateTrimmedPositions(seqReads);
		assertEquals(results.size(), 1);
		
		
		allReads = new ArrayList<>();
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		results = validator.validateNoMissingPositions(seqReads);
		assertEquals(results.size(), 0);
		
		
		// Apobec mutations
		allReads = new ArrayList<>();
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AAG", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV>(
					hiv.getGene("HIV1PR"), 16, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("GAA", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1PR"), 27, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("GAA", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1PR"), 40, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AAU", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1PR"), 30, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AGC", Long.valueOf(100));
		posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1PR"), 73, 1000, allCodonReads);
		allReads.add(posCodonReads);
		
		seqReads = SequenceReads.fromCodonReadsTable(
				"test", hiv.getStrain("HIV1"), allReads, Double.valueOf(0.01), Long.valueOf(1000));
		results = validator.validateNoTooManyApobec(seqReads);
		assertEquals(results.size(), 1);
	}
}