package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.genotypes.GenotypeRegressionTest.SequenceWithExpectedGenotype;
import edu.stanford.hivdb.hivfacts.HIV;

public class BoundGenotypeRegressionTest {

	private final static HIV hiv = HIV.getInstance();
//	private final GenotypeReference<HIV> genotype = hiv.getGenotypeReferences().get(0);
//	private final BoundGenotype<HIV> boundGenotype = genotype.getBoundGenotype(
//			genotype.getSequence(),
//			genotype.getFirstNA(),
//			genotype.getLastNA(),
//			new ArrayList<Integer>());

	@Test
	public void testGetDisplayGenotypesRegression() {
		InputStream json = (
			GenotypeRegressionTest.class.getClassLoader()
			.getResourceAsStream("GenotypeRegression/GenotypeRegression.json"));
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