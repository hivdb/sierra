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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugresistance.database.MutationComboScores;
import edu.stanford.hivdb.drugresistance.database.MutationComboScores.ComboScore;
import edu.stanford.hivdb.drugresistance.database.MutationScores;
import edu.stanford.hivdb.drugresistance.database.MutationScores.MutScore;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.mutations.AA;

public class HivdbRulesForAsiConstructor {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	private Drug drug;
	@SuppressWarnings("unused")
	private HivdbVersion version;
	private List<String> individualMutRules = new ArrayList<>();
	private List<String> comboMutRules = new ArrayList<>();
	private List<String> compoundRules = new ArrayList<>();
	private List<String> allRules = new ArrayList<>();

	/**
	 * Provides access to all of the Hivdb rules (aka scores) for a drug based upon
	 *    individual mutations, combination mutations, and compound mutation pairs.
	 *    This class is used by the Asi constructor
	 * @param version
	 * @param drug
	 * @throws SQLException
	 */
	public HivdbRulesForAsiConstructor(HivdbVersion version, Drug drug) throws SQLException {
		this.version = version;
		this.drug = drug;
		genOrderedListIndividualMutScores();
		genOrderedListComboMutScores();

	}

	public List<String> getIndividualMutRules() { return individualMutRules; }
	public List<String> getComboMutRules() { return comboMutRules; }
	public List<String> getCompoundRules() { return compoundRules; }
	public List<String> getAllRules() {
		allRules.addAll(individualMutRules);
		allRules.addAll(comboMutRules);
		allRules.addAll(compoundRules);
		return allRules;
	}


	private void genOrderedListComboMutScores() {
		for (List<ComboScore> comboScores :
				MutationComboScores.groupComboScoresByPositions(drug).values()) {
			String[] ruleTextArr = comboScores
				.stream()
				.map(cs -> {
					String rule = cs.rule.replace("+", " AND ");
					rule = AA.toASIFormat(rule);
					int score = cs.score.intValue();
					return String.format("(%s) => %d", rule, score);
				})
				.toArray(String[]::new);
			if (ruleTextArr.length == 1) {
				comboMutRules.add(ruleTextArr[0]);
			}
			else {
				compoundRules.add(
					"MAX (" + String.join(", ", ruleTextArr) + ")");
			}
		}
	}

	private void genOrderedListIndividualMutScores() {
		for (Map.Entry<Integer, List<MutScore>> entry :
				MutationScores.groupMutScoresByPos(drug).entrySet()) {
			int pos = entry.getKey();
			List<MutScore> mutScores = entry.getValue();
			String[] ruleTextArr = mutScores
				.stream()
				.map(ms -> {
					String aa = AA.toASIFormat("" + ms.aa);
					int score = ms.score.intValue();
					return String.format("%d%s => %d", pos, aa, score);
				})
				.toArray(String[]::new);
			if (ruleTextArr.length == 1) {
				individualMutRules.add(ruleTextArr[0]);
			}
			else {
				individualMutRules.add(
					"MAX ( " + String.join(", ", ruleTextArr) + " )");
			}
		}
	}

}
