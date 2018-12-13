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
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.newFieldDefinition;

public class FrameShiftDef {

	public static GraphQLObjectType oFrameShift = newObject()
		.name("FrameShift")
		.description("Frame shift (NAs length < 3) found in aligned sequence.")
		.field(newFieldDefinition()
			.type(oGene)
			.name("gene")
			.description("Gene the frame shift belongs to.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLInt)
			.name("position")
			.description("Position of the frame shift.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isInsertion")
			.description("The frame shift is an insertion or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isDeletion")
			.description("The frame shift is a deletion or not.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLInt)
			.name("size")
			.description("DNA size of the frame shift.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("NAs")
			.description("Nucleic acid(s) of the frame shift.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("text")
			.description("Formatted readable text of this frame shift.")
			.build())
		.build();

}
