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

package edu.stanford.hivdb.drugresistance.database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
import edu.stanford.hivdb.drugresistance.database.MutationPatterns.MutationPattern;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.testutils.MockDatabase;

/**
 * Prints to file a list of all mutation patterns and their counts by drugClass using the most active
 * version (i.e. by obtaining the patterns from MutationPatterns)
 *
 */
public class MutationPatternsTest {

	@Test
	public void testConstructor() {
		MutationPatterns mutPatterns = new MutationPatterns(DrugClass.INSTI);
		Map<String, Integer> allPatternCounts = mutPatterns.getAllPatternCounts();
		assertEquals(DrugClass.INSTI, mutPatterns.getDrugClass());
		assertTrue(mutPatterns.getPatternCount("E157Q") > 351);
		assertTrue(allPatternCounts.get("E157Q") > 351);
		assertTrue(mutPatterns.getPatternCount("E138A,G140S,Q148H") > 18);
		assertTrue(mutPatterns.getPatternCount("N155H,E157Q,G163K") > 4);
	}

	@Test
	public void testGetOrderedMutPatterns() {
		MutationPatterns mutPatterns = new MutationPatterns(DrugClass.NRTI);
		List<String> mutPatternStrList = mutPatterns.getOrderedMutPatterns();
		assertTrue(mutPatternStrList.size() > 4749);
		assertEquals("M184V", mutPatternStrList.get(0));
	}

	@Test
	public void testGroupMutationPatternsByPatternAndDrugs() {
		MutationPatterns mutPatterns = new MutationPatterns(DrugClass.NNRTI);
		Map<String, Map<Drug, MutationPattern>> mutPatternMap = mutPatterns.groupMutationPatternsByPatternAndDrugs();
		assertEquals((Integer) 115, mutPatternMap.get("A98G,K103N,E138Q,K238T").get(Drug.EFV).totalScore);
	}

	@Test
	public void testDataLoader() throws SQLException {
		MockDatabase db = new MockDatabase();
		MutationPatterns.PatternDataLoader dl = new MutationPatterns.PatternDataLoader();
		assertEquals("allMutationPatterns", dl.getFieldName());
		dl.db = db;
		String sql =
			"SELECT SequenceID, Pos, AA FROM tblMutations WHERE Gene = ? " +
			// no stop codon
			"AND AA != '.' ORDER BY SequenceID, Pos, AA";
		db.whenSelect(
			sql,
			new Object[] {
				"PR"
			},
			new String[] {
				"SequenceID",
				"Pos",
				"AA"
			},
			new Object[][] {
				{"2209", 54, "V"},
				{"2209", 82, "F"},
				{"2211", 54, "L"},
				{"2211", 82, "A"},
				{"2569", 74, "S"},
				{"2569", 82, "A"},
				{"2585", 46, "I"},
				{"2585", 48, "V"},
				{"2585", 54, "T"},
				{"2585", 71, "L"},
				{"2585", 82, "A"},
				{"2604", 73, "S"},
				{"2604", 82, "T"},
				{"2604", 90, "M"},
				{"2609", 62, "V"},
				{"2609", 73, "S"},
				{"2617", 53, "L"},
				{"2617", 73, "S"},
				{"2617", 90, "M"},
				{"2619", 70, "T"},
				{"2619", 73, "S"},
			}
		);
		Map<DrugClass, List<MutationPattern>> result = dl.load();
		assertEquals(48, result.get(DrugClass.PI).size());
		assertEquals(0, result.get(DrugClass.NRTI).size());
		assertEquals(0, result.get(DrugClass.NNRTI).size());
		assertEquals(0, result.get(DrugClass.INSTI).size());
	}

}
