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

public class PrettyPairwiseDef {

	public static GraphQLObjectType oPrettyPairwise = newObject()
		.name("PrettyPairwise")
		.description("Formatted pairwise result of the aligned sequence.")
		.field(field -> field
			.type(new GraphQLList(GraphQLString))
			.name("positionLine")
			.description("Formmated numeric position line."))
		.field(field -> field
			.type(new GraphQLList(GraphQLString))
			.name("refAALine")
			.description("Formmated reference protein sequence line."))
		.field(field -> field
			.type(new GraphQLList(GraphQLString))
			.name("alignedNAsLine")
			.description("Formmated aligned DNA sequence line."))
		.field(field -> field
			.type(new GraphQLList(GraphQLString))
			.name("mutationLine")
			.description("Formmated amino acid mutation line."))
		.build();

}
