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

package edu.stanford.hivdb.drugresistance.reports;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
import edu.stanford.hivdb.drugresistance.reports.TabularPatternsComparison.ComparisonDataLoader;
import edu.stanford.hivdb.drugs.DrugClass;

public class TabularPatternsComparisonTest {

	@Test
	public void testGetInstance() {
		TabularPatternsComparison instance1 = TabularPatternsComparison.getInstance(DrugClass.INSTI);
		TabularPatternsComparison instance2 = TabularPatternsComparison.getInstance(DrugClass.INSTI);
		assertEquals(instance1, instance2);
	}

	@Test
	public void testToString() {
		TabularPatternsComparison instance = TabularPatternsComparison.getInstance(DrugClass.INSTI);
		assertEquals(
			"Pattern\tCount\tDTG\tEVG\tRAL\tNum Diffs\tMax Diff",
			instance.toString().split("\n")[0]);
	}

	@Test
	public void testComparisonDataLoader() throws Exception {
		ComparisonDataLoader dl = spy(new ComparisonDataLoader());
		for (DrugClass drugClass : DrugClass.values()) {
			MutationPatterns mp = new MutationPatterns(drugClass);
			Map<String, Integer> pCount = mp.getAllPatternCounts()
				.entrySet()
				.stream()
				.filter(e -> e.getValue() > 200)
				.collect(Collectors.toMap(
					e -> e.getKey(),
					e -> e.getValue(),
					(v1, v2) -> v1,
					LinkedHashMap::new));

			when(dl.getAllPatternCounts(drugClass)).thenReturn(pCount);
		}

		Map<?, ?> result = dl.load();
		assertEquals(4, result.keySet().size());
		assertEquals("allRows", dl.getFieldName());
	}

}
