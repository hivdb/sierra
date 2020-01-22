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

import edu.stanford.hivdb.genotypes.GenotypeClassificationLevel;

public class SubtypeV2Def {

	private static GraphQLEnumType.Builder subtypeLevelBuilder() {
		GraphQLEnumType.Builder oSubtypeLevelBuilder = GraphQLEnumType.newEnum()
			.name("HIVClassificationLevel")
			.description("Classification level of genotypes: species, group or subtype.");
		for (GenotypeClassificationLevel level : GenotypeClassificationLevel.values()) {
			oSubtypeLevelBuilder = oSubtypeLevelBuilder.value(level.toString(), level);
		}
		return oSubtypeLevelBuilder;
	}

	public static GraphQLEnumType oSubtypeLevel = subtypeLevelBuilder().build();

	public static GraphQLObjectType oSubtype = newObject()
		.name("HIVSubtype")
		.field(
			field -> field
			.type(GraphQLString)
			.name("indexName")
			.description(
				"Short name of current species, group, or subtype. " +
				"Also used for indexing internally."
			)
		)
		.field(
			field -> field
			.type(GraphQLString)
			.name("displayName")
			.description(
				"Full name of current species, group, or subtype."
			)
		)
		.field(
			field -> field
			.type(oSubtypeLevel)
			.name("classificationLevel")
			.description("Classification level of the subtype.")
		)
		.build();

	public static GraphQLObjectType oBoundSubtypeV2 = newObject()
		.name("HIVBoundSubtype")
		.field(
			field -> field
			.type(GraphQLString)
			.name("display")
			.description("The display subtype(s) with the distance percent.")
		)
		.field(
			field -> field
			.type(GraphQLString)
			.name("displayWithoutDistance")
			.description("The display subtype(s) without the distance percent.")
		)
		.field(
			field -> field
			.type(oSubtype)
			.name("subtype")
			.description(
				"The original subtype found by comparison. The value of this " +
				"field is UNPROCESSED. You probably want to use field `display` " +
				"for the final result.")
		)
		.field(
			field -> field
			.type(oSubtype)
			.name("genotype")
			.deprecate("Use field `subtype` instead.")
		)
		.field(
			field -> field
			.type(new GraphQLList(oSubtype))
			.name("displaySubtypes")
			.description(
				"There are several rules applied for subtype displaying. " +
				"This field lists subtypes that were used in constructing " +
				"the final result in `display` and `displayWithoutDistance`.")
		)
		.field(
			field -> field
			.type(new GraphQLList(oSubtype))
			.name("displayGenotypes")
			.deprecate("Use field `displaySubtypes` instead.")
		)
		.field(
			field -> field
			.type(GraphQLInt)
			.name("firstNA")
			.description("The first compared/matched NA position in HXB2.")
		)
		.field(
			field -> field
			.type(GraphQLInt)
			.name("lastNA")
			.description("The last compared/matched NA position in HXB2.")
		)
		.field(
			field -> field
			.type(GraphQLFloat)
			.name("distance")
			.description(
				"The distance between bound sequence and coressponding reference. " +
				"Noted that 0 <= distance <= 1."
			)
		)
		.field(
			field -> field
			.type(GraphQLString)
			.name("distancePcnt")
			.description(
				"The distance between bound sequence and coressponding reference. " +
				"Noted that 0% <= distancePcnt <= 100%."
			)
		)
		.field(
			field -> field
			.type(GraphQLString)
			.name("referenceAccession")
			.description(
				"Accession number of the reference being compared."
			)
		)
		.field(
				field -> field
				.type(GraphQLString)
				.name("referenceCountry")
				.description(
					"Country where this reference sequence was collected."
				)
			)
		.field(
				field -> field
				.type(GraphQLInt)
				.name("referenceYear")
				.description(
					"Year this reference sequence was collected."
				)
			)
		.field(
			field -> field
			.type(new GraphQLList(GraphQLInt))
			.name("discordanceList")
			.description(
				"A full list of HXB2 positions that present with discordance."
			)
		)
		.build();
}
