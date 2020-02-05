package edu.stanford.hivdb.genotypes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.genotypes.Genotype.RegionalGenotype;
import edu.stanford.hivdb.hivfacts.HIV;

public class GenotypeRegressionTest {

	private static Set<String> skippedSequences = new HashSet<>();
	private final static HIV hiv = HIV.getInstance();

	protected static class SequenceWithExpectedGenotype {
		String expectedGenotypeName;
		TestSequence testSequence;

		Genotype<HIV> getExpectedGenotype() {
			return hiv.getGenotype(expectedGenotypeName);
		}
	}

	static {
	}

	@Test
	public void test() throws FileNotFoundException {
		InputStream json = (
			GenotypeRegressionTest.class.getClassLoader()
			.getResourceAsStream("testSequences/lots.json"));
		List<SequenceWithExpectedGenotype> lots = new Gson().fromJson(
				new BufferedReader(new InputStreamReader(json)),
			    new TypeToken<List<SequenceWithExpectedGenotype>>(){}.getType());
		System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("regressionresult.tsv")), true));
		System.out.println(
			"AccessionID\tFirstNA\tLastNA\tExpectedGenotype\tResultGenotype\t" +
			"Distance\tRegionalGenotypes\tReferenceAccessionID\tPrimaryGenotype\t" +
			"1stOrigGenotype\tSecondaryGenotype\t2ndOrigGenotype\tIsExpected");
		for (SequenceWithExpectedGenotype one : lots) {
			TestSequence seq = one.testSequence;
			if (skippedSequences.contains(seq.accession)) {
				continue;
			}
			GenotypeResult<HIV> result = hiv.getGenotyper().compareAll(
				seq.sequence, seq.firstNA, seq.lastNA);
			BoundGenotype<HIV> primary = result.getFirstMatch();
			BoundGenotype<HIV> secondary = result.getFallbackMatch();
			BoundGenotype<HIV> useGeno = result.getBestMatch();
			String expectedGenotype = one.getExpectedGenotype().getDisplayName();
			//StringBuffer errMsg = new StringBuffer();
			//// errMsg.append("Wrong genotype for sequence <");
			//// errMsg.append(seq.accession);
			//// errMsg.append(">:");
			//errMsg.append("skippedSequences.add(\"" + seq.accession + "\"); //");
			String display = useGeno.getDisplayWithoutDistance();
			String regionals = useGeno.getRegionalGenotypes()
				.stream()
				.sorted((r1, r2) -> r2.getProportion().compareTo(r1.getProportion()))
				.map(RegionalGenotype::toString)
				.collect(Collectors.joining(", "));
			System.out.println(
				seq.accession + '\t' + seq.firstNA + '\t' + seq.lastNA +
				'\t' + expectedGenotype + '\t' + display + '\t' +
				useGeno.getDistance() + '\t' + regionals + '\t' +
				useGeno.getReferenceAccession() + '\t' + primary +
				'\t' + primary.getGenotype() + '\t' + secondary + '\t' +
				secondary.getGenotype() + '\t' + (expectedGenotype.equals(display)));
			//// assertEquals(errMsg.toString(), expectedGenotype, calcGenotype);
			//if (expectedGenotype != calcGenotype) {
			//	System.out.println(
			//		errMsg.toString() + "(" + seq.firstNA + "-" + seq.lastNA +
			//		") expected " + expectedGenotype + " but was " + calcGenotype);
			//}
		}
	}

}
