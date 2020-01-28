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

import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import static edu.stanford.hivdb.graphql.StrainDef.oStrain;

public class DrugResistanceAlgorithmDef {


	private static GraphQLEnumType newASIAlgorithmEnum() {
		GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
			.name("ASIAlgorithm")
			.description("ASI algorithm.");
		HIV hiv = HIV.getInstance();
		for (DrugResistanceAlgorithm<HIV> alg : hiv.getDrugResistAlgorithms(hiv.getStrain("HIV1"))) {
			builder.value(alg.getEnumCompatName(), alg.getName());
		}
		return builder.build();
	}

	public static GraphQLEnumType oASIAlgorithm = newASIAlgorithmEnum();

	public static GraphQLObjectType oDrugResistanceAlgorithm = newObject()
		.name("DrugResistanceAlgorithm")
		.description("A drug resistance algorithm.")
		.field(field -> field
			.type(GraphQLString)
			.name("text")
			.description("get key name of this algorithm."))
		.field(field -> field
			.type(GraphQLString)
			.name("display")
			.description("algorithm family and version for display."))
		.field(field -> field
			.type(GraphQLString)
			.name("family")
			.description("algorithm family."))
		.field(field -> field
			.type(GraphQLString)
			.name("version")
			.description("algorithm version."))
		.field(field -> field
			.type(oStrain)
			.name("strain")
			.description("algorithm target strain."))
		.field(field -> field
			.type(GraphQLString)
			.name("publishDate")
			.description("Publish date of this version."))
		.build();

	public static DataFetcher<DrugResistanceAlgorithm<HIV>> currentHIVDBVersionFetcher = env -> {
		HIV hiv = HIV.getInstance();
		DrugResistanceAlgorithm<HIV> latestAlg = hiv.getLatestDrugResistAlgorithm("HIVDB");
		return latestAlg;
	};

}
