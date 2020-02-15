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

package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.comments.BoundComment;
import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.comments.ConditionType;
import edu.stanford.hivdb.comments.ConditionalComment;
import edu.stanford.hivdb.comments.ConditionalComments;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AAMutation;

public class ConditionalCommentTest {
	
	private static final HIV hiv = HIV.getInstance();
	private ConditionalComment<HIV> comment1;
	private Map<String, Object> conditionValue1 = new HashMap<>();
	private String comment_name1 = "DRVHigh";
	private DrugClass<HIV> drugclass1 = hiv.getDrugClass("PI");
	private String comment_text1 = "There is evidence for high-level DRV "
			+ "resistance. If DRV is administered it should be used twice daily.";
	
	private ConditionalComment<HIV> comment2;
	private String comment_name2 = "IN151A";
	private Map<String, Object> conditionValue2 = new HashMap<>();
	private DrugClass<HIV> drugclass2 = hiv.getDrugClass("INSTI");
	private String comment_text2 = "V151A is an extremely rare non-polymorphic mutation "
			+ "associated with minimally reduced susceptibility to RAL and EVG.";

	
	// ConditionalComment Class is only used inside of ConditionalComments
	// The Constructor is for load json object.
	@Before
	public void testConstructor() {
		
		conditionValue1.put("drug", "DRV");
		List<Double> levels = new ArrayList<>();
		levels.add(new Double(5.0));
		conditionValue1.put("levels", levels);
		
		
		comment1 = new ConditionalComment<HIV>(
					hiv.getStrain("HIV1"),
					comment_name1,
					drugclass1,
					ConditionType.DRUGLEVEL,
					conditionValue1,
					comment_text1);

		conditionValue2.put("gene", "IN");
		conditionValue2.put("pos", 151.0);
		conditionValue2.put("aas", "A");
		comment2 = new ConditionalComment<HIV>(
				hiv.getStrain("HIV1"),
				comment_name2,
				drugclass2,
				ConditionType.MUTATION,
				conditionValue2,
				comment_text2);
	}

	@Test
	public void testGetMutationGene() {
		assertNull(comment1.getMutationGene());
		
		assertNotNull(comment2.getMutationGene());
		assertTrue(comment2.getMutationGene() instanceof Gene);
		assertEquals(comment2.getMutationGene().getName(), "HIV1IN");
	}

	@Test
	public void testGetMutationPosition() {
		assertNull(comment1.getMutationPosition());
		
		assertEquals(comment2.getMutationPosition(), Integer.valueOf(151));
	}

	@Test
	public void testGetMutationAAs() {
		assertNull(comment1.getMutationAAs());
		
		assertEquals(comment2.getMutationAAs(), "A");
	}
	
	@Test
	public void testGetMutationGenePosition() {
		assertNull(comment1.getMutationPosition());
		
		assertEquals(comment2.getMutationGenePosition().getPosition(), new Integer(151));
	}
	
	@Test
	public void testGetDrugLevels() {
		System.out.println(comment1.getDrugLevels());
		
		assertEquals(comment2.getDrugLevels().size(), 0);
	}

	@Test
	public void testGetDrugLevelsText() {
		assertEquals(comment1.getDrugLevelsText(), "DRV: 5");
		
		assertEquals(comment2.getDrugLevelsText(), "");
	}

	@Test
	public void testGetName() {
		assertEquals("DRVHigh", comment1.getName());
	}
	
	@Test
	public void testGetText() {
		assertEquals(comment_text1, comment1.getText());
	}
	
	@Test
	public void testGetDrugClass() {
		assertEquals(hiv.getDrugClass("PI"), comment1.getDrugClass());
	}
	
	@Test
	public void testGetConditionType() {
		assertEquals(ConditionType.DRUGLEVEL, comment1.getConditionType());
	}

	@Test
	public void testGetGene() {
		assertEquals(hiv.getGene("HIV1PR"), comment1.getGene());
		
		//WithGene Interface
		assertEquals(comment1.getAbstractGene(), "PR");
	}
}
