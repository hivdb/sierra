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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.alignment.Aligner;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison.ComparableDrugScore;
import edu.stanford.hivdb.drugresistance.algorithm.Asi.SIREnum;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class AlgorithmComparisonTest {

	@Test
	public void testRegression() {
		Type mapType =
			new TypeToken<
				Map<String, Map<String, AlgorithmComparison>>
			>() {}.getType();
		BufferedReader bufferedReader = MyFileUtils.readResource(
			AlgorithmComparisonTest.class,
			"AlgorithmComparisonTestExpecteds.json");
		Map<TestSequencesProperties, Map<String, AlgorithmComparison>>
			expecteds = Json.loads(bufferedReader, mapType);
		for (TestSequencesProperties property :
				TestSequencesProperties.values()) {
			if (!property.forRoutineTesting) {
				continue;
			}
			final InputStream testSequenceInputStream =
					TestSequencesFiles.getTestSequenceInputStream(property);
			final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);

			List<AlignedSequence> allAligneds =
					Aligner.parallelAlign(sequences);

			for (AlignedSequence alignedSeq : allAligneds) {
				Sequence sequence = alignedSeq.getInputSequence();
				// System.out.println(sequence.getHeader());
				Map<Gene, MutationSet> mutationSets = alignedSeq.getMutations().groupByGene();
				List<ComparableDrugScore>
					 actual = new AlgorithmComparison(mutationSets, Arrays.asList(Algorithm.values())).getComparisonResults();
				List<ComparableDrugScore>
					expected = expecteds.get(property.toString()).get(sequence.getHeader() + "-" + sequence.getSHA512()).getComparisonResults();
				if (expected == null) {
					// fix gson error
					expected = Collections.emptyList();
				}
				assertEquals(expected, actual);
			}
		}
	}

	@Test
	public void testConstructorAcceptAsiListMap() {
		Map<Gene, List<Asi>> asiListMap = new EnumMap<>(Gene.class);
		MutationSet mutations = new MutationSet("PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");
		for (Gene gene : Gene.values()) {
			asiListMap.put(gene, new ArrayList<>());
			MutationSet geneMuts = mutations.getGeneMutations(gene);
			asiListMap.get(gene).add(new AsiHivdb(gene, geneMuts));
			asiListMap.get(gene).add(new AsiAnrs(gene, geneMuts));
			asiListMap.get(gene).add(new AsiRega(gene, geneMuts));
		}
		AlgorithmComparison cmp = new AlgorithmComparison(asiListMap);
		List<ComparableDrugScore> r = cmp.getComparisonResults();
		assertEquals(SIREnum.I, getComparableDrugScore(r, Drug.ABC, "ANRS").SIR);
		assertEquals("Possible resistance", getComparableDrugScore(r, Drug.ABC, "ANRS").interpretation);
		assertEquals(SIREnum.R, getComparableDrugScore(r, Drug.EFV, "ANRS").SIR);
		assertEquals("Resistance", getComparableDrugScore(r, Drug.EFV, "ANRS").interpretation);
		assertEquals(SIREnum.R, getComparableDrugScore(r, Drug.EFV, "HIVDB").SIR);
		assertEquals("High-Level Resistance", getComparableDrugScore(r, Drug.EFV, "HIVDB").interpretation);
		assertEquals(SIREnum.R, getComparableDrugScore(r, Drug.EFV, "REGA").SIR);
		assertEquals("Resistant GSS 0", getComparableDrugScore(r, Drug.EFV, "REGA").interpretation);
		assertEquals(asiListMap.get(Gene.IN), cmp.getAsiList(Gene.IN));
	}

	private ComparableDrugScore getComparableDrugScore(
			List<ComparableDrugScore> list, Drug drug, String alg) {
		return list
			.stream()
			.filter(ds -> ds.drug == drug && ds.algorithm.equals(alg)).findFirst().get();
	}

	@Test
	public void testConstructorAcceptHivdbVersion() {
		HivdbVersion[] versions = new HivdbVersion[] {
			HivdbVersion.V7_0,
			HivdbVersion.V8_0_1
		};
		MutationSet mutations = new MutationSet(Gene.RT, "M41L,L74I,M184V,T215Y");
		AlgorithmComparison cmp = new AlgorithmComparison(mutations.groupByGene(), versions);
		List<ComparableDrugScore> r = cmp.getComparisonResults();
		assertEquals("High-level resistance", getComparableDrugScore(r, Drug.AZT, "HIVDB_7.0").interpretation);
		assertEquals("Intermediate Resistance", getComparableDrugScore(r, Drug.AZT, "HIVDB_8.0.1").interpretation);
	}

	@Test
	public void testCalcAsiListFromCustomAlgorithms() throws IOException {
		MutationSet mutations = new MutationSet(Gene.RT, "M41L,L74I,M184V,T215Y");
		BufferedReader reader = MyFileUtils.readResource(AlgorithmComparisonTest.class, "AlgXMLs/HIVDB_7.5.xml");
		String xml = IOUtils.toString(reader);
		Map<String, String> xmls = new HashMap<>();
		xmls.put("test", xml);
		List<Asi> r = AlgorithmComparison.calcAsiListFromCustomAlgorithms(Gene.RT, mutations, xmls);
		assertEquals("test", r.get(0).getAlgorithmName());
	}
}