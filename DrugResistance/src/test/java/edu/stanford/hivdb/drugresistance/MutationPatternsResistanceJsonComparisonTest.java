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

			final Map<MutationSet, GeneDR> allResistanceResultsAsi =
				GeneDRAsi.parallelConstructor(drugClass.gene(), new HashSet<>(mutationLists));

			final Map<MutationSet, GeneDR> allResistanceResultsFast =
				GeneDRFast.parallelConstructor(drugClass.gene(), new HashSet<>(mutationLists));

			// Test the totalScore files
			// Calculated scores for each mutation list
			Map<String, Map<Drug, Integer>> mutPatternScoresCalculatedAsi = new HashMap<>();
			Map<String, Map<Drug, Integer>> mutPatternScoresCalculatedFast = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResultsAsi = allResistanceResultsAsi.get(mutations);
				GeneDR resistanceResultsFast = allResistanceResultsFast.get(mutations);

				Map<Drug, Integer> totalScoresAsi = new HashMap<>();
				Map<Drug, Integer> totalScoresFast = new HashMap<>();

				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					double totalScoreAsi = resistanceResultsAsi.getTotalDrugScore(drug);
					double totalScoreFast = resistanceResultsFast.getTotalDrugScore(drug);
					totalScoresAsi.put(drug, (int)totalScoreAsi);
					totalScoresFast.put(drug, (int)totalScoreFast);
				}
				mutPatternScoresCalculatedAsi.put(fmtMutationList,  totalScoresAsi);
				mutPatternScoresCalculatedFast.put(fmtMutationList,  totalScoresFast);
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
					"%s Doesn't equal to %s or %s.",
					mutPatternScoresExpected.size(),
					mutPatternScoresCalculatedAsi.size(),
					mutPatternScoresCalculatedFast.size()
				),
				mutPatternScoresExpected.size() == mutPatternScoresCalculatedAsi.size() &&
				mutPatternScoresExpected.size() == mutPatternScoresCalculatedFast.size()
				);
			for (String mutList : mutPatternScoresExpected.keySet()) {
				for (Drug drug : mutPatternScoresExpected.get(mutList).keySet()) {
					int expectedScore = mutPatternScoresExpected.get(mutList).get(drug);
					int calculatedScoreAsi = mutPatternScoresCalculatedAsi.get(mutList).get(drug);
					int calculatedScoreFast = mutPatternScoresCalculatedFast.get(mutList).get(drug);
					Assert.assertTrue(
						String.format(
							"Error in %s. %s doesn't equal to %s or %s",
							testMutationPatterns, expectedScore, calculatedScoreAsi, calculatedScoreFast),
						expectedScore == calculatedScoreAsi &&
						expectedScore == calculatedScoreFast);
					//System.out.println(mutList + ":" + drug + " :exp:" + expectedScore + " :calc" + calculatedScore);
				}
			}

			// Test the levels files
			// Calculated levels for each mutation list
			Map<String, Map<Drug, Integer>> mutPatternLevelsCalculatedAsi = new HashMap<>();
			Map<String, Map<Drug, Integer>> mutPatternLevelsCalculatedFast = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResultsAsi = allResistanceResultsAsi.get(mutations);
				GeneDR resistanceResultsFast = allResistanceResultsFast.get(mutations);
				Map<Drug, Integer> levelsAsi = new HashMap<>();
				Map<Drug, Integer> levelsFast = new HashMap<>();
				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					int levelAsi = resistanceResultsAsi.getDrugLevel(drug);
					int levelFast = resistanceResultsFast.getDrugLevel(drug);
					levelsAsi.put(drug, levelAsi);
					levelsFast.put(drug, levelFast);
				}
				mutPatternLevelsCalculatedAsi.put(fmtMutationList, levelsAsi);
				mutPatternLevelsCalculatedFast.put(fmtMutationList, levelsFast);
			}

			// Retrieve pre-existing saved levels in json format
			final InputStream mutPatternDrugLevelsJsonInputStream =
				MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
					"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".levels.json");
			BufferedReader brLevels = new BufferedReader(new InputStreamReader(mutPatternDrugLevelsJsonInputStream));
			Map<String, Map<Drug, Integer>> mutPatternLevelsExpected = Json.loads(brLevels, mapTypeScores);

			// Compare the results
			Assert.assertTrue(
				mutPatternLevelsExpected.size() == mutPatternLevelsCalculatedAsi.size() &&
				mutPatternLevelsExpected.size() == mutPatternLevelsCalculatedFast.size()
			);
			for (String mutList : mutPatternLevelsExpected.keySet()) {
				for (Drug drug : mutPatternLevelsExpected.get(mutList).keySet()) {
					int expectedLevel = mutPatternLevelsExpected.get(mutList).get(drug);
					int calculatedLevelAsi = mutPatternLevelsCalculatedAsi.get(mutList).get(drug);
					int calculatedLevelFast = mutPatternLevelsCalculatedFast.get(mutList).get(drug);
					Assert.assertTrue(
						expectedLevel == calculatedLevelAsi &&
						expectedLevel == calculatedLevelFast
					);
					//System.out.println(mutList + ":" + drug + " :exp:" + expectedLevel + " :calc" + calculatedLevel);
				}
			}


			// Test the mutTypes files
			// Calculated levels for each mutation list
			Map<String, Map<MutType, String>> mutPatternMutTypesCalculatedAsi = new HashMap<>();
			Map<String, Map<MutType, String>> mutPatternMutTypesCalculatedFast = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResultsAsi = allResistanceResultsAsi.get(mutations);
				GeneDR resistanceResultsFast = allResistanceResultsFast.get(mutations);
				Map<MutType, String> mutTypesAsi = new HashMap<>();
				Map<MutType, String> mutTypesFast = new HashMap<>();
				Map<MutType, MutationSet> mutTypeMutListsAsi = resistanceResultsAsi.groupMutationsByTypes();
				Map<MutType, MutationSet> mutTypeMutListsFast = resistanceResultsFast.groupMutationsByTypes();
				for (MutType mutType : mutTypeMutListsAsi.keySet()) {
					MutationSet mutListAsi = mutTypeMutListsAsi.get(mutType);
					String mutListStringAsi = mutListAsi.join();
					mutTypesAsi.put(mutType,  mutListStringAsi);
				}
				for (MutType mutType : mutTypeMutListsFast.keySet()) {
					MutationSet mutListFast = mutTypeMutListsFast.get(mutType);
					String mutListStringFast = mutListFast.join();
					mutTypesFast.put(mutType,  mutListStringFast);
				}
				mutPatternMutTypesCalculatedAsi.put(fmtMutationList, mutTypesAsi);
				mutPatternMutTypesCalculatedFast.put(fmtMutationList, mutTypesFast);
			}

			// Retrieve pre-existing saved mutTypes in json format
			Type mapTypeMutTypes = new TypeToken<Map<String, Map<MutType, String>>>() {}.getType();
			final InputStream mutPatternMutTypesJsonInputStream =
					MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
						"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".mutTypes.json");
			BufferedReader brMutTypes = new BufferedReader(new InputStreamReader(mutPatternMutTypesJsonInputStream));
			Map<String, Map<MutType, String>> mutPatternMutTypesExpected = Json.loads(brMutTypes, mapTypeMutTypes);


			// Compare the results
			Assert.assertTrue(
				mutPatternMutTypesExpected.size() == mutPatternMutTypesCalculatedAsi.size() &&
				mutPatternMutTypesExpected.size() == mutPatternMutTypesCalculatedFast.size()
			);
			for (String mutList : mutPatternMutTypesExpected.keySet()) {
				for (MutType mutType : mutPatternMutTypesExpected.get(mutList).keySet()) {
					String expectedMutList = mutPatternMutTypesExpected.get(mutList).get(mutType);
					String calculatedMutListAsi = mutPatternMutTypesCalculatedAsi.get(mutList).get(mutType);
					String calculatedMutListFast = mutPatternMutTypesCalculatedFast.get(mutList).get(mutType);
					Assert.assertEquals(expectedMutList, calculatedMutListAsi);
					Assert.assertEquals(expectedMutList, calculatedMutListFast);
					//System.out.println(mutList + ":" + mutType + " exp:" + expectedMutList +
					//		                                     " calc:" + calculatedMutList);
				}
			}


			// Test the comments files
			// Calculated levels for each mutation list
			Map<String, Map<String, String>> mutPatternCommentsCalculatedAsi = new HashMap<>();
			Map<String, Map<String, String>> mutPatternCommentsCalculatedFast = new HashMap<>();
			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR geneDRAsi = allResistanceResultsAsi.get(mutations);
				GeneDR geneDRFast = allResistanceResultsFast.get(mutations);

				Map<String, String> mutCommentsAsi = ConditionalComments
					.getComments(geneDRAsi).stream()
					.collect(Collectors.toMap(
						bc -> bc.getName(),
						bc -> bc.getText()));
				Map<String, String> mutCommentsFast = ConditionalComments
					.getComments(geneDRFast).stream()
					.collect(Collectors.toMap(
						bc -> bc.getName(),
						bc -> bc.getText()));

				mutPatternCommentsCalculatedAsi.put(fmtMutationList, mutCommentsAsi);
				mutPatternCommentsCalculatedFast.put(fmtMutationList, mutCommentsFast);
			}

			// Retrieve pre-existing saved comments
			Type mapTypeMutComments = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
			final InputStream mutPatternCommentsJsonInputStream =
					MutationPatternsResistanceJsonComparisonTest.class.getClassLoader().getResourceAsStream(
						"MutationPatternsExpectedResistanceResults/Patterns" + drugClass + ".comments.json");
			BufferedReader brComments = new BufferedReader(new InputStreamReader(mutPatternCommentsJsonInputStream));
			Map<String, Map<String, String>> mutPatternCommentsExpected = Json.loads(brComments, mapTypeMutComments);

			// Compare the results
			Assert.assertTrue(
				mutPatternCommentsExpected.size() == mutPatternCommentsCalculatedAsi.size() &&
				mutPatternCommentsExpected.size() == mutPatternCommentsCalculatedFast.size()
			);
			for (String mutList : mutPatternCommentsExpected.keySet()) {
				for (String mutString : mutPatternCommentsExpected.get(mutList).keySet()) {
					String expectedComment = mutPatternCommentsExpected.get(mutList).get(mutString);
					String calculatedCommentAsi = mutPatternCommentsCalculatedAsi.get(mutList).get(mutString);
					String calculatedCommentFast = mutPatternCommentsCalculatedFast.get(mutList).get(mutString);
					//System.out.println("  ExpectedComment:" + expectedComment);
					//System.out.println("CalculatedComment:" + calculatedComment);

					try {
						// Fix this AssertionError by re-run MutationPatternsResistanceToJson.java and clean Eclipse
						Assert.assertTrue(expectedComment.equals(calculatedCommentAsi));
						Assert.assertTrue(expectedComment.equals(calculatedCommentFast));
					} catch(AssertionError e) {
						System.out.println("         MutationList: " + mutList);
						System.out.println("      ExpectedComment: " + expectedComment);
						System.out.println(" CalculatedCommentAsi: " + calculatedCommentAsi);
						System.out.println("CalculatedCommentFast: " + calculatedCommentFast);
						throw e;
					}
					//System.out.println(mutList + ":\nexp:" + expectedComment + "\ncal:" + calculatedComment);
				}
			}
		}
	}
}
