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

import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.utilities.SimpleMemoizer;

import static edu.stanford.hivdb.graphql.GeneDef.oGene;
import static edu.stanford.hivdb.graphql.DrugClassDef.*;

public class ConditionalCommentDef {

	private static GraphQLEnumType.Builder newCommentType() {
		GraphQLEnumType.Builder commentTypeBuilder = GraphQLEnumType.newEnum()
			.name("CommentType")
			.description("Comment type.");
		for (CommentType cmtType : CommentType.values()) {
			commentTypeBuilder.value(cmtType.toString(), cmtType);
		}
		return commentTypeBuilder;
	}

	public static GraphQLEnumType oCommentType = newCommentType()
		.build();

	public static SimpleMemoizer<GraphQLObjectType> oBoundComment = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("BoundMutationComment")
			.description("Comment bound to a certain mutation object.")
			.field(field -> field
				.type(GraphQLString)
				.name("name")
				.description("Unique name of the comment.")
			)
			.field(field -> field
				.type(oGene.get(name))
				.name("gene")
				.description("Corresponding gene.")
			)
			.field(field -> field
				.type(oDrugClass.get(name))
				.name("drugClass")
				.description("Corresponding drug class.")
			)
			.field(field -> field
				.type(oCommentType)
				.name("type")
				.description("Mutation type of this comment.")
			)
			.field(field -> field
				.type(GraphQLString)
				.name("text")
				.description("Comment text.")
			)
			.field(field -> field
				.type(GraphQLString)
				.name("triggeredAAs")
				.deprecate("Use `boundMutation { displayAAs }` instead.")
				.description("Mutated amino acid(s) that triggered the comment.")
			)
			.field(field -> field
				.type(new GraphQLTypeReference("Mutation"))
				.name("boundMutation")
				.description("The mutation that bound to this comment.")
			)
			.field(field -> field
				.type(new GraphQLList(GraphQLString))
				.name("highlightText")
				.description("Text should be highlighted in the comment.")
			)
			.build()
		)
	);

	public static SimpleMemoizer<GraphQLObjectType> oCommentsByType = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("CommentsByType")
			.field(field -> field
				.type(oCommentType)
				.name("mutationType")
				.deprecate("Use `commentType` instead.")
				.description("Type of these comments.")
			)
			.field(field -> field
				.type(oCommentType)
				.name("commentType")
				.description("Type of these comments.")
			)
			.field(field -> field
				.type(new GraphQLList(oBoundComment.get(name)))
				.name("comments")
				.description("Comments belong to this type.")
			)
			.build()
		)
	);
}
