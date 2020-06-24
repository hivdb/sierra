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

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Json;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.InvalidSyntaxError;

public class SierraSchemaTest {

	@Test
	public void testSequenceAnalysisDataFetcher() {
		Map<String, Object> arguments = new LinkedHashMap<>();
		List<Map<String, String>> sequences =
			FastaUtils.readStream(TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.PROBLEM_SEQUENCES))
			.stream()
			.map(seq -> {
				Map<String, String> seqMap = new LinkedHashMap<>();
				seqMap.put("header", seq.getHeader());
				seqMap.put("sequence", seq.getSequence());
				return seqMap;
			})
			.collect(Collectors.toList());
		arguments.put("sequences", sequences);
		GraphQL gql = GraphQL.newGraphQL(SierraSchema.schema).build();
		ExecutionInput input = ExecutionInput.newExecutionInput()
			.query(
				"query ($sequences: [UnalignedSequenceInput]) {\n" +
				//"  viewer {\n" +
				"    genes { name mutationTypes }\n" +
				"    sequenceAnalysis(sequences: $sequences) {\n" +
				"      inputSequence { header MD5 }\n" +
				"      validationResults { level message }\n" +
				"      availableGenes { name }\n" +
				"      mutations {\n" +
				"        gene { name }\n" +
				"        consensus\n" +
				"        position\n" +
				"        AAs\n" +
				"        triplet\n" +
				"        isInsertion\n" +
				"        isDeletion\n" +
				"        isIndel\n" +
				"        isAmbiguous\n" +
				"        isApobecMutation\n" +
				"        isApobecDRM\n" +
				"        hasStop\n" +
				"        isUnusual\n" +
				"        types\n" +
				"        primaryType\n" +
				"        comments { type text }\n" +
				"        shortText\n" +
				"        text\n" +
				"      }\n" +
				"      mixturePcnt\n" +
				"      genotypes {display, distancePcnt} \n" +
				"      subtypeText\n" +
				"      frameShifts {gene {name}, position, isInsertion, isDeletion, size, NAs, text}\n" +
				"    }\n" +
				//"  }\n" +
				"}")
			.variables(arguments)
			.build();

		ExecutionResult result = gql.execute(input);
		for (GraphQLError error : result.getErrors()) {
			if (error instanceof ExceptionWhileDataFetching) {
				((ExceptionWhileDataFetching) error).getException().printStackTrace();
			}
			else if (error instanceof InvalidSyntaxError) {
				System.err.println(((InvalidSyntaxError) error).getMessage());
				System.err.println(((InvalidSyntaxError) error).getLocations());
			}
		}
		assertTrue(
			"Found errors in query: " + Json.dumps(result.getErrors()),
			result.getErrors().isEmpty());
		System.out.println(Json.dumps(result.getData()));
	}

	@Test
	public void testSubtypeEnum() {
		Map<String, Object> arguments = new LinkedHashMap<>();
		List<Map<String, String>> sequences =
			FastaUtils.readStream(TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.PROBLEM_SEQUENCES))
			.stream()
			.map(seq -> {
				Map<String, String> seqMap = new LinkedHashMap<>();
				seqMap.put("header", seq.getHeader());
				seqMap.put("sequence", seq.getSequence());
				return seqMap;
			})
			.collect(Collectors.toList());
		arguments.put("sequences", sequences);
		GraphQL gql = GraphQL.newGraphQL(SierraSchema.schema).build();
		ExecutionInput input = ExecutionInput.newExecutionInput()
			.query(
				"query ($sequences: [UnalignedSequenceInput]) {\n" +
				"  sequenceAnalysis(sequences: $sequences) {\n" +
				"    subtypes {name} \n" +
				"  }\n" +
				"}")
			.variables(arguments)
			.build();
	
		ExecutionResult result = gql.execute(input);
		for (GraphQLError error : result.getErrors()) {
			if (error instanceof ExceptionWhileDataFetching) {
				((ExceptionWhileDataFetching) error).getException().printStackTrace();
			}
			else if (error instanceof InvalidSyntaxError) {
				System.err.println(((InvalidSyntaxError) error).getMessage());
				System.err.println(((InvalidSyntaxError) error).getLocations());
			}
		}
		assertTrue(
			"Found errors in query: " + Json.dumps(result.getErrors()),
			result.getErrors().isEmpty());
		System.out.println(Json.dumps(result.getData()));
	}
}
