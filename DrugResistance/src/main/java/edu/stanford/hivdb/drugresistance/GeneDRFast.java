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

package edu.stanford.hivdb.drugresistance;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.drugresistance.algorithm.FastHivdb;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.ngs.GeneSequenceReads;

/**
 * A replacement of GeneDRAsi
 * Initialize with Gene, mutTypes, mutCommentsAsi, drugClassTotalDrugScoresAsi,
 * drugClassDrugMutScoresAsi, and drugClassDrugComboMutScoresAsi
 *
 * drugClassDrugMutScoresAsi, and drugClassDrugComboMutScoresAsi differ from those that instantiate GeneDRHivdb
 * as they have keys for each drugClass whether or not there are DRMs associated with that class
 *
 * drugClassTotalDrugScoresAsi is supplied by AsiHivdb but not by any of the classes that directly
 * query HIVDB_Scores
 *
 * drugClassDrugMutScoresAsi, and drugClassDrugComboMutScoresAsi need to be manipulated to four new maps
 * to provide all data required for reports. These new maps should be identical to the four maps created in
 * GeneDRHivdb
 */
public class GeneDRFast extends GeneDR {

	private final FastHivdb fastHivdb;

	public static Map<Gene, GeneDR> getResistanceByGeneFromAlignedGeneSeqs(List<AlignedGeneSeq> alignedGeneSeqs) {
		Map<Gene, GeneDR> resistanceForSequence = new EnumMap<>(Gene.class);
		for (AlignedGeneSeq geneSeq : alignedGeneSeqs) {
			Gene gene = geneSeq.getGene();
			final GeneDR geneDR = new GeneDRFast(gene, geneSeq.getMutations());
			resistanceForSequence.put(gene, geneDR);
		}
		return resistanceForSequence;
	}

	public static Map<Gene, GeneDR> getResistanceByGeneFromReads(List<GeneSequenceReads> allGeneSeqReads) {
		Map<Gene, GeneDR> resistanceForSequence = new EnumMap<>(Gene.class);
		for (GeneSequenceReads geneSeqReads : allGeneSeqReads) {
			Gene gene = geneSeqReads.getGene();
			final GeneDR geneDR = new GeneDRFast(gene, geneSeqReads.getMutations());
			resistanceForSequence.put(gene, geneDR);
		}
		return resistanceForSequence;
	}

	public GeneDRFast(Gene gene, MutationSet mutations) {
		super(gene, mutations);
		fastHivdb = new FastHivdb(gene, mutations);

		drugClassDrugMutScores = fastHivdb.getDrugClassDrugMutScores();
		drugClassDrugComboMutScores = fastHivdb.getDrugClassDrugComboMutScores();
		postConstructor();
	}

	public static Map<MutationSet, GeneDR> parallelConstructor(Gene gene, Set<MutationSet> allMuts) {
		return allMuts
			.parallelStream()
			.collect(Collectors.toMap(
				muts -> muts,
				muts -> new GeneDRFast(gene, muts)
			));
	}

	@Override
	public Map<Drug, Double> getDrugClassTotalDrugScores(DrugClass drugClass) {
		return fastHivdb.getDrugClassTotalDrugScores(drugClass);
	}

	@Override
	public Double getTotalDrugScore(Drug drug) {
		return fastHivdb.getTotalScore(drug);
	}

	@Override
	public Integer getDrugLevel(Drug drug) {
		return fastHivdb.getDrugLevel(drug);
	}

	@Override
	public String getDrugLevelText(Drug drug) {
		return fastHivdb.getDrugLevelText(drug);
	}

	@Override
	public String getDrugLevelSIR(Drug drug) {
		return fastHivdb.getDrugLevelSir(drug);
	}

}
