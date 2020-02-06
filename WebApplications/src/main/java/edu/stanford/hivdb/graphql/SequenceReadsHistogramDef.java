package edu.stanford.hivdb.graphql;

import graphql.schema.*;
import graphql.schema.GraphQLFieldDefinition.Builder;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import edu.stanford.hivdb.seqreads.SequenceReadsHistogram;
import edu.stanford.hivdb.seqreads.SequenceReadsHistogram.AggregationOption;
import edu.stanford.hivdb.seqreads.SequenceReadsHistogram.WithSequenceReadsHistogram;

public class SequenceReadsHistogramDef {

	public static GraphQLEnumType enumAggregationOption;
	public static GraphQLObjectType oSeqReadsHistogramBin;

	public static DataFetcher<SequenceReadsHistogram<?>> seqReadsHistogramDataFetcher = env -> {
		WithSequenceReadsHistogram<?> seqReads = env.getSource();
		double lowerLimit = env.getArgument("pcntLowerLimit");
		double upperLimit = env.getArgument("pcntUpperLimit");
		Integer numBins = env.getArgument("numBins");
		List<Double> binTicks = env.getArgument("binTicks");
		boolean cumulative = env.getArgument("cumulative");
		AggregationOption aggBy = env.getArgument("aggregatesBy");
		if (numBins != null) {
			return seqReads.getHistogram(
				lowerLimit, upperLimit, numBins,
				cumulative, aggBy);
		}
		else {
			return seqReads.getHistogram(
				lowerLimit, upperLimit,
				binTicks.toArray(new Double[binTicks.size()]),
				cumulative, aggBy);
		}
	};
	
	static {
		GraphQLEnumType.Builder newEnumAggregatesOption =
			GraphQLEnumType.newEnum().name("EnumSequenceReadsHistogramAggregatesOption");
		for (AggregationOption opt : AggregationOption.values()) {
			newEnumAggregatesOption.value(opt.toString(), opt);
		}
		enumAggregationOption = newEnumAggregatesOption.build();
		
		oSeqReadsHistogramBin = newObject()
			.name("SequenceReadsHistogramBin")
			.description("A single bin data of the histogram.")
			.field(field -> field
				.name("percentStart")
				.type(GraphQLFloat)
				.description("Percent start (X axis) of this bin."))
			.field(field -> field
				.name("percentStop")
				.type(GraphQLFloat)
				.description("Percent stop (X axis) of this bin."))
			.field(field -> field
				.name("count")
				.type(GraphQLInt)
				.description("Total count (Y axis) of this bin."))
			.build();
	}

	public static GraphQLObjectType oSeqReadsHistogram = newObject()
		.name("SequenceReadsHistogram")
		.description("Histogram data for sequence reads.")
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("usualSites")
			.description("Usual sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("drmSites")
			.description("Sites with drug resistance mutations histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("unusualSites")
			.description("Unusual sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("unusualApobecSites")
			.description("Unusual & APOBEC sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("unusualNonApobecSites")
			.description("Unusual & Non-APOBEC sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("apobecSites")
			.description("APOBEC sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("apobecDrmSites")
			.description("APOBEC DRM sites histogram data."))
		.field(field -> field
			.type(new GraphQLList(oSeqReadsHistogramBin))
			.name("stopCodonSites")
			.description("Stop codon sites histogram data."))
		.field(field -> field
			.type(GraphQLInt)
			.name("numPositions")
			.description("Total number of positions."))
		.build();

	public static UnaryOperator<Builder> oSeqReadsHistogramBuilder = field -> field
		.type(oSeqReadsHistogram)
		.name("histogram")
		.description("Histogram data for sequence reads.")
		.argument(arg -> arg
			.name("pcntLowerLimit")
			.type(GraphQLFloat)
			.defaultValue(0.001d)
			.description("Percent lower limit of filtering codon reads (range: 0-100)."))
		.argument(arg -> arg
			.name("pcntUpperLimit")
			.type(GraphQLFloat)
			.defaultValue(0.2d)
			.description("Percent lower limit of filtering codon reads (range: 0-100)."))
		.argument(arg -> arg
		 	.name("numBins")
		 	.type(GraphQLInt)
		 	.description("Number of bins wanted in this histogram. (either `numBins` or `binTicks` must be provided)"))
		.argument(arg -> arg
		 	.name("binTicks")
		 	.type(new GraphQLList(GraphQLFloat))
		 	.defaultValue(Arrays.asList(0.001, 0.002, 0.005, 0.01, 0.02, 0.05, 0.1, 0.2))
		 	.description("Bin ticks wanted in this histogram. (either `numBins` or `binTicks` must be provided)"))
		.argument(arg -> arg
			.name("cumulative")
			.type(GraphQLBoolean)
			.defaultValue(true)
			.description("Generate cumulative histogram data instead."))
		.argument(arg -> arg
			.name("aggregatesBy")
			.type(enumAggregationOption)
			.defaultValue(AggregationOption.Position)
			.description("Aggregation option."));
}
