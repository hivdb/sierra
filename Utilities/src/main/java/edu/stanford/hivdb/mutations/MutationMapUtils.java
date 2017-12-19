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

package edu.stanford.hivdb.mutations;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MutationMapUtils {
	public enum SortOrder {ASC, DESC};
	/**
	 * @param unsortedMap
	 * @return sorted map
	 */
	public static<T> Map<T, Double> sortByComparator (Map<T, Double> unsortedMap, SortOrder sortOrder) {
		List<Map.Entry<T, Double>> list = new LinkedList<Map.Entry<T, Double>>(unsortedMap.entrySet());

		// Sort list with comparator; Sort in descending order
		if (sortOrder.equals(SortOrder.DESC)) {
			Collections.sort(list, new Comparator<Map.Entry<T, Double>>(){
				public int compare(Map.Entry<T, Double> o1, Map.Entry<T, Double> o2) {
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});
		} else {
			Collections.sort(list, new Comparator<Map.Entry<T, Double>>(){
				public int compare(Map.Entry<T, Double> o1, Map.Entry<T, Double> o2) {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});
		}

		// Convert sorted map back to a Map
		Map<T, Double> sortedMap = new LinkedHashMap<>();
		for (Iterator<Map.Entry<T, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<T, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/**
	 *
	 * @param map
	 * @return a map in which the value is an integer rather than a double
	 */
	public static Map<Mutation, Integer> convertMutScoresToInts(Map<Mutation, Double> map) {
		Map<Mutation, Integer> newMutScores = new HashMap<>();
		for (Mutation mut : map.keySet()) {
			int value = map.get(mut).intValue();
			newMutScores.put(mut, value);
		}
		return newMutScores;
	}

	/**
	 * Used only by getScoredMuts in Algorithm comparison
	 */
	public static String printMutScoresAsInts(Map<Mutation, Double> map) {
		StringBuffer output = new StringBuffer();
		for (Mutation mut : map.keySet()) {
			int value = map.get(mut).intValue();
			output.append(mut.getHumanFormat() + " (" + value + "), ");
		}
		if (output.length() > 0) {
			output.setLength(output.length() - 2);
		}
		return output.toString();
	}





	public static String printMutSetScoresAsInts(Map<MutationSet, Double> comboMutsSortedByScore) {
		StringBuffer output = new StringBuffer();
		for (MutationSet mutList : comboMutsSortedByScore.keySet()) {
			int value = comboMutsSortedByScore.get(mutList).intValue();
			String mutListOutput = mutList.join(" + ");
			output.append(mutListOutput + " (" + value + "), ");
		}
		output.setLength(output.length() - 2);
		return output.toString();
	}

	public static String printMutScoresAsDouble(Map<Mutation, Double> map) {
		StringBuffer output = new StringBuffer();
		for (Mutation mut : map.keySet()) {
			Double value = map.get(mut);
			output.append(mut.getHumanFormat() + " (" + value + "), ");
		}
		output.setLength(output.length() - 2);
		return output.toString();
	}

	public static String printMutSetScoresAsDouble(Map<MutationSet, Double> comboMutsSortedByScore) {
		StringBuffer output = new StringBuffer();
		for (MutationSet mutSet : comboMutsSortedByScore.keySet()) {
			Double value = comboMutsSortedByScore.get(mutSet);
			String mutSetOutput = mutSet.join(" + ");
			output.append(mutSetOutput + " (" + value + "), ");
		}
		output.setLength(output.length() - 2);
		return output.toString();
	}

}
