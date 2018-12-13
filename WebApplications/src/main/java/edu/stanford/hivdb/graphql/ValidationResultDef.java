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

import edu.stanford.hivdb.alignment.ValidationLevel;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class ValidationResultDef {

	private static GraphQLEnumType newValidationLevel() {
		GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
			.name("ValidationLevel")
			.description("Level for validation result.");
		for (ValidationLevel vl : ValidationLevel.values()) {
			builder.value(vl.toString(), vl);
		}
		return builder.build();
	}

	public static GraphQLEnumType oValidationLevel = newValidationLevel();

	public static GraphQLObjectType oValidationResult = newObject()
		.name("ValidationResult")
		.description("Validation result for sequence or mutation list.")
		.field(newFieldDefinition()
			.name("level")
			.type(oValidationLevel)
			.description("The level of this validation result.")
			.build())
		.field(newFieldDefinition()
			.name("message")
			.type(GraphQLString)
			.description("Description of this validation result.")
			.build())
		.build();
}
