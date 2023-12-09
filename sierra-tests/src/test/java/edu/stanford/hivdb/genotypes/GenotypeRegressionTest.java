package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.hivfacts.HIV;

import edu.stanford.hivdb.testutils.TestUtils;

public class GenotypeRegressionTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	private static Set<String> skippedSequences = new HashSet<>();
	
	protected static class SequenceWithExpectedGenotype {
		String expectedGenotypeName;
		String expectedGenotypeDisplay;
		TestSequence testSequence;

		protected SequenceWithExpectedGenotype(
			String expectedGenotypeName,
			String expectedGenotypeDisplay,
			TestSequence testSequence
		) {
			this.expectedGenotypeName = expectedGenotypeName;
			this.expectedGenotypeDisplay = expectedGenotypeDisplay;
			this.testSequence = testSequence;
		}
		
		String getExpectedGenotypeDisplay() {
			return expectedGenotypeDisplay != null ?
				expectedGenotypeDisplay :
				getExpectedGenotype().getDisplayName();
		}
		
		Genotype<HIV> getExpectedGenotype() {
			return hiv.getGenotype(expectedGenotypeName);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (!(o instanceof SequenceWithExpectedGenotype)) {
				return false;
			}
			SequenceWithExpectedGenotype other = (SequenceWithExpectedGenotype) o;
			return (
				this.expectedGenotypeDisplay.equals(other.expectedGenotypeDisplay) &&
				this.expectedGenotypeName.equals(other.expectedGenotypeName) &&
				this.testSequence.accession.equals(other.testSequence.accession)
			);
		}
		
		@Override
		public String toString() {
			return String.format(
				"<%s name=%s display=%s>",
				testSequence.accession,
				expectedGenotypeDisplay,
				expectedGenotypeName);
		}
	}

	//@Test
	public void test() throws FileNotFoundException {
		
		InputStream json = TestUtils.readTestResource("GenotypeRegression/GenotypeRegression.json");
		
		List<SequenceWithExpectedGenotype> verifications = new Gson().fromJson(
				new BufferedReader(new InputStreamReader(json)),
			    new TypeToken<List<SequenceWithExpectedGenotype>>(){}.getType());
	

		String jsonFilePath = "newGenotypeRegression.json";
		
		List<SequenceWithExpectedGenotype> rows = new ArrayList<>();
		
		// int count = 0;
		
		for (SequenceWithExpectedGenotype verify : verifications) {
			/* if (count > 100) {
				break;
			}
			count ++; */
			TestSequence seq = verify.testSequence;
			
			if (skippedSequences.contains(seq.accession)) {
				continue;
			}
			
			Genotyper<HIV> genotyper = hiv.getGenotyper();
			GenotypeResult<HIV> result = genotyper.compareAll(
				seq.sequence, seq.firstNA, seq.lastNA);
			
			BoundGenotype<HIV> best = result.getBestMatch();
			String display = best.getDisplayWithoutDistance();
			
			SequenceWithExpectedGenotype oneRow = new SequenceWithExpectedGenotype(
				best.getGenotype().getIndexName(),
				display,
				seq
			);
				
			rows.add(oneRow);
		}
		TestUtils.writeJSONFile(jsonFilePath, rows);
		assertEquals(verifications, rows);
	}

}
