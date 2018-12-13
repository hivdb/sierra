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
import java.util.HashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.AA;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Cachable;

public class MutationScores {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	public static class MutScore {
		public final Gene gene;
		public final DrugClass drugClass;
		public final String cons;
		public final Integer pos;
		public final Character aa;
		public final Drug drug;
		public final Double score;

		public MutScore(
				Gene gene, DrugClass drugClass, Integer pos,
				Character aa, Drug drug, Double score) {
			this.gene = gene;
			this.drugClass = drugClass;
			this.cons = gene.getReference(pos);
			this.pos = pos;
			this.aa = aa;
			this.drug = drug;
			this.score = score;
		}
	}

	@Cachable.CachableField
	private static List<MutScore> mutScores;


	static {
		Cachable.setup(MutationScores.class, () -> {
			try {
				populateMutScores();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
	}

	public static List<MutScore> getMutScores() { return mutScores; }

	public static List<MutScore> getMutScores(DrugClass drugClass) {
		return mutScores.stream()
			.filter(m -> m.drugClass == drugClass)
			.collect(Collectors.toList());
	}

	public static Map<Integer, List<MutScore>> groupMutScoresByPos(Drug drug) {
		return mutScores
		.stream()
		.filter(ms -> ms.drug == drug)
		.collect(Collectors.groupingBy(
			ms -> ms.pos, TreeMap::new, Collectors.toList()));
	}

	/**
	 * Receives a list of mutations and a gene.
	 *
	 * Iterate through each mutation, check whether it is in the List mutScores
	 * and populate the Map drugClassDrugMutScores: DrugClass=>Drug=>Mutation=>score
	 *
	 * @param gene
	 * @param mutations
	 * @return Map: DrugClass => Drug => Mutation => score
	 * @throws SQLException
	 */
	public static Map<DrugClass, Map<Drug, Map<Mutation, Double>>>
			getDrugClassMutScoresForMutSet
			(Gene gene, MutationSet mutations) throws SQLException {

		// Filter the mutation list to those found in the submitted gene
		MutationSet geneSeqMutList = mutations.getGeneMutations(gene);

		// Initialize the map to be returned
		Map<DrugClass, Map<Drug, Map<Mutation, Double>>>
		   	drugClassDrugMutScores = new EnumMap<>(DrugClass.class);

		for (Mutation seqMut: geneSeqMutList) {
			Map<Drug, List<MutScore>> matches = matchMutScore(seqMut);
			if (matches.size() == 0) {
				continue;
			}

			for (Drug drug: matches.keySet()) {

				MutScore matched = matches.get(drug)
					.stream()
					.max((m1, m2) -> Double.compare(m1.score, m2.score))
					.get();

				drugClassDrugMutScores.putIfAbsent(
					matched.drugClass,
				   	new EnumMap<>(Drug.class));

				drugClassDrugMutScores
					.get(matched.drugClass)
					.putIfAbsent(matched.drug, new HashMap<>());

				double origScore = drugClassDrugMutScores
					.get(matched.drugClass)
					.get(matched.drug)
					.getOrDefault(seqMut, -99.0);

				drugClassDrugMutScores
					.get(matched.drugClass)
					.get(matched.drug)
					.put(seqMut, Math.max(matched.score, origScore));
			}

		}

		return drugClassDrugMutScores;
	}

	/**
	 * Find all MutScore objects match with seqMut.
	 */
	private static Map<Drug, List<MutScore>> matchMutScore(Mutation seqMut) {
		return mutScores
			.stream()
			.filter(ms ->
				   	ms.gene == seqMut.getGene() &&
					ms.pos == seqMut.getPosition() &&
					seqMut.getAAsWithoutReference().contains(String.valueOf(ms.aa)))
			.collect(Collectors.groupingBy(m -> m.drug));
	}

	private static void populateMutScores() throws SQLException {
		final JdbcDatabase db = JdbcDatabase.getResultsDB();

		final String sqlStatement =
			"SELECT Gene, DrugClass, Pos, AA, Drug, Score " +
			"FROM tblScoresWithVersions WHERE Version=? " +
			"ORDER BY Gene, Pos, AA, Drug";

		mutScores = db.iterate(sqlStatement, rs -> {
			Gene gene = Gene.valueOf(rs.getString("Gene"));
			DrugClass drugClass = DrugClass.valueOf(rs.getString("DrugClass"));
			int pos = rs.getInt("Pos");
			String aas = rs.getString("AA");
			Drug drug = Drug.valueOf(rs.getString("Drug"));
			double score = rs.getDouble("Score");
			aas = AA.toInternalFormat(aas);
			char aa = aas.charAt(0);

			return new MutScore(
				gene, drugClass, pos, aa, drug, score);
		}, HivdbVersion.getLatestVersion().name());

	}

}
