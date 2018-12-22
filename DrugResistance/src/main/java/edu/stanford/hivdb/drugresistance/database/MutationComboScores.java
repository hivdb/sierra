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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.AA;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Cachable;

/**
 * Statically queries HIVDB_Scores and populates the List combinationScores
 * Provides a method getComboMutDrugScores to obtain all of the scores for a gene's mutations
 */
public class MutationComboScores {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	private static double DEFAULT_MIN_SCORE = -99.0;

	public static class ComboScore {
		public final Gene gene;
		public final DrugClass drugClass;
		public final String rule;
		public final Drug drug;
		public final Double score;
		private transient MutationSet ruleMutations;

		public ComboScore(
				Gene gene, DrugClass drugClass,
				String rule, Drug drug, Double score) {
			this.gene = gene;
			this.drugClass = drugClass;
			this.rule = rule;
			this.drug = drug;
			this.score = score;
		}

		public MutationSet getRuleMutations() {
			if (ruleMutations == null) {
				ruleMutations = new MutationSet(gene, rule);
			}
			return ruleMutations;
		}

		public List<Integer> getPositions() {
			return Collections.unmodifiableList(
				getRuleMutations()
					.stream()
					.map(mut -> mut.getPosition())
					.sorted()
					.collect(Collectors.toList())
			);
		}
	}

	@Cachable.CachableField
	private static List<ComboScore> combinationScores;

	static {
		Cachable.setup(MutationComboScores.class, () -> {
			try {
				populateComboMutDrugScores();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
	}

	public static List<ComboScore> getCombinationScores() { return combinationScores; };
	public static List<ComboScore> getCombinationScores(DrugClass drugClass) {
		return combinationScores
			.stream()
			.filter(cs -> cs.drugClass == drugClass)
			.collect(Collectors.toList());
	};

	public static Map<List<Integer>, List<ComboScore>> groupComboScoresByPositions(Drug drug) {
		return combinationScores
			.stream()
			.filter(cs -> cs.drug == drug)
			.collect(Collectors.groupingBy(
				cs -> cs.getPositions(),
				LinkedHashMap::new, Collectors.toList()));
	}

	/**
	 * Receives a list of mutations in a sequence and a gene.
	 *
	 * Returns null or a Map containing
	 * 	 drugClass => Drugs => comboMutString => scores.
	 *
	 * @param drugClass. seqMutList
	 * @return Map<DrugClass, Map<Drug, Map<String, Double>>> comboMutDrugScores
	 *
	 * TODO: Note: if M41L + T215SY are present two rules should trigger 41L+215SCDEIVNAL and 41L+215FY
	 *   which would give an erroneously high value of 15 instead of 10. For some reason this is not happening.
	 *   The correct value is returned
	 */
	public static Map<DrugClass, Map<Drug, Map<MutationSet, Double>>>
			getComboMutDrugScoresForMutSet (Gene gene, MutationSet mutations) {
		
		// Filter the mutation list to those found in the submitted gene
		MutationSet geneSeqMuts = mutations.getGeneMutations(gene);
		
		// The map to be returned
		Map<DrugClass, Map<Drug, Map<MutationSet, Double>>>
			comboMutDrugScores = new EnumMap<>(DrugClass.class);
		
		Map<Drug, List<MutationSet>> matchedMutsListMap = new EnumMap<>(Drug.class);
		Map<Drug, List<ComboScore>> matchesMap =
			matchComboScore(gene, geneSeqMuts, matchedMutsListMap);
		
		for (Drug drug : matchesMap.keySet()) {
			List<ComboScore> matches = matchesMap.get(drug);
			List<MutationSet> matchedMutsList = matchedMutsListMap.get(drug);
			for (int i = 0; i < matches.size(); i++) {
				ComboScore matched = matches.get(i);
				MutationSet matchedMuts = matchedMutsList.get(i);
				
				comboMutDrugScores.putIfAbsent(
					matched.drugClass,
					new EnumMap<>(Drug.class));
				
				comboMutDrugScores
					.get(matched.drugClass)
					.putIfAbsent(matched.drug, new HashMap<>());

				Double origScore = comboMutDrugScores
					.get(matched.drugClass)
					.get(matched.drug)
					.getOrDefault(matchedMuts, DEFAULT_MIN_SCORE);

				comboMutDrugScores
					.get(matched.drugClass)
					.get(matched.drug)
					.put(
						matchedMuts,
						Math.max(matched.score, origScore));
			}
		}

		return comboMutDrugScores;
	}
	
	private static Map<Drug, List<ComboScore>> matchComboScore(
			Gene gene, MutationSet geneSeqMuts,
			Map<Drug, List<MutationSet>> matchedMutsListMap) {
		return combinationScores
			.stream()
			.filter(cs -> {
				if (gene != cs.gene) {
					return false;
				}
				
				// TODO: can be optimized if mutation support "roughlyEquals",
				// since actually this is just finding intersection items
				MutationSet csMuts = cs.getRuleMutations();
				MutationSet matchedMuts = csMuts.intersectsWith(geneSeqMuts);
				
				// Every mutation in csMuts must match to trigger rule
				if (matchedMuts.size() < csMuts.size()) {
					return false;
				}
							
				// misleading to mutate argument, especially in this lambda
				matchedMutsListMap
					.putIfAbsent(cs.drug, new ArrayList<>());
				
				matchedMutsListMap
					.get(cs.drug)
					.add(geneSeqMuts);

				return true;
			})
			.collect(Collectors.groupingBy(cs -> cs.drug));
	}
	
	// Query HIVDB_Scores to obtain all the mutation combination scores
	private static void populateComboMutDrugScores() throws SQLException {
		final JdbcDatabase db = JdbcDatabase.getResultsDB();
		final String sqlStatement =
			"SELECT Gene, DrugClass, Rule, Drug, Score " +
			"FROM tblCombinationScoresWithVersions WHERE VERSION=? " +
			"ORDER BY Gene, Rule, Drug";

		combinationScores = db.iterate(sqlStatement, rs -> {
			Gene gene = Gene.valueOf(rs.getString("Gene"));
			DrugClass drugClass = DrugClass.valueOf(rs.getString("DrugClass"));
			String rule = rs.getString("Rule");
			Drug drug = Drug.valueOf(rs.getString("Drug"));
			double score = rs.getDouble("Score");
			rule = AA.toInternalFormat(rule);
			return new ComboScore(gene, drugClass, rule, drug, score);
		}, HivdbVersion.getLatestVersion().name());
	}
}
