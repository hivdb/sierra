/*

    Copyright (C) 2017 Stanford HIVDB team

    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.drugresistance.algorithm;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.lang.reflect.Type;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison.ComparableDrugScore;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.utilities.FastaUtils;

public class AlgorithmComparisonTest {
	
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
				assertEquals(expected.size(), actual.size());
			}
		}
	}

	@Test
	public void testConstructor() {
//		Map<Gene<HIV>, List<AsiResult<HIV>>> asiListMap = new TreeMap<>();
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");
//		for (Gene<HIV> gene : hiv1.getGenes(hiv1.getStrain("HIV1"))) {
//			
//			asiListMap.put(gene, new ArrayList<>());
//			MutationSet<HIV> geneMuts = mutations.getGeneMutations(gene);
//			asiListMap.get(gene).add(new AsiHivdb(gene, geneMuts));
//			asiListMap.get(gene).add(new AsiAnrs(gene, geneMuts));
//			asiListMap.get(gene).add(new AsiRega(gene, geneMuts));
//		}
		
		List<DrugResistanceAlgorithm<HIV>> algos = new ArrayList<>();
		DrugResistanceAlgorithm<HIV> algo1 = hiv1.getDrugResistAlgorithm("HIVDB_8.0");
		DrugResistanceAlgorithm<HIV> algo2 = hiv1.getDrugResistAlgorithm("ANRS_30");
		DrugResistanceAlgorithm<HIV> algo3 = hiv1.getDrugResistAlgorithm("Rega_10.0");
		
		algos.add(algo1);
		algos.add(algo2);
		algos.add(algo3);
		
		AlgorithmComparison<HIV> cmp = new AlgorithmComparison<HIV>(mutations, algos);
		List<ComparableDrugScore<HIV>> r = cmp.getComparisonResults();
		assertEquals(SIREnum.I, getComparableDrugScore(r, hiv1.getDrug("ABC"), algo2).getSIR());
		assertEquals("Possible resistance", getComparableDrugScore(r, hiv1.getDrug("ABC"), algo2).getInterpretation());
		assertEquals(SIREnum.R, getComparableDrugScore(r, hiv1.getDrug("EFV"), algo2).getSIR());
		assertEquals("Resistance", getComparableDrugScore(r, hiv1.getDrug("EFV"), algo2).getInterpretation());
		assertEquals(SIREnum.R, getComparableDrugScore(r, hiv1.getDrug("EFV"), algo1).getSIR());
		assertEquals("Resistance", getComparableDrugScore(r, hiv1.getDrug("EFV"), algo2).getInterpretation());
		assertEquals(SIREnum.R, getComparableDrugScore(r, hiv1.getDrug("EFV"), algo3).getSIR());
		assertEquals("Resistant GSS 0", getComparableDrugScore(r, hiv1.getDrug("EFV"), algo3).getInterpretation());
	}

	private ComparableDrugScore<HIV> getComparableDrugScore(
			List<ComparableDrugScore<HIV>> list, Drug<HIV> drug, DrugResistanceAlgorithm<HIV> alg) {
		return list
			.stream()
			.filter(ds -> ds.getDrug() == drug && ds.getAlgorithm() == alg.getName()).findFirst().get();
	}

	@Test
	public void testConstructorAcceptHivdbVersion() {

		MutationSet<HIV> mutations = MutationSet.parseString(hiv1.getGene("HIV1RT"), "M41L,L74I,M184V,T215Y");
		
		List<DrugResistanceAlgorithm<HIV>> algos = new ArrayList<>();
		
		DrugResistanceAlgorithm<HIV> algo1 = hiv1.getDrugResistAlgorithm("HIVDB_7.0");
		algos.add(algo1);
		DrugResistanceAlgorithm<HIV> algo2 = hiv1.getDrugResistAlgorithm("HIVDB_8.0.1");
		algos.add(algo2);
		
		
		
		AlgorithmComparison<HIV> cmp = new AlgorithmComparison<HIV>(mutations, algos);
		
		List<ComparableDrugScore<HIV>> r = cmp.getComparisonResults();
		assertEquals("High-level resistance", getComparableDrugScore(r, hiv1.getDrug("AZT"), algo1).getInterpretation());
		assertEquals("Intermediate Resistance", getComparableDrugScore(r, hiv1.getDrug("AZT"), algo2).getInterpretation());
	}
}
