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
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;

import static edu.stanford.hivdb.graphql.Exceptions.*;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.newFieldDefinition;

import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.stanford.hivdb.utilities.Sequence;

import static graphql.schema.GraphQLInputObjectType.newInputObject;

public class UnalignedSequenceDef {

	private static final int MAXIMUM_SEQUENCE_SIZE = 15000;

	public static List<Sequence> toSequenceList(List<Map<String, String>> input) {
		if (input == null) {
			return Collections.emptyList();
		}
		List<Sequence> seqs = input
			.stream()
			.filter(seqInput -> {
				return seqInput instanceof Map
					&& seqInput.get("header") != null
					&& seqInput.get("sequence") != null;
			})
			.map(seqInput -> new Sequence(
				seqInput.get("header"),
				seqInput.get("sequence")
			))
			.collect(Collectors.toList());

		// a simple validation
		for (Sequence seq : seqs) {
			if (seq.getLength() > MAXIMUM_SEQUENCE_SIZE) {
				throw new SequenceSizeLimitExceededException(String.format(
					"The length of sequence '%s' exceeded the maximum limitation. (%d > %d)",
					seq.getHeader(), seq.getLength(), MAXIMUM_SEQUENCE_SIZE
				));
			}
		}
		return seqs;
	}

	public static GraphQLObjectType oUnalignedSequence = newObject()
		.name("UnalignedSequenceOutput")
		.description("Unaligned sequence.")
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("header")
			.description("Name of the sequence.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("sequence")
			.description("The sequence itself as a string.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("MD5")
			.description("Hex MD5 value of the sequence.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("SHA512")
			.description("Hex SHA512 value of the sequence.")
			.build())
		.build();

	public static GraphQLInputObjectType iUnalignedSequence = newInputObject()
		.name("UnalignedSequenceInput")
		.description("Unaligned sequence Input Type.")
		.field(newInputObjectField()
			.type(GraphQLString)
			.name("header")
			.description("Name of the sequence.")
			.build())
		.field(newInputObjectField()
			.type(GraphQLString)
			.name("sequence")
			.description("The sequence itself as a string.")
			.build())
		.build();
}
