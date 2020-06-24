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
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Virus;

public class SubtypeDef {


	public static SimpleMemoizer<GraphQLEnumType> oSubtype = new SimpleMemoizer<>(
		name -> {
			GraphQLEnumType.Builder oSubtypeBuilder = GraphQLEnumType.newEnum()
				.name("Subtype")
				.description("SubtypeName");
			for (Genotype<?> subtype : Virus.getInstance(name).getGenotypes()) {
				oSubtypeBuilder = oSubtypeBuilder.value(subtype.getIndexName(), subtype.getIndexName());
			}
			return oSubtypeBuilder.build();
		}
	);

	public static SimpleMemoizer<GraphQLObjectType> oBoundSubtype = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("BoundSubtype")
			.description("Subtype of certain sequence.")
			.field(field -> field
				.type(oSubtype.get(name))
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
			.build()
		)
	);
}