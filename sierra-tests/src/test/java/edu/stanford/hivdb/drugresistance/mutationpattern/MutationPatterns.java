/*

    Copyright (C) 2024 Stanford HIVDB team

    This file is part of Sierra.

    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Sierra.  If not, see <https://www.gnu.org/licenses/>.
*/
package edu.stanford.hivdb.drugresistance.mutationpattern;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.ASIDrugSusc;
import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.testutils.TestUtils;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Strain;


public class MutationPatterns {
	
	private static HIV hiv = HIV.getInstance();

	public static class MutationPattern {
		public final Gene<HIV> gene;
		public final DrugClass<HIV> drugClass;
		public final MutationSet<HIV> mutations;
		public final Integer count;
		private transient GeneDR<HIV> geneDR = null;

		public MutationPattern(
			Gene<HIV> gene,
			DrugClass<HIV> drugClass,
			MutationSet<HIV> mutations,
			Integer count
		) {
			this.gene = gene;
			this.drugClass = drugClass;
			this.mutations = mutations;
			this.count = count;
		}
		
		public GeneDR<HIV> getGeneDR(DrugResistanceAlgorithm<HIV> algorithm) {
			if (geneDR == null) {
				geneDR = new GeneDR<>(gene, mutations, algorithm);
			}
			return geneDR;
		}
		
		@Override
		public String toString() {
			return String.format(
				"MutationPattern(%s, count=%d, gene=%s, drugClass=%d)",
				mutations.join(), count, gene, drugClass);
		}
	}

	private static Map<DrugClass<HIV>, List<MutationPattern>> allMutationPatterns;

	public static Map<DrugClass<HIV>, List<MutationPattern>> load() throws IOException {
		String resPath = "patterns_hiv1.json";
		String payload = TestUtils.readTestResourceToString(resPath);
		ArrayList<LinkedHashMap<String, ?>> rawPatterns = Json.loads(
			payload,
			new TypeToken<ArrayList<LinkedHashMap<String, ?>>>(){}
		);
		LinkedHashMap<DrugClass<HIV>, List<MutationPattern>> patterns = new LinkedHashMap<>();
		Strain<HIV> hiv1b = hiv.getMainStrain();
		for (LinkedHashMap<String, ?> rawPattern : rawPatterns) {
			DrugClass<HIV> dc = hiv.getDrugClass((String) rawPattern.get("drug_class"));
			Gene<HIV> gene = hiv1b.getGene(dc.getAbstractGene());
			Integer count = ((Double) rawPattern.get("count")).intValue();
			MutationSet<HIV> mutations = MutationSet.parseString(
				gene,
				(String) rawPattern.get("pattern")
			);
			MutationPattern pattern = new MutationPattern(gene, dc, mutations, count);
			if (!patterns.containsKey(dc)) {
				patterns.put(dc, new ArrayList<>());
			}
			patterns.get(dc).add(pattern);
		}
		return patterns;
	}

	static {
		try {
			allMutationPatterns = load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private DrugClass<HIV> drugClass;

	/**
	 * The tblPatterns<DrugClass> in HIVDB_Scores has the following fields:
	 * Pattern, Count, Version, Drug, Level, TotalScore, MutScore, ComboScore, MutScoreText, ComboScoreText
	 * The Version should always be the same as the active version used by the program
	 *
	 * @param drugClass
	 */
	public MutationPatterns(DrugClass<HIV> drugClass) {
		this.drugClass = drugClass;
	}

	public DrugClass<HIV> getDrugClass() { return drugClass; }
	
	public List<MutationPattern> getPatterns() {
		return allMutationPatterns.getOrDefault(drugClass, Collections.emptyList());
	}
	
	public String dumps(DrugResistanceAlgorithm<HIV> algorithm) {
		List<Map<String, ?>> results = new ArrayList<>();
		for (MutationPattern pattern : getPatterns()) {
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("gene", pattern.gene.getAbstractGene());
			result.put("drugClass", pattern.drugClass.getName());
			result.put("pattern", pattern.mutations.join());
			result.put("count", pattern.count);
			GeneDR<HIV> geneDR = pattern.getGeneDR(algorithm);
			for(Drug<HIV> drug : pattern.drugClass.getDrugs()) {
				ASIDrugSusc<HIV> susc = geneDR.getDrugSusc(drug);
				result.put(String.format("%s Level", drug.getDisplayAbbr()), susc.getLevel());
				result.put(String.format("%s Score", drug.getDisplayAbbr()), susc.getScore());
			}
			results.add(result);
		}
		return Json.dumps(results);
	}
	
}
