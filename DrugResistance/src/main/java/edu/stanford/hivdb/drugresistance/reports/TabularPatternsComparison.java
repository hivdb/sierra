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

package edu.stanford.hivdb.drugresistance.reports;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;
import edu.stanford.hivdb.drugresistance.algorithm.Asi;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Cachable;
import edu.stanford.hivdb.utilities.Cachable.DataLoader;
import edu.stanford.hivdb.utilities.TSV;

/**
 * Compare the results obtained using the current version of the algorithm and the new version of
 * the algorithm using data in HIVDB_Results. Note: the new version of the algorithm is the
 * default "live" version of the algorithm in the HIVDB_Scores database
 */
public class TabularPatternsComparison {

	public final static HivdbVersion[] VERSIONS = new HivdbVersion[] {
		HivdbVersion.getPrevMajorVersion(),
		HivdbVersion.getLatestVersion()
	};

	@Cachable.CachableField
	private static Map<DrugClass, List<List<String>>> allRows;

	private static Map<DrugClass, TabularPatternsComparison> singletons;
	private final List<String> headers;
	private final DrugClass drugClass;

	static {
		Cachable.setup(TabularPatternsComparison.class);
		singletons = new EnumMap<>(DrugClass.class);
	}

	private TabularPatternsComparison(DrugClass drugClass) {
		headers = new ArrayList<>();
		headers.add("Pattern");
		headers.add("Count");
		for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
			headers.add(drug.getDisplayAbbr());
		}
		headers.add("Num Diffs");
		headers.add("Max Diff");
		this.drugClass = drugClass;
	}

	public static TabularPatternsComparison getInstance(DrugClass drugClass) {
		if (!singletons.containsKey(drugClass)) {
			singletons.put(drugClass, new TabularPatternsComparison(drugClass));
		}
		return singletons.get(drugClass);
	}

	@Override public String toString() {
		List<List<String>> rows = allRows.get(drugClass);
		return TSV.dumps(headers, rows);
	}

	public static class ComparisonDataLoader implements DataLoader<Map<DrugClass, List<List<String>>>> {

		protected Map<String, Integer> getAllPatternCounts(DrugClass drugClass) {
			return new MutationPatterns(drugClass).getAllPatternCounts();
		}

		@Override
		public String getFieldName() {
			return "allRows";
		}

		@Override
		public Map<DrugClass, List<List<String>>> load() {
			Map<DrugClass, List<List<String>>> results = new EnumMap<>(DrugClass.class);
			for (DrugClass drugClass : DrugClass.values()) {
				Gene gene = drugClass.gene();
				List<Drug> drugs = drugClass.getDrugsForHivdbTesting();
				List<List<String>> rows = new ArrayList<>();
				results.put(drugClass, rows);

				Map<String, Integer> patternCounts = getAllPatternCounts(drugClass);

				for (String pattern : patternCounts.keySet()) {
					List<String> rowContents = new ArrayList<>();
					Integer patternCount = patternCounts.get(pattern);
					MutationSet mutPattern = new MutationSet(gene, pattern);
					Map<Gene, MutationSet> geneMutSet = new HashMap<>();
					geneMutSet.put(gene, mutPattern);
					rowContents.add(pattern);
					rowContents.add(patternCount.toString());

					AlgorithmComparison algorithmComparison =
							new AlgorithmComparison(geneMutSet, VERSIONS);
					List<Asi> asiObjs = algorithmComparison.getAsiList(gene);
					Asi leftAsi = asiObjs.get(0);
					Asi rightAsi = asiObjs.get(1);
					int numDiffs = 0;
					int maxDiff = 0;
					for (Drug drug : drugs) {
						int leftLevel = leftAsi.getDrugLevel(drug);
						int leftScore = leftAsi.getTotalScore(drug).intValue();
						int rightLevel = rightAsi.getDrugLevel(drug);
						int rightScore = rightAsi.getTotalScore(drug).intValue();
						String row = "";
						if (leftLevel == rightLevel && leftScore == rightScore) {
							row = String.format("%s (%s)", leftLevel, leftScore);
						}
						else if (leftLevel == rightLevel) {
							row = String.format(
								"%s (%s => %s)", leftLevel, leftScore, rightScore);
						}
						else {
							row = String.format(
								"%s (%s) => %s (%s)",
								leftLevel, leftScore,
								rightLevel, rightScore);
							numDiffs ++;
							maxDiff =Math.max(
								Math.abs(rightLevel - leftLevel), maxDiff);
						}
						rowContents.add(row);
					}
					rowContents.add("" + numDiffs);
					rowContents.add("" + maxDiff);
					rows.add(rowContents);
				}
			}
			return results;
		}
	}

}
