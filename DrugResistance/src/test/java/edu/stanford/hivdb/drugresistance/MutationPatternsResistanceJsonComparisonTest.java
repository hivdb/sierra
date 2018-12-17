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

package edu.stanford.hivdb.drugresistance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.Assert;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles.TestMutationPatterns;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments;
import edu.stanford.hivdb.drugresistance.database.MutationPatternFileReader;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Json;

/**
 * 1. Read each of the files containing lists of mutation files from src/test/resources/MutationPatternsFiles
 * 2. Run Resistance.determineResistanceForMutList
 * 3. Convert the resistance results to json
 * 4. Compare the results to the json files in src/test/resource/MutationPatternsExpectedResistanceResults
 */
public class MutationPatternsResistanceJsonComparisonTest {

	@Test
	public void testDetermineResistanceForMutList() throws IOException {

		for (TestMutationPatterns testMutationPatterns : TestMutationPatterns.values()) {
			final InputStream mutationPatternsInputStream =
					TestMutationPatternFiles.getTestMutationPatternsInputStream(testMutationPatterns);
			DrugClass drugClass = testMutationPatterns.getDrugClass();
			//System.out.println("In MutationPatternsResistanceToJson:" + testMutationPatterns.toString());
			final List<MutationSet> mutationLists =
					MutationPatternFileReader.readMutationListsForDrugClass(drugClass, mutationPatternsInputStream);

			final Map<MutationSet, GeneDR> allResistanceResults =
				GeneDRFast.parallelConstructor(drugClass.gene(), new HashSet<>(mutationLists));
			// Test the totalScore files
			// Calculated scores for each mutation list
			Map<String, Map<Drug, Integer>> mutPatternScoresCalculated = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResults = allResistanceResults.get(mutations);
				Map<Drug, Integer> totalScores = new HashMap<>();
				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					double totalScore = resistanceResults.getTotalDrugScore(drug);
					totalScores.put(drug, (int)totalScore);
				}
				mutPatternScoresCalculated.put(fmtMutationList,  totalScores);
			}

			// Retrieve pre-existing saved scores in json format
			Type mapTypeScores = new TypeToken<Map<String, Map<Drug, Integer>>>() {}.getType();
			final InputStream mutPatternDrugScoresJsonInputStream =
				MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
					"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".scores.json");
			BufferedReader brScores = new BufferedReader(new InputStreamReader(mutPatternDrugScoresJsonInputStream));
			Map<String, Map<Drug, Integer>> mutPatternScoresExpected = Json.loads(brScores, mapTypeScores);

			// Compare the results
			Assert.assertTrue(
				String.format(
					"%s Doesn't equal to %s.",
					mutPatternScoresExpected.size(),
					mutPatternScoresCalculated.size()
				),
				mutPatternScoresExpected.size() == mutPatternScoresCalculated.size());
			for (String mutList : mutPatternScoresExpected.keySet()) {
				for (Drug drug : mutPatternScoresExpected.get(mutList).keySet()) {
					int expectedScore = mutPatternScoresExpected.get(mutList).get(drug);
					int calculatedScore = mutPatternScoresCalculated.get(mutList).get(drug);
					Assert.assertTrue(
						String.format(
							"Error in %s. %s doesn't equal to %s",
							testMutationPatterns, expectedScore, calculatedScore),
						expectedScore == calculatedScore);
					//System.out.println(mutList + ":" + drug + " :exp:" + expectedScore + " :calc" + calculatedScore);
				}
			}

			// Test the levels files
			// Calculated levels for each mutation list
			Map<String, Map<Drug, Integer>> mutPatternLevelsCalculated = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResults = allResistanceResults.get(mutations);
				Map<Drug, Integer> levels = new HashMap<>();
				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					int level = resistanceResults.getDrugLevel(drug);
					levels.put(drug, level);
				}
				mutPatternLevelsCalculated.put(fmtMutationList, levels);
			}

			// Retrieve pre-existing saved levels in json format
			final InputStream mutPatternDrugLevelsJsonInputStream =
				MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
					"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".levels.json");
			BufferedReader brLevels = new BufferedReader(new InputStreamReader(mutPatternDrugLevelsJsonInputStream));
			Map<String, Map<Drug, Integer>> mutPatternLevelsExpected = Json.loads(brLevels, mapTypeScores);

			// Compare the results
			Assert.assertTrue(mutPatternLevelsExpected.size() == mutPatternLevelsCalculated.size());
			for (String mutList : mutPatternLevelsExpected.keySet()) {
				for (Drug drug : mutPatternLevelsExpected.get(mutList).keySet()) {
					int expectedLevel = mutPatternLevelsExpected.get(mutList).get(drug);
					int calculatedLevel = mutPatternLevelsCalculated.get(mutList).get(drug);
					Assert.assertTrue(expectedLevel == calculatedLevel);
					//System.out.println(mutList + ":" + drug + " :exp:" + expectedLevel + " :calc" + calculatedLevel);
				}
			}


			// Test the mutTypes files
			// Calculated levels for each mutation list
			Map<String, Map<MutType, String>> mutPatternMutTypesCalculated = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResults = allResistanceResults.get(mutations);
				Map<MutType, String> mutTypes = new HashMap<>();
				Map<MutType, MutationSet> mutTypeMutLists = resistanceResults.groupMutationsByTypes();
				for (MutType mutType : mutTypeMutLists.keySet()) {
					MutationSet mutList = mutTypeMutLists.get(mutType);
					String mutListString = mutList.join();
					mutTypes.put(mutType,  mutListString);
				}
				mutPatternMutTypesCalculated.put(fmtMutationList, mutTypes);
			}

			// Retrieve pre-existing saved mutTypes in json format
			Type mapTypeMutTypes = new TypeToken<Map<String, Map<MutType, String>>>() {}.getType();
			final InputStream mutPatternMutTypesJsonInputStream =
					MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
						"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".mutTypes.json");
			BufferedReader brMutTypes = new BufferedReader(new InputStreamReader(mutPatternMutTypesJsonInputStream));
			Map<String, Map<MutType, String>> mutPatternMutTypesExpected = Json.loads(brMutTypes, mapTypeMutTypes);


			// Compare the results
			Assert.assertTrue(mutPatternMutTypesExpected.size() == mutPatternMutTypesCalculated.size());
			for (String mutList : mutPatternMutTypesExpected.keySet()) {
				for (MutType mutType : mutPatternMutTypesExpected.get(mutList).keySet()) {
					String expectedMutList = mutPatternMutTypesExpected.get(mutList).get(mutType);
					String calculatedMutList = mutPatternMutTypesCalculated.get(mutList).get(mutType);
					Assert.assertEquals(expectedMutList, calculatedMutList);
					//System.out.println(mutList + ":" + mutType + " exp:" + expectedMutList +
					//		                                     " calc:" + calculatedMutList);
				}
			}


			// Test the comments files
			// Calculated levels for each mutation list
			Map<String, Map<String, String>> mutPatternCommentsCalculated = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR geneDR = allResistanceResults.get(mutations);

				Map<String, String> mutComments = ConditionalComments
					.getComments(geneDR).stream()
					.collect(Collectors.toMap(
						bc -> bc.getName(),
						bc -> bc.getText()));

				mutPatternCommentsCalculated.put(fmtMutationList, mutComments);
			}

			// Retrieve pre-existing saved comments
			Type mapTypeMutComments = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
			final InputStream mutPatternCommentsJsonInputStream =
					MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
						"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".comments.json");
			BufferedReader brComments = new BufferedReader(new InputStreamReader(mutPatternCommentsJsonInputStream));
			Map<String, Map<String, String>> mutPatternCommentsExpected = Json.loads(brComments, mapTypeMutComments);

			// Compare the results
			Assert.assertTrue(mutPatternCommentsExpected.size() == mutPatternCommentsCalculated.size());
			for (String mutList : mutPatternCommentsExpected.keySet()) {
				for (String mutString : mutPatternCommentsExpected.get(mutList).keySet()) {
					String expectedComment = mutPatternCommentsExpected.get(mutList).get(mutString);
					String calculatedComment = mutPatternCommentsCalculated.get(mutList).get(mutString);
					//System.out.println("  ExpectedComment:" + expectedComment);
					//System.out.println("CalculatedComment:" + calculatedComment);

					try {
						// Fix this AssertionError by re-run MutationPatternsResistanceToJson.java and clean Eclipse
						Assert.assertTrue(expectedComment.equals(calculatedComment));
					} catch(AssertionError e) {
						System.out.println("     MutationList: " + mutList);
						System.out.println("  ExpectedComment: " + expectedComment);
						System.out.println("CalculatedComment: " + calculatedComment);
						throw e;
					}
					//System.out.println(mutList + ":\nexp:" + expectedComment + "\ncal:" + calculatedComment);
				}
			}
		}
	}
}
