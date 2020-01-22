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

package edu.stanford.hivdb.drugresistance.algorithm;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugresistance.scripts.HivdbRulesForAsiConstructor;
import edu.stanford.hivdb.hivfacts.HIVDrug;
import edu.stanford.hivdb.hivfacts.HIVDrugClass;

public class HivdbAsiRulesForDrugTest {

	@Test
	public void test() throws SQLException {
		for (HIVDrugClass drugClass : HIVDrugClass.values()) {
			for (HIVDrug drug : drugClass.getDrugs()) {
				HivdbRulesForAsiConstructor hivdbAsiRulesForDrug = new HivdbRulesForAsiConstructor(HivdbVersion.getLatestVersion(), drug);
				List<String> individualMutRules = hivdbAsiRulesForDrug.getIndividualMutRules();
				System.out.println(drug);
				for (String rule : individualMutRules) {
					System.out.println(rule);
				}
				List<String> comboMutRules = hivdbAsiRulesForDrug.getComboMutRules();
				for (String rule : comboMutRules) {
					System.out.println(rule);
				}
				List<String> compoundRules = hivdbAsiRulesForDrug.getCompoundRules();
				for (String rule : compoundRules) {
					System.out.println(rule);
				}
			}
		}
	}

}
