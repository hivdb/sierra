package edu.stanford.hivdb.drugresistance.algorithm;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison.ComparableDrugScore;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;

public class AlgorithmComparisonRegressionTest {
	
	private static final HIV hiv1 = HIV.getInstance();
	
	@Test
	public void testRegression() {
		Type mapType =
			new TypeToken<
				Map<String, Map<String, AlgorithmComparison<HIV>>>
			>() {}.getType();
		BufferedReader bufferedReader = MyFileUtils.readResource(
			AlgorithmComparisonTest.class,
			"AlgorithmComparisonTestExpecteds.json");
		Map<TestSequencesProperties, Map<String, AlgorithmComparison<HIV>>>
			expecteds = Json.loads(bufferedReader, mapType);
		
		List<DrugResistanceAlgorithm<HIV>> algos = new ArrayList<>();
		
//		System.out.println(hiv1.getDrugResistAlgorithms());
		DrugResistanceAlgorithm<HIV> algo1 = hiv1.getDrugResistAlgorithm("HIVDB_8.9-1");
		DrugResistanceAlgorithm<HIV> algo2 = hiv1.getDrugResistAlgorithm("Rega_9.1");
//		DrugResistanceAlgorithm<HIV> algo1 = hiv1.getDrugResistAlgorithm("HIVDB_8.9.1");
		
		algos.add(algo1);
		algos.add(algo2);
		
		for (TestSequencesProperties property :
				TestSequencesProperties.values()) {
			if (!property.forRoutineTesting) {
				continue;
			}
			final InputStream testSequenceInputStream =
					TestSequencesFiles.getTestSequenceInputStream(property);
			final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);

			NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv1);
			List<AlignedSequence<HIV>> allAligneds = aligner.parallelAlign(sequences);

			for (AlignedSequence<HIV> alignedSeq : allAligneds) {
				Sequence sequence = alignedSeq.getInputSequence();
				// System.out.println(sequence.getHeader());
				MutationSet<HIV> mutations = alignedSeq.getMutations();
				
				List<ComparableDrugScore<HIV>>
					 actual = new AlgorithmComparison<HIV>(mutations, algos).getComparisonResults();
				
				List<ComparableDrugScore<HIV>>
					expected = expecteds.get((Object) property.toString()).get(sequence.getHeader() + "-" + sequence.getSHA512()).getComparisonResults();
				if (expected == null) {
					// fix gson error
					expected = Collections.emptyList();
				}

				// Issue, the AlgorithmComparisonTestExpecteds.json file is not fit with algorithm
				assertEquals(Json.dumps(expected), Json.dumps(actual));
			}
		}
	}
}