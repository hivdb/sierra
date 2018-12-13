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

package edu.stanford.hivdb.drugresistance.database;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.stanford.hivdb.drugresistance.algorithm.FastHivdb;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.AAMutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Cachable;
import edu.stanford.hivdb.utilities.Cachable.DataLoader;
import edu.stanford.hivdb.utilities.Database;

/**
 * Creates and returns mutPatterns and mutPatternCounts from HIVDB_Scores using the active.
 *   HivdbVersion (in HIVDB_Scores)
 */
public class MutationPatterns {

	public static class MutationPattern {
		public final String pattern;
		public final Integer count;
		public final DrugClass drugClass;
		public final Drug drug;
		public final Integer level;
		public final Integer totalScore;

		public MutationPattern(
				String pattern, int count, Drug drug, int level, int score) {
			this.pattern = pattern;
			this.count = count;
			this.drugClass = drug.getDrugClass();
			this.drug = drug;
			this.level = level;
			this.totalScore = score;
		}

		/*@Override
		public String toString() {
			return String.format(
				"MutationPattern(%s, count=%d, drug=%s, level=%d, score=%d)",
				pattern, count, drug, level, totalScore);
		}*/
	}

	@Cachable.CachableField
	private static Map<DrugClass, List<MutationPattern>> allMutationPatterns;

	static {
		Cachable.setup(MutationPatterns.class);
	}

	private DrugClass drugClass;
	private Map<String, Integer> patternCounts;

	/**
	 * The tblPatterns<DrugClass> in HIVDB_Scores has the following fields:
	 * Pattern, Count, Version, Drug, Level, TotalScore, MutScore, ComboScore, MutScoreText, ComboScoreText
	 * The Version should always be the same as the active version used by the program
	 *
	 * @param drugClass
	 */
	public MutationPatterns(DrugClass drugClass) {
		this.drugClass = drugClass;
		this.patternCounts = allMutationPatterns.get(drugClass)
			.stream()
			.collect(Collectors.toMap(
				mp -> mp.pattern,
				mp -> mp.count,
				(c1, c2) -> c1,
				LinkedHashMap::new));
	}

	public DrugClass getDrugClass() { return drugClass; }

	public Map<String, Map<Drug, MutationPattern>>
			groupMutationPatternsByPatternAndDrugs() {
		return allMutationPatterns.get(drugClass)
			.stream()
			.collect(Collectors.groupingBy(
				mp -> mp.pattern,
				Collectors.toMap(
					mp -> mp.drug, mp -> mp,
					(mp1, mp2) -> mp1,
					() -> new EnumMap<>(Drug.class))));
	}

	public Map<String, Integer> getAllPatternCounts() {
		return this.patternCounts;
	}

	public int getPatternCount(String pattern) {
		return patternCounts.get(pattern);
	}

	public List<String> getOrderedMutPatterns() {
		return allMutationPatterns
			.get(drugClass)
			.stream()
			.map(mp -> mp.pattern)
			.distinct()
			.collect(Collectors.toList());
	}

	public static class PatternDataLoader implements DataLoader<Map<DrugClass, List<MutationPattern>>> {

		protected Database db = JdbcDatabase.getResultsDB();

		@Override
		public String getFieldName() {
			return "allMutationPatterns";
		}

		private Map<DrugClass, List<MutationPattern>> calcGenePatterns(
				Gene gene, Collection<MutationSet> allMutations) {

			System.out.println(String.format(
				"Start processing ASI for %s...", gene
			));

			FastHivdb[] asiArray = allMutations
				.stream()
				.unordered()
				.map(mutations -> new FastHivdb(gene, mutations))
				.toArray(FastHivdb[]::new);

			System.out.println("Finished processing ASI.");

			// drugClass => triggeredMuts => count
			final Map<DrugClass, Map<MutationSet, Long>> countMapByDrugClass =
				gene.getDrugClasses()
				.stream()
				.collect(Collectors.toMap(
					dc -> dc,
					dc -> (Arrays
						.stream(asiArray)
						.collect(Collectors.groupingBy(
							asi -> asi.getTriggeredMutations(dc),
							Collectors.counting()
						))
					)
				));

			// drugClass => triggeredMuts => FastHivdb
			final Map<DrugClass, Map<MutationSet, FastHivdb>> asiMapByDrugClass =
				gene.getDrugClasses()
				.stream()
				.collect(Collectors.toMap(
					dc -> dc,
					dc -> (Arrays
						.stream(asiArray)
						.collect(Collectors.toMap(
							asi -> asi.getTriggeredMutations(dc),
							asi -> asi,
							(asi1, asi2) -> asi1
						))
					)
				));

			final Map<DrugClass, List<MutationPattern>> result = new EnumMap<>(DrugClass.class);
			for (DrugClass drugClass : gene.getDrugClasses()) {
				result.put(
					drugClass,
				   	asiMapByDrugClass
				   	.get(drugClass)
					.entrySet()
					.stream()
					.flatMap(e -> {
						MutationSet triggeredMuts = e.getKey();
						FastHivdb asi = e.getValue();
						String pattern = triggeredMuts.join();
						int count = countMapByDrugClass
							.get(drugClass).get(triggeredMuts).intValue();

						// this returns all drugScores of a single pattern
						return asi
						.getDrugClassTotalDrugScores()
						.get(drugClass)
						.entrySet()
						.stream()
						.map(f -> {
							Drug drug = f.getKey();
							Integer score = f.getValue().intValue();
							Integer level = asi.getDrugLevel(drug);
							// drop all 0 scores
							if (score == 0) { return null; }
							return new MutationPattern(
								pattern, count, drug, level, score);
						})
						.filter(mp -> mp != null);
					})
					.sorted((mp1, mp2) -> {

						// ORDER BY count DESC
						int countDiff = mp2.count.compareTo(mp1.count);
						if (countDiff != 0) { return countDiff; }

						// then ORDER BY pattern
						int patternDiff = mp1.pattern.compareTo(mp2.pattern);
						if (patternDiff != 0) { return patternDiff; }

						// then ORDER BY drug
						int drugDiff = mp1.drug.compareTo(mp2.drug);
						return drugDiff;
					})
					.collect(Collectors.toList())
				);
			}
			return result;
		}

		@Override
		public Map<DrugClass, List<MutationPattern>> load() throws SQLException {
			String sql =
				"SELECT SequenceID, Pos, AA " +
				"FROM tblMutations WHERE Gene = ? " +
				// no stop codon
				"AND AA != '.' ORDER BY SequenceID, Pos, AA";

			Map<DrugClass, List<MutationPattern>> allResult = new EnumMap<>(DrugClass.class);
			for (Gene gene : Gene.values()) {
				Map<Integer, MutationSet> allMutations = db.iterateMap(
					sql,
					(rs, map) -> {
						Integer seqId = rs.getInt("SequenceID");
						Integer pos = rs.getInt("Pos");
						String aa = rs.getString("AA");
						MutationSet muts = map.getOrDefault(seqId, new MutationSet());
						muts = muts.mergesWith(new AAMutation(gene, pos, aa.toCharArray()));
						map.put(seqId, muts);
						return null;
					},
					gene.toString());
				allResult.putAll(calcGenePatterns(gene, allMutations.values()));
			}
			return allResult;
		}

	}

}
