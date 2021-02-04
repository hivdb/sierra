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
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLObjectType.newObject;

import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Virus;

import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import static edu.stanford.hivdb.graphql.PrettyPairwiseDef.*;
import static edu.stanford.hivdb.graphql.MutationSetDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.FrameShiftDef.*;

public class AlignedGeneSequenceDef {


	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeAlignedGeneSequenceCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("AlignedGeneSequence", "mutations"),
				new MutationSetDataFetcher<>(virusIns, "mutations")
			)
			.dataFetcher(
				coordinates("AlignedGeneSequence", "adjustedAlignedAAs"),
				adjustedAlignedAAsFetcher
			)
			.dataFetcher(
				coordinates("AlignedGeneSequence", "adjustedAlignedNAs"),
				adjustedAlignedNAsFetcher
			)
			.build()
		);
	}

	public static DataFetcher<String> adjustedAlignedAAsFetcher = env -> {
		AlignedGeneSeq<?> seq = env.getSource();
		String strain = env.getArgument("targetStrain");
		if (strain == null) {
			return seq.getAdjustedAlignedAAs();
		} else {
			return seq.getAdjustedAlignedAAs(strain);
		}
	};

	public static DataFetcher<String> adjustedAlignedNAsFetcher = env -> {
		AlignedGeneSeq<?> seq = env.getSource();
		String strain = env.getArgument("targetStrain");
		if (strain == null) {
			return seq.getAdjustedAlignedNAs();
		} else {
			return seq.getAdjustedAlignedNAs(strain);
		}
	};

	public static SimpleMemoizer<GraphQLObjectType> oAlignedGeneSequence = new SimpleMemoizer<>(
		virusName -> (
			newObject()
			.name("AlignedGeneSequence")
			.field(field -> field
				.type(oGene.get(virusName))
				.name("gene")
				.description("Sequence gene and the reference sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("firstAA")
				.description(
					"The first aligned position (start from 1) " +
					"in protein relative to the reference sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("lastAA")
				.description(
					"The last aligned position (start from 1) " +
					"in protein relative to the reference sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("firstNA")
				.description(
					"The first aligned position (start from 1) " +
					"in DNA relative to the input sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("lastNA")
				.description(
					"The last aligned position (start from 1) " +
					"in DNA relative to the input sequence.")
			)
			.field(field -> field
				.type(GraphQLFloat)
				.name("matchPcnt")
				.description(
					"The match percentage of input sequence aligned " +
					"to the reference sequence.")
			)
			.field(field -> field
				.type(GraphQLInt)
				.name("size")
				.description(
					"The amino acid size of this sequence without unsequenced region (Ns).")
			)
			.field(field -> field
				.type(oPrettyPairwise)
				.name("prettyPairwise")
				.description("Formatted pairwise output of the aligned sequence.")
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
			.field(field -> field
				.type(GraphQLString)
				.name("adjustedAlignedNAs")
				.description("(HXB2 numbering) adjusted aligned DNA sequence without insertions and insertion gaps.")
				.argument(newArgument()
					.name("targetStrain")
					.type(GraphQLString)
					.description("Sequences alignment target strain.")
				)
			)
			.field(field -> field
				.type(GraphQLString)
				.name("adjustedAlignedAAs")
				.description(
					"(HXB2 numbering) adjusted aligned protein sequence without insertions and insertion gaps. " +
					"Mixtures are represented as \"X\"."
				)
				.argument(newArgument()
					.name("targetStrain")
					.type(GraphQLString)
					.description("Sequences alignment target strain.")
				)
			)
			.field(field -> newMutationSet(virusName, field, "mutations")
				.description("All mutations found in the aligned sequence."))
			.field(field -> field
				.type(new GraphQLList(oFrameShift.get(virusName)))
				.name("frameShifts")
				.description("All frame shifts found in the aligned sequence.")
			)
			.build()
		)
	);

}
