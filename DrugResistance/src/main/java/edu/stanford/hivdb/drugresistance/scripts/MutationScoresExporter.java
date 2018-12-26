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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.drugresistance.database.MutationScores;
import edu.stanford.hivdb.drugresistance.database.MutationComboScores;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.MyStringUtils;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class MutationScoresExporter {

	private static class MutationElem {

		@SuppressWarnings("unused")
		private Gene gene;
		@SuppressWarnings("unused")
		private String cons;
		@SuppressWarnings("unused")
		private int pos;
		@SuppressWarnings("unused")
		private String aas;

		public MutationElem(
				Gene gene, int pos, String aas) {
			this.gene = gene;
			this.cons = gene.getConsensus(pos);
			this.pos = pos;
			this.aas =
			   	MyStringUtils.sortAlphabetically(aas).toUpperCase()
				.replace("-", "Deletion")
				.replace("_", "Insertion");
		}

	}


	private static class RuleItem {
		@SuppressWarnings("unused")
		private List<MutationElem> mutations;
		private Map<String, Integer> drugScores = new TreeMap<>();

		public RuleItem(
				MutationElem... mutations) {
			this.mutations = Arrays.asList(mutations);
		}
	}

	public static void main(String[] args) {
		Map<DrugClass, Object> result = new EnumMap<>(DrugClass.class);
		for (DrugClass drugClass : DrugClass.values()) {
			result.put(drugClass, getScores(drugClass));
		}
		MyFileUtils.writeFile("__output/mutation-score.json", Json.dumps(result));
	}

	private static Map<String, Object> getScores(DrugClass drugClass) {
		List<RuleItem> ruleItems = new ArrayList<>();
		Map<String, RuleItem> ruleItemsMap = new HashMap<>();

		MutationScores
			.getMutScores(drugClass)
			.stream()
			.forEach(m -> {
				RuleItem item = null;
				String mutStr = "" + m.pos + m.aa;
				if (ruleItemsMap.containsKey(mutStr)) {
					item = ruleItemsMap.get(mutStr);
				}
				else {
					MutationElem mutElem = new MutationElem(m.gene, m.pos, "" + m.aa);
					item = new RuleItem(mutElem);
					ruleItemsMap.put(mutStr, item);
					ruleItems.add(item);
				}
				item.drugScores.put(m.drug.getDisplayAbbr(), (int)(double) m.score);
			});

		MutationComboScores
			.getCombinationScores(drugClass)
			.stream()
			.forEach(c -> {
				RuleItem item = null;
				if (ruleItemsMap.containsKey(c.rule)) {
					item = ruleItemsMap.get(c.rule);
				}
				else {
					MutationElem[] mutElems = parseCombo(c.gene, c.rule);
					item = new RuleItem(mutElems);
					ruleItemsMap.put(c.rule, item);
					ruleItems.add(item);
				}
				item.drugScores.put(c.drug.getDisplayAbbr(), (int)(double) c.score);
			});

		Map<String, Object> finalOutput = new TreeMap<>();
		finalOutput.put("drugClass", drugClass);
		finalOutput.put(
			"drugs",
			drugClass.getDrugsForHivdbTesting()
			.stream().map(drug -> drug.getDisplayAbbr()).toArray());
		finalOutput.put("items", ruleItems);

		return finalOutput;
	}

	private static MutationElem[] parseCombo(Gene gene, String rule) {
		List<MutationElem> rList = new ArrayList<>();
		for (Mutation mut : new MutationSet(gene, rule)) {
			int pos = mut.getPosition();
			String aas = mut.getAAs();
			rList.add(new MutationElem(gene, pos, aas));
		}
		MutationElem[] r = new MutationElem[rList.size()];
		rList.toArray(r);
		return r;
	}

}
