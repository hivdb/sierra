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

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.alignment.Aligner;
import edu.stanford.hivdb.alignment.TabularSequenceSummary;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRAsi;
import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;
import edu.stanford.hivdb.alignment.PrettyAlignments;
import edu.stanford.hivdb.drugresistance.reports.XmlOutput;
import edu.stanford.hivdb.drugresistance.reports.TabularAlgorithmsComparison;
import edu.stanford.hivdb.drugresistance.reports.TabularResistanceSummary;


@Path("sequence-analysis")
public class SequenceAnalysisService {

	@FormParam("algorithms")
	protected String algorithmsCSV;

	@FormParam("customAlgorithms")
	protected String customAlgorithmsStr;

	private class SequenceAnalysisServiceOutput {

		private final Sequence[] sequences;
		private final Set<String> outputOptions;

		// caches
		private Map<Sequence, AlignedSequence> alignedSeqMap;
		private Map<Sequence, Map<Gene, GeneDR>> drsMap;

		public SequenceAnalysisServiceOutput(
				Sequence[] sequences, String[] outputOptions) {
			this.sequences = sequences;
			this.outputOptions = new TreeSet<>(Arrays.asList(outputOptions));
			this.alignedSeqMap = (
				Aligner.parallelAlign(Arrays.asList(sequences))
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

		private AlignedSequence getAlignedSeq(Sequence seq) {
			return alignedSeqMap.get(seq);
		}

		private Map<Gene, GeneDR> getDRs(Sequence seq) {
			if (!drsMap.containsKey(seq)) {
				List<AlignedGeneSeq> aligneds =
					getAlignedSeq(seq).getAlignedGeneSequences();
				drsMap.put(
					seq, GeneDRAsi.getResistanceByGeneFromAlignedGeneSeqs(aligneds));
			}
			return drsMap.get(seq);
		}

		private String getAlignmentTsv() {
			List<AlignedSequence> overallResults = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());
			return new TabularSequenceSummary(overallResults).toString();
		}

		private String getAlgorithmComparisonTsv() {
			List<AlignedSequence> overallResults = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());
			List<Algorithm> algorithms = Arrays
				.stream(algorithmsCSV.split(","))
				.map(Algorithm::valueOf)
				.collect(Collectors.toList());
			List<Map<String, String>> customAlgorithmList =
				Json.loads(customAlgorithmsStr, new TypeToken<List<Map<String, String>>>() {}.getType());
			Map<String, String> customAlgorithms = customAlgorithmList
				.stream()
				.collect(Collectors.toMap(
					m -> m.get("name"),
					m -> m.get("xml"),
					(a, b) -> a,
					LinkedHashMap::new));
			return new TabularAlgorithmsComparison(
				overallResults, algorithms, customAlgorithms).toString();
		}

		private Map<Gene, String> getPrettyAlignmentTsvs() {
			return Arrays
			// TODO: HIV2 support
			.stream(Gene.values(Strain.HIV1))
			.collect(Collectors.toMap(
				gene -> gene,
				gene -> {
					List<AlignedGeneSeq> aligneds = Arrays
						.stream(sequences)
						.map(seq -> getAlignedSeq(seq).getAlignedGeneSequence(gene))
						.filter(aligned -> aligned != null)
						.collect(Collectors.toList());
					return new PrettyAlignments(gene, aligneds).toString();
				}
			));
		}

		private String getDRTsv() {
			List<Map<Gene, GeneDR>> overallResults = Arrays
				.stream(sequences)
				.map(this::getDRs)
				.collect(Collectors.toList());
			return new TabularResistanceSummary(
				Arrays.asList(sequences), overallResults).toString();
		}

		private String getDRXml() {
			List<AlignedSequence> alignedSequences = Arrays
				.stream(sequences)
				.map(this::getAlignedSeq)
				.collect(Collectors.toList());

			List<Map<Gene, GeneDR>> allResistanceResults = Arrays
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
