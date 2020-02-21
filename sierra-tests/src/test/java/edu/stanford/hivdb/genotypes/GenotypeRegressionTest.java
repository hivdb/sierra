package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.genotypes.Genotype.RegionalGenotype;
import edu.stanford.hivdb.hivfacts.HIV;

import edu.stanford.hivdb.testutils.TestUtils;

public class GenotypeRegressionTest {

	
	private final static HIV hiv = HIV.getInstance();
	
	private static Set<String> skippedSequences = new HashSet<>();
	

	protected static class SequenceWithExpectedGenotype {
		String expectedGenotypeName;
		TestSequence testSequence;

		Genotype<HIV> getExpectedGenotype() {
			return hiv.getGenotype(expectedGenotypeName);
		}
	}

	@Test
	public void test() throws FileNotFoundException {
		
		InputStream json = TestUtils.readTestResource("GenotypeRegression/GenotypeRegression.json");
		
		List<SequenceWithExpectedGenotype> verifications = new Gson().fromJson(
				new BufferedReader(new InputStreamReader(json)),
			    new TypeToken<List<SequenceWithExpectedGenotype>>(){}.getType());
	

		String tsvFilePath = "GenotypeRegressionResult.tsv";
		
		String[] tsvHeader = new String[] {
				"AccessionID",
				"FirstNA",
				"LastNA",
				"ExpectedGenotype",
				"ResultGenotype",
				"Distance",
				"RegionalGenotypes",
				"ReferenceAccessionID",
				"PrimaryGenotype",
				"1stOrigGenotype",
				"SecondaryGenotype",
				"2ndOrigGenotype",
				"IsExpected"
		};
		
		List<List<String>> rows = new ArrayList<List<String>>();
		

		
		for (SequenceWithExpectedGenotype verify : verifications) {
			
			TestSequence seq = verify.testSequence;
			
			if (skippedSequences.contains(seq.accession)) {
				continue;
			}
			
			Genotyper<HIV> genotyper = hiv.getGenotyper();
			GenotypeResult<HIV> result = genotyper.compareAll(
				seq.sequence, seq.firstNA, seq.lastNA);
			
			BoundGenotype<HIV> primary = result.getFirstMatch();
			BoundGenotype<HIV> secondary = result.getFallbackMatch();
			BoundGenotype<HIV> best = result.getBestMatch();
			
			
			String expectedGenotype = verify.getExpectedGenotype().getDisplayName();
			
			String display = best.getDisplayWithoutDistance();
			String regionals = best.getRegionalGenotypes()
				.stream()
				.sorted((r1, r2) -> r2.getProportion().compareTo(r1.getProportion()))
				.map(RegionalGenotype::toString)
				.collect(Collectors.joining(", "));
			
			List<String> oneRow = new ArrayList<String>();
			oneRow.add(seq.accession);
			oneRow.add(String.valueOf(seq.firstNA));
			oneRow.add(String.valueOf(seq.lastNA));
			oneRow.add(expectedGenotype);
			oneRow.add(display);
			oneRow.add(String.valueOf(best.getDistance()));
			oneRow.add(regionals);
			oneRow.add(best.getReferenceAccession());
			oneRow.add(primary.toString());
			oneRow.add(primary.getGenotype().toString());
			oneRow.add(secondary.toString());
			oneRow.add(secondary.getGenotype().toString());
			oneRow.add(String.valueOf(expectedGenotype.contentEquals(display)));
			
			
			StringBuilder errMessage = new StringBuilder();
			errMessage.append("Wrong genotype for sequence <");
			errMessage.append(seq.accession);
			errMessage.append(">:");
			errMessage.append("skippedSequences.add(\"" + seq.accession + "\"); //");
			
			assertEquals(tsvHeader.length, oneRow.size());
			rows.add(oneRow);
			
			// Assert or Print errors
//			assertEquals(errMessage.toString(), expectedGenotype, display);
//			if (expectedGenotype != display) {
//				errMessage.append("(");
//				errMessage.append(String.valueOf(seq.firstNA));
//				errMessage.append("-");
//				errMessage.append(String.valueOf(seq.lastNA));
//				errMessage.append(")");
//				errMessage.append(" expected ");
//				errMessage.append(expectedGenotype);
//				errMessage.append(" but was ");
//				errMessage.append(display);
//			}
//			List<String> message = new ArrayList<>();
//			message.add(errMessage.toString());
//			rows.add(message);
		}
		
		TestUtils.writeTSVFile(tsvFilePath, tsvHeader, rows);
	}

}
