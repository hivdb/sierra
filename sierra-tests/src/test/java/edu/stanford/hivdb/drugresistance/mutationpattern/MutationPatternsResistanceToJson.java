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

package edu.stanford.hivdb.drugresistance.mutationpattern;

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

import edu.stanford.hivdb.comments.ConditionalComments;
import edu.stanford.hivdb.drugresistance.GeneDR;
//import edu.stanford.hivdb.drugresistance.GeneDRFast;
//import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles;
//import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles.TestMutationPatterns;
//import edu.stanford.hivdb.drugresistance.database.MutationPatternFileReader;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.MutationType;
import edu.stanford.hivdb.utilities.Json;

//public class MutationPatternsResistanceToJson {
//	private static final String PATH_PATTERN =
//			"src/test/resources/MutationPatternsExpectedResistanceResults/Patterns%s.%s.json";
//
//	public static void main(String[] args) {
//
//		for (TestMutationPatterns testMutationPatterns : TestMutationPatterns.values()) {
//			final InputStream mutationPatternsInputStream =
//					TestMutationPatternFiles.getTestMutationPatternsInputStream(testMutationPatterns);
//			HIVDrugClass drugClass = testMutationPatterns.getDrugClass();
//			System.out.println("In MutationPatternsResistanceToJson:" + testMutationPatterns.toString());
//			final List<MutationSet> mutationLists =
//					MutationPatternFileReader.readMutationListsForDrugClass(drugClass, mutationPatternsInputStream);
//
//			Map<String, Map<HIVDrug, Integer>> totalDrugScores = new TreeMap<>();
//			Map<String, Map<HIVDrug, Integer>> totalDrugLevels = new TreeMap<>();
//			Map<String, Map<MutationType, String>> mutationTypes = new TreeMap<>();
//			Map<String, Map<String, String>> mutationComments = new TreeMap<>();
//
//			for (MutationSet mutations : mutationLists) {
//				String fmtMutationList = mutations.join();
//				GeneDR resistanceResults = new GeneDRFast(HIVGene.valueOf(HIVStrain.HIV1, drugClass.gene()), mutations);
//
//				Map<HIVDrug, Integer> totalScores = new EnumMap<>(HIVDrug.class);
//				Map<HIVDrug, Integer> totalLevels = new EnumMap<>(HIVDrug.class);
//				Map<MutationType, String> mutTypeLists = new EnumMap<>(MutationType.class);
//
//				for (HIVDrug drug : drugClass.getDrugs()) {
//					double totalScore = resistanceResults.getTotalDrugScore(drug);
//					int level = resistanceResults.getDrugLevel(drug);
//					totalScores.put(drug, (int)totalScore);
//					totalLevels.put(drug,  level);
//				}
//
//				Map<MutationType, MutationSet> mutTypes = new EnumMap<>(resistanceResults.groupMutationsByTypes());
//				for (MutationType mutType : mutTypes.keySet()) {
//					MutationSet mutList = mutTypes.get(mutType);
//					String mutListString = mutList.join();
//					mutTypeLists.put(mutType,  mutListString);
//				}
//
//				Map<String, String> resistanceComments = ConditionalComments
//					.getComments(resistanceResults).stream()
//					.collect(Collectors.toMap(
//						bc -> bc.getName(),
//						bc -> bc.getText(),
//						(c1, c2) -> c1, TreeMap::new
//					));
//
//				totalDrugScores.put(fmtMutationList,  totalScores);
//				totalDrugLevels.put(fmtMutationList,  totalLevels);
//				mutationTypes.put(fmtMutationList, mutTypeLists);
//				mutationComments.put(fmtMutationList,  resistanceComments);
//			}
//
//			final String allScores = Json.dumps(totalDrugScores);
//			final String allLevels = Json.dumps(totalDrugLevels);
//			final String allMutTypes = Json.dumps(mutationTypes);
//			final String allMutComments = Json.dumps(mutationComments);
//			Path pathScores = Paths.get(String.format(PATH_PATTERN, drugClass, "scores"));
//			Path pathLevels = Paths.get(String.format(PATH_PATTERN, drugClass, "levels"));
//			Path pathMutTypes = Paths.get(String.format(PATH_PATTERN, drugClass, "mutTypes"));
//			Path pathComments =  Paths.get(String.format(PATH_PATTERN, drugClass, "comments"));
//
//			try {
//				Files.write(pathScores, allScores.getBytes());
//				Files.write(pathLevels, allLevels.getBytes());
//				Files.write(pathMutTypes, allMutTypes.getBytes());
//				Files.write(pathComments, allMutComments.getBytes());
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}
