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

import edu.stanford.hivdb.utilities.TSV;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.NumberFormats;


public class TabularSequenceSummary {
	private static final String[] headerFields = {
			"Sequence Name", "Genes", "PR Start", "PR End", "RT Start",
			"RT End", "IN Start", "IN End", "Subtype(%)", "Pcnt Mix",
			"PR Major", "PR Accessory", "PR Other", "NRTI", "NNRTI",
			"RT Other", "IN Major", "IN Accessory", "IN Other", "PR SDRMs",
			"RT SDRMs", "PI TSMs", "NRTI TSMs", "NNRTI TSMs", "INSTI TSMs",
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
			String genes = StringUtils.join(geneList, ",");

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
			// NRTI, NNRTI, RTOther,
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
		if (seqResult.containsKey(Gene.PR)) {
			MutationSet sdrms = seqResult.get(Gene.PR).getSdrms();
			prSdrms = sdrms.join();
		}
		if (seqResult.containsKey(Gene.RT)) {
			MutationSet sdrms = seqResult.get(Gene.RT).getSdrms();
			rtSdrms = sdrms.join();
		}
		sdrmList.add(prSdrms);
		sdrmList.add(rtSdrms);
		return sdrmList;

	}

	public static List<String> determineMutLists(AlignedSequence alignedSeq) {
		List<String> mutListStrings = new ArrayList<>();
		Map<Gene, AlignedGeneSeq> seqResult = alignedSeq.getAlignedGeneSequenceMap();

		for (Gene gene : Gene.values()) {
			if (!seqResult.containsKey(gene)) {
				mutListStrings.add("NA");
				mutListStrings.add("NA");
				mutListStrings.add("NA");
			} else {
				AlignedGeneSeq seq = seqResult.get(gene);
				for (MutType mutType : gene.getMutationTypes()) {
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

		for (Gene gene : Gene.values()) {
			if (!seqResult.containsKey(gene)) {
				if (gene.equals(Gene.RT)) {
					nonDrmTsmsList.add("NA");
					nonDrmTsmsList.add("NA");
				} else {
					nonDrmTsmsList.add("NA");
				}
			} else {
				AlignedGeneSeq seq = seqResult.get(gene);
				Map<DrugClass, MutationSet> allNonDrmTsms = seq.getNonDrmTsms();
				for (DrugClass drugClass : gene.getDrugClasses()) {
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
		if (seqResult.containsKey(Gene.PR)) {
			firstAAPR = "" + seqResult.get(Gene.PR).getFirstAA();
			lastAAPR = "" + seqResult.get(Gene.PR).getLastAA();
		}
		if (seqResult.containsKey(Gene.RT)) {
			firstAART = "" + seqResult.get(Gene.RT).getFirstAA();
			lastAART = "" + seqResult.get(Gene.RT).getLastAA();
		}
		if (seqResult.containsKey(Gene.IN)) {
			firstAAIN = "" + seqResult.get(Gene.IN).getFirstAA();
			lastAAIN = "" + seqResult.get(Gene.IN).getLastAA();
		}
		geneBoundaries.addAll(Arrays.asList(new String[] {
			firstAAPR, lastAAPR, firstAART, lastAART, firstAAIN, lastAAIN}));
		return geneBoundaries;
	}

}
