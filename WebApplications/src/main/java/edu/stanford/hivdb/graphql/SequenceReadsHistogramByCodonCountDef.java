package edu.stanford.hivdb.graphql;

import graphql.schema.*;
import graphql.schema.GraphQLFieldDefinition.Builder;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import static edu.stanford.hivdb.graphql.SequenceReadsHistogramDef.enumAggregationOption;
import static edu.stanford.hivdb.seqreads.SequenceReadsHistogram.AggregationOption;
import static edu.stanford.hivdb.seqreads.SequenceReadsHistogramByCodonCount.WithSequenceReadsHistogramByCodonCount;

import edu.stanford.hivdb.seqreads.SequenceReadsHistogramByCodonCount;


public class SequenceReadsHistogramByCodonCountDef {

	public static GraphQLObjectType oSeqReadsHistogramByCodonCountBin;

	public static DataFetcher<SequenceReadsHistogramByCodonCount<?>> seqReadsHistogramByCodonCountDataFetcher = env -> {
		WithSequenceReadsHistogramByCodonCount<?> seqReads = env.getSource();
		List<Double> codonCountCutoffs = env.getArgument("codonCountCutoffs");
		AggregationOption aggBy = env.getArgument("aggregatesBy");
		return seqReads.getHistogramByCodonCount(
			codonCountCutoffs.toArray(new Long[codonCountCutoffs.size()]),
			aggBy);
	};
	
	static {		
		oSeqReadsHistogramByCodonCountBin = newObject()
			.name("SequenceReadsHistogramByCodonCountBin")
			.description("A single bin data of the histogram.")
			.field(field -> field
				.name("cutoff")
				.type(GraphQLLong)
				.description("Codon count cutoff (minimal) of this bin."))
			.field(field -> field
				.name("count")
				.type(GraphQLInt)
				.description("Total count (Y axis) of this bin."))
			.build();
	}

	public static GraphQLObjectType oSeqReadsHistogramByCodonCount = newObject()
		.name("SequenceReadsHistogramByCodonCount")
		.description("Histogram data for sequence reads.")
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("usualSites")
			.description("Usual sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("drmSites")
			.description("Sites with drug resistance mutations histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("unusualSites")
			.description("Unusual sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("unusualApobecSites")
			.description("Unusual & APOBEC sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("unusualNonApobecSites")
			.description("Unusual & Non-APOBEC sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("apobecSites")
			.description("APOBEC sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("apobecDrmSites")
			.description("APOBEC DRM sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramByCodonCountBin))
			.name("stopCodonSites")
			.description("Stop codon sites histogram data."))
		.field(field -> field
			.type(GraphQLInt)
			.name("numPositions")
			.description("Total number of positions."))
		.build();

	public static UnaryOperator<Builder> oSeqReadsHistogramByCodonCountBuilder = field -> field
		.type(oSeqReadsHistogramByCodonCount)
		.name("histogramByCodonCount")
		.description("Histogram data for sequence reads.")
		.argument(arg -> arg
		 	.name("codonCountCutoffs")
		 	.type(new GraphQLList(GraphQLLong))
		 	.defaultValue(Arrays.asList(16, 32, 64, 128, 256, 512, 1024, 2048))
		 	.description("Codon count cutoffs wanted in this histogram."))
		.argument(arg -> arg
			.name("aggregatesBy")
			.type(enumAggregationOption)
			.defaultValue(AggregationOption.Position)
			.description("Aggregation option."));
}
