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

package edu.stanford.hivdb.alignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import edu.stanford.hivdb.utilities.TSV;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GeneEnum;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.utilities.NumberFormats;


public class TabularSequenceSummary {
	private static final String[] headerFields = {
			"Sequence Name", "Genes", "PR Start", "PR End", "RT Start",
			"RT End", "IN Start", "IN End", "Subtype (%)", "Pcnt Mix",
			"PR Major", "PR Accessory", "PR Other", "NRTI Major", "NRTI Accessory",
			"NNRTI Major", "NNRTI Accessory", "RT Other", "IN Major", 
			"IN Accessory", "IN Other", "PR SDRMs", "RT SDRMs", "IN SDRMs",
			"PI TSMs", "NRTI TSMs", "NNRTI TSMs", "INSTI TSMs",
			"Num Frame Shifts", "Frame Shifts",	"Num Insertions", "Insertions",
			"Num Deletions", "Deletions", "Num Stop Codons", "StopCodons",
			"Num BDHVN", "BDHVN", "Num Apobec Mutations", "Apobec Mutations",
			"Num Unusual Mutations", "UnusualMutations"};
	private List<List<String>> sequenceRows = new ArrayList<>();
	private Map<String, Map<String, String>> tabularResults = new HashMap<>();

	/**
	 *
	 */
	public TabularSequenceSummary (List<AlignedSequence> overallResults) {

		for (AlignedSequence alignedSeq : overallResults) {
			List<String> sequenceRecord = new ArrayList<>();

			List<Gene> geneList = alignedSeq.getAvailableGenes();
			MutationSet seqMutations = alignedSeq.getMutations();
			String seqName = alignedSeq.getInputSequence().getHeader();

			tabularResults.put(seqName, new HashMap<String, String>());
			String genes = StringUtils.join(
				geneList.stream().map(g -> g.getName()).toArray(), ",");

			// sequenceName
			sequenceRecord.add(seqName);

			// Genes
			sequenceRecord.add(genes);

			// PRStart, PREnd, RTStart, RTEnd, INStart, INEnd
			sequenceRecord.addAll(determineGeneBoundaries(alignedSeq));

			// Subtype(%)
			sequenceRecord.add(determineSubtype(alignedSeq));

			// PcntMix
			sequenceRecord.add(
				NumberFormats.prettyDecimalAsString(alignedSeq.getMixturePcnt()));

			// PRMajor, PRAccessory, PROther,
			// NRTIMajor, NRTIAccessory, NNRTIMajor, NNRTIAccessory, RTOther,
			// INMajor, INAccessory, INOther
			sequenceRecord.addAll(determineMutLists(alignedSeq));

		  	// PRSDRMs, RTSDRMs
			sequenceRecord.addAll(determineSdrms(alignedSeq));

		  	// PI-TSMs, NRTI-TSMs, NNRTI-TSMs, INSTI-TSMs
			sequenceRecord.addAll(determineNonDrmTsms(alignedSeq));

		  	// NumFS, FrameShifts
			sequenceRecord.addAll(determineFrameShiftText(alignedSeq));
			// NumIns, Insertions
			sequenceRecord.addAll(determineSeqInsertions(seqMutations));
			// NumDel, Deletions
			sequenceRecord.addAll(determineSeqDeletions(seqMutations));
			// NumStops, StopCodons
			sequenceRecord.addAll(determineSeqStopCodons(seqMutations));
			// NumBDHVN, BDHVN
			sequenceRecord.addAll(determineSeqBDHVN(seqMutations));
			// NumApobec, ApobecMuts
			sequenceRecord.addAll(determineApobecFields(seqMutations));
			// NumUnusual, UnusualMuts
			sequenceRecord.addAll(determineSeqUnusualMuts(seqMutations));
			sequenceRows.add(sequenceRecord);

			for (int i=0; i<headerFields.length; i++) {
				String field = headerFields[i];
				String dataItem = sequenceRecord.get(i);
				tabularResults.get(seqName).put(field, dataItem);
			}
		}
	}

	@Override
	public String toString() {
		return TSV.dumps(headerFields, sequenceRows);
	}

	public String getHeader() {
		return TSV.dumpsHeader(headerFields);
	}

	public String getBody() {
		return TSV.dumpsBody(sequenceRows);
	}

	public Map<String, Map<String, String>> getTable() { return tabularResults; }
	public String[] getHeaderFields() { return headerFields; }

	private static String determineSubtype(AlignedSequence alignedSeq) {
		return alignedSeq.getSubtypeText();
	}

	private static List<String> mutationListToTabularResult(MutationSet mutations) {
		List<String> fields = new ArrayList<>();
		String text = "None";
		if (mutations.size() > 0) {
			text = mutations.join();
		}
		fields.add("" + mutations.size());
		fields.add(text);
		return fields;
	}

	private static List<String> determineApobecFields(MutationSet mutations) {
		Apobec apobec = new Apobec(mutations);
		MutationSet apobecMuts = apobec.getApobecMuts();
		return mutationListToTabularResult(apobecMuts);
	}

	private static List<String> determineSeqUnusualMuts(MutationSet mutations) {
		MutationSet unusualMuts = mutations.getUnusualMutations();
		return mutationListToTabularResult(unusualMuts);
	}

	// TODO: What if bdhvn does not affect the amino acid. Check how this is handled
	private static List<String> determineSeqBDHVN(MutationSet mutations) {
		MutationSet bdhvnMuts = mutations.getAmbiguousCodons();
		return mutationListToTabularResult(bdhvnMuts);
	}

	private static List<String> determineSeqStopCodons(MutationSet mutations) {
		MutationSet stopCodons = mutations.getStopCodons();
		return mutationListToTabularResult(stopCodons);
	}

	private static List<String> determineSeqDeletions(MutationSet mutations) {
		MutationSet deletions = mutations.getDeletions();
		return mutationListToTabularResult(deletions);
	}

	private static List<String> determineSeqInsertions(MutationSet mutations) {
		MutationSet insertions = mutations.getInsertions();
		return mutationListToTabularResult(insertions);
	}


	private static List<String> determineFrameShiftText(AlignedSequence alignedSeq) {
		List<FrameShift> frameShifts = alignedSeq.getFrameShifts();
		List<String> frameShiftFields = new ArrayList<>();
		String frameShiftsString = FrameShift.getHumanReadableList(frameShifts);
		frameShiftFields.add(Integer.toString(frameShifts.size()));
		frameShiftFields.add(frameShiftsString);
		return frameShiftFields;
	}

	private static List<String> determineSdrms(AlignedSequence alignedSeq) {
		List<String> sdrmList = new ArrayList<>();
		Map<Gene, AlignedGeneSeq> seqResult = alignedSeq.getAlignedGeneSequenceMap();
		String prSdrms = "NA";
		String rtSdrms = "NA";
		String inSdrms = "NA";
		// TODO: HIV2 Support
		if (seqResult.containsKey(Gene.valueOf("HIV1PR"))) {
			MutationSet sdrms = seqResult.get(Gene.valueOf("HIV1PR")).getSdrms();
			prSdrms = sdrms.join();
		}
		if (seqResult.containsKey(Gene.valueOf("HIV1RT"))) {
			MutationSet sdrms = seqResult.get(Gene.valueOf("HIV1RT")).getSdrms();
			rtSdrms = sdrms.join();
		}
		if (seqResult.containsKey(Gene.valueOf("HIV1IN"))) {
			MutationSet sdrms = seqResult.get(Gene.valueOf("HIV1IN")).getSdrms();
			inSdrms = sdrms.join();
		}
		sdrmList.add(prSdrms);
		sdrmList.add(rtSdrms);
		sdrmList.add(inSdrms);
		return sdrmList;

	}

	public static List<String> determineMutLists(AlignedSequence alignedSeq) {
		List<String> mutListStrings = new ArrayList<>();
		Map<Gene, AlignedGeneSeq> seqResult = alignedSeq.getAlignedGeneSequenceMap();
		for (Gene gene : Gene.values(alignedSeq.getStrain())) {
			if (!seqResult.containsKey(gene)) {
				if (gene.getGeneEnum() == GeneEnum.RT) {
					mutListStrings.addAll(Arrays.asList("NA", "NA", "NA", "NA", "NA"));
				}
				else {
					mutListStrings.addAll(Arrays.asList("NA", "NA", "NA"));
				}
			} else {
				AlignedGeneSeq seq = seqResult.get(gene);
				List<MutType> mutTypes;
				if (gene == Gene.valueOf("HIV1RT")) {
					mutTypes = Arrays.asList(MutType.NRTI, null, MutType.NNRTI, null, MutType.Other);
				}
				else if (gene == Gene.valueOf("HIV2ART") || gene == Gene.valueOf("HIV2BRT")) {
					mutTypes = Arrays.asList(MutType.Major, MutType.Accessory, null, null, MutType.Other);
				}
				else {
					mutTypes = Arrays.asList(MutType.Major, MutType.Accessory, MutType.Other);
				}
				for (MutType mutType : mutTypes) {
					if (mutType == null) {
						mutListStrings.add("NA");
						continue;
					}
					MutationSet mutTypeMutations = seq.getMutationsByMutType(mutType);
					if (mutTypeMutations.isEmpty()) {
						mutListStrings.add("None");
					} else {
						mutListStrings.add(mutTypeMutations.join());
					}
				}
			}
		}
		return mutListStrings;
	}


	// Four lists are returned. They should be in the following order: PI, NRTI, NNRTI, INSTI
	private static List<String> determineNonDrmTsms(AlignedSequence alignedSeq) {
		List<String> nonDrmTsmsList = new ArrayList<>();
		Map<Gene, AlignedGeneSeq> seqResult = alignedSeq.getAlignedGeneSequenceMap();

		// TODO: HIV2 Support
		for (Gene gene : Gene.values(alignedSeq.getStrain())) {
			if (!seqResult.containsKey(gene)) {
				if (gene.getGeneEnum() == GeneEnum.RT) {
					nonDrmTsmsList.add("NA");
					nonDrmTsmsList.add("NA");
				} else {
					nonDrmTsmsList.add("NA");
				}
			} else {
				AlignedGeneSeq seq = seqResult.get(gene);
				Map<DrugClass, MutationSet> allNonDrmTsms = seq.getNonDrmTsms();
				List<DrugClass> drugClasses;
				switch (gene.getGeneEnum()) {
					case PR:
						drugClasses = Arrays.asList(DrugClass.PI);
						break;
					case RT:
						drugClasses = Arrays.asList(DrugClass.NRTI, DrugClass.NNRTI);
						break;
					default:
						// case IN:
						drugClasses = Arrays.asList(DrugClass.INSTI);
						break;
				}
				
				for (DrugClass drugClass : drugClasses) {
					if (!allNonDrmTsms.containsKey(drugClass)) {
						nonDrmTsmsList.add("NA");
						continue;
					}
					MutationSet nonDrmTsms = allNonDrmTsms.get(drugClass);

					String nonDrmTsmsText;
					if (nonDrmTsms.size() > 0) {
						nonDrmTsmsText = nonDrmTsms.join();
					} else {
						nonDrmTsmsText = "None";
					}
					nonDrmTsmsList.add(nonDrmTsmsText);
				}
			}
		}

		return nonDrmTsmsList;
	}


	private static List<String> determineGeneBoundaries (AlignedSequence alignedSeq) {
		List<String>geneBoundaries = new ArrayList<>();
		String firstAAPR, lastAAPR, firstAART, lastAART, firstAAIN, lastAAIN;
		firstAAPR = lastAAPR = firstAART = lastAART = firstAAIN = lastAAIN = "NA";
		Map<Gene, AlignedGeneSeq> seqResult = alignedSeq.getAlignedGeneSequenceMap();
		Strain strain = alignedSeq.getStrain();
		if (seqResult.containsKey(Gene.valueOf(strain, "PR"))) {
			firstAAPR = "" + seqResult.get(Gene.valueOf(strain, "PR")).getFirstAA();
			lastAAPR = "" + seqResult.get(Gene.valueOf(strain, "PR")).getLastAA();
		}
		if (seqResult.containsKey(Gene.valueOf(strain, "RT"))) {
			firstAART = "" + seqResult.get(Gene.valueOf(strain, "RT")).getFirstAA();
			lastAART = "" + seqResult.get(Gene.valueOf(strain, "RT")).getLastAA();
		}
		if (seqResult.containsKey(Gene.valueOf(strain, "IN"))) {
			firstAAIN = "" + seqResult.get(Gene.valueOf(strain, "IN")).getFirstAA();
			lastAAIN = "" + seqResult.get(Gene.valueOf(strain, "IN")).getLastAA();
		}
		geneBoundaries.addAll(Arrays.asList(new String[] {
			firstAAPR, lastAAPR, firstAART, lastAART, firstAAIN, lastAAIN}));
		return geneBoundaries;
	}

}
