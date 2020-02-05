package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.genotypes.GenotypeRegressionTest.SequenceWithExpectedGenotype;
import edu.stanford.hivdb.hivfacts.HIV;

public class BoundGenotypeTest {
	
	private final static HIV hiv = HIV.getInstance();

	@Test
	public void testGetDisplayGenotypes() {
		InputStream json = (
			GenotypeRegressionTest.class.getClassLoader()
			.getResourceAsStream("testSequences/lots.json"));
		List<SequenceWithExpectedGenotype> lots = new Gson().fromJson(
				new BufferedReader(new InputStreamReader(json)),
			    new TypeToken<List<SequenceWithExpectedGenotype>>(){}.getType());
		for (SequenceWithExpectedGenotype eseq : lots) {
			TestSequence seq = eseq.testSequence;
			if (seq.accession.equals("AB356209")) {
				GenotypeResult<HIV> result = hiv.getGenotyper().compareAll(
					seq.sequence, seq.firstNA, seq.lastNA);
				BoundGenotype<HIV> primary = result.getFirstMatch();
				assertEquals("B (3.18%)", primary.getDisplay());
				assertEquals(1005, primary.getSequence().length());
			}
			else if (seq.accession.equals("DQ345008-DQ345037")) {
				GenotypeResult<HIV> result = hiv.getGenotyper().compareAll(
					seq.sequence, seq.firstNA, seq.lastNA);
				BoundGenotype<HIV> primary = result.getFirstMatch();
				assertEquals("A (3.44%)", primary.getDisplay());
				assertEquals(1047, primary.getSequence().length());
			}
		}
	}
	
}
