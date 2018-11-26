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

//package edu.stanford.hivdb.drugresistance;

//import java.util.EnumMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import edu.stanford.hivdb.alignment.AlignedGeneSeq;
//import edu.stanford.hivdb.drugresistance.algorithm.AsiHivdb;
//import edu.stanford.hivdb.drugs.Drug;
//import edu.stanford.hivdb.drugs.DrugClass;
//import edu.stanford.hivdb.mutations.Gene;
//import edu.stanford.hivdb.mutations.MutationSet;

/**
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
//public class GeneDRAsi extends GeneDR {
//
//	protected final AsiHivdb asiObject;
//
//	public static Map<Gene, GeneDR> getResistanceByGene(Map<Gene, AlignedGeneSeq> alignedGeneSeqs) {
//		Map<Gene, GeneDR> resistanceForSequence = new EnumMap<>(Gene.class);
//		for (Gene gene : alignedGeneSeqs.keySet()) {
//			final GeneDR geneDR = new GeneDRAsi(gene, alignedGeneSeqs.get(gene));
//			resistanceForSequence.put(gene, geneDR);
//		}
//		return resistanceForSequence;
//	}
//
//	public GeneDRAsi(Gene gene, AlignedGeneSeq seq) {
//		this(gene, seq.getMutations());
//	}
//
//	public GeneDRAsi(Gene gene, MutationSet mutations) {
//		super(gene, mutations);
//		this.asiObject = new AsiHivdb(gene, mutations);
//
//		drugClassDrugMutScores = asiObject.getDrugClassDrugMutScores();
//		drugClassDrugComboMutScores = asiObject.getDrugClassDrugComboMutScores();
//		postConstructor();
//	}
//
//	public static Map<MutationSet, GeneDR> parallelConstructor(Gene gene, Set<MutationSet> allMuts) {
//		return allMuts
//			.parallelStream()
//			.collect(Collectors.toMap(
//				muts -> muts,
//				muts -> new GeneDRAsi(gene, muts)
//			));
//	}
//
//	@Override
//	public Map<Drug, Double> getDrugClassTotalDrugScores(DrugClass drugClass) {
//		return asiObject.getDrugClassTotalDrugScores(drugClass);
//	}
//
//	@Override
//	public Double getTotalDrugScore(Drug drug) {
//		return asiObject.getTotalScore(drug);
//	}
//
//	@Override
//	public Integer getDrugLevel(Drug drug) {
//		return asiObject.getDrugLevel(drug);
//	}
//
//	@Override
//	public String getDrugLevelText(Drug drug) {
//		return asiObject.getDrugLevelText(drug);
//	}
//
//	@Override
//	public String getDrugLevelSIR(Drug drug) {
//		return asiObject.getDrugLevelSir(drug);
//	}
//}