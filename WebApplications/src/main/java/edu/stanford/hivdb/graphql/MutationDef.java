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

import java.util.List;

import edu.stanford.hivdb.drugresistance.database.ConditionalComments;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments.BoundComment;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.Mutation;

import static edu.stanford.hivdb.graphql.GeneDef.oGene;
import static edu.stanford.hivdb.graphql.ConditionalCommentDef.oBoundComment;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class MutationDef {

	private static GraphQLEnumType.Builder newMutationType() {
		GraphQLEnumType.Builder mutationTypeBuilder = GraphQLEnumType.newEnum()
			.name("MutationType")
			.description("Mutation type.");
		for (MutType mutType : MutType.values()) {
			mutationTypeBuilder.value(mutType.toString(), mutType);
		}
		return mutationTypeBuilder;
	}

	public static GraphQLEnumType oMutationType = newMutationType()
		.build();

	private static DataFetcher<List<BoundComment>> commentsDataFetcher = new DataFetcher<List<BoundComment>>() {
		@Override
		public List<BoundComment> get(DataFetchingEnvironment environment) {
			Mutation mut = (Mutation) environment.getSource();
			return ConditionalComments.getComments(mut);
		}
	};
	
	public static GraphQLObjectType oAAPercent = newObject()
		.name("AAPercent")
		.field(newFieldDefinition()
			.type(GraphQLChar)
			.name("AA")
			.description("A single amino acid.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLFloat)
			.name("percent")
			.description("Percent (max: 100) of this amino acid in the mutation.")
			.build())
		.build();
	
	public static GraphQLObjectType oMutation = newObject()
		.name("Mutation")
		.field(newFieldDefinition()
			.type(oGene)
			.name("gene")
			.description("Mutation gene.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("reference")
			.description(
				"(Subtype B) Amino acid reference at this gene sequence position.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("consensus")
			.dataFetcher(env -> ((Mutation) env.getSource()).getReference())
			.deprecate("Use field `reference` instead.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLInt)
			.name("position")
			.description("Position of the mutation.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("displayAAs")
			.description(
				"The mutated AA(s) with possibly inserted AA(s).")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("AAs")
			.description(
				"The mutated AA(s) with possibly inserted AA(s). Highly ambiguous mixture is not replaced to X.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(GraphQLString))
			.name("displayAAChars")
			.description("A list of AAs.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(GraphQLString))
			.name("AAChars")
			.description("A list of AAs. Highly ambiguous mixture is not replaced to X.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oAAPercent))
			.name("AAPercents")
			.description("A list of AAs with their percents (only available for CodFISH input).")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("triplet")
			.description(
				"The mutated codon when the mutation is extracting from " +
				"an aligned sequence.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("insertedNAs")
			.description(
				"The inserted codon(s) when the mutation is extracting from " +
				"an aligned sequence.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isInsertion")
			.description("The mutation is an insertion or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isDeletion")
			.description("The mutation is a deletion or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isIndel")
			.description("The mutation is an insertion/deletion, or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isAmbiguous")
			.description("The mutation is a highly ambiguous mutation or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isApobecMutation")
			.description(
				"The mutation is a signature APOBEC-mediated G-to-A " +
				"hypermutation or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isApobecDRM")
			.description(
				"The mutation is a drug resistance mutation (DRM) might " +
				"be caused by APOBEC-mediated G-to-A hypermutation or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isUnsequenced")
			.description(
				"If the mutation is from unsequenced region.")
			.build())
		.field(newFieldDefinition()
				.type(GraphQLBoolean)
				.name("isDRM")
				.description(
					"If the mutation is a drug resistance mutation (DRM) or not.")
				.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isDRM")
			.description(
				"If the mutation is a drug resistance mutation (DRM) or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("hasStop")
			.description(
				"The mutation contains stop codon or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isUnusual")
			.description(
				"The mutation is a low prevalence (unusual) mutation or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isSDRM")
			.description(
				"The mutation is a Surveillance Drug Resistance Mutation (SDRM) or not.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oMutationType))
			.name("types")
			.description(
				"Ordered list of mutation type(s). List size can be " +
				"larger than 1 when the mutation is a mixture.")
			.build())
		.field(newFieldDefinition()
			.type(oMutationType)
			.name("primaryType")
			.description("Primary type of the mutation.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oBoundComment))
			.name("comments")
			.description("Mutation comments.")
			.dataFetcher(commentsDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("text")
			.description("Formatted text of the mutation (without gene).")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("shortText")
			.description(
				"Formatted short text of the mutation (without gene).")
			.build())
		.build();

}
