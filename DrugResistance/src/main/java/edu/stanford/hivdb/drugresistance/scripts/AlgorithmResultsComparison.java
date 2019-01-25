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

package edu.stanford.hivdb.drugresistance.scripts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison.ComparableDrugScore;
import edu.stanford.hivdb.drugresistance.algorithm.Asi.SIREnum;
import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.TSV;

/**
 * Compare the results obtained using the current version of the algorithm and the new version of
 * the algorithm using data in HIVDB_Results. Note: the new version of the algorithm is the
 * default "live" version of the algorithm in the HIVDB_Scores database
 *
 * Although one would expect the patterns of mutations to be slightly different between the two
 * algorithms (with a large overlap), the current tables in HIVDB_Results have the same patterns
 * for each algorithm. This simplifies the comparison being performed in this class. But this will
 * likely need to be addressed in the future.
 *
 */
public class AlgorithmResultsComparison {

	private static List<Algorithm> algorithms =
			Arrays.asList(new Algorithm[] {Algorithm.HIVDB, Algorithm.REGA});
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	public static void main (String [] args) throws SQLException {
		for (DrugClass drugClass : DrugClass.values()) {
			// TODO: HIV2 support
			Gene gene = Gene.valueOf(Strain.HIV1, drugClass.gene());
			List<String> header = new ArrayList<>();
			List<List<String>> rows = new ArrayList<>();

			header.add("Pattern");
			header.add("Count");
			for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
				for (Algorithm alg : algorithms) {
					header.add("" + alg + "-" + drug + "-SIR");
					header.add("" + alg + "-" + drug + "-Level");
				}
			}
			header.add("NumDiffs");
			header.add("MaxDiff");

			Map<String, Integer> patternCounts = genPatternCounts(drugClass);

			for (String pattern : patternCounts.keySet()) {
				System.out.println(pattern);
				List<String> rowContents = new ArrayList<>();
				Integer patternCount = patternCounts.get(pattern);
				MutationSet mutPattern = new MutationSet(gene, pattern);
				Map<Gene, MutationSet> geneMutSet = new HashMap<>();
				geneMutSet.put(gene, mutPattern);
				rowContents.add(pattern);
				rowContents.add(patternCount.toString());

				AlgorithmComparison algorithmComparison =
						new AlgorithmComparison(geneMutSet, algorithms);
				Map<Drug, List<ComparableDrugScore>> results =
						algorithmComparison.getComparisonResults()
						.stream()
						.filter(ds -> ds.drug.getDrugClass() == drugClass)
						.collect(Collectors.groupingBy(ds -> ds.drug));
				int numDiffs = 0;
				int maxDiff = 0;
				for (Drug drug : results.keySet()) {
					Map<String, ComparableDrugScore> algResults = results.get(drug)
						.stream().collect(Collectors.toMap(
							ds -> ds.algorithm,
							ds -> ds,
							(ds1, ds2) -> ds1,
							LinkedHashMap::new
						));
					int diff = 3;
					for (String algorithm : algResults.keySet()) {
						ComparableDrugScore result = algResults.get(algorithm);
						SIREnum sir = result.SIR;
						if (diff == 3) {
							diff = sir.ordinal();
						}
						else {
							diff = Math.abs(diff - sir.ordinal());
							if (diff != 0) {
								numDiffs ++;
							}
							maxDiff = Math.max(diff, maxDiff);
						}
						String interpretation = result.interpretation;
						rowContents.add("" + sir);
						rowContents.add(interpretation);
					}
				}
				rowContents.add("" + numDiffs);
				rowContents.add("" + maxDiff);
				rows.add(rowContents);
			}
			String output = TSV.dumps(header, rows);
			MyFileUtils.writeFile("AlgComparisons/" + drugClass + ".tsv", output);
		}
	}

	private static Map<String, Integer> genPatternCounts (DrugClass drugClass) {
		MutationPatterns mutPatterns = new MutationPatterns(drugClass);
		return mutPatterns.getAllPatternCounts();
	}

}
