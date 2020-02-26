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

package edu.stanford.hivdb.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.stanford.hivdb.graphql.SierraSchema;
import edu.stanford.hivdb.utilities.Json;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;

@Path("/hiv2/graphql")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HIV2GraphQLService {

	private final GraphQL graphql;

	public HIV2GraphQLService() {
		graphql = GraphQL.newGraphQL(SierraSchema.hiv2Schema).build();
	}

	private static class GraphQLRequest {
		public String query;
		public Map<String, Object> variables;
	}

	@POST
	public Response execute(String x) {
		GraphQLRequest request = Json.loads(x, GraphQLRequest.class);
		String query = request.query;
		Object context = null;
		Map<String, Object> variables = request.variables;
		if (query == null) { query = ""; }
		if (variables == null) {variables = Collections.emptyMap(); }
		ExecutionInput input = (
			ExecutionInput
			.newExecutionInput()
			.query(query).context(context).variables(variables)
			.build()
		);
		ExecutionResult result = graphql.execute(input);
		List<Map<String, Object>> errors = handleErrors(result);
		Map<String, Object> output = new LinkedHashMap<>();
		Status status = Status.OK;
		if (!errors.isEmpty()) {
			// react-relay rejected when
			// key "errors" presented even it's empty
			output.put("errors", errors);
			status = Status.BAD_REQUEST;
		}
		output.put("data", result.getData());
		return Response
			.status(status)
			.type(MediaType.APPLICATION_JSON)
			.entity(Json.dumps(output))
			.build();
	}

	private List<Map<String, Object>> handleErrors(ExecutionResult result) {
		List<Map<String, Object>> errors = new ArrayList<>();
		for (GraphQLError error : result.getErrors()) {
			Map<String, Object> errorMap = new LinkedHashMap<>();
			errorMap.put("type", error.getErrorType());
			errorMap.put("message", error.getMessage());
			errorMap.put("locations", error.getLocations());
			if (error instanceof ExceptionWhileDataFetching) {
				Throwable innerExc = ((ExceptionWhileDataFetching) error).getException();
				List<Map<String, Object>> details = new ArrayList<>();
				do {
					Map<String, Object> errDetail = new LinkedHashMap<>();
					errDetail.put("exception", innerExc.toString());
					errDetail.put("message", innerExc.getMessage());
					errDetail.put("stackTrace", innerExc.getStackTrace());
					details.add(errDetail);
					innerExc = innerExc.getCause();
				} while (innerExc != null);
				errorMap.put("details", details);
				/*if (!(innerExc instanceof InvalidMutationStringException)) {
					throw new RuntimeException("Unhandled exception", innerExc);
				}*/
			}
			errors.add(errorMap);
			/*else if (error instanceof InvalidSyntaxError) {
				Map<String, Object> errorMap = new LinkedHashMap<>();
				errorMap.put("type", "InvalidSyntaxError");
				errorMap.put("message", ((InvalidSyntaxError) error).getMessage());
				errorMap.put("locations", ((InvalidSyntaxError) error).getLocations());
				errors.add(errorMap);
			}*/
			// errors.add(error);
		}
		return errors;
	}
}
