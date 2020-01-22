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

// import java.util.ArrayList;
// import java.util.EnumMap;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// 
// import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;
// import edu.stanford.hivdb.drugresistance.algorithm.AsiResult;
// import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
// import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
// import edu.stanford.hivdb.hivfacts.HIVDrug;
// import edu.stanford.hivdb.hivfacts.HIVDrugClass;
// import edu.stanford.hivdb.hivfacts.HIVGene;
// import edu.stanford.hivdb.hivfacts.HIVStrain;
// import edu.stanford.hivdb.mutations.MutationSet;
// import edu.stanford.hivdb.utilities.Cachable;
// import edu.stanford.hivdb.utilities.Cachable.DataLoader;
// import edu.stanford.hivdb.utilities.TSV;

/**
 * Compare the results obtained using the current version of the algorithm and the new version of
 * the algorithm using data in HIVDB_Results. Note: the new version of the algorithm is the
 * default "live" version of the algorithm in the HIVDB_Scores database
 */
@Deprecated
public class TabularPatternsComparison {

//	public final static HivdbVersion[] VERSIONS = new HivdbVersion[] {
//		HivdbVersion.getPrevMajorVersion(),
//		HivdbVersion.getLatestVersion()
//	};
//
//	@Cachable.CachableField
//	private static Map<HIVDrugClass, List<List<String>>> allRows;
//
//	private static Map<HIVDrugClass, TabularPatternsComparison> singletons;
//	private final List<String> headers;
//	private final HIVDrugClass drugClass;
//
//	static {
//		Cachable.setup(TabularPatternsComparison.class);
//		singletons = new EnumMap<>(HIVDrugClass.class);
//	}
//
//	private TabularPatternsComparison(HIVDrugClass drugClass) {
//		headers = new ArrayList<>();
//		headers.add("Pattern");
//		headers.add("Count");
//		for (HIVDrug drug : drugClass.getDrugs()) {
//			headers.add(drug.getDisplayAbbr());
//		}
//		headers.add("Num Diffs");
//		headers.add("Max Diff");
//		this.drugClass = drugClass;
//	}
//
//	public static TabularPatternsComparison getInstance(HIVDrugClass drugClass) {
//		if (!singletons.containsKey(drugClass)) {
//			singletons.put(drugClass, new TabularPatternsComparison(drugClass));
//		}
//		return singletons.get(drugClass);
//	}
//
//	@Override public String toString() {
//		List<List<String>> rows = allRows.get(drugClass);
//		return TSV.dumps(headers, rows);
//	}
//
//	public static class ComparisonDataLoader implements DataLoader<Map<HIVDrugClass, List<List<String>>>> {
//
//		protected Map<String, Integer> getAllPatternCounts(HIVDrugClass drugClass) {
//			return new MutationPatterns(drugClass).getAllPatternCounts();
//		}
//
//		@Override
//		public String getFieldName() {
//			return "allRows";
//		}
//
//		@Override
//		public Map<HIVDrugClass, List<List<String>>> load() {
//			Map<HIVDrugClass, List<List<String>>> results = new EnumMap<>(HIVDrugClass.class);
//			for (HIVDrugClass drugClass : HIVDrugClass.values()) {
//				// TODO: no pattern comparison for HIV2
//				HIVGene gene = HIVGene.valueOf(HIVStrain.HIV1, drugClass.gene());
//				List<HIVDrug> drugs = drugClass.getDrugs();
//				List<List<String>> rows = new ArrayList<>();
//				results.put(drugClass, rows);
//
//				Map<String, Integer> patternCounts = getAllPatternCounts(drugClass);
//
//				for (String pattern : patternCounts.keySet()) {
//					List<String> rowContents = new ArrayList<>();
//					Integer patternCount = patternCounts.get(pattern);
//					MutationSet mutPattern = new MutationSet(gene, pattern);
//					Map<HIVGene, MutationSet> geneMutSet = new HashMap<>();
//					geneMutSet.put(gene, mutPattern);
//					rowContents.add(pattern);
//					rowContents.add(patternCount.toString());
//
//					AlgorithmComparison algorithmComparison =
//							new AlgorithmComparison(geneMutSet, VERSIONS);
//					List<AsiResult> asiObjs = algorithmComparison.getAsiList(gene);
//					AsiResult leftAsi = asiObjs.get(0);
//					AsiResult rightAsi = asiObjs.get(1);
//					int numDiffs = 0;
//					int maxDiff = 0;
//					for (HIVDrug drug : drugs) {
//						int leftLevel = leftAsi.getDrugLevel(drug);
//						int leftScore = leftAsi.getTotalScore(drug).intValue();
//						int rightLevel = rightAsi.getDrugLevel(drug);
//						int rightScore = rightAsi.getTotalScore(drug).intValue();
//						String row = "";
//						if (leftLevel == rightLevel && leftScore == rightScore) {
//							row = String.format("%s (%s)", leftLevel, leftScore);
//						}
//						else if (leftLevel == rightLevel) {
//							row = String.format(
//								"%s (%s => %s)", leftLevel, leftScore, rightScore);
//						}
//						else {
//							row = String.format(
//								"%s (%s) => %s (%s)",
//								leftLevel, leftScore,
//								rightLevel, rightScore);
//							numDiffs ++;
//							maxDiff =Math.max(
//								Math.abs(rightLevel - leftLevel), maxDiff);
//						}
//						rowContents.add(row);
//					}
//					rowContents.add("" + numDiffs);
//					rowContents.add("" + maxDiff);
//					rows.add(rowContents);
//				}
//			}
//			return results;
//		}
//	}

}
