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

package edu.stanford.hivdb.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRAsi;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.extras.XmlOutput;
import edu.stanford.hivdb.reports.ResistanceSummaryTSV;
import edu.stanford.hivdb.reports.SequenceSummaryTSV;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.PrettyAlignments;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.drugresistance.reports.TabularAlgorithmsComparison;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;


@Path("sequence-analysis")
public class SequenceAnalysisService {

	private final HIV hiv = HIV.getInstance();

	@FormParam("compareAlgorithms")
	protected String algorithmsCSV;

	@FormParam("compareCustomAlgorithms")
	protected String customAlgorithmsStr;
	
	@FormParam("drugResistanceAlgorithm")
	protected String drAlgorithm;

	private class SequenceAnalysisServiceOutput {

		private final Sequence[] sequences;
		private final Set<String> outputOptions;

		// caches
		private Map<Sequence, AlignedSequence<HIV>> alignedSeqMap;
		private Map<Sequence, Map<Gene<HIV>, GeneDR<HIV>>> drsMap;

		public SequenceAnalysisServiceOutput(
				Sequence[] sequences, String[] outputOptions) {
			this.sequences = sequences;
			this.outputOptions = new TreeSet<>(Arrays.asList(outputOptions));
			this.alignedSeqMap = (
				NucAminoAligner.getInstance(hiv)
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
				String textSequences, String textOutputOptions) {
			this(
				FastaUtils.readString(textSequences).toArray(new Sequence[0]),
				textOutputOptions.split(","));
		}

		private AlignedSequence<HIV> getAlignedSeq(Sequence seq) {
			return alignedSeqMap.get(seq);
		}

		private Map<Gene<HIV>, GeneDR<HIV>> getDRs(Sequence seq) {
			if (!drsMap.containsKey(seq)) {
				List<AlignedGeneSeq<HIV>> aligneds =
					getAlignedSeq(seq).getAlignedGeneSequences();
				DrugResistanceAlgorithm<HIV> alg = hiv.getDrugResistAlgorithm(drAlgorithm);
				drsMap.put(
					seq, GeneDRAsi.getResistanceByGeneFromAlignedGeneSeqs(aligneds, alg));
			}
			return drsMap.get(seq);
		}

		private String getAlignmentTsv() {
			List<AlignedSequence<HIV>> overallResults = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());
			return SequenceSummaryTSV.getInstance(hiv).getReport(overallResults);
		}

		private String getAlgorithmComparisonTsv() {
			List<AlignedSequence<HIV>> overallResults = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());
			List<DrugResistanceAlgorithm<HIV>> algorithms = new ArrayList<>(
				hiv.getDrugResistAlgorithms(
					Lists.newArrayList(algorithmsCSV.split(","))
				)
			);
			List<Map<String, String>> customAlgorithmList =
				Json.loads(customAlgorithmsStr, new TypeToken<List<Map<String, String>>>() {}.getType());
			algorithms.addAll(
				customAlgorithmList
				.stream()
				.map(m -> new DrugResistanceAlgorithm<>(
					m.get("name"), hiv, m.get("xml")
				))
				.collect(Collectors.toList())
			);
			return new TabularAlgorithmsComparison<>(
				overallResults, algorithms).toString();
		}

		private Map<Gene<HIV>, String> getPrettyAlignmentTsvs() {
			return (
				// TODO: HIV2 support
				hiv.getStrain("HIV1").getGenes()
				.stream()
				.collect(Collectors.toMap(
					gene -> gene,
					gene -> {
						List<AlignedGeneSeq<HIV>> aligneds = Arrays
							.stream(sequences)
							.map(seq -> getAlignedSeq(seq).getAlignedGeneSequence(gene))
							.filter(aligned -> aligned != null)
							.collect(Collectors.toList());
						return new PrettyAlignments<>(gene, aligneds).toString();
					}
				))
			);
		}

		private String getDRTsv() {
			List<AlignedSequence<HIV>> alignedSequences = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());
			List<Map<Gene<HIV>, GeneDR<HIV>>> allResistanceResults = Arrays
				.stream(sequences)
				.map(this::getDRs)
				.collect(Collectors.toList());
			DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm(drAlgorithm);
			return ResistanceSummaryTSV.getInstance(hiv).getReport(
				alignedSequences, allResistanceResults, algorithm
			).toString();
		}

		private String getDRXml() {
			List<AlignedSequence<HIV>> alignedSequences = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());
			List<Map<Gene<HIV>, GeneDR<HIV>>> allResistanceResults = Arrays
				.stream(sequences)
				.map(this::getDRs)
				.collect(Collectors.toList());

			return new XmlOutput(
				alignedSequences, allResistanceResults).toString();
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


	/**
	 * Service endpoint that provide multiple types of results.
	 *
	 * @param sequences The input sequences in FASTA format.
	 * @param outputOptions Comma delimited values output options
	 * @return All results packed in JSON format.
	 */
	@POST
	@Produces("application/json")
	public Response getAll(
			@FormParam("sequences") String sequences,
			@FormParam("outputOptions") String outputOptions) {

		SequenceAnalysisServiceOutput output =
			new SequenceAnalysisServiceOutput(sequences, outputOptions);

		return Response.ok(output.toString()).build();
	}
}
