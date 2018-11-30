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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
import edu.stanford.hivdb.drugresistance.database.MutationPatterns.MutationPattern;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;

public class MutationPatternScoresExporter {

	private static final Pattern mutationRegex;

	static {
		mutationRegex = Pattern.compile("^(.)(\\d+)(.|Insertion|Deletion)$");
	}

	private static class MutationElem {
		@SuppressWarnings("unused")
		private Gene gene;
		@SuppressWarnings("unused")
		private String cons;
		@SuppressWarnings("unused")
		private int pos;
		@SuppressWarnings("unused")
		private String aa;

		public MutationElem(Gene gene, int pos, String aa) {
			this.gene = gene;
			this.cons = gene.getReference(pos);
			this.pos = pos;
			this.aa = aa;
		}
	}

	private static List<MutationElem> parsePatternString(
			DrugClass drugClass, String pattern) {
		List<MutationElem> mutElements = new ArrayList<>();
		String[] mutArr = StringUtils.split(pattern, ',');
		Gene gene = drugClass.gene();
		for (int i=0; i < mutArr.length; i++) {
			Matcher mm = mutationRegex.matcher(mutArr[i]);
			mm.find();
			int pos = Integer.parseInt(mm.group(2));
			String aa = mm.group(3);
			MutationElem mutElem = new MutationElem(gene, pos, aa);
			mutElements.add(mutElem);
		}
		return mutElements;
	}

	private static Map<String, Object> getScores(DrugClass drugClass) {
		MutationPatterns mpObj = new MutationPatterns(drugClass);
		Map<String, Map<Drug, MutationPattern>>
			mutPatterns = mpObj.groupMutationPatternsByPatternAndDrugs();
		List<String> orderdPatterns = mpObj.getOrderedMutPatterns();

		List<Map<String, Object>> patterns = new ArrayList<>();
		for (String patternStr: orderdPatterns) {
			List<MutationElem>
			   	mutElements = parsePatternString(drugClass, patternStr);
			Map<String, Map<String, Integer>>
				drugScores = mutPatterns.get(patternStr)
					.entrySet()
					.stream()
					.collect(Collectors.toMap(
						e -> e.getKey().getDisplayAbbr(),
						e -> {
							MutationPattern mp = e.getValue();
							Map<String, Integer> score = new TreeMap<>();
							score.put("score", mp.totalScore);
							score.put("level", mp.level);
							return score;
						},
						(e1, e2) -> e1,
						() -> new TreeMap<>()));
			int count = mpObj.getPatternCount(patternStr);

			Map<String, Object> pattern = new TreeMap<>();
			pattern.put("mutations", mutElements);
			pattern.put("count", count);
			pattern.put("drugScores", drugScores);
			patterns.add(pattern);
		}

		Map<String, Object> finalOutput = new TreeMap<>();
		finalOutput.put("drugClass", drugClass);
		finalOutput.put(
			"drugs",
			drugClass.getDrugsForHivdbTesting()
			.stream().map(drug -> drug.getDisplayAbbr()).toArray());
		finalOutput.put("patterns", patterns);

		return finalOutput;
	}

	public static void main(String[] args) {
		Map<DrugClass, Object> result = new EnumMap<>(DrugClass.class);
		for (DrugClass drugClass : DrugClass.values()) {
			result.put(drugClass, getScores(drugClass));
		}
		MyFileUtils.writeFile("__output/pattern-score.json", Json.dumps(result));
	}

}
