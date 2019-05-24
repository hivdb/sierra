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

package edu.stanford.hivdb.graphql;

import graphql.GraphQLException;
import graphql.schema.*;
import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.ngs.GeneSequenceReads;
import edu.stanford.hivdb.ngs.SequenceReads;
import edu.stanford.hivdb.ngs.SequenceReadsHistogram;
import edu.stanford.hivdb.ngs.SequenceReadsHistogram.AggregationOption;

import static edu.stanford.hivdb.graphql.MutationSetDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.GeneSequenceReadsDef.*;
import static edu.stanford.hivdb.graphql.MutationStatsDef.oMutationStats;
import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.SubtypeV2Def.*;
import static edu.stanford.hivdb.graphql.PositionCodonReadsDef.*;
import static edu.stanford.hivdb.graphql.SequenceReadsHistogramDef.*;
import static edu.stanford.hivdb.graphql.DescriptiveStatisticsDef.*;

public class SequenceReadsAnalysisDef {

	private static DataFetcher<List<BoundGenotype>> subtypesDataFetcher = new DataFetcher<List<BoundGenotype>>() {
		@Override
		public List<BoundGenotype> get(DataFetchingEnvironment environment) {
			int first = environment.getArgument("first");
			SequenceReads seqReads = (SequenceReads) environment.getSource();
			HIVGenotypeResult subtypeResult = seqReads.getSubtypeResult();
			if (subtypeResult == null) {
				return Collections.emptyList();
			}
			return subtypeResult.getAllMatches().subList(0, first);
		}
	};

	private static DataFetcher<List<GeneDR>> drugResistanceDataFetcher = new DataFetcher<List<GeneDR>>() {
		@Override
		public List<GeneDR> get(DataFetchingEnvironment environment) {
			SequenceReads seqReads = (SequenceReads) environment.getSource();
			List<GeneSequenceReads> allGeneSeqReads = seqReads.getAllGeneSequenceReads();
			return new ArrayList<>(GeneDRFast.getResistanceByGeneFromReads(allGeneSeqReads).values());
		}
	};
	
	private static DataFetcher<SequenceReadsHistogram> seqReadsHistogramDataFetcher = new DataFetcher<SequenceReadsHistogram>() {
		
		@Override
		public SequenceReadsHistogram get(DataFetchingEnvironment environment) {
			SequenceReads seqReads = (SequenceReads) environment.getSource();
			double lowerLimit = environment.getArgument("pcntLowerLimit");
			double upperLimit = environment.getArgument("pcntUpperLimit");
			int numBins = environment.getArgument("numBins");
			boolean cumulative = environment.getArgument("cumulative");
			AggregationOption aggBy = environment.getArgument("aggregatesBy");
			return seqReads.getHistogram(lowerLimit, upperLimit, numBins, cumulative, aggBy);
		}
	};

	public static SequenceReads toSequenceReadsList(Map<String, Object> input) {
		String name = (String) input.get("name");
		if (name == null) {
			throw new GraphQLException("`name` is a required field but doesn't have value");
		}
		Strain strain = (Strain) input.get("strain");
		List<PositionCodonReads> allReads = (
			((List<?>) input.get("allReads"))
			.stream()
			.map(pcr -> toPositionCodonReads(strain, (Map<?, ?>) pcr))
			.collect(Collectors.toList()));
		if (allReads == null) {
			throw new GraphQLException("`allReads` is a required field but doesn't have value");
		}
		Double minPrevalence = (Double) input.get("minPrevalence");
		if (minPrevalence == null) {
			throw new GraphQLException("`minPrevalence` is a required field but doesn't have value");
		}
		return SequenceReads.fromCodonReadsTable(
			(String) input.get("name"),
			allReads,
			(Double) input.get("minPrevalence"),
			(Long) input.get("minReadDepth"));
	}

	public static GraphQLInputType iSequenceReads = newInputObject()
		.name("SequenceReadsInput")
		.field(field -> field
			.type(GraphQLString)
			.name("name")
			.description("An identifiable name for identifying the result from the returning list."))
		.field(field -> field
			.type(enumStrain)
			.name("strain")
			.description("Strain of this sequence, choice: HIV1, HIV2A, HIV2B."))
		.field(field -> field
			.type(new GraphQLList(iPositionCodonReads))
			.name("allReads")
			.description("List of all reads belong to this sequence."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("minPrevalence")
			.defaultValue(-1.0d)
			.description(
				"The minimal prevalence cutoff to apply on each codon. " +
				"Leave this field empty or specify a negative number to " +
				"use the dynamic cutoff based on sequencing quality."))
		.field(field -> field
			.type(GraphQLLong)
			.name("minReadDepth")
			.defaultValue(1000L)
			.description(
				"The minal read depth for each codon. Default to 1000 " +
				"if this field was left empty or had a negative number" +
				"specified."))
		.build();

	public static GraphQLObjectType oSequenceReadsAnalysis = newObject()
		.name("SequenceReadsAnalysis")
		.field(field -> field
			.type(GraphQLString)
			.name("name")
			.description("Name of this sequence."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("cutoffSuggestionLooserLimit")
			.description(
				"Algorithm suggested minimal prevalence cutoff. " +
				"This cutoff is looser and may include more problematic mutations."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("cutoffSuggestionStricterLimit")
			.description(
				"Algorithm suggested minimal prevalence cutoff. " +
				"This cutoff is stricter and include less problematic mutations."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("minPrevalence")
			.description(
				"The minimal prevalence cutoff applied on this sequence. " +
				"If the same name field didn't specified in `SequenceReadsInput`, " +
				"this value was dynamically selected by the program " +
				"based on sequencing quality."))
		.field(field -> field
			.type(GraphQLLong)
			.name("minReadDepth")
			.description(
				"The minimal read depth for each codon of this sequence."
			))
		.field(field -> field
			.type(GraphQLFloat)
			.name("medianReadDepth")
			.description("Median read depth of all codons in this sequence.")
		)
		.field(field -> field
			.type(new GraphQLList(oGene))
			.name("availableGenes")
			.description("Available genes found in the sequence reads."))
		.field(field -> field
			.type(new GraphQLList(oGeneSequenceReads))
			.name("allGeneSequenceReads")
			.description("List of sequence reads distinguished by genes."))
		.field(field -> field
			.type(new GraphQLList(oBoundSubtypeV2))
			.name("subtypes")
			.argument(arg -> arg
				.type(GraphQLInt)
				.name("first")
				.defaultValue(2)
				.description(
					"Fetch only the first nth closest subtypes. Default to 2."))
			.dataFetcher(subtypesDataFetcher)
			.description(
				"List of HIV1 groups or subtypes, or HIV species. " +
				"Sorted by the similarity from most to least."))
		.field(field -> field
			.type(oBoundSubtypeV2)
			.name("bestMatchingSubtype")
			.description(
				"The best matching subtype."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("mixturePcnt")
			.description(
				"Mixture pecentage of the consensus. Notes only RYMWKS " +
				"are counted."))
		.field(newMutationSet("mutations")
			.description("All mutations found in the sequence reads.")
			.build())
		.field(field -> field
			.type(new GraphQLList(oMutationStats))
			.name("mutationStats")
			.argument(arg -> arg
				.type(new GraphQLList(GraphQLFloat))
				.name("allMinPrevalence")
				.description(
					"Specify the prevalence cutoff of fetching mutation stats."
				))
			.dataFetcher(env -> (
				((SequenceReads) env.getSource())
				.getMutationStats(
					env.getArgument("allMinPrevalence")
				)
			))
			.description("List of statistics of mutations."))
		.field(field -> field
			.type(new GraphQLList(oMutationStats))
			.name("allMutationStats")
			.description(
				"List of statistics of mutations at all prevalence points. (INTERNAL)"))
		// .field(field -> field
		// 	.type(new GraphQLList(oFrameShift))
		// 	.name("frameShifts")
		// 	.description("All frame shifts found in the aligned sequence."))
		.field(field -> field
			.type(new GraphQLList(oDrugResistance))
			.name("drugResistance")
			.description("List of drug resistance results by genes.")
			.dataFetcher(drugResistanceDataFetcher))
		.field(field -> field
			.type(oSeqReadsHistogram)
			.name("histogram")
			.description("Histogram data for sequence reads.")
			.argument(arg -> arg
				.name("pcntLowerLimit")
				.type(GraphQLFloat)
				.defaultValue(.1d)
				.description("Percent lower limit of filtering codon reads (range: 0-100)."))
			.argument(arg -> arg
				.name("pcntUpperLimit")
				.type(GraphQLFloat)
				.defaultValue(20d)
				.description("Percent lower limit of filtering codon reads (range: 0-100)."))
			.argument(arg -> arg
			 	.name("numBins")
			 	.type(GraphQLInt)
			 	.defaultValue(8)
			 	.description("Number of bins wanted in this histogram."))
			.argument(arg -> arg
				.name("cumulative")
				.type(GraphQLBoolean)
				.defaultValue(true)
				.description("Generate cumulative histogram data instead."))
			.argument(arg -> arg
				.name("aggregatesBy")
				.type(enumAggregationOption)
				.defaultValue(AggregationOption.Position)
				.description("Aggregation option."))
			.dataFetcher(seqReadsHistogramDataFetcher))
		.field(field -> field
			.name("readDepthStats")
			.type(oDescriptiveStatistics)
			.description("Descriptive statistics of all read depth.")
		)
		.build();

}
