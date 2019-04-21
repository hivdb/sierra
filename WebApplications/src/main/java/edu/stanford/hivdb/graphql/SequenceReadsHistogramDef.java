package edu.stanford.hivdb.graphql;

import graphql.schema.*;
import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;

import edu.stanford.hivdb.ngs.SequenceReadsHistogram.AggregationOption;

public class SequenceReadsHistogramDef {

	public static GraphQLEnumType enumAggregationOption;
	public static GraphQLObjectType oSeqReadsHistogram;
	public static GraphQLObjectType oSeqReadsHistogramBin;
	
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
		
		oSeqReadsHistogram = newObject()
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
			.build();
	}
	
}
