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
import java.util.List;
import java.util.Map;

import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;
import edu.stanford.hivdb.drugresistance.algorithm.Asi;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.TSV;

public class TabularAlgorithmsComparison {

	private List<String> headers;
	private List<AlignedSequence> alignedSeqs;
	private List<Algorithm> algorithms;
	private Map<String, String> customAlgorithms;

	public TabularAlgorithmsComparison(
			List<AlignedSequence> alignedSeqs,
			List<Algorithm> algorithms,
			Map<String, String> customAlgorithms) {
		this.alignedSeqs = alignedSeqs;
		this.algorithms = algorithms;
		this.customAlgorithms = customAlgorithms;
		headers = new ArrayList<>();
		headers.add("sequenceName");
		headers.add("gene");
		headers.add("drugName");
		for (Algorithm alg : algorithms) {
			headers.add(alg + ".drugLevel");
		}
		for (String cAlg : customAlgorithms.keySet()) {
			headers.add(cAlg + ".drugLevel");
		}
	}

	@Override
	public String toString() {
		List<List<String>> rows = new ArrayList<>();
		for (AlignedSequence alignedSeq : alignedSeqs) {
			MutationSet allMuts = alignedSeq.getMutations();
			for (Gene gene : alignedSeq.getAvailableGenes()) {
				MutationSet muts = allMuts.getGeneMutations(gene);
				List<Asi> asiList = AlgorithmComparison
					.calcAsiListFromAlgorithms(gene, muts, algorithms);
				asiList.addAll(AlgorithmComparison
					.calcAsiListFromCustomAlgorithms(
						gene, muts, customAlgorithms));
				for (DrugClass dc : gene.getDrugClasses()) {
					for (Drug drug : dc.getDrugsForHivdbTesting()) {
						List<String> row = new ArrayList<>();
						row.add(alignedSeq.getInputSequence().getHeader());
						row.add(gene.toString());
						row.add(drug.getDisplayAbbr());
						for (Asi asi : asiList) {
							String sir = asi.getDrugLevelSir(drug);
							row.add(sir == null ? "-" : sir);
						}
						rows.add(row);
					}
				}
			}
		}
		return TSV.dumps(headers, rows);
	}

}
