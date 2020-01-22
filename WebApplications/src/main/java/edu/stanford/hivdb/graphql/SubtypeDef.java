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

import edu.stanford.hivdb.genotypes.Genotype;
import edu.stanford.hivdb.hivfacts.HIV;

public class SubtypeDef {


	private static GraphQLEnumType.Builder createSubtypeBuilder() {
		HIV hiv = HIV.getInstance();
		GraphQLEnumType.Builder oSubtypeBuilder = GraphQLEnumType.newEnum()
			.name("Subtype")
			.description("SubtypeName");
		for (Genotype<HIV> subtype : hiv.getGenotypes()) {
			oSubtypeBuilder = oSubtypeBuilder.value(subtype.getIndexName(), subtype);
		}
		return oSubtypeBuilder;
	}

	public static GraphQLEnumType oSubtype = createSubtypeBuilder().build();

	public static GraphQLObjectType oBoundSubtype = newObject()
		.name("BoundSubtype")
		.description("Subtype of certain sequence.")
		.field(field -> field
			.type(oSubtype)
			.name("name")
			.description("Name of the subtype."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("distancePcnt")
			.description(
				"The distance percentage compares to the subtype of given " +
				"sequence. 0.0 means completely the same."))
		.field(field -> field
			.type(GraphQLString)
			.name("display")
			.description(
				"String of shown subtype and distance percentage. The shown " +
				"subtype can be in the form of unknown subtype of recombination " +
				"like \"B + C\"."))
		.build();
}