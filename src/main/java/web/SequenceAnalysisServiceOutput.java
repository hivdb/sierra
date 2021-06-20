package edu.stanford.hivdb.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.drugresistance.algorithm.TabularAlgorithmsComparison;
import edu.stanford.hivdb.reports.ResistanceSummaryTSV;
import edu.stanford.hivdb.reports.SequenceSummaryTSV;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.Aligner;
import edu.stanford.hivdb.sequences.PrettyAlignments;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;

public class SequenceAnalysisServiceOutput<VirusT extends Virus<VirusT>> {

	/**
	 * 
	 */
	private final SequenceAnalysisService<VirusT> sequenceAnalysisService;

	private final VirusT virusIns;

	private final Sequence[] sequences;
	private final Set<String> outputOptions;

	// caches
	private Map<Sequence, AlignedSequence<VirusT>> alignedSeqMap;
	private Map<Sequence, Map<Gene<VirusT>, GeneDR<VirusT>>> drsMap;

	public SequenceAnalysisServiceOutput(
		VirusT virusIns, SequenceAnalysisService<VirusT> sequenceAnalysisService,
		Sequence[] sequences, String[] outputOptions
	) {
		this.virusIns = virusIns;
		this.sequenceAnalysisService = sequenceAnalysisService;
		this.sequences = sequences;
		this.outputOptions = new TreeSet<>(Arrays.asList(outputOptions));
		this.alignedSeqMap = (
			Aligner.getInstance(virusIns)
			.parallelAlign(Arrays.asList(sequences))
			.stream()
			.collect(Collectors.toMap(
				as -> as.getInputSequence(),
				as -> as,
				(as1, as2) -> as1,
				LinkedHashMap::new
			))
		);
		this.drsMap = new LinkedHashMap<>();
	}

	public SequenceAnalysisServiceOutput(
		VirusT virusIns, SequenceAnalysisService<VirusT> sequenceAnalysisService,
		String textSequences, String textOutputOptions
	) {
		this(
			virusIns, sequenceAnalysisService,
			FastaUtils.readString(textSequences).toArray(new Sequence[0]),
			textOutputOptions.split(","));
	}

	private AlignedSequence<VirusT> getAlignedSeq(Sequence seq) {
		return alignedSeqMap.get(seq);
	}

	private Map<Gene<VirusT>, GeneDR<VirusT>> getDRs(Sequence seq) {
		if (!drsMap.containsKey(seq)) {
			List<AlignedGeneSeq<VirusT>> aligneds = getAlignedSeq(seq).getAlignedGeneSequences();
			DrugResistanceAlgorithm<VirusT> alg = virusIns.getDrugResistAlgorithm(sequenceAnalysisService.drAlgorithm);
			drsMap.put(seq, GeneDR.newFromAlignedGeneSeqs(aligneds, alg));
		}
		return drsMap.get(seq);
	}

	private String getAlignmentTsv() {
		List<AlignedSequence<VirusT>> overallResults = Arrays
			.stream(sequences)
			.map(this::getAlignedSeq)
			.collect(Collectors.toList());
		return SequenceSummaryTSV.getInstance(virusIns).getReport(overallResults);
	}

	private String getAlgorithmComparisonTsv() {
		List<AlignedSequence<VirusT>> overallResults = Arrays
			.stream(sequences)
			.map(this::getAlignedSeq)
			.collect(Collectors.toList());
		List<DrugResistanceAlgorithm<VirusT>> algorithms = new ArrayList<>(
			virusIns.getDrugResistAlgorithms(
				Lists.newArrayList(sequenceAnalysisService.algorithmsCSV.split(","))
			)
		);
		List<Map<String, String>> customAlgorithmList =
			Json.loads(sequenceAnalysisService.customAlgorithmsStr, new TypeToken<List<Map<String, String>>>() {}.getType());
		algorithms.addAll(
			customAlgorithmList
			.stream()
			.map(m -> new DrugResistanceAlgorithm<>(
				m.get("name"), virusIns, m.get("xml")
			))
			.collect(Collectors.toList())
		);
		return new TabularAlgorithmsComparison<>(
			overallResults, algorithms).toString();
	}

	private Map<String, String> getPrettyAlignmentTsvs() {
		return (
			virusIns.getMainStrain().getGenes()
			.stream()
			.collect(Collectors.toMap(
				gene -> gene.getAbstractGene(),
				tgtGene -> {
					List<AlignedGeneSeq<VirusT>> aligneds = Arrays
						.stream(sequences)
						// use abstract gene name here
						.map(seq -> getAlignedSeq(seq).getAlignedGeneSequence(tgtGene.getAbstractGene()))
						.filter(aligned -> aligned != null)
						.collect(Collectors.toList());
					return new PrettyAlignments<>(tgtGene, aligneds).toString();
				}
			))
		);
	}

	private String getDRTsv() {
		List<AlignedSequence<VirusT>> alignedSequences = Arrays
			.stream(sequences)
			.map(this::getAlignedSeq)
			.collect(Collectors.toList());
		List<Map<Gene<VirusT>, GeneDR<VirusT>>> allResistanceResults = Arrays
			.stream(sequences)
			.map(this::getDRs)
			.collect(Collectors.toList());
		DrugResistanceAlgorithm<VirusT> algorithm = virusIns.getDrugResistAlgorithm(sequenceAnalysisService.drAlgorithm);
		return ResistanceSummaryTSV.getInstance(virusIns).getReport(
			alignedSequences, allResistanceResults, algorithm
		).toString();
	}

	private String getDRXml() {
		List<AlignedSequence<VirusT>> alignedSequences = Arrays
			.stream(sequences)
			.map(this::getAlignedSeq)
			.collect(Collectors.toList());
		List<Map<Gene<VirusT>, GeneDR<VirusT>>> allResistanceResults = Arrays
			.stream(sequences)
			.map(this::getDRs)
			.collect(Collectors.toList());

		return new XmlOutput<VirusT>(
			virusIns, alignedSequences, allResistanceResults).toString();
	}

	@Override
	public String toString() {
		Map<String, Object> output = new TreeMap<>();

		for (String outputOption : outputOptions) {
			switch (outputOption) {
				case "alignment.tsv":
				case "alignments.tsv":
					output.put(outputOption, getAlignmentTsv());
					break;
				case "pretty-alignment.tsv":
				case "pretty-alignments.tsv":
					output.put(outputOption, getPrettyAlignmentTsvs());
					break;
				case "drug-resistance.tsv":
				case "drug-resistances.tsv":
					output.put(outputOption, getDRTsv());
					break;
				case "drug-resistance.xml":
				case "drug-resistances.xml":
					output.put(outputOption, getDRXml());
					break;
				case "alg-comparison.tsv":
				case "alg-comparisons.tsv":
					output.put(outputOption, getAlgorithmComparisonTsv());
					break;
			}
		}

		return Json.dumps(output);
	}

}