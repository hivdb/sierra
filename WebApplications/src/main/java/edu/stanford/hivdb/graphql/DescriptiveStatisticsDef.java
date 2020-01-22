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
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DescriptiveStatisticsDef {


	private static DataFetcher<Double> percentileDataFetcher = env -> {
		DescriptiveStatistics descStats = env.getSource();
		double p = env.getArgument("p");
		Double result = descStats.getPercentile(p);
		if (Double.isNaN(result)) {
			return null;
		}
		return result;
	};

	public static GraphQLCodeRegistry descriptiveStatisticsCodeRegistry = newCodeRegistry()
		.dataFetcher(
			coordinates("DescriptiveStatistics", "percentile"),
			percentileDataFetcher
		)
		.build();
	
	public static GraphQLObjectType oDescriptiveStatistics = newObject()
		.name("DescriptiveStatistics")
		.description("Descriptive statistics for a list of values.")
		.field(field -> field
			.name("mean")
			.type(GraphQLFloat)
			.description("The arithmetic mean of the available values.")
		)
		.field(field -> field
			.name("standardDeviation")
			.type(GraphQLFloat)
			.description("The standard deviation of the available values.")
		)
		.field(field -> field
			.name("min")
			.type(GraphQLFloat)
			.description("The minimum of the available values.")
		)
		.field(field -> field
			.name("max")
			.type(GraphQLFloat)
			.description("The maximum of the available values.")
		)
		.field(field -> field
			.name("n")
			.type(GraphQLFloat)
			.description("The number of the available values.")
		)
		.field(field -> field
			.name("sum")
			.type(GraphQLFloat)
			.description("The sum of the available values.")
		)
		.field(field -> field
			.name("values")
			.type(new GraphQLList(GraphQLFloat))
			.description("The set of available values.")
		)
		.field(field -> field
			.name("percentile")
			.type(GraphQLFloat)
			.argument(arg -> arg
				.name("p")
				.type(GraphQLFloat)
				.description("The requested percentile (scaled from 0 - 100)")
			)
			.description("An estimate for the pth percentile of the stored values.")
		)
		.build();

}
