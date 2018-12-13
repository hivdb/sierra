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

package edu.stanford.hivdb.drugresistance.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles;
import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles.TestMutationPatterns;
import edu.stanford.hivdb.drugresistance.database.ConditionalComments;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MutationFileReader;


public class MutationPatternsResistanceToJson {
	private static final String PATH_PATTERN =
			"src/test/resources/MutationPatternsExpectedResistanceResults/Patterns%s.%s.json";

	public static void main(String[] args) {

		for (TestMutationPatterns testMutationPatterns : TestMutationPatterns.values()) {
			final InputStream mutationPatternsInputStream =
					TestMutationPatternFiles.getTestMutationPatternsInputStream(testMutationPatterns);
			DrugClass drugClass = testMutationPatterns.getDrugClass();
			System.out.println("In MutationPatternsResistanceToJson:" + testMutationPatterns.toString());
			final List<MutationSet> mutationLists =
					MutationFileReader.readMutationListsForDrugClass(drugClass, mutationPatternsInputStream);

			Map<String, Map<Drug, Integer>> totalDrugScores = new TreeMap<>();
			Map<String, Map<Drug, Integer>> totalDrugLevels = new TreeMap<>();
			Map<String, Map<MutType, String>> mutationTypes = new TreeMap<>();
			Map<String, Map<String, String>> mutationComments = new TreeMap<>();

			for (MutationSet mutations : mutationLists) {
				String fmtMutationList = mutations.join();
				GeneDR resistanceResults = new GeneDRFast(drugClass.gene(), mutations);

				Map<Drug, Integer> totalScores = new EnumMap<>(Drug.class);
				Map<Drug, Integer> totalLevels = new EnumMap<>(Drug.class);
				Map<MutType, String> mutTypeLists = new EnumMap<>(MutType.class);

				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					double totalScore = resistanceResults.getTotalDrugScore(drug);
					int level = resistanceResults.getDrugLevel(drug);
					totalScores.put(drug, (int)totalScore);
					totalLevels.put(drug,  level);
				}

				Map<MutType, MutationSet> mutTypes = new EnumMap<>(resistanceResults.groupMutationsByTypes());
				for (MutType mutType : mutTypes.keySet()) {
					MutationSet mutList = mutTypes.get(mutType);
					String mutListString = mutList.join();
					mutTypeLists.put(mutType,  mutListString);
				}

				Map<String, String> resistanceComments = ConditionalComments
					.getComments(resistanceResults).stream()
					.collect(Collectors.toMap(
						bc -> bc.getName(),
						bc -> bc.getText(),
						(c1, c2) -> c1, TreeMap::new
					));

				totalDrugScores.put(fmtMutationList,  totalScores);
				totalDrugLevels.put(fmtMutationList,  totalLevels);
				mutationTypes.put(fmtMutationList, mutTypeLists);
				mutationComments.put(fmtMutationList,  resistanceComments);
			}

			final String allScores = Json.dumps(totalDrugScores);
			final String allLevels = Json.dumps(totalDrugLevels);
			final String allMutTypes = Json.dumps(mutationTypes);
			final String allMutComments = Json.dumps(mutationComments);
			Path pathScores = Paths.get(String.format(PATH_PATTERN, drugClass, "scores"));
			Path pathLevels = Paths.get(String.format(PATH_PATTERN, drugClass, "levels"));
			Path pathMutTypes = Paths.get(String.format(PATH_PATTERN, drugClass, "mutTypes"));
			Path pathComments =  Paths.get(String.format(PATH_PATTERN, drugClass, "comments"));

			try {
				Files.write(pathScores, allScores.getBytes());
				Files.write(pathLevels, allLevels.getBytes());
				Files.write(pathMutTypes, allMutTypes.getBytes());
				Files.write(pathComments, allMutComments.getBytes());

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
