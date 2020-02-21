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

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.mutations.MutationMapUtils;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.MutationMapUtils.SortOrder;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;


/**
 * This is used to compare drug resistance results using an Asi file vs. using the scores in HIVDB_Scores.
 * It has a main method and it currently has not test class.
 * It is not used while running Sierra.
 * It has not been tried in a while.
 *
 */

public class HivdbAsiComparison {
//	private static final String FILE_COMPLETE_COMPARISON = "HivdbAsiComparison.txt";
//	private static final String FILE_DIFFERENCES = "HivdbAsiDifferences.txt";
//	private static final String DELIMITER = "\t";
//	private static final String[] headerFields = {"SeqName", "Gene", "HIVDrugClass", "Drug",
//			"TotalScore-HIVDB", "TotalScore-Asi", "TotalScore-Dif",
//			"IndividualScoredMuts-HIVDB", "IndividualScoredMuts-Asi", "IndividualScores-Dif",
//			"CombinationScoredMuts-HIVDB", "CombinationScoredMuts-Asi", "CombinationScores-Dif"};
//
//	public static void main(String[] args) throws SQLException {
//		final InputStream testSequenceInputStream =
//				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.COMPOUND_SCORES);
//		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
//		final String header = String.join(DELIMITER, headerFields) + "\n";
//
//		MyFileUtils.writeFile(FILE_COMPLETE_COMPARISON, header);
//		StringBuffer completeOutput = new StringBuffer();
//		StringBuffer differencesOutput = new StringBuffer();
//
//		for (Sequence seq : sequences) {
//			Map<HIVGene, AlignedGeneSeq> alignedGeneSeqs = NucAminoAligner.align(seq).getAlignedGeneSequenceMap();
//			for (HIVGene gene : alignedGeneSeqs.keySet()) {
//				AlignedGeneSeq alignedGeneSeq = alignedGeneSeqs.get(gene);
//				final MutationSet mutations = alignedGeneSeq.getMutations();
//
//				// Get resistance data using the Asi algorithm
//				// Note this also gets the comments and mutTypeLists so these are obtained twice
//				GeneDR geneDRAsi = new GeneDRFast(gene, mutations);
//
//				final GeneDR geneDRHivdb = new GeneDRHivdb(gene, mutations);
//
//				// If there are no DRMs belonging to the drugClass then no line will be printed
//				// If there are DRMs belonging to the drugClass; they should be present regardless
//				// of whether the HIVDB algorithm was derived from HIVDB_Scores or from the Asi xml.
//				for (HIVDrugClass drugClass : gene.getDrugClasses()) {
//					if (geneDRHivdb.drugClassHasScoredMuts(drugClass) || geneDRAsi.drugClassHasScoredMuts(drugClass)) {
//						String seqGeneComparison =
//								compareResults(seq.getHeader(), gene, drugClass, geneDRHivdb, geneDRAsi);
//						System.out.println("SeqGeneComparisons:\n" + seqGeneComparison + "\n");
//						completeOutput.append(seqGeneComparison);
//						String seqGeneDifferences =
//								getDifferences(seq.getHeader(), drugClass, geneDRHivdb, geneDRAsi);
//						System.out.println("SeqGeneDifferences:\n" + seqGeneDifferences);
//						if (!seqGeneDifferences.isEmpty()) {
//							differencesOutput.append(seqGeneDifferences);
//						}
//					}
//				}
//			}
//			MyFileUtils.appendFile(FILE_COMPLETE_COMPARISON, completeOutput.toString());
//			MyFileUtils.appendFile(FILE_DIFFERENCES, differencesOutput.toString());
//		}
//	}
//
//	public static String compareResults (final String seqName, final HIVGene gene, final HIVDrugClass drugClass,
//			final GeneDR geneHivdbResults, final GeneDR geneAsiResults) {
//		StringBuffer output = new StringBuffer();
//
//		for (HIVDrug drug : drugClass.getDrugs()) {
//			output.append(seqName + DELIMITER);
//			output.append(gene + DELIMITER);
//			output.append(drugClass + DELIMITER);
//			output.append(drug + DELIMITER);
//
//			int drugScoreHIVDB = geneHivdbResults.getTotalDrugScore(drug).intValue();
//			int drugScoreAsi = geneAsiResults.getTotalDrugScore(drug).intValue();
//			int drugScoresDif = drugScoreHIVDB - drugScoreAsi;
//			output.append(drugScoreHIVDB + DELIMITER);
//			output.append(drugScoreAsi + DELIMITER);
//			output.append(drugScoresDif + DELIMITER);
//
//			String scoredMutsHivdb = getScoredMuts(geneHivdbResults, drug);
//			String scoredMutsAsi = getScoredMuts(geneAsiResults, drug);
//			String scoredMutsDif = "None" + DELIMITER;
//			if (!scoredMutsHivdb.equals(scoredMutsAsi)) { scoredMutsDif = "Differences" + DELIMITER;}
//			output.append(scoredMutsHivdb);
//			output.append(scoredMutsAsi);
//			output.append(scoredMutsDif);
//
//			String comboScoredMutsHivdb = getComboScoredMuts(geneHivdbResults, drug);
//			String comboScoredMutsAsi = getComboScoredMuts(geneAsiResults, drug);
//			String comboScoredMutsDif = "None" + DELIMITER;
//			if (!comboScoredMutsHivdb.equals(comboScoredMutsAsi)) {comboScoredMutsDif = "Differences" + DELIMITER;}
//			output.append(comboScoredMutsHivdb);
//			output.append(comboScoredMutsAsi);
//			output.append(comboScoredMutsDif);
//			output.append("\n");
//		}
//		return output.toString();
//	}
//
//	public static String getDifferences(final String seqName, final HIVDrugClass drugClass, final GeneDR geneHivdbResults,
//			final GeneDR geneAsiResults) {
//		StringBuffer output = new StringBuffer();
//		for (HIVDrug drug : drugClass.getDrugs()) {
//			int drugScoreHivdb = geneHivdbResults.getTotalDrugScore(drug).intValue();
//			int drugScoreAsi = geneAsiResults.getTotalDrugScore(drug).intValue();
//			if (drugScoreHivdb - drugScoreAsi != 0) {
//				output.append(seqName + ":" + drug + ":HIVDB:" + drugScoreHivdb +
//						                             ":ASI:" + drugScoreAsi + "\n");
//			}
//			String scoredMutsHivdb = getScoredMuts(geneHivdbResults, drug);
//			String scoredMutsAsi = getScoredMuts(geneAsiResults, drug);
//			if (!scoredMutsHivdb.equals(scoredMutsAsi)) {
//				output.append(seqName + ":" + drug + ":HIVDB:" + scoredMutsHivdb + "\n");
//				output.append(seqName + ":" + drug + ":  Asi:" + scoredMutsAsi + "\n");
//			}
//			String comboScoredMutsHivdb = getComboScoredMuts(geneHivdbResults, drug);
//			String comboScoredMutsAsi = getComboScoredMuts(geneAsiResults, drug);
//			if (!comboScoredMutsHivdb.equals(comboScoredMutsAsi)) {
//				output.append(seqName + ":" + drug + ":HIVDB:" + comboScoredMutsHivdb + "\n");
//				output.append(seqName + ":" + drug + ":  Asi:" + comboScoredMutsAsi + "\n");
//			}
//		}
//		return output.toString();
//	}
//
//	private static String getScoredMuts(GeneDR geneDR, HIVDrug drug) {
//		StringBuffer output = new StringBuffer();
//		if (geneDR.drugHasScoredMuts(drug)) {
//			Map<HIVAAMutation, Double> individualMutScores = geneDR.getScoredIndividualMutsForDrug(drug);
//			List<HIVAAMutation> mutations = new ArrayList<>(individualMutScores.keySet());
//
//			Map<HIVAAMutation, Double> mutationsSortedByPosition = new LinkedHashMap<>();
//			Collections.sort(mutations);
//			for (HIVAAMutation mutation : mutations) {
//				mutationsSortedByPosition.put(mutation, individualMutScores.get(mutation));
//			}
//
//			String fmtMutListWithScores = formatMutList(mutationsSortedByPosition);
//			output.append(fmtMutListWithScores + DELIMITER);
//		} else {
//			output.append("None" + DELIMITER);
//		}
//		return output.toString();
//	}
//
//	public static String formatMutList(Map<HIVAAMutation, Double> map) {
//		StringBuffer output = new StringBuffer();
//		for (HIVAAMutation mut : map.keySet()) {
//			int value = map.get(mut).intValue();
//			String formattedMut = "";
//			if (mut.isDeletion()) {
//				formattedMut = mut.getReference() + mut.getPosition() + "Deletion";
//			} else if (mut.isInsertion()) {
//				formattedMut = mut.getReference() + mut.getPosition() + "Insertion";
//			} else {
//				formattedMut = mut.getHumanFormat();
//			}
//			output.append(formattedMut + "(" + value + "),");
//		}
//		if (output.length() > 0) {
//			output.setLength(output.length() - 1);
//		}
//		return output.toString();
//	}
//
//
//	private static String getComboScoredMuts(GeneDR geneDR, HIVDrug drug) {
//		StringBuffer output = new StringBuffer();
//		if (geneDR.drugHasScoredComboMuts(drug)) {
//			Map<MutationSet, Double> comboMutScores = geneDR.getScoredComboMutsForDrug(drug);
//			Map<MutationSet, Double> comboMutsSortedByScore =
//					MutationMapUtils.sortByComparator(comboMutScores, SortOrder.DESC);
//			output.append(MutationMapUtils.printMutSetScoresAsInts(comboMutsSortedByScore) + DELIMITER);
//		} else {
//			output.append("None" + DELIMITER);
//		}
//		return output.toString();
//	}




}


