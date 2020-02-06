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

import graphql.schema.*;
import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.stream.Collectors;

import edu.stanford.hivdb.seqreads.GeneSequenceReads;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Virus;

// import edu.stanford.hivdb.ngs.GeneSequenceReads;

import static edu.stanford.hivdb.graphql.MutationSetDef.*;
// import static edu.stanford.hivdb.graphql.MutationStatsDef.oMutationStats;
import static edu.stanford.hivdb.graphql.PositionCodonReadsDef.*;
import static edu.stanford.hivdb.graphql.SequenceReadsHistogramDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.DescriptiveStatisticsDef.*;

public class GeneSequenceReadsDef {

	private static DataFetcher<String> internalJsonAllPositionCodonReadsDataFetcher = env -> {
		GeneSequenceReads<?> geneSeqReads = env.getSource();
		return Json.dumpsUgly(
			geneSeqReads
			.getAllPositionCodonReads()
			.stream()
			.map(pcr -> pcr.extMap(
				(Boolean) env.getArgument("mutationOnly"),
				(double) env.getArgument("maxProportion"),
				(double) env.getArgument("minProportion")
			))
			.collect(Collectors.toList()));
	};

	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeGeneSequenceReadsCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("GeneSequenceReads", "internalJsonAllPositionCodonReads"),
				internalJsonAllPositionCodonReadsDataFetcher
			)
			.dataFetcher(
				coordinates("GeneSequenceReads", "histogram"),
				seqReadsHistogramDataFetcher
			)
			.dataFetcher(
				coordinates("GeneSequenceReads", "mutations"),
				new MutationSetDataFetcher<>(virusIns, "mutations")
			)
			.build()
		);
	}

	public static SimpleMemoizer<GraphQLObjectType> oGeneSequenceReads = new SimpleMemoizer<>(
		virusName -> (
			newObject()
			.name("GeneSequenceReads")
			.field(field -> field
				.type(oGene.get(virusName))
				.name("gene")
				.description("Sequence gene and the reference sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("firstAA")
				.description(
					"The first sequenced position (start from 1) " +
					"in protein relative to the reference sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("lastAA")
				.description(
					"The last sequenced position (start from 1) " +
					"in protein relative to the reference sequence.")
			)
			.field(field -> field
				.type(new GraphQLList(oPositionCodonReads.get(virusName)))
				.name("allPositionCodonReads")
				.description(
					"Position codon reads in this gene sequence.")
			)
			.field(field -> codonReadsArgs.apply(field)
				.type(GraphQLString)
				.name("internalJsonAllPositionCodonReads")
				.description(
					"Position codon reads in this gene sequence (json formated)."))
			// .field(field -> field
			// 	.type(GraphQLFloat)
			// 	.name("matchPcnt")
			// 	.description(
			// 		"The match percentage of input sequence aligned " +
			// 		"to the reference sequence.")
			// )
			.field(field -> field
				.type(GraphQLInt)
				.name("size")
				.description(
					"The amino acid size of this sequence including unsequenced region.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("numPositions")
				.description(
					"The sequenced positions of this sequence.")
			)
			.field(field -> field
				.name("readDepthStats")
				.type(oDescriptiveStatistics)
				.description("Descriptive statistics of all read depth.")
			)
			.field(field -> field
				.type(GraphQLString)
				.name("alignedNAs")
				.description("Aligned DNA sequence without insertions and insertion gaps.")
			)
			.field(field -> field
				.type(GraphQLString)
				.name("alignedAAs")
				.description(
					"Aligned protein sequence without insertions and insertion gaps. " +
					"Mixtures are represented as \"X\"."
				)
			)
			.field(field -> newMutationSet(virusName, field, "mutations")
				.description("All mutations found in the aligned sequence.")
			)
			.field(oSeqReadsHistogramBuilder)
			.build()
		)
	);

}
