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
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Sequence;
import edu.stanford.hivdb.utilities.TSV;


/**
 * Print the sequence name, list of genes, "major" and "minor" mutations for each gene,
 *   resistance scores and levels for selected drugs
 * Currrently the header and list of drugs for a class (not the complete list) are constants
 *
 */
public class TabularResistanceSummary {
	private static final List<String> headerFields;
	private static final Drug[] pis;
	private static final Drug[] nrtis;
	private static final Drug[] nnrtis;
	private static final Drug[] instis;

	static {
		pis = DrugClass.PI.getDrugsForHivdbTesting().toArray(new Drug[0]);
		nrtis = DrugClass.NRTI.getDrugsForHivdbTesting().toArray(new Drug[0]);
		nnrtis = DrugClass.NNRTI.getDrugsForHivdbTesting().toArray(new Drug[0]);
		instis = DrugClass.INSTI.getDrugsForHivdbTesting().toArray(new Drug[0]);

		List<String> hFields = new ArrayList<>();
		hFields.add("Sequence Name");
		hFields.add("Genes");
		hFields.add("PR Major");
		hFields.add("PR Accessory");
		for (Drug drug : pis) {
			hFields.add(drug.getDisplayAbbr() + " Score");
			hFields.add(drug.getDisplayAbbr() + " Level");
		}
		hFields.add("NRTI");
		hFields.add("NNRTI");
		for (Drug drug : nrtis) {
			hFields.add(drug.getDisplayAbbr() + " Score");
			hFields.add(drug.getDisplayAbbr() + " Level");
		}
		for (Drug drug : nnrtis) {
			hFields.add(drug.getDisplayAbbr() + " Score");
			hFields.add(drug.getDisplayAbbr() + " Level");
		}
		hFields.add("IN Major");
		hFields.add("IN Accessory");
		for (Drug drug : instis) {
			hFields.add(drug.getDisplayAbbr() + " Score");
			hFields.add(drug.getDisplayAbbr() + " Level");
		}
		hFields.add("Algorithm Name");
		hFields.add("Algorithm Version");
		hFields.add("Algorithm Date");
		headerFields = Collections.unmodifiableList(hFields);
	}

	private List<List<String>> sequenceRows = new ArrayList<>();
	private Map<String, Map<String, String>> tabularResults = new TreeMap<>();

	public TabularResistanceSummary(List<Sequence> sequences, List<Map<Gene, GeneDR>> allResistanceResults) {
		System.out.println("NumSequences:" + sequences.size() + " NumResistanceResults:" + allResistanceResults.size());
		for (int i=0; i<sequences.size(); i++) {
			Map<Gene, GeneDR> resistanceResults = allResistanceResults.get(i);

			String seqName = sequences.get(i).getHeader();
			tabularResults.put(seqName, new TreeMap<String, String>());

			List<Gene> geneList = new ArrayList<>();
			for (Gene gene : Gene.values()) {
				if (resistanceResults.containsKey(gene)) {
					geneList.add(gene);
				}
			}
			String genes = StringUtils.join(geneList, ",");

			List<String> sequenceRecord = new ArrayList<>();
			sequenceRecord.add(seqName);
			sequenceRecord.add(genes.toString());
			sequenceRecord.addAll(getScoredMutationsPR(resistanceResults));
			sequenceRecord.addAll(getScoresPR(resistanceResults));
			sequenceRecord.addAll(getScoredMutationsRT(resistanceResults));
			sequenceRecord.addAll(getScoresRT(resistanceResults));
			sequenceRecord.addAll(getScoredMutationsIN(resistanceResults));
			sequenceRecord.addAll(getScoresIN(resistanceResults));

			HivdbVersion latest = HivdbVersion.getLatestVersion();
			sequenceRecord.add("HIVdb");
			sequenceRecord.add(latest.toString());
			sequenceRecord.add(latest.getPublishDate());
			sequenceRows.add(sequenceRecord);

			for (int j=0; j<headerFields.size(); j++) {
				String field = headerFields.get(j);
				String dataItem = sequenceRecord.get(j);
				tabularResults.get(seqName).put(field, dataItem);
			}
		}
	}

	@Override
	public String toString() {
		return TSV.dumps(headerFields, sequenceRows);
	}

	public Map<String, Map<String, String>> getTable() { return tabularResults; }
	public List<String> getHeaderFields() { return headerFields; }

	private static List<String> getScoresIN(Map<Gene, GeneDR> resistanceResults) {
		List<String> resistanceScoresAndLevels = new ArrayList<>();
		if (resistanceResults.containsKey(Gene.IN)) {
			GeneDR geneDR = resistanceResults.get(Gene.IN);
			for (Drug drug : instis) {
				int score = geneDR.getTotalDrugScore(drug).intValue();
				int level = geneDR.getDrugLevel(drug);
				resistanceScoresAndLevels.add(Integer.toString(score));
				resistanceScoresAndLevels.add(Integer.toString(level));
			}
		} else {
			String filler = StringUtils.repeat("NA", ",", instis.length * 2);
			resistanceScoresAndLevels = Arrays.asList(filler.split(","));
		}
		return resistanceScoresAndLevels;
	}


	private static List<String> getScoredMutationsPR(Map<Gene, GeneDR> resistanceResults) {
		List<String> scoredMutations = new ArrayList<>();
		String prMajor = "NA";
		String prAccessory = "NA";
		if (resistanceResults.containsKey(Gene.PR)) {
			GeneDR prResults = resistanceResults.get(Gene.PR);
			if (prResults.groupMutationsByTypes().containsKey(MutType.Major)) {
				MutationSet majorMuts = prResults.groupMutationsByTypes().get(MutType.Major);
				prMajor = majorMuts.join();
			} else {
				prMajor = "None";
			}
			if (prResults.groupMutationsByTypes().containsKey(MutType.Accessory)) {
				MutationSet accMuts = prResults.groupMutationsByTypes().get(MutType.Accessory);
				prAccessory = accMuts.join();
			} else {
				prAccessory = "None";
			}
		}
		scoredMutations.add(prMajor);
		scoredMutations.add(prAccessory);
		return scoredMutations;
	}

	private static List<String> getScoresPR(Map<Gene, GeneDR> resistanceResults) {
		List<String> resistanceScoresAndLevels = new ArrayList<>();
		if (resistanceResults.containsKey(Gene.PR)) {
			GeneDR geneDR = resistanceResults.get(Gene.PR);
			for (Drug drug : pis) {
				int score = geneDR.getTotalDrugScore(drug).intValue();
				int level = geneDR.getDrugLevel(drug);
				resistanceScoresAndLevels.add(Integer.toString(score));
				resistanceScoresAndLevels.add(Integer.toString(level));
			}
		} else {
			String filler = StringUtils.repeat("NA", ",", pis.length * 2);
			resistanceScoresAndLevels = Arrays.asList(filler.split(","));
		}
		return resistanceScoresAndLevels;
	}

	private static List<String> getScoresRT(Map<Gene, GeneDR> resistanceResults) {
		List<String> resistanceScoresAndLevels = new ArrayList<>();
		if (resistanceResults.containsKey(Gene.RT)) {
			GeneDR geneDR = resistanceResults.get(Gene.RT);
			for (Drug drug : nrtis) {
				int score = geneDR.getTotalDrugScore(drug).intValue();
				int level = geneDR.getDrugLevel(drug);
				resistanceScoresAndLevels.add(Integer.toString(score));
				resistanceScoresAndLevels.add(Integer.toString(level));
			}
			for (Drug drug : nnrtis) {
				int score = geneDR.getTotalDrugScore(drug).intValue();
				int level = geneDR.getDrugLevel(drug);
				resistanceScoresAndLevels.add(Integer.toString(score));
				resistanceScoresAndLevels.add(Integer.toString(level));
			}
		} else {
			int numRTIs = nrtis.length + nnrtis.length;
			String filler = StringUtils.repeat("NA", ",", numRTIs * 2);
			resistanceScoresAndLevels = Arrays.asList(filler.split(","));
		}
		return resistanceScoresAndLevels;
	}



	private static List<String> getScoredMutationsRT(Map<Gene, GeneDR> resistanceResults) {
		List<String> scoredMutations = new ArrayList<>();
		String nrti = "NA";
		String nnrti = "NA";
		if (resistanceResults.containsKey(Gene.RT)) {
			GeneDR rtResults = resistanceResults.get(Gene.RT);
			if (rtResults.groupMutationsByTypes().containsKey(MutType.NRTI)) {
				MutationSet nrtiMuts = rtResults.groupMutationsByTypes().get(MutType.NRTI);
				nrti = nrtiMuts.join();
			} else {
				nrti = "None";
			}
			if (rtResults.groupMutationsByTypes().containsKey(MutType.NNRTI)) {
				MutationSet nnrtiMuts = rtResults.groupMutationsByTypes().get(MutType.NNRTI);
				nnrti = nnrtiMuts.join();
			} else {
				nnrti = "None";
			}
		}
		scoredMutations.add(nrti);
		scoredMutations.add(nnrti);
		return scoredMutations;
	}

	private static List<String> getScoredMutationsIN(Map<Gene, GeneDR> resistanceResults) {
		List<String> scoredMutations = new ArrayList<>();
		String inMajor = "NA";
		String inAccessory = "NA";
		if (resistanceResults.containsKey(Gene.IN)) {
			GeneDR inResults = resistanceResults.get(Gene.IN);
			if (inResults.groupMutationsByTypes().containsKey(MutType.Major)) {
				MutationSet major = inResults.groupMutationsByTypes().get(MutType.Major);
				inMajor = major.join();
			} else {
				inMajor = "None";
			}
			if (inResults.groupMutationsByTypes().containsKey(MutType.Accessory)) {
				MutationSet accessory = inResults.groupMutationsByTypes().get(MutType.Accessory);
				inAccessory = accessory.join();
			} else {
				inAccessory = "None";
			}
		}
		scoredMutations.add(inMajor);
		scoredMutations.add(inAccessory);
		return scoredMutations;
	}


}
