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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Cachable;
import edu.stanford.hivdb.utilities.Cachable.DataLoader;
import edu.stanford.hivdb.utilities.Database;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.TSV;


/**
 *
 * The main method in this class compares the scores and combination scores for two
 *   versions of the HivDb algorithm. The CURRENT_VERSION which is usually what is
 *   currently running on the web and the NEW_VERSION which is the latest version being
 *   tested.
 */
public class TabularRulesComparison {
	private static final String TBL_SCORES = "tblScoresWithVersions";
	private static final String TBL_COMBINATION_SCORES = "tblCombinationScoresWithVersions";
	public static final HivdbVersion[][] VERSIONPAIRS = HivdbVersion.getVersionComparisonPairs();

	@Cachable.CachableField
	private static Map<String, Map<DrugClass, List<List<String>>>> allRows;

	private static Map<String, Map<DrugClass, TabularRulesComparison>> singletons;
	private final List<String> headers;
	private final String versionPair;
	private final DrugClass drugClass;

	static {
		Cachable.setup(TabularRulesComparison.class);
		singletons = new LinkedHashMap<>();
	}

	private TabularRulesComparison(String versionPair, DrugClass drugClass) {
		headers = new ArrayList<>();
		headers.add("Rule");
		for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
			headers.add(drug.getDisplayAbbr());
		}
		headers.add("Num Diffs");
		headers.add("Max Diff");
		this.versionPair = versionPair;
		this.drugClass = drugClass;
	}

	public static TabularRulesComparison getInstance(String versionPair, DrugClass drugClass) {
		if (!allRows.containsKey(versionPair)) {
			throw new IllegalArgumentException("unsupport version pair: " + versionPair);
		}
		if (!singletons.containsKey(versionPair)) {
			singletons.put(versionPair, new EnumMap<>(DrugClass.class));
		}
		Map<DrugClass, TabularRulesComparison> partial = singletons.get(versionPair);
		if (!partial.containsKey(drugClass)) {
			partial.put(drugClass, new TabularRulesComparison(versionPair, drugClass));
		}
		return partial.get(drugClass);
	}

	@Override public String toString() {
		List<List<String>> rows = allRows.get(versionPair).get(drugClass);
		return TSV.dumps(headers, rows);
	}

	public static class ComparisonDataLoader implements DataLoader<Map<String, Map<DrugClass, List<List<String>>>>> {

		protected Database db = JdbcDatabase.getResultsDB();

		@Override
		public String getFieldName() {
			return "allRows";
		}

		@Override
		public Map<String, Map<DrugClass, List<List<String>>>> load() throws SQLException {
			Map<String, Map<DrugClass, List<List<String>>>> results = new LinkedHashMap<>();

			for (HivdbVersion[] oneVersionPair : VERSIONPAIRS) {

				Map<DrugClass, List<List<String>>> oneResults = new EnumMap<>(DrugClass.class);
				HivdbVersion leftVersion = oneVersionPair[0];
				HivdbVersion rightVersion = oneVersionPair[1];
				results.put(leftVersion + ":" + rightVersion, oneResults);

				for (DrugClass drugClass : DrugClass.values()) {

					List<List<String>> rows = new ArrayList<>();
					oneResults.put(drugClass, rows);
					// Identify the score changes for individual mutations
					Map<Mutation, Map<Drug, Map<HivdbVersion, Integer>>> mutDrugVersionScoreChanges =
							genMutDrugVersionScoreChanges(drugClass, leftVersion, rightVersion);

					// Print out the score changes for individual mutations
					for (Mutation mut : mutDrugVersionScoreChanges.keySet()) {
						List<String> row = new ArrayList<>();
						rows.add(row);
						row.add(mut.getHumanFormat());
						int numDiffs = 0;
						int maxDiff = 0;
						for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
							Map<HivdbVersion, Integer> drugScores =
									mutDrugVersionScoreChanges.get(mut)
									.getOrDefault(drug, new EnumMap<>(HivdbVersion.class));
							int leftScore = drugScores.getOrDefault(leftVersion, 0);
							int rightScore = drugScores.getOrDefault(rightVersion, 0);
							if (leftScore == rightScore) {
								row.add("" + leftScore);
							}
							else {
								numDiffs ++;
								maxDiff = Math.max(
									Math.abs(rightScore - leftScore), maxDiff);
								row.add(String.format("%d => %d", leftScore, rightScore));
							}
						}
						row.add("" + numDiffs);
						row.add("" + maxDiff);
					}

					// Identify the score changes for combination mutations
					Map<String, Map<Drug, Map<HivdbVersion, Integer>>> comboMutDrugVersionScoreChanges =
							genComboMutVersionScoreChanges(drugClass, leftVersion, rightVersion);

					// Print out the score changes for combination mutations
					for (String comboMutRule : comboMutDrugVersionScoreChanges.keySet()) {
						List<String> row = new ArrayList<>();
						rows.add(row);
						row.add(comboMutRule);
						int numDiffs = 0;
						int maxDiff = 0;
						for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
							Map<HivdbVersion, Integer> drugScores =
									comboMutDrugVersionScoreChanges.get(comboMutRule)
									.getOrDefault(drug, new EnumMap<>(HivdbVersion.class));
							int leftScore = drugScores.getOrDefault(leftVersion, 0);
							int rightScore = drugScores.getOrDefault(rightVersion, 0);
							if (leftScore == rightScore) {
								row.add("" + leftScore);
							}
							else {
								numDiffs ++;
								maxDiff = Math.max(
									Math.abs(rightScore - leftScore), maxDiff);
								row.add(String.format("%d => %d", leftScore, rightScore));
							}
						}
						row.add("" + numDiffs);
						row.add("" + maxDiff);
					}
				}
			}
			return results;
		}

		protected Map<String, Map<Drug, Map<HivdbVersion, Integer>>> genComboMutVersionScoreChanges(
				DrugClass drugClass, HivdbVersion leftVersion, HivdbVersion rightVersion)
				throws SQLException {

			String sqlStmt = "SELECT Rule, Drug, Version, Score FROM " + TBL_COMBINATION_SCORES +
				" WHERE DrugClass=? AND Version in (?, ?) ORDER BY Rule, Drug, Version";

			return db.selectAll(sqlStmt, rs -> {
				Map<String, Map<Drug, Map<HivdbVersion, Integer>>>rulesMap = new LinkedHashMap<>();
				while (rs.next()) {
					Drug drug = Drug.valueOf(rs.getString("Drug"));
					String rule = new MutationSet(
						drug.getDrugClass().gene(),
						rs.getString("Rule")).join('+');
					int score = rs.getInt("Score");
					HivdbVersion version = HivdbVersion.valueOf(rs.getString("Version"));
					if (!rulesMap.containsKey(rule)) {
						rulesMap.put(rule, new HashMap<Drug, Map<HivdbVersion, Integer>>());
					}
					Map<Drug, Map<HivdbVersion, Integer>> drugsMap = rulesMap.get(rule);
					if (!drugsMap.containsKey(drug)) {
						drugsMap.put(drug, new HashMap<HivdbVersion, Integer>());
						drugsMap.get(drug).put(leftVersion, 0);
						drugsMap.get(drug).put(rightVersion, 0);
					}
					Map<HivdbVersion, Integer> versionsMap = drugsMap.get(drug);
					if (version.name().equals(leftVersion.getDBName())) {
						versionsMap.put(leftVersion, score);
					}
					if (version.name().equals(rightVersion.getDBName())) {
						versionsMap.put(rightVersion, score);
					}
				}
				return rulesMap;
			}, drugClass.name(), leftVersion.getDBName(), rightVersion.getDBName());
		}

		protected Map<Mutation, Map<Drug, Map<HivdbVersion, Integer>>> genMutDrugVersionScoreChanges(
				DrugClass drugClass, HivdbVersion leftVersion, HivdbVersion rightVersion)
				throws SQLException {

			String sqlStmt = "SELECT Gene, Pos, AA, Drug, Version, Score FROM " + TBL_SCORES +
					" WHERE DrugClass=? AND Version in (?, ?) ORDER BY Pos, AA, Drug, Version";

			return db.selectAll(sqlStmt, rs -> {
				Map<Mutation, Map<Drug, Map<HivdbVersion, Integer>>> mutsMap = new LinkedHashMap<>();
				while (rs.next()) {
					Gene gene = Gene.valueOf(rs.getString("Gene"));
					int pos = rs.getInt("Pos");
					String aa = rs
						.getString("AA")
						.replaceAll("#", "_")
						.replaceAll("~", "-");
					Mutation mut = new Mutation(gene, pos, aa);
					Drug drug = Drug.valueOf(rs.getString("Drug"));
					int score = rs.getInt("Score");
					HivdbVersion version = HivdbVersion.valueOf(rs.getString("Version"));
					if (!mutsMap.containsKey(mut)) {
						mutsMap.put(mut, new HashMap<Drug, Map<HivdbVersion, Integer>>());
					}
					if (!mutsMap.get(mut).containsKey(drug)) {
						mutsMap.get(mut).put(drug, new HashMap<HivdbVersion, Integer>());
						mutsMap.get(mut).get(drug).put(leftVersion, 0);
						mutsMap.get(mut).get(drug).put(rightVersion, 0);
					}
					if (version.name().equals(leftVersion.getDBName())) {
						mutsMap.get(mut).get(drug).put(leftVersion, score);
					}
					if (version.name().equals(rightVersion.getDBName())) {
						mutsMap.get(mut).get(drug).put(rightVersion, score);
					}

				}
				return mutsMap;
			}, drugClass.name(), leftVersion.getDBName(), rightVersion.getDBName());
		}
	}

}
