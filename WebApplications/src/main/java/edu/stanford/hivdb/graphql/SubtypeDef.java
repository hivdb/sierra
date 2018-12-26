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

import edu.stanford.hivdb.subtype.Subtype;

import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.newFieldDefinition;

public class SubtypeDef {

	public static GraphQLEnumType oSubtype;

	public static GraphQLObjectType oBoundSubtype;

	static {
		GraphQLEnumType.Builder oSubtypeBuilder = GraphQLEnumType.newEnum()
			.name("Subtype")
			.description("SubtypeName");
		for (Subtype subtype : Subtype.values()) {
			oSubtypeBuilder = oSubtypeBuilder.value(subtype.toString(), subtype);
		}

		oSubtype = oSubtypeBuilder.build();

		oBoundSubtype = newObject()
			.name("BoundSubtype")
			.description("Subtype of certain sequence.")
			.field(newFieldDefinition()
				.type(oSubtype)
				.name("name")
				.description("Name of the subtype.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("distancePcnt")
				.description(
					"The distance percentage compares to the subtype of given " +
					"sequence. 0.0 means completely the same.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLString)
				.name("display")
				.description(
					"String of shown subtype and distance percentage. The shown " +
					"subtype can be in the form of unknown subtype of recombination " +
					"like \"B + C\"."))
			.build();
	}
}
