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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.comments.ConditionType;
import edu.stanford.hivdb.comments.ConditionalComment;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.GenePosition;
import edu.stanford.hivdb.viruses.Strain;
import edu.stanford.hivdb.hivfacts.HIV;

public class ConditionalCommentTest {

	private static final HIV hiv = HIV.getInstance();
	
	private static ConditionalComment<HIV> commentDrug = null;
	
	private static ConditionalComment<HIV> commentMutation = null;
	
	private static void initInstance() {
		Strain<HIV> strain = hiv.getStrain("HIV1");
		String commentName = "DRVHigh";
		DrugClass<HIV> drugClass = hiv.getDrugClass("PI");
		ConditionType condType = ConditionType.DRUGLEVEL;
		
		Map<String, Object> condValue = new HashMap<>();
		List<Double> levels = new ArrayList<>();
		levels.add(new Double(5.0));
		condValue.put("drug", "DRV");
		condValue.put("levels", levels);
		
		String comment = "There is evidence for high-level DRV resistance. "
				+ "If DRV is administered it should be used twice daily.";
		
		commentDrug = new ConditionalComment<HIV>(
				strain,
				commentName,
				drugClass,
				condType,
				condValue,
				comment
				);
		
		// For commentMutation
		commentName = "IN151A";
		drugClass = hiv.getDrugClass("INSTI");
		condType = ConditionType.MUTATION;
		
		condValue = new HashMap<>();
		condValue.put("gene", "IN");
		condValue.put("pos", 151.0);
		condValue.put("aas", "A");
		
		comment = "V151A is an extremely rare non-polymorphic mutation "
				+ "associated with minimally reduced susceptibility to RAL and EVG.";
		
		commentMutation = new ConditionalComment<HIV>(
				strain,
				commentName,
				drugClass,
				condType,
				condValue,
				comment
				);
	}
	
	static {
		initInstance();
	}


	// ConditionalComment Class is only used inside of ConditionalComments
	// The Constructor is for load json object.
	@Before
	public void testConstructor() {

		assertNotNull(commentDrug);
		assertNotNull(commentMutation);
	}

	@Test
	public void testGetMutationGene() {
		assertNull(commentDrug.getMutationGene());
		assertNotNull(commentMutation.getMutationGene());
		
		assertEquals(commentMutation.getMutationGene(), hiv.getGene("HIV1IN"));
	}

	@Test
	public void testGetMutationPosition() {
		assertNull(commentDrug.getMutationPosition());
		assertNotNull(commentMutation.getMutationPosition());

		assertEquals(commentMutation.getMutationPosition(), Integer.valueOf(151));
	}

	@Test
	public void testGetMutationAAs() {
		assertNull(commentDrug.getMutationAAs());
		assertNotNull(commentMutation.getMutationAAs());

		assertEquals(commentMutation.getMutationAAs(), "A");
	}

	@Test
	public void testGetMutationGenePosition() {
		assertNull(commentDrug.getMutationGenePosition());
		assertNotNull(commentMutation.getMutationGenePosition());
		
		GenePosition<HIV> genePos = new GenePosition<HIV>(hiv.getGene("HIV1IN"), 151);
		assertEquals(commentMutation.getMutationGenePosition(), genePos);
	}

	@Test
	public void testGetDrugLevels() {
		assertEquals(commentDrug.getDrugLevels().size(), 1);
		assertEquals(commentMutation.getDrugLevels().size(), 0);
		
		Strain<HIV> strain = hiv.getStrain("HIV1");
		String commentName = "DRVHighAndTPVIntermediate";
		DrugClass<HIV> drugClass = hiv.getDrugClass("PI");
		ConditionType condType = ConditionType.DRUGLEVEL;
		
		Map<String, Object> condValue = new HashMap<>();
		List<Object> andValue = new ArrayList<>();
		
		Map<String, Object> condValueOfAnd = new HashMap<>();
		List<Double> levels = new ArrayList<>();
		levels.add(new Double(5.0));
		condValueOfAnd.put("drug", "DRV");
		condValueOfAnd.put("levels", levels);
		andValue.add(condValueOfAnd);
		levels = new ArrayList<>();
		levels.add(new Double(4.0));
		condValueOfAnd.put("drug", "TPV");
		condValueOfAnd.put("levels", levels);
		andValue.add(condValueOfAnd);
		condValue.put("and", andValue);
		
		String comment = "There is evidence for high-level DRV resistance. "
				+ "If DRV is administered it should be used twice daily.";
		
		ConditionalComment<HIV> commentDrugAnd = new ConditionalComment<HIV>(
				strain,
				commentName,
				drugClass,
				condType,
				condValue,
				comment
				);
		
		assertEquals(commentDrugAnd.getDrugLevels().size(), 1);
	}

	@Test
	public void testGetDrugLevelsText() {
		assertEquals(commentDrug.getDrugLevelsText(), "DRV: 5");

		assertEquals(commentMutation.getDrugLevelsText(), "");
	}

	@Test
	public void testGetName() {
		assertEquals("DRVHigh", commentDrug.getName());
	}

	@Test
	public void testGetText() {
		assertTrue(commentDrug.getText().contains("DRV"));
	}

	@Test
	public void testGetDrugClass() {
		assertEquals(hiv.getDrugClass("PI"), commentDrug.getDrugClass());
		assertEquals(hiv.getDrugClass("INSTI"), commentMutation.getDrugClass());
	}

	@Test
	public void testGetConditionType() {
		assertEquals(ConditionType.DRUGLEVEL, commentDrug.getConditionType());
		assertEquals(ConditionType.MUTATION, commentMutation.getConditionType());
	}

	@Test
	public void testGetGene() {
		assertEquals(hiv.getGene("HIV1PR"), commentDrug.getGene());	
		assertEquals(hiv.getGene("HIV1IN"), commentMutation.getGene());	
	}
	
	//WithGene Interface
	@Test
	public void testGetStrain() {
		assertEquals(hiv.getStrain("HIV1"), commentDrug.getStrain());
		assertEquals(hiv.getStrain("HIV1"), commentMutation.getStrain());
	}
	
	@Test
	public void testGetAbstractGene() {
		assertEquals(commentDrug.getAbstractGene(), "PR");
		assertEquals(commentMutation.getAbstractGene(), "IN");
	}
}
